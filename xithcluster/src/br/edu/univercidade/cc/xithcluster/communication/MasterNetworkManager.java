package br.edu.univercidade.cc.xithcluster.communication;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.BufferOverflowException;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
		CLOSED,
		STARTING,
		STARTED
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
	
	private int currentFrameIndex = 0;
	
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
	
	/*private synchronized int getSkipNextFrames() {
		int framesToSkip = 0;
		
		// TODO:

		return framesToSkip;
	}*/
	
	private void notifyFrameStart(int frameIndex) {
		try {
			sendStartFrameMessage(composerConnection, frameIndex);
			
			for (INonBlockingConnection rendererConnection : renderersConnections) {
				sendStartFrameMessage(rendererConnection, frameIndex);
			}
			
			currentFrameIndex = frameIndex;
		} catch (IOException e) {
			log.error("Error sending frame start notification: " + frameIndex, e);
		}
	}
	
	private boolean sendPendingUpdates() {
		Map<INonBlockingConnection, List<PendingUpdate>> updatesPerRenderer;
		INonBlockingConnection rendererConnection;
		List<PendingUpdate> updates;
		
		if (updateManager.hasPendingUpdates()) {
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
						sendUpdateMessage(rendererConnection, updatesPackager.serialize(updates));
						
						log.info(updates.size() + " update(s) were sent to renderer " + getRendererIndex(rendererConnection));
					} catch (IOException e) {
						log.error("Error sending pending updates", e);
						
						return false;
					}
				}
			}
			
			log.info("Pending updates sent successfully");
		}
		
		return true;
	}
	
	public synchronized boolean startNewSession() {
		BranchGroup scene;
		View pointOfView;
		List<BranchGroup> distributedScenes;
		BranchGroup rendererScene;
		byte[] pointOfViewData;
		byte[] sceneData;
		int rendererIndex;
		
		sessionState = SessionState.STARTING;
		
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
				log.error("Error serializing the scene", e);
				return false;
			}
			
			log.trace("POV data size: " + pointOfViewData.length + " bytes");
			log.trace("Scene data size: " + sceneData.length + " bytes");
			
			try {
				sendStartSessionMessage(
						rendererConnection, 
						getRendererIndex(rendererConnection),
						XithClusterConfiguration.screenWidth, 
						XithClusterConfiguration.screenHeight,
						XithClusterConfiguration.targetFPS,
						pointOfViewData, 
						sceneData);
			} catch (IOException e) {
				log.error("Error sending distributed scene", e);
				return false;
			}
		}
		
		if (composerConnection != null) {
			try {
				sendStartSessionMessage(
						XithClusterConfiguration.screenWidth, 
						XithClusterConfiguration.screenHeight,
						XithClusterConfiguration.targetFPS);
			} catch (IOException e) {
				log.error("Error notifying composer", e);
				return false;
			}
		}
		
		return true;
	}
	
	private void closeCurrentSession() {
		sessionState = SessionState.CLOSED;
		composerSessionStarted = false;
		renderersSessionStartedMask.clear();
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
	
	private boolean onConnected(INonBlockingConnection arg0) {
		if (isRendererConnection(arg0)) {
			return onRendererConnected(arg0);
		} else if (isComposerConnection(arg0)) {
			return onComposerConnected(arg0);
		} else {
			log.error("Unknown connection refused");
			
			return false;
		}
	}
	
	private boolean onRendererConnected(INonBlockingConnection arg0) {
		INonBlockingConnection rendererConnection;
		
		rendererConnection = arg0;
		
		setRendererIndex(rendererConnection);
		renderersConnections.add(rendererConnection);
		
		rendererConnection.setAutoflush(false);
		
		log.info("New renderer connected");
		
		return true;
	}
	
	private boolean onComposerConnected(INonBlockingConnection arg0) {
		if (!isThereAlreadyAConnectedComposer()) {
			composerConnection = arg0;
			
			composerConnection.setAutoflush(false);
			
			log.info("Composer connected");
			
			return true;
		} else {
			log.error("There can be only one composer");
			
			return false;
		}
	}
	
	private boolean isRendererConnection(INonBlockingConnection arg0) {
		return arg0.getLocalPort() == XithClusterConfiguration.renderersConnectionPort;
	}
	
	private boolean isComposerConnection(INonBlockingConnection arg0) {
		return arg0.getLocalPort() == XithClusterConfiguration.composerConnectionPort;
	}
	
	private void onSessionStarted(INonBlockingConnection arg0) {
		if (isRendererConnection(arg0)) {
			renderersSessionStartedMask.set(getRendererIndex(arg0));
		} else if (isComposerConnection(arg0)) {
			composerSessionStarted = true;
		}
		
		if (composerSessionStarted && renderersSessionStartedMask.cardinality() == renderersConnections.size()) {
			sessionState = SessionState.STARTED;
			
			log.info("Session started successfully");
			
			notifyFrameStart(currentFrameIndex);
		}
	}

	private void onDisconnected(INonBlockingConnection arg0) {
		if (isRendererConnection(arg0)) {
			onRendererDisconnect(arg0);
		} else if (isComposerConnection(arg0)) {
			onComposerDisconnect();
		} else {
			// TODO:
			throw new AssertionError("Should never happen!");
		}
	}

	
	private boolean onComposerDisconnect() {
		composerConnection = null;
		
		log.info("Composer disconnected");
		
		return true;
	}
	
	private boolean onRendererDisconnect(INonBlockingConnection arg0) {
		int rendererIndex;
		
		rendererIndex = getRendererIndex(arg0);
		
		renderersSessionStartedMask.clear(rendererIndex);
		
		renderersConnections.remove(rendererIndex);
		synchronized (renderersConnections) {
			for (int j = rendererIndex; j < renderersConnections.size(); j++) {
				renderersConnections.get(j).setAttachment(j);
			}
		}
		
		log.info("Renderer disconnected");
		
		return true;
	}
	
	private void sendStartSessionMessage(INonBlockingConnection rendererConnection, int rendererIndex, int screenWidth, int screenHeight, double targetFPS, byte[] pointOfViewData, byte[] sceneData) throws BufferOverflowException, ClosedChannelException, SocketTimeoutException, IOException {
		rendererConnection.write(MessageType.START_SESSION.ordinal());
		rendererConnection.flush();
		
		rendererConnection.write(rendererIndex);
		rendererConnection.write(screenWidth);
		rendererConnection.write(screenHeight);
		rendererConnection.write(targetFPS);
		rendererConnection.write(pointOfViewData.length);
		rendererConnection.write(pointOfViewData);
		rendererConnection.write(sceneData.length);
		rendererConnection.write(sceneData);
		rendererConnection.flush();
	}
	
	private void sendStartSessionMessage(int screenWidth, int screenHeight, double targetFPS) throws BufferOverflowException, ClosedChannelException, SocketTimeoutException, IOException {
		composerConnection.write(MessageType.START_SESSION.ordinal());
		composerConnection.flush();
		
		composerConnection.write(screenWidth);
		composerConnection.write(screenHeight);
		composerConnection.write(targetFPS);
		composerConnection.flush();
	}
	
	private void sendUpdateMessage(INonBlockingConnection rendererConnection, byte[] updateData) throws BufferOverflowException, IOException {
		rendererConnection.write(MessageType.UPDATE.ordinal());
		rendererConnection.flush();
		
		rendererConnection.write(updateData);
		rendererConnection.flush();
	}

	/*private void sendGetFramesToSkipMessage(INonBlockingConnection composerConnection) throws BufferOverflowException, IOException {
		composerConnection.write(MessageType.GET_FRAMES_TO_SKIP.ordinal());
		composerConnection.flush();
	}*/

	private void sendStartFrameMessage(INonBlockingConnection connection, int frameIndex) throws BufferOverflowException, IOException {
		connection.write(MessageType.START_FRAME.ordinal());
		connection.flush();
		
		connection.write(frameIndex);
		connection.flush();
	}

	@Override
	public void update(long gameTime, long frameTime, TimingMode timingMode) {
		Deque<Message> messages;
		Message message;
		Iterator<Message> iterator;
		Iterator<Message> descendingIterator;
		boolean clusterConfigurationChanged;
		
		messages = MessageQueue.getInstance().retrieveMessages();
		
		if (sessionState != SessionState.STARTING) {
			clusterConfigurationChanged = false;
			descendingIterator = messages.descendingIterator();
			while (descendingIterator.hasNext()) {
				message = descendingIterator.next();
				if (message.getType() == MessageType.CONNECTED) {
					onConnected(message.getSource());
				} else if (message.getType() == MessageType.DISCONNECTED) {
					onDisconnected(message.getSource());
				} else {
					continue;
				}
				clusterConfigurationChanged = true;
				descendingIterator.remove();
			}
		
			if (clusterConfigurationChanged && isThereAtLeastOneRendererAndOneComposer()) {
				if (sessionState == SessionState.STARTED) {
					closeCurrentSession();
				}
				
				startNewSession();
			} else {
				iterator = messages.iterator();
				while (iterator.hasNext()) {
					message = iterator.next();
					if (message.getType() == MessageType.FINISHED_FRAME) {
						if (currentFrameIndex == (Integer) message.getParameters()[0]) {
							notifyFrameStart(currentFrameIndex++);
						} else {
							// TODO: Do nothing!
						}
					} else if (message.getType() == MessageType.SESSION_STARTED) {
						// TODO: Do nothing!
					}
				}
				
				sendPendingUpdates();
			}
		} else {
			iterator = messages.iterator();
			while (iterator.hasNext()) {
				message = iterator.next();
				if (message.getType() == MessageType.SESSION_STARTED) {
					onSessionStarted(message.getSource());
				} else {
					// post-back
					MessageQueue.getInstance().postMessage(message);
				}
			}
		}
	}

}
