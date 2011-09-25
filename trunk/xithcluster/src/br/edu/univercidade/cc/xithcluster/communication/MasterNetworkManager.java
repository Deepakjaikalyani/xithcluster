package br.edu.univercidade.cc.xithcluster.communication;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.BufferOverflowException;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.apache.log4j.Logger;
import org.xith3d.loop.Updatable;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.View;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.IServer;
import org.xsocket.connection.Server;
import br.edu.univercidade.cc.xithcluster.DistributedRenderLoop;
import br.edu.univercidade.cc.xithcluster.DistributionStrategy;
import br.edu.univercidade.cc.xithcluster.PendingUpdate;
import br.edu.univercidade.cc.xithcluster.PendingUpdate.Type;
import br.edu.univercidade.cc.xithcluster.UpdateManager;
import br.edu.univercidade.cc.xithcluster.XithClusterConfiguration;
import br.edu.univercidade.cc.xithcluster.serialization.packagers.PointOfViewPackager;
import br.edu.univercidade.cc.xithcluster.serialization.packagers.ScenePackager;
import br.edu.univercidade.cc.xithcluster.serialization.packagers.UpdatesPackager;

public final class MasterNetworkManager implements Updatable {
	
	private Logger log = Logger.getLogger(MasterNetworkManager.class);
	
	private enum SessionState {
		CLOSED, STARTING, STARTED
	}
	
	private final MasterMessageBroker masterMessageBroker = new MasterMessageBroker();
	
	private final UpdatesPackager updatesPackager = new UpdatesPackager();
	
	private final PointOfViewPackager pointOfViewPackager = new PointOfViewPackager();
	
	private final ScenePackager scenePackager = new ScenePackager();
	
	private final DistributedRenderLoop distributedRenderLoop;
	
	private final UpdateManager updateManager;
	
	private DistributionStrategy distributionStrategy;
	
	private IServer composerServer;
	
	private IServer renderersServer;
	
	private INonBlockingConnection composerConnection;
	
	private List<INonBlockingConnection> renderersConnections = Collections.synchronizedList(new ArrayList<INonBlockingConnection>());
	
	private SessionState sessionState = SessionState.CLOSED;
	
	private boolean composerSessionStarted = false;
	
	private final BitSet renderersSessionStartedMask = new BitSet();
	
	private int currentFrame = 0;
	
	private boolean finishedFrame = false;
	
	private boolean forceFrameStart = false;
	
	public MasterNetworkManager(DistributedRenderLoop distributedRenderLoop, UpdateManager updateManager, DistributionStrategy distributionStrategy) {
		this.distributedRenderLoop = distributedRenderLoop;
		this.updateManager = updateManager;
		this.distributionStrategy = distributionStrategy;
	}
	
	public void initialize() throws UnknownHostException, IOException {
		renderersServer = new Server(XithClusterConfiguration.listeningAddress, XithClusterConfiguration.renderersConnectionPort, masterMessageBroker);
		composerServer = new Server(XithClusterConfiguration.listeningAddress, XithClusterConfiguration.composerConnectionPort, masterMessageBroker);
		
		renderersServer.start();
		composerServer.start();
	}
	
