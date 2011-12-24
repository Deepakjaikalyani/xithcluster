package br.edu.univercidade.cc.xithcluster;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import org.apache.log4j.Logger;
import org.xith3d.loop.opscheduler.impl.OperationSchedulerImpl;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.NonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.CompressionMethod;
import br.edu.univercidade.cc.xithcluster.messages.Message;
import br.edu.univercidade.cc.xithcluster.messages.MessageQueue;
import br.edu.univercidade.cc.xithcluster.messages.MessageType;
import br.edu.univercidade.cc.xithcluster.messages.RendererMessageBroker;

public class RendererNetworkManager extends OperationSchedulerImpl implements Observer {
	
	private Logger log = Logger.getLogger(RendererNetworkManager.class);
	
	// TODO: May be a naive optimization...
	private boolean trace = log.isTraceEnabled();
	
	private RendererMessageBroker rendererMessageBroker = new RendererMessageBroker();
	
	private String masterListeningAddress;
	
	private int masterListeningPort;
	
	private CompressionMethod compressionMethod;
	
	private String composerListeningAddress;
	
	private int composerListeningPort;
	
	private int compositionOrder;
	
	private Renderer renderer;
	
	protected INonBlockingConnection masterConnection;
	
	private INonBlockingConnection composerConnection;
	
	// private UpdatesPackager updatesPackager = new UpdatesPackager();
	
	private SceneDeserializer sceneDeserializer;
	
	private Thread sceneDeserializationThread;
	
	private DeserializationResult deserializationResult;
	
	private SessionState sessionState = SessionState.CLOSED;
	
	private boolean hasSentCurrentFrameComposer = true;
	
	private long currentFrame;
	
	private long clockCount;
	
	private long lastClockCount;
	
	public RendererNetworkManager(String masterListeningAddress, int masterListeningPort, String composerListeningAddress, int composerListeningPort, int compositionOrder, CompressionMethod compressionMethod) {
		if (masterListeningAddress == null || masterListeningAddress.isEmpty() || //
		composerListeningAddress == null || composerListeningAddress.isEmpty() || //
		compressionMethod == null) {
			throw new IllegalArgumentException();
		}
		
		this.masterListeningAddress = masterListeningAddress;
		this.masterListeningPort = masterListeningPort;
		this.composerListeningAddress = composerListeningAddress;
		this.composerListeningPort = composerListeningPort;
		this.compositionOrder = compositionOrder;
		this.compressionMethod = compressionMethod;
	}
	
	public void setSceneRenderer(Renderer renderer) {
		if (renderer == null) {
			throw new IllegalArgumentException();
		}
		
		this.renderer = renderer;
	}
	
	public void start() throws IOException {
		masterConnection = new NonBlockingConnection(masterListeningAddress, masterListeningPort, rendererMessageBroker);
		masterConnection.setAutoflush(false);
	}
	
	private boolean isConnectedToComposer() {
		return composerConnection != null && composerConnection.isOpen();
	}
	
	private boolean isSessionReadyToStart() {
		return sessionState == SessionState.OPENING && deserializationResult != null;
	}
	
	private void startNewSession() {
		try {
			sendSessionStartedMessage();
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error notifying master node that session started successfully", e);
		}
		
		renderer.updateScene(deserializationResult.getPointOfView(), deserializationResult.getScene());
		
		deserializationResult = null;
		sceneDeserializationThread = null;
		
		log.info("Scene deserialized with success");
		
		sessionState = SessionState.OPENED;
		
		log.info("Session started successfully");
	}
	
	private void closeSession() {
		sessionState = SessionState.CLOSED;
		
		log.info("Current session was closed");
	}
	
