package br.edu.univercidade.cc.xithcluster;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.BufferOverflowException;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.xith3d.loop.opscheduler.impl.OperationSchedulerImpl;
import org.xith3d.scenegraph.BranchGroup;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.Server;
import br.edu.univercidade.cc.xithcluster.distribution.DistributionStrategy;
import br.edu.univercidade.cc.xithcluster.hud.components.FPSCounter;
import br.edu.univercidade.cc.xithcluster.messages.ComponentConnectedMessage;
import br.edu.univercidade.cc.xithcluster.messages.ComposerConnectedMessage;
import br.edu.univercidade.cc.xithcluster.messages.FinishedFrameMessage;
import br.edu.univercidade.cc.xithcluster.messages.MasterMessageBroker;
import br.edu.univercidade.cc.xithcluster.messages.MessageBroker;
import br.edu.univercidade.cc.xithcluster.messages.RendererConnectedMessage;
import br.edu.univercidade.cc.xithcluster.messages.RendererStartSessionMessage;
import br.edu.univercidade.cc.xithcluster.messages.SessionStartedMessage;
import br.edu.univercidade.cc.xithcluster.serialization.packagers.PointOfViewPackager;
import br.edu.univercidade.cc.xithcluster.serialization.packagers.ScenePackager;
import br.edu.univercidade.cc.xithcluster.serialization.packagers.UpdatesPackager;
import br.edu.univercidade.cc.xithcluster.update.UpdateManager;

public final class NetworkManager extends OperationSchedulerImpl {
	
	private Logger log = Logger.getLogger(NetworkManager.class);
	
	// TODO: May be a naive optimization...
	private boolean trace = log.isTraceEnabled();
	
	private MessageBroker messageBroker = new MasterMessageBroker();
	
	private UpdatesPackager updatesPackager = new UpdatesPackager();
	
	private PointOfViewPackager pointOfViewPackager = new PointOfViewPackager();
	
	private ScenePackager scenePackager = new ScenePackager();
	
	private String listeningAddress;
	
	private int renderersConnectionPort;
	
	private int composerConnectionPort;
	
	private SceneManager sceneManager;
	
	private UpdateManager updateManager;
	
	private DistributionStrategy distributionStrategy;
	
	private Server composerServer;
	
	private Server renderersServer;
	
	private INonBlockingConnection composerConnection;
	
	private FPSCounter fpsCounter;
	
	private List<INonBlockingConnection> renderersConnections = Collections.synchronizedList(new ArrayList<INonBlockingConnection>());
	
	private SessionState sessionState = SessionState.CLOSED;
	
	private boolean composerSessionStarted = false;
	
	private final BitSet renderersSessionStartedMask = new BitSet();
	
	private int currentFrame = 0;
	
	private boolean finishedFrame = false;
	
	private boolean forceFrameStart = false;
	
	private long lastClockCount = 0L;

	private List<Renderer> renderers = new ArrayList<Renderer>();
	
	public NetworkManager(String listeningAddress, int renderersConnectionPort, int composerConnectionPort, DistributionStrategy distributionStrategy) {
		if (listeningAddress == null || listeningAddress.isEmpty() //
				|| distributionStrategy == null) {
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
		
		// TODO: Test if Server can be correctly created passing a null handler
		// to his constructor!
		renderersServer = new Server(listeningAddress, renderersConnectionPort, null);
		messageBroker.handleServerConnection(renderersServer);
		composerServer = new Server(listeningAddress, composerConnectionPort, null);
		messageBroker.handleServerConnection(composerServer);
		
		renderersServer.start();
		composerServer.start();
	}
	
	private void sendPendingUpdates() {
		SortedUpdateShares sortedUpdateShares = updateManager.sortUpdates(getNumberOfRenderers());
		for (Renderer renderer : renderers) {
			sendUpdateMessageToRenderer(renderer, sortedUpdateShares.nextUpdateShare());
		}
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
	
	// private int getRendererIndex(INonBlockingConnection rendererConnection) {
	// Integer rendererId;
	//
	// rendererId = (Integer) rendererConnection.getAttachment();
	//
	// return rendererId.intValue();
	// }
	//
	// private void setRendererIndex(INonBlockingConnection arg0) {
	// arg0.setAttachment(renderersConnections.size());
	// }
	
	// private boolean isThereAtLeastOneRendererAndOneComposer() {
	// return !renderersConnections.isEmpty() && composerConnection != null;
	// }
	//
	// private boolean isThereAlreadyAConnectedComposer() {
	// return composerConnection != null;
	// }
	
	private void tryToDistributeTheScene() {
		sessionState = SessionState.STARTING;
		
		try {
			distributeScene();
			log.info("Starting a new session");
		} catch (Throwable t) {
			sessionState = SessionState.CLOSED;
			log.error("Error starting session", t);
		}
	}
	
	// private boolean isSessionReadyToStart() {
	// return composerSessionStarted &&
	// renderersSessionStartedMask.cardinality() == renderersConnections.size();
	// }
	
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
			sendStartFrameMessage(composer);
			
			for (Renderer renderer : renderers) {
				sendStartFrameMessage(renderer);
			}
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error sending start new frame notification: " + currentFrame, e);
		}
	}
	
