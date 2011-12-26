package br.edu.univercidade.cc.xithcluster;

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
import org.xith3d.loop.opscheduler.impl.OperationSchedulerImpl;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Node;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.IServer;
import org.xsocket.connection.Server;
import br.edu.univercidade.cc.xithcluster.distribution.DistributionStrategy;
import br.edu.univercidade.cc.xithcluster.hud.components.FPSCounter;
import br.edu.univercidade.cc.xithcluster.messages.MasterMessageBroker;
import br.edu.univercidade.cc.xithcluster.messages.Message;
import br.edu.univercidade.cc.xithcluster.messages.MessageQueue;
import br.edu.univercidade.cc.xithcluster.messages.MessageType;
import br.edu.univercidade.cc.xithcluster.serialization.packagers.PointOfViewPackager;
import br.edu.univercidade.cc.xithcluster.serialization.packagers.ScenePackager;
import br.edu.univercidade.cc.xithcluster.serialization.packagers.UpdatesPackager;
import br.edu.univercidade.cc.xithcluster.update.PendingUpdate;
import br.edu.univercidade.cc.xithcluster.update.PendingUpdate.Type;
import br.edu.univercidade.cc.xithcluster.update.UpdateManager;

public final class NetworkManager extends OperationSchedulerImpl {
	
	private Logger log = Logger.getLogger(NetworkManager.class);
	
	// TODO: May be a naive optimization...
	private boolean trace = log.isTraceEnabled();
	
	private MasterMessageBroker masterMessageBroker = new MasterMessageBroker();
	
	private UpdatesPackager updatesPackager = new UpdatesPackager();
	
	protected PointOfViewPackager pointOfViewPackager = new PointOfViewPackager();
	
	protected ScenePackager scenePackager = new ScenePackager();
	
	private String listeningAddress;
	
	private int renderersConnectionPort;
	
	private int composerConnectionPort;
	
	protected SceneManager sceneManager;
	
	private UpdateManager updateManager;
	
	protected DistributionStrategy distributionStrategy;
	
	private IServer composerServer;
	
	private IServer renderersServer;
	
	protected INonBlockingConnection composerConnection;
	
	private FPSCounter fpsCounter;
	
	protected List<INonBlockingConnection> renderersConnections = Collections.synchronizedList(new ArrayList<INonBlockingConnection>());
	
	protected SessionState sessionState = SessionState.CLOSED;
	
	private boolean composerSessionStarted = false;
	
	private final BitSet renderersSessionStartedMask = new BitSet();
	
	private int currentFrame = 0;
	
	private boolean finishedFrame = false;
	
	private boolean forceFrameStart = false;
	
	private long lastClockCount = 0L;
	
	public NetworkManager(String listeningAddress, int renderersConnectionPort, int composerConnectionPort, DistributionStrategy distributionStrategy) {
		if (listeningAddress == null || listeningAddress.isEmpty() ||
				distributionStrategy == null) {
			throw new IllegalArgumentException();
		}
		
		this.listeningAddress = listeningAddress;
		this.renderersConnectionPort = renderersConnectionPort;
		this.composerConnectionPort = composerConnectionPort;
		this.distributionStrategy = distributionStrategy;
	}
	
	public void setSceneRenderer(SceneManager sceneManager) {
		if (sceneManager == null) {
			throw new IllegalArgumentException();
		}
		
		this.sceneManager = sceneManager;
	}
	
	public void setFPSCounter(FPSCounter fpsCounter) {
		if (fpsCounter == null) {
			throw new IllegalArgumentException();
		}
		
		this.fpsCounter = fpsCounter;
	}
	
	public void start() throws UnknownHostException, IOException {
		if (sceneManager == null) {
			// TODO:
			throw new RuntimeException("Scene renderer must be set");
		}
		
		if (updateManager == null) {
			// TODO:
			throw new RuntimeException("Update manager must be set");
		}
		
		renderersServer = new Server(listeningAddress, renderersConnectionPort, masterMessageBroker);
		composerServer = new Server(listeningAddress, composerConnectionPort, masterMessageBroker);
		
		renderersServer.start();
		composerServer.start();
	}
	