	private void sendCurrentFrameToComposer() {
		byte[] colorAndAlphaBuffer;
		byte[] depthBuffer;
		
		if (trace) {
			log.trace("Sending current frame to composer: " + currentFrame);
		}
		
		colorAndAlphaBuffer = renderer.getColorAndAlphaBuffer();
		depthBuffer = renderer.getDepthBuffer();
		
		switch (compressionMethod) {
		case PNG:
			// TODO: Deflate!
			break;
		}
		
		try {
			sendNewImageMessage(colorAndAlphaBuffer, depthBuffer);
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error sending image buffers to composer", e);
		}
	}
	
	private void onStartFrame(Message message) {
		currentFrame = (Long) message.getParameters()[0];
		clockCount = (Long) message.getParameters()[1];
		
		if (trace) {
			log.trace("Start frame received");
			log.trace("Current frame: " + currentFrame);
			log.trace("Clock count: " + clockCount);
		}
		
		hasSentCurrentFrameComposer = false;
	}
	
	private void onUpdate(Message message) {
		if (trace) {
			log.trace("Update received");
		}
		
		// TODO:
	}
	
	private void onStartSession(Message message) {
		int rendererId;
		int screenWidth;
		int screenHeight;
		double targetFPS;
		byte[] pointOfViewData;
		byte[] sceneData;
		
		if (trace) {
			log.trace("Start session received");
		}
		
		try {
			rendererId = (Integer) message.getParameters()[0];
			screenWidth = (Integer) message.getParameters()[1];
			screenHeight = (Integer) message.getParameters()[2];
			targetFPS = (Double) message.getParameters()[3];
			pointOfViewData = (byte[]) message.getParameters()[4];
			sceneData = (byte[]) message.getParameters()[5];
		} catch (Throwable t) {
			// TODO:
			throw new RuntimeException("Error reading start session message parameters", t);
		}
		
		sessionState = SessionState.OPENING;
		
		if (trace) {
			log.trace("rendererId=" + rendererId);
			log.trace("targetScreenWidth=" + screenWidth);
			log.trace("targetScreenHeight=" + screenHeight);
			log.trace("targetFPS: " + targetFPS);
			log.trace("pointOfViewData.length=" + pointOfViewData.length);
			log.trace("sceneData.length=" + sceneData.length);
		}
		
		if (!isConnectedToComposer()) {
			log.info("Connecting to composer...");
			
			try {
				composerConnection = new NonBlockingConnection(composerListeningAddress, composerListeningPort);
				composerConnection.setAutoflush(false);
			} catch (IOException e) {
				// TODO:
				throw new RuntimeException("Error connecting to composer", e);
			}
		}
		
		log.info("Sending composition order: " + compositionOrder);
		
		sendCompositionOrder();
		
		if (isParallelSceneDeserializationHappening()) {
			log.debug("Interrupting previous parallel scene deserialization");
			
			interruptParallelSceneDeserialization();
		}
		
		renderer.updateOnScreenInformation(rendererId, screenWidth, screenHeight);
		
		log.debug("Starting parallel scene deserialization");
		
		startParallelSceneDeserialization(pointOfViewData, sceneData);
		
		log.info("Session started successfully");
	}
	
	private void sendCompositionOrder() {
		try {
			sendSetCompositionOrderMessage(compositionOrder);
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error notifying composer node the renderer's composition order", e);
		}
	}
	
	private boolean isParallelSceneDeserializationHappening() {
		return sceneDeserializationThread != null;
	}
	
	private void startParallelSceneDeserialization(byte[] pointOfViewData, byte[] sceneData) {
		sceneDeserializer = new SceneDeserializer(pointOfViewData, sceneData);
		sceneDeserializer.addObserver(this);
		
		sceneDeserializationThread = new Thread(sceneDeserializer);
		sceneDeserializationThread.start();
	}
	
	private void interruptParallelSceneDeserialization() {
		sceneDeserializationThread.interrupt();
	}
	
	private void sendSessionStartedMessage() throws BufferOverflowException, IOException {
		masterConnection.write(MessageType.SESSION_STARTED.ordinal());
		masterConnection.flush();
	}
	