	private boolean sendPendingUpdates() {
		Map<INonBlockingConnection, List<PendingUpdate>> updatesPerRenderer;
		INonBlockingConnection rendererConnection;
		List<PendingUpdate> updates;
		
		log.info("Sending " + updateManager.getPendingUpdates().size() + " pending update(s)");
		
		// FIXME: Optimize
		updatesPerRenderer = new HashMap<INonBlockingConnection, List<PendingUpdate>>();
		for (PendingUpdate pendingUpdate : updateManager.getPendingUpdates()) {
			// TODO:
			if (pendingUpdate.getType() == Type.NODE_ADDED || pendingUpdate.getType() == Type.NODE_REMOVED) {
				rendererConnection = (INonBlockingConnection) ((Node) pendingUpdate.getTarget()).getUserData(ConnectionSetter.CONNECTION_USER_DATA);
			} else {
				rendererConnection = null;
			}
			
			if (rendererConnection != null) {
				updates = updatesPerRenderer.get(rendererConnection);
				
				if (updates == null) {
					updates = new ArrayList<PendingUpdate>();
					updatesPerRenderer.put(rendererConnection, updates);
				}
				
				updates.add(pendingUpdate);
			}
		}
		
		for (int i = 0; i < renderersConnections.size(); i++) {
			rendererConnection = renderersConnections.get(i);
			updates = updatesPerRenderer.get(rendererConnection);
			
			if (updates != null) {
				try {
					sendUpdateMessageToRenderer(rendererConnection, updatesPackager.serialize(updates));
					
					log.info(updates.size() + " update(s) were sent to renderer " + getRendererIndex(rendererConnection));
				} catch (IOException e) {
					log.error("Error sending pending updates", e);
					
					return false;
				}
			}
		}
		
		log.info("Pending updates sent successfully");
		
		return true;
	}
	
	private void distributeScene() {
		BranchGroup scene;
		View pointOfView;
		List<BranchGroup> distributedScenes;
		BranchGroup rendererScene;
		byte[] pointOfViewData;
		byte[] sceneData;
		int rendererIndex;
		
		scene = distributedRenderLoop.getScene();
		pointOfView = distributedRenderLoop.getPointOfView();
		
		log.info("Starting a new session");
		log.info("Executing " + distributionStrategy.getClass().getSimpleName() + "...");
		
		distributedScenes = distributionStrategy.distribute(scene, renderersConnections.size());
		
		if (distributedScenes.size() != renderersConnections.size()) {
			// TODO:
			throw new RuntimeException("The number of distributions is not the same as the number of renderers");
		}
		
		for (INonBlockingConnection rendererConnection : renderersConnections) {
			rendererIndex = getRendererIndex(rendererConnection);
			rendererScene = distributedScenes.get(rendererIndex);
			
			log.debug("**************");
			log.debug("Renderer " + rendererIndex);
			log.debug("**************");
			
			ConnectionSetter.setConnection(rendererScene, rendererConnection);
			
			try {
				pointOfViewData = pointOfViewPackager.serialize(pointOfView);
				sceneData = scenePackager.serialize(rendererScene);
			} catch (IOException e) {
				// TODO:
				throw new RuntimeException("Error serializing the scene", e);
			}
			
			log.trace("POV data size: " + pointOfViewData.length + " bytes");
			log.trace("Scene data size: " + sceneData.length + " bytes");
			
			try {
				sendStartSessionMessageToRenderer(rendererConnection, pointOfViewData, sceneData);
			} catch (IOException e) {
				log.error("Error sending distributed scene", e);
			}
		}
		
		if (composerConnection != null) {
			try {
				sendStartSessionMessageToComposer();
			} catch (IOException e) {
				// TODO:
				throw new RuntimeException("Error notifying composer", e);
			}
		}
	}
	
	private int getRendererIndex(INonBlockingConnection rendererConnection) {
		Integer rendererId;
		
		rendererId = (Integer) rendererConnection.getAttachment();
		
		return rendererId.intValue();
	}
	
	private void setRendererIndex(INonBlockingConnection arg0) {
		arg0.setAttachment(renderersConnections.size());
	}
	
	private boolean isThereAtLeastOneRendererAndOneComposer() {
		return !renderersConnections.isEmpty() && composerConnection != null;
	}
	
	private boolean isThereAlreadyAConnectedComposer() {
		return composerConnection != null;
	}
	
	private void tryToDistributeTheScene() {
		sessionState = SessionState.STARTING;
		
		try {
			distributeScene();
			log.info("Starting a new session");
		} catch (Throwable t) {
			sessionState = SessionState.CLOSED;
			log.error("Error starting session");
		}
	}
	
	private boolean isSessionReadyToStart() {
		return composerSessionStarted && renderersSessionStartedMask.cardinality() == renderersConnections.size();
	}

