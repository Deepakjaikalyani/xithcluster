package br.edu.univercidade.cc.xithcluster.communication;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import org.apache.log4j.Logger;
import org.xith3d.loop.Updatable;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.NonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.DeserializationResult;
import br.edu.univercidade.cc.xithcluster.Renderer;
import br.edu.univercidade.cc.xithcluster.RendererConfiguration;
import br.edu.univercidade.cc.xithcluster.SceneDeserializer;

public final class RendererNetworkManager extends NetworkManager implements Observer, Updatable {
	
	private enum SessionState {
		CLOSED, STARTING, STARTED
	}
	
	private Logger log = Logger.getLogger(RendererNetworkManager.class);
	
	private boolean trace = log.isTraceEnabled();
	
	private RendererMessageBroker rendererMessageBroker = new RendererMessageBroker();
	
	private Renderer renderer;
	
	private INonBlockingConnection composerConnection;
	
	// private UpdatesPackager updatesPackager = new UpdatesPackager();
	
	private SceneDeserializer sceneDeserializer;
	
	private Thread sceneDeserializationThread;
	
	private DeserializationResult deserializationResult;
	
	private SessionState sessionState = SessionState.CLOSED;
	
	private boolean hasSentCurrentFrameCompositor = true;
	
	private int currentFrame = -1;
	
	public RendererNetworkManager(Renderer renderer) {
		this.renderer = renderer;
	}
	
	public void initialize() throws IOException {
		masterConnection = new NonBlockingConnection(RendererConfiguration.masterListeningAddress, RendererConfiguration.masterListeningPort, rendererMessageBroker);
		masterConnection.setAutoflush(false);
	}
	
	private boolean isConnectedToComposer() {
		return composerConnection != null && composerConnection.isOpen();
	}
	
	private boolean isSessionReadyToStart() {
		return sessionState == SessionState.STARTING && deserializationResult != null;
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
		
		sessionState = SessionState.STARTED;
		
		log.info("Session started successfully");
	}
	
	private void closeCurrentSession() {
		sessionState = SessionState.CLOSED;
		
		log.info("Current session was closed");
	}

	private void sendCurrentFrameToCompositor() {
		byte[] colorAndAlphaBuffer;
		
		if (trace) {
			log.trace("Sending current frame to compositor: " + currentFrame);
		}
		
		colorAndAlphaBuffer = renderer.getColorAndAlphaBuffer();
		
		switch (RendererConfiguration.compressionMethod) {
		case PNG:
			// TODO: Deflate!
			break;
		}
		
		try {
			sendNewImageMessage(colorAndAlphaBuffer, renderer.getDepthBuffer());
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error sending image buffers to composer", e);
		}
	}

	private void onStartFrame(Message message) {
		currentFrame = (Integer) message.getParameters()[0];
		
		if (trace) {
			log.trace("Start frame received: " + currentFrame);
		}
		
		hasSentCurrentFrameCompositor = false;
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
		
		sessionState = SessionState.STARTING;
		
		if (trace) {
			log.trace("rendererId=" + rendererId);
			log.trace("screenWidth=" + screenWidth);
			log.trace("screenHeight=" + screenHeight);
			log.trace("targetFPS: " + targetFPS);
			log.trace("pointOfViewData.length=" + pointOfViewData.length);
			log.trace("sceneData.length=" + sceneData.length);
		}
		
		if (!isConnectedToComposer()) {
			log.info("Connecting to composer...");
			
			try {
				composerConnection = new NonBlockingConnection(RendererConfiguration.composerListeningAddress, RendererConfiguration.composerListeningPort);
				composerConnection.setAutoflush(false);
			} catch (IOException e) {
				// TODO:
				throw new RuntimeException("Error connecting to composer", e);
			}
		}
		
		log.info("Sending composition order: " + RendererConfiguration.compositionOrder);
		
		sendCompositionOrder();
		
		if (isParallelSceneDeserializationHappening()) {
			log.debug("Interrupting previous parallel scene deserialization");
			
			interruptParallelSceneDeserialization();
		}
		
		renderer.setId(rendererId);
		renderer.setScreenSize(screenWidth, screenHeight);
		
		log.debug("Starting parallel scene deserialization");
		
		startParallelSceneDeserialization(pointOfViewData, sceneData);
		
		log.info("Session started successfully");
	}

	private void sendCompositionOrder() {
		try {
			sendSetCompositionOrderMessage(RendererConfiguration.compositionOrder);
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
		composerConnection.write(RendererConfiguration.compressionMethod.ordinal());
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
		// TODO: API bridge
		super.update();
	}
	
	@Override
	protected void processMessages(Queue<Message> messages) {
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
		
		if (sessionState == SessionState.STARTED) {
			if (!composerConnection.isOpen()) {
				closeCurrentSession();
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
			
			if (hasSentCurrentFrameCompositor) {
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
				}
			} else {
				sendCurrentFrameToCompositor();
				
				hasSentCurrentFrameCompositor = true;
			}
		} else if (isSessionReadyToStart()) {
			startNewSession();
		}
	}

}