	private void onFinishedFrame(FinishedFrameMessage message) {
		if (trace) {
			log.trace("Finished frame received");
		}
		
		message.update(this);
	}
		
	public void updateFrameIndex(long frameIndex) {
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
	
	// private void onConnected(INonBlockingConnection arg0) {
	// if (isRendererConnection(arg0)) {
	// onRendererConnected(arg0);
	//
	// log.info("New renderer connected");
	// } else if (isComposerConnection(arg0)) {
	// if (isThereAlreadyAConnectedComposer()) {
	// log.error("There can be only one composer");
	// } else {
	// onComposerConnected(arg0);
	//
	// log.info("New composer connected");
	// }
	// } else {
	// log.error("Unknown connection refused");
	// }
	// }
	
	// private void onRendererConnected(INonBlockingConnection arg0) {
	// INonBlockingConnection rendererConnection;
	//
	// rendererConnection = arg0;
	//
	// setRendererIndex(rendererConnection);
	// renderersConnections.add(rendererConnection);
	//
	// rendererConnection.setAutoflush(false);
	// }
	//
	// private void onComposerConnected(INonBlockingConnection arg0) {
	// composerConnection = arg0;
	// composerConnection.setAutoflush(false);
	// }
	
	private void onRendererConnected(RendererConnectedMessage message) {
		Renderer renderer = new Renderer(message.getSourceConnection());
		
		addComponent(renderer);
	}
	
	private void addComponent(Component component) {
	// TODO:
	}

	private void onComposerConnected(ComposerConnectedMessage message) {
		Composer composer = new Composer(message.getSourceConnection());
		
		addComponent(composer);
	}
	
	private void onSessionStarted(SessionStartedMessage message) {
		if (trace) {
			log.trace("Session started received");
		}
		
		Component component = getComponentById(message.getComponentId());
		
		if (component == null) {
			throw new AssertionError("Component connection cannot be null");
		}
		
		component.sessionStarted();
	}
	
	private Component getComponentById(int componentId) {
		// TODO:
		return null;
	}

	private void onDisconnected(ComponentConnectedMessage message) {
		Component component = getComponentById(message.getComponentId());
		
		component.invalidateSession();
	}
	
	private void sendStartSessionMessageToRenderer(Component component) throws BufferOverflowException, ClosedChannelException, SocketTimeoutException, IOException {
		SceneData sceneData = sceneManager.getSceneData();
		
		RendererStartSessionMessage message = new RendererStartSessionMessage(sceneData);
		
		message.sendTo(component);
		
		// rendererConnection.write(MessageType.START_SESSION.ordinal());
		// rendererConnection.flush();
		//
		// rendererConnection.write(getRendererIndex(rendererConnection));
		// rendererConnection.write(sceneManager.getScreenSize().width);
		// rendererConnection.write(sceneManager.getScreenSize().height);
		// rendererConnection.write(sceneManager.getTargetFPS());
		// rendererConnection.write(pointOfViewData.length);
		// rendererConnection.write(pointOfViewData);
		// rendererConnection.write(sceneData.length);
		// rendererConnection.write(sceneData);
		// rendererConnection.flush();
	}
	
	// private void sendStartSessionMessageToComposer() throws
	// BufferOverflowException, ClosedChannelException, SocketTimeoutException,
	// IOException {
	// composerConnection.write(MessageType.START_SESSION.ordinal());
	// composerConnection.flush();
	//
	// composerConnection.write(sceneManager.getScreenSize().width);
	// composerConnection.write(sceneManager.getScreenSize().height);
	// composerConnection.write(sceneManager.getTargetFPS());
	// composerConnection.flush();
	// }
	
	private void sendUpdateMessage(Renderer renderer, UpdateData updateData) throws BufferOverflowException, IOException {
		UpdateMessage message = new UpdateMessage(updateData);
		
		message.sendTo(renderer);
		
		// rendererConnection.write(MessageType.UPDATE.ordinal());
		// rendererConnection.flush();
		//
		// rendererConnection.write(updateData);
		// rendererConnection.flush();
	}
	
	private void sendStartFrameMessage(Component component) throws BufferOverflowException, IOException {
		StartFrameMessage message = new StartFrameMessage(frameIndex, clockCount);
		
		message.sendTo(component);
		// connection.write(MessageType.START_FRAME.ordinal());
		// connection.flush();
		//
		// connection.write(frameIndex);
		// connection.write(clockCount);
		// connection.flush();
	}
	
	@Override
	public void update(long gameTime, long frameTime, TimingMode timingMode) {
		// Queue<Message> messages;
		//
		// messages = MessageQueue.startReadingMessages();
		
		messageBroker.processMessages(this, gameTime, frameTime, timingMode);
		
		// MessageQueue.stopReadingMessages();
	}
	
	// ================================
	// Network messages processing loop
	// ================================
	private void processMessages(long clockCount, long frameTime, TimingMode timingMode) {
		// Message message;
		// Iterator<Message> iterator;
		// boolean clusterConfigurationChanged;
		//
		// if (sessionState == SessionState.STARTED || sessionState ==
		// SessionState.CLOSED) {
		// clusterConfigurationChanged = false;
		// iterator = messages.iterator();
		// while (iterator.hasNext()) {
		// message = iterator.next();
		// if (message.getType() == MessageType.CONNECTED) {
		// onConnected(message.getSource());
		// } else if (message.getType() == MessageType.DISCONNECTED) {
		// onDisconnected(message.getSource());
		// } else {
		// continue;
		// }
		//
		// clusterConfigurationChanged = true;
		// iterator.remove();
		// }
		//
		// if (clusterConfigurationChanged) {
		// if (sessionState == SessionState.STARTED) {
		// closeCurrentSession();
		// }
		//
		// if (sessionState == SessionState.CLOSED &&
		// isThereAtLeastOneRendererAndOneComposer()) {
		// tryToDistributeTheScene();
		// }
		// } else {
		// iterator = messages.iterator();
		// while (iterator.hasNext()) {
		// message = iterator.next();
		// if (message.getType() == MessageType.FINISHED_FRAME) {
		// onFinishedFrame(message);
		// iterator.remove();
		// }
		// }
		//
		// if (finishedFrame || forceFrameStart) {
		// startNewFrame(clockCount);
		//
		// updateXith3DScheduledOperations(clockCount, frameTime, timingMode);
		//
		// updateFPS(clockCount, frameTime, timingMode);
		// }
		//
		// if (updateManager.hasPendingUpdates()) {
		// sendPendingUpdates();
		// }
		// }
		// } else if (sessionState == SessionState.STARTING) {
		// iterator = messages.iterator();
		// while (iterator.hasNext()) {
		// message = iterator.next();
		// if (message.getType() == MessageType.SESSION_STARTED) {
		// onSessionStarted(message);
		// iterator.remove();
		// }
		// }
		//
		// if (isSessionReadyToStart()) {
		// startNewSession();
		// }
		// }
	}
	
	private void updateFPS(long clockCount, long frameTime, TimingMode timingMode) {
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