	private void startNewSession() {
		sessionState = SessionState.STARTED;
		
		log.info("Session started successfully");
		
		forceFrameStart = true;
	}
	
	private void closeCurrentSession() {
		sessionState = SessionState.CLOSED;
		
		renderersSessionStartedMask.clear();
		composerSessionStarted = false;
		
		log.info("Current session closed");
	}

	private void startNewFrame() {
		log.info("Starting new frame");
		
		finishedFrame = false;
		forceFrameStart = false;
		
		currentFrame += 1;
		
		log.trace("currentFrame=" + currentFrame);
		
		try {
			sendStartFrameMessage(composerConnection, currentFrame);
			
			for (INonBlockingConnection rendererConnection : renderersConnections) {
				sendStartFrameMessage(rendererConnection, currentFrame);
			}
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error sending start new frame notification: " + currentFrame, e);
		}
	}

	private void onFinishedFrame(Message message) {
		int frameIndex;
		
		log.info("Finished frame received");
		
		frameIndex = (Integer) message.getParameters()[0];
		
		if (currentFrame == frameIndex) {
			log.info("Finished current frame");
			log.trace("currentFrame=" + currentFrame);
			
			finishedFrame = true;
		} else {
			log.error("Out-of-sync finished frame received");
			log.trace("frameIndex=" + frameIndex);
		}
	}
	
	private void onConnected(INonBlockingConnection arg0) {
		if (isRendererConnection(arg0)) {
			onRendererConnected(arg0);
			
			log.info("New renderer connected");
		} else if (isComposerConnection(arg0)) {
			if (isThereAlreadyAConnectedComposer()) {
				log.error("There can be only one composer");
			} else {
				onComposerConnected(arg0);
				
				log.info("New composer connected");
			}
		} else {
			log.error("Unknown connection refused");
		}
	}
	
	private void onRendererConnected(INonBlockingConnection arg0) {
		INonBlockingConnection rendererConnection;
		
		rendererConnection = arg0;
		
		setRendererIndex(rendererConnection);
		renderersConnections.add(rendererConnection);
		
		rendererConnection.setAutoflush(false);
	}
	
	private void onComposerConnected(INonBlockingConnection arg0) {
		composerConnection = arg0;
		composerConnection.setAutoflush(false);
	}
	
	private boolean isRendererConnection(INonBlockingConnection arg0) {
		return arg0.getLocalPort() == XithClusterConfiguration.renderersConnectionPort;
	}
	
	private boolean isComposerConnection(INonBlockingConnection arg0) {
		return arg0.getLocalPort() == XithClusterConfiguration.composerConnectionPort;
	}
	
	private void onSessionStarted(Message message) {
		INonBlockingConnection connection;
		
		log.info("Session started received");
		
		connection = message.getSource();
		if (isRendererConnection(connection)) {
			renderersSessionStartedMask.set(getRendererIndex(connection));
		} else if (isComposerConnection(connection)) {
			composerSessionStarted = true;
		}
	}
	
	private void onDisconnected(INonBlockingConnection arg0) {
		if (isRendererConnection(arg0)) {
			onRendererDisconnect(arg0);
			
			log.info("Renderer disconnected");
		} else if (isComposerConnection(arg0)) {
			onComposerDisconnect();
			
			log.info("Composer disconnected");
		} else {
			// TODO:
			throw new AssertionError("Should never happen!");
		}
	}
	
	private void onComposerDisconnect() {
		composerConnection = null;
	}
	
	private void onRendererDisconnect(INonBlockingConnection arg0) {
		int rendererIndex;
		
		rendererIndex = getRendererIndex(arg0);
		
		renderersSessionStartedMask.clear(rendererIndex);
		
		renderersConnections.remove(rendererIndex);
		for (int j = rendererIndex; j < renderersConnections.size(); j++) {
			renderersConnections.get(j).setAttachment(j);
		}
	}
	