	private void sendPendingUpdates() {
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
					// TODO:
					throw new RuntimeException("Error sending pending updates", e);
				}
			}
		}
		
		log.info("Pending updates sent successfully");
	}
	
	private void distributeScene() {
		SceneInfo sceneInfo;
		List<BranchGroup> distributedScenes;
		BranchGroup rendererScene;
		byte[] pointOfViewData;
		byte[] sceneData;
		int rendererIndex;
		
		sceneInfo = sceneManager.getSceneInfo();
		
		log.info("Starting a new session");
		
		if (trace) {
			log.trace("Executing " + distributionStrategy.getClass().getSimpleName() + "...");
		}
		
		distributedScenes = distributionStrategy.distribute(sceneInfo.getRoot(), renderersConnections.size());
		
		if (distributedScenes.size() != renderersConnections.size()) {
			// TODO:
			throw new RuntimeException("The number of distributions is not the same as the number of renderers");
		}
		
		for (INonBlockingConnection rendererConnection : renderersConnections) {
			rendererIndex = getRendererIndex(rendererConnection);
			rendererScene = distributedScenes.get(rendererIndex);
			
			if (trace) {
				log.trace("**************");
				log.trace("Renderer " + rendererIndex);
				log.trace("**************");
			}
			
			ConnectionSetter.setConnection(rendererScene, rendererConnection);
			
			try {
				pointOfViewData = pointOfViewPackager.serialize(sceneInfo.getPointOfView());
				sceneData = scenePackager.serialize(rendererScene);
			} catch (IOException e) {
				// TODO:
				throw new RuntimeException("Error serializing the scene", e);
			}
			
			if (trace) {
				log.trace("pointOfViewData.length=" + pointOfViewData.length);
				log.trace("sceneData.length=" + sceneData.length);
			}
			
			try {
				sendStartSessionMessageToRenderer(rendererConnection, pointOfViewData, sceneData);
			} catch (IOException e) {
				// TODO:
				throw new RuntimeException("Error sending distributed scene", e);
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
	
	private void openSession() {
		sessionState = SessionState.OPENING;
		
		try {
			distributeScene();
			log.info("Starting a new session");
		} catch (Throwable t) {
			sessionState = SessionState.CLOSED;
			log.error("Error starting session", t);
		}
	}
	
	private boolean isSessionReadyToStart() {
		return composerSessionStarted && renderersSessionStartedMask.cardinality() == renderersConnections.size();
	}
	
	private void closeSession() {
		sessionState = SessionState.CLOSED;
		
		renderersSessionStartedMask.clear();
		composerSessionStarted = false;
		
		log.info("Current session closed");
	}
	
	private void startNewFrame(long clockCount) {
		if (trace) {
			log.trace("Starting new frame");
		}
		
		finishedFrame = false;
		forceFrameStart = false;
		
		currentFrame += 1;
		
		if (trace) {
			log.trace("currentFrame=" + currentFrame);
			log.trace("clockCount=" + clockCount);
		}
		
		try {
			sendStartFrameMessage(composerConnection, currentFrame, clockCount);
			
			for (INonBlockingConnection rendererConnection : renderersConnections) {
				sendStartFrameMessage(rendererConnection, currentFrame, clockCount);
			}
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error sending start new frame notification: " + currentFrame, e);
		}
	}
	
	private void onFinishedFrame(Message message) {
		long frameIndex;
		
		if (trace) {
			log.trace("Finished frame received");
		}
		
		frameIndex = (Long) message.getParameters()[0];
		
		if (currentFrame == frameIndex) {
			if (trace) {
				log.trace("Finished current frame: " + currentFrame);
			}
			
			finishedFrame = true;
		} else {
			if (trace) {
				log.trace("Out-of-sync finished frame received: " + frameIndex);
			}
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
		return arg0.getLocalPort() == renderersConnectionPort;
	}
	
	private boolean isComposerConnection(INonBlockingConnection arg0) {
		return arg0.getLocalPort() == composerConnectionPort;
	}
	
	private void onSessionStarted(Message message) {
		INonBlockingConnection connection;
		
		if (trace) {
			log.trace("Session started received");
		}
		
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
		rendererConnection.write(sceneManager.getScreenSize().width);
		rendererConnection.write(sceneManager.getScreenSize().height);
		rendererConnection.write(sceneManager.getTargetFPS());
		rendererConnection.write(pointOfViewData.length);
		rendererConnection.write(pointOfViewData);
		rendererConnection.write(sceneData.length);
		rendererConnection.write(sceneData);
		rendererConnection.flush();
	}
	
	private void sendStartSessionMessageToComposer() throws BufferOverflowException, ClosedChannelException, SocketTimeoutException, IOException {
		composerConnection.write(MessageType.START_SESSION.ordinal());
		composerConnection.flush();
		
		composerConnection.write(sceneManager.getScreenSize().width);
		composerConnection.write(sceneManager.getScreenSize().height);
		composerConnection.write(sceneManager.getTargetFPS());
		composerConnection.flush();
	}
	
	private void sendUpdateMessageToRenderer(INonBlockingConnection rendererConnection, byte[] updateData) throws BufferOverflowException, IOException {
		rendererConnection.write(MessageType.UPDATE.ordinal());
		rendererConnection.flush();
		
		rendererConnection.write(updateData);
		rendererConnection.flush();
	}
	
	private void sendStartFrameMessage(INonBlockingConnection connection, long frameIndex, long clockCount) throws BufferOverflowException, IOException {
		connection.write(MessageType.START_FRAME.ordinal());
		connection.flush();
		
		connection.write(frameIndex);
		connection.write(clockCount);
		connection.flush();
	}
	
	@Override
	public void update(long gameTime, long frameTime, TimingMode timingMode) {
		Queue<Message> messages;
		
		messages = MessageQueue.startReadingMessages();
		
		processMessages(gameTime, frameTime, timingMode, messages);
		
		MessageQueue.stopReadingMessages();
	}
	
	// ================================
	// Network messages processing loop
	// ================================
	protected void processMessages(long clockCount, long frameTime, TimingMode timingMode, Queue<Message> messages) {
		if (sessionState == SessionState.OPENING) {
			processMessageForOpeningSessionState(messages);
		} else if (sessionState == SessionState.OPENED || sessionState == SessionState.CLOSED) {
			processMessageForOpenedOrClosedSessionState(clockCount, frameTime, timingMode, messages);
		}
	}
	
	private void processMessageForOpeningSessionState(Queue<Message> messages) {
		Message message;
		Iterator<Message> iterator;
		
		iterator = messages.iterator();
		while (iterator.hasNext()) {
			message = iterator.next();
			if (message.getType() == MessageType.SESSION_STARTED) {
				onSessionStarted(message);
				iterator.remove();
			}
		}
		
		if (isSessionReadyToStart()) {
			sessionState = SessionState.OPENED;
			
			log.info("Session started successfully");
			
			forceFrameStart = true;
		}
	}
	
	private void processMessageForOpenedOrClosedSessionState(long clockCount, long frameTime, TimingMode timingMode, Queue<Message> messages) {
		boolean clusterConfigurationChanged = processConnectAndDisconnectMessages(messages);
		
		if (sessionState == SessionState.OPENED) {
			if (clusterConfigurationChanged) {
				closeSession();
				
				// if (isThereAtLeastOneRendererAndOneComposer()) {
				// tryToDistributeTheScene();
				// }
				
				return;
			}
			
			processFinishedFrameMessages(messages);
			
			if (finishedFrame || forceFrameStart) {
				startNewFrame(clockCount);
				updateXith3DScheduledOperations(clockCount, frameTime, timingMode);
				updateFPS(clockCount, timingMode);
			}
			
			if (updateManager.hasPendingUpdates()) {
				sendPendingUpdates();
			}
		}
		else if (sessionState == SessionState.CLOSED) {
			if (isThereAtLeastOneRendererAndOneComposer()) {
				openSession();
			}
		}
		else {
			throw new AssertionError(sessionState);
		}
	}
	
	private void processFinishedFrameMessages(Queue<Message> messages) {
		Message message;
		Iterator<Message> iterator;
		
		iterator = messages.iterator();
		while (iterator.hasNext()) {
			message = iterator.next();
			if (message.getType() == MessageType.FINISHED_FRAME) {
				onFinishedFrame(message);
				iterator.remove();
			}
		}
	}
	
	private boolean processConnectAndDisconnectMessages(Queue<Message> messages) {
		boolean clusterConfigurationChanged = false;
		Iterator<Message> iterator = messages.iterator();
		while (iterator.hasNext()) {
			Message message = iterator.next();
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
		
		return clusterConfigurationChanged;
	}
	
	private void updateFPS(long clockCount, TimingMode timingMode) {
		long elapsedTime;
		double fps;
		
		if (fpsCounter == null) {
			return;
		}
		
		if (lastClockCount > 0) {
			elapsedTime = clockCount - lastClockCount;
			
			fps = timingMode.getDivisor() / elapsedTime;
			
			fpsCounter.update(fps);
		}
		
		lastClockCount = clockCount;
	}
	
	private void updateXith3DScheduledOperations(long gameTime, long frameTime, TimingMode timingMode) {
		super.update(gameTime, frameTime, timingMode);
	}
	
	public void addUpdateManager(UpdateManager updateManager) {
		if (updateManager == null) {
			throw new IllegalArgumentException();
		}
		
		this.updateManager = updateManager;
	}
	
}