	private void sendNewImageMessage(byte[] colorAndAlphaBuffer, byte[] depthBuffer) throws BufferOverflowException, IOException {
		composerConnection.write(MessageType.NEW_IMAGE.ordinal());
		composerConnection.flush();
		
		composerConnection.write(currentFrame);
		composerConnection.write(compressionMethod.ordinal());
		composerConnection.write(colorAndAlphaBuffer.length);
		composerConnection.write(colorAndAlphaBuffer);
		composerConnection.write(depthBuffer.length);
		composerConnection.write(depthBuffer);
		composerConnection.flush();
	}
	
	private void sendSetCompositionOrderMessage(int compositionOrder) throws BufferOverflowException, IOException {
		composerConnection.write(MessageType.SET_COMPOSITION_ORDER.ordinal());
		composerConnection.flush();
		
		composerConnection.write(compositionOrder);
		composerConnection.flush();
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if (o == sceneDeserializer) {
			deserializationResult = (DeserializationResult) arg;
		}
	}
	
	@Override
	public void update(long gameTime, long frameTime, TimingMode timingMode) {
		Queue<Message> messages;
		
		checkMasterNodeConnection();
		
		messages = MessageQueue.startReadingMessages();
		
		processMessages(gameTime, frameTime, timingMode, messages);
		
		MessageQueue.stopReadingMessages();
	}
	
	private void checkMasterNodeConnection() {
		if (!masterConnection.isOpen()) {
			System.err.println("Master node disconnected");
			
			// TODO:
			System.exit(-1);
		}
	}
	
	// ================================
	// Network messages processing loop
	// ================================
	protected void processMessages(long gameTime, long frameTime, TimingMode timingMode, Queue<Message> messages) {
		Message message;
		Message lastUpdateMessage;
		Message firstStartFrameMessage;
		Message lastStartSessionMessage;
		Iterator<Message> iterator;
		
		/*
		 * Consider only the last start session message.
		 */
		lastStartSessionMessage = null;
		iterator = messages.iterator();
		while (iterator.hasNext()) {
			message = iterator.next();
			if (message.getType() == MessageType.START_SESSION) {
				lastStartSessionMessage = message;
				iterator.remove();
			}
		}
		
		if (lastStartSessionMessage != null) {
			onStartSession(lastStartSessionMessage);
		}
		
		if (sessionState == SessionState.OPENED) {
			if (!composerConnection.isOpen()) {
				closeSession();
				return;
			}
			
			/*
			 * Consider only the last update message received.
			 */
			lastUpdateMessage = null;
			iterator = messages.iterator();
			while (iterator.hasNext()) {
				message = iterator.next();
				if (message.getType() == MessageType.UPDATE) {
					lastUpdateMessage = message;
				} else {
					continue;
				}
				
				iterator.remove();
			}
			
			if (lastUpdateMessage != null) {
				onUpdate(lastUpdateMessage);
			}
			
			if (hasSentCurrentFrameComposer) {
				/*
				 * Consider only the first start frame message received.
				 */
				firstStartFrameMessage = null;
				iterator = messages.iterator();
				while (iterator.hasNext()) {
					message = iterator.next();
					if (message.getType() == MessageType.START_FRAME) {
						firstStartFrameMessage = message;
						iterator.remove();
						break;
					}
				}
				
				if (firstStartFrameMessage != null) {
					onStartFrame(firstStartFrameMessage);
					
					updateXith3DScheduledOperations();
				}
			} else {
				sendCurrentFrameToComposer();
				
				hasSentCurrentFrameComposer = true;
			}
		} else if (isSessionReadyToStart()) {
			startNewSession();
		}
	}
	
	private void updateXith3DScheduledOperations() {
		long frameTime = clockCount - lastClockCount;
		
		super.update(clockCount, frameTime, TimingMode.MILLISECONDS);
		
		lastClockCount = clockCount;
	}
	
}