	private void sendStartSessionMessageToRenderer(INonBlockingConnection rendererConnection, byte[] pointOfViewData, byte[] sceneData) throws BufferOverflowException, ClosedChannelException, SocketTimeoutException, IOException {
		rendererConnection.write(MessageType.START_SESSION.ordinal());
		rendererConnection.flush();
		
		rendererConnection.write(getRendererIndex(rendererConnection));
		rendererConnection.write(XithClusterConfiguration.screenWidth);
		rendererConnection.write(XithClusterConfiguration.screenHeight);
		rendererConnection.write(XithClusterConfiguration.targetFPS);
		rendererConnection.write(pointOfViewData.length);
		rendererConnection.write(pointOfViewData);
		rendererConnection.write(sceneData.length);
		rendererConnection.write(sceneData);
		rendererConnection.flush();
	}
	
	private void sendStartSessionMessageToComposer() throws BufferOverflowException, ClosedChannelException, SocketTimeoutException, IOException {
		composerConnection.write(MessageType.START_SESSION.ordinal());
		composerConnection.flush();
		
		composerConnection.write(XithClusterConfiguration.screenWidth);
		composerConnection.write(XithClusterConfiguration.screenHeight);
		composerConnection.write(XithClusterConfiguration.targetFPS);
		composerConnection.flush();
	}
	
	private void sendUpdateMessageToRenderer(INonBlockingConnection rendererConnection, byte[] updateData) throws BufferOverflowException, IOException {
		rendererConnection.write(MessageType.UPDATE.ordinal());
		rendererConnection.flush();
		
		rendererConnection.write(updateData);
		rendererConnection.flush();
	}
	
	private void sendStartFrameMessage(INonBlockingConnection connection, int frameIndex) throws BufferOverflowException, IOException {
		connection.write(MessageType.START_FRAME.ordinal());
		connection.flush();
		
		connection.write(frameIndex);
		connection.flush();
	}
	
	/*
	 * ================================ 
	 * Network messages processing loop
	 * ================================
	 */
	@Override
	public void update(long gameTime, long frameTime, TimingMode timingMode) {
		Queue<Message> messages;
		Message message;
		Iterator<Message> iterator;
		boolean clusterConfigurationChanged;
		
		messages = MessageQueue.startReadingMessages();
		
		if (sessionState == SessionState.STARTED || sessionState == SessionState.CLOSED) {
			clusterConfigurationChanged = false;
			iterator = messages.iterator();
			while (iterator.hasNext()) {
				message = iterator.next();
				if (message.getType() == MessageType.CONNECTED) {
					onConnected(message.getSource());
				} else if (message.getType() == MessageType.DISCONNECTED) {
					onDisconnected(message.getSource());
				} else {
					continue;
				}
				
				clusterConfigurationChanged = true;
				iterator.remove();
			}
			
			if (clusterConfigurationChanged) {
				if (sessionState == SessionState.STARTED) {
					closeCurrentSession();
				} 
				
				if (sessionState == SessionState.CLOSED && isThereAtLeastOneRendererAndOneComposer()) {
					tryToDistributeTheScene();
				}
			} else {
				iterator = messages.iterator();
				while (iterator.hasNext()) {
					message = iterator.next();
					if (message.getType() == MessageType.FINISHED_FRAME) {
						onFinishedFrame(message);
						iterator.remove();
					}
				}
				
				if (finishedFrame || forceFrameStart) {
					startNewFrame();
				}
				
				if (updateManager.hasPendingUpdates()) {
					sendPendingUpdates();
				}
			}
		} else if (sessionState == SessionState.STARTING) {
			iterator = messages.iterator();
			while (iterator.hasNext()) {
				message = iterator.next();
				if (message.getType() == MessageType.SESSION_STARTED) {
					onSessionStarted(message);
					iterator.remove();
				}
			}
			
			if (isSessionReadyToStart()) {
				startNewSession();
			}
		}
		
		MessageQueue.stopReadingMessages();
	}

}
