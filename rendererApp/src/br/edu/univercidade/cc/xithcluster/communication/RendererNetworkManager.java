package br.edu.univercidade.cc.xithcluster.communication;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.util.Deque;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import org.apache.log4j.Logger;
import org.xith3d.loop.Updatable;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.NonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.CompressionMethod;
import br.edu.univercidade.cc.xithcluster.Renderer;
import br.edu.univercidade.cc.xithcluster.RendererConfiguration;
import br.edu.univercidade.cc.xithcluster.SceneDeserializer;
import br.edu.univercidade.cc.xithcluster.SceneDeserializer.DeserializationResult;

public final class RendererNetworkManager implements Observer, Updatable {
	
	private Logger log = Logger.getLogger(RendererNetworkManager.class);
	
	private enum SessionState {
		CLOSED,
		STARTING,
		STARTED
	}
	
	private RendererMessageBroker rendererMessageBroker = new RendererMessageBroker();
	
	private SceneDeserializer sceneDeserializer = new SceneDeserializer();
	
	private Renderer renderer;
	
	private INonBlockingConnection masterConnection;
	
	private INonBlockingConnection composerConnection;
	
	//private UpdatesPackager updatesPackager = new UpdatesPackager();
	
	private Thread sceneDeserializationThread;
	
	private SessionState sessionState = SessionState.CLOSED;
	
	private boolean renderFrame = false;
	
	private int currentFrameIndex = -1;

	private DeserializationResult deserializationResult;
	
	public RendererNetworkManager(Renderer renderer) {
		this.renderer = renderer;
		this.sceneDeserializer.addObserver(this);
	}
	
	public void initialize() throws IOException {
		masterConnection = new NonBlockingConnection(RendererConfiguration.masterListeningAddress, RendererConfiguration.masterListeningPort, rendererMessageBroker);
		masterConnection.setAutoflush(false);
	}
	
	private void notifySessionStarted() {
		try {
			sendSessionStartedMessage();
		} catch (IOException e) {
			log.error("Error notifying master node that session started successfully", e);
		}
	}
	
	private void onUpdate(byte[] updatesData) {
		// TODO:
		//updateScene(updatesPackager.deserialize(updatesData));
	}
	
	private void onStartFrame(int frameIndex) {
		renderFrame = true;
		currentFrameIndex = frameIndex;
	}
	
	private void onStartSession(int id, int screenWidth, int screenHeight, double targetFPS, byte[] pointOfViewData, byte[] sceneData) {
		sessionState = SessionState.STARTING;
		
		log.debug("****************");
		log.debug("Session starting");
		log.debug("****************");
		
		log.trace("Received id: " + id);
		log.trace("Screen width: " + screenWidth);
		log.trace("Screen height: " + screenHeight);
		log.trace("Target FPS: " + targetFPS);
		log.trace("POV data size: " + pointOfViewData.length + " bytes");
		log.trace("Scene data size: " + sceneData.length + " bytes");
		
		if (composerConnection == null || !composerConnection.isOpen()) {
			log.trace("Connecting to composer");
			try {
				composerConnection = new NonBlockingConnection(RendererConfiguration.composerListeningAddress, RendererConfiguration.composerListeningPort);
				composerConnection.setAutoflush(false);
			} catch (IOException e) {
				// TODO:
				throw new RuntimeException("Error connecting to composer", e);
			}
		}
		
		log.trace("Sending composition order: " + RendererConfiguration.compositionOrder);
		notifyCompositionOrder();
		
		if (isParallelSceneDeserializationHappening()) {
			log.trace("Interrupting parallel scene deserialization");
			interruptParallelSceneDeserialization();
		}
		
		renderer.setId(id);
		renderer.setScreenSize(screenWidth, screenHeight);
		
		startParallelSceneDeserialization(pointOfViewData, sceneData);
		
		log.info("Session started successfully");
	}
	
	private void notifyCompositionOrder() {
		try {
			sendSetCompositionOrderMessage(RendererConfiguration.compositionOrder);
		} catch (IOException e) {
			log.error("Error notifying composer node the renderer's composition order", e);
		}
	}
	
	private void startParallelSceneDeserialization(byte[] pointOfViewData, byte[] sceneData) {
		sceneDeserializer.setSceneData(pointOfViewData, sceneData);
		
		sceneDeserializationThread = new Thread(sceneDeserializer);
		sceneDeserializationThread.start();
	}
	
	private void interruptParallelSceneDeserialization() {
		sceneDeserializationThread.interrupt();
		
		while (sceneDeserializationThread.isInterrupted()) {
			try {
				Thread.sleep(100L);
			} catch (InterruptedException e) {
			}
		}
	}
	
	private boolean isParallelSceneDeserializationHappening() {
		return sceneDeserializationThread != null;
	}
	
	private void sendSessionStartedMessage() throws BufferOverflowException, IOException {
		masterConnection.write(MessageType.SESSION_STARTED.ordinal());
		masterConnection.flush();
	}
	
	private void sendNewImageMessage(int frameIndex, CompressionMethod compressionMethod, byte[] colorAndAlphaBuffer, byte[] depthBuffer) throws BufferOverflowException, IOException {
		composerConnection.write(MessageType.NEW_IMAGE.ordinal());
		composerConnection.flush();
		
		composerConnection.write(frameIndex);
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
		Deque<Message> messages;
		Message message;
		Iterator<Message> iterator;
		Iterator<Message> descendingIterator;
		Message lastUpdateMessage;
		Message lastStartFrameMessage;
		byte[] colorAndAlphaBuffer;
		
		messages = MessageQueue.getInstance().retrieveMessages();
		if (sessionState == SessionState.STARTED) {
			/*
			 * Consider only the last update message received.
			 */
			lastUpdateMessage = null;
			descendingIterator = messages.descendingIterator();
			while (descendingIterator.hasNext()) {
				message = descendingIterator.next();
				if (message.getType() == MessageType.UPDATE) {
					lastUpdateMessage = message;
					break;
				}
			}
			
			if (lastUpdateMessage != null) {
				onUpdate((byte[]) lastUpdateMessage.getParameters()[0]);
			}
			
			if (!renderFrame) {
				/*
				 * Consider only the last start frame message received.
				 */
				lastStartFrameMessage = null;
				descendingIterator = messages.descendingIterator();
				while (descendingIterator.hasNext()) {
					message = descendingIterator.next();
					if (lastStartFrameMessage == null && message.getType() == MessageType.START_FRAME) {
						lastStartFrameMessage = message;
					} else {
						log.error("Start frame message lost: " + message.getParameters()[0]);
					}
				}
				
				if (lastStartFrameMessage != null) {
					onStartFrame((Integer) lastStartFrameMessage.getParameters()[0]);
				}
			} else {
				colorAndAlphaBuffer = renderer.getColorAndAlphaBuffer();
				
				switch (RendererConfiguration.compressionMethod) {
				case PNG:
					// TODO: Deflate!
					break;
				}
				
				try {
					sendNewImageMessage(currentFrameIndex, RendererConfiguration.compressionMethod, colorAndAlphaBuffer, renderer.getDepthBuffer());
				} catch (IOException e) {
					log.error("Error sending image buffers to composer", e);
				}
				
				renderFrame = false;
			}
		} else if (sessionState == SessionState.CLOSED) {
			/*
			 * Consider only start session messages, all others are ignored.
			 */
			iterator = messages.iterator();
			while (iterator.hasNext()) {
				message = iterator.next();
				if (message.getType() == MessageType.START_SESSION) {
					onStartSession((Integer) message.getParameters()[0], (Integer) message.getParameters()[1], (Integer) message.getParameters()[2], (Double) message.getParameters()[3], (byte[]) message.getParameters()[4], (byte[]) message.getParameters()[5]);
					break;
				}
			}
		} else if (deserializationResult != null) {
			/*
			 * Scene rebuilding is an operation that should never cause "abends"
			 * so we should never prevent the cluster session to be started
			 * because of it.
			 */
			notifySessionStarted();
			
			sessionState = SessionState.STARTED;
			
			renderer.updateScene(deserializationResult.getPointOfView(), deserializationResult.getScene());
			
			deserializationResult = null;
		}
	}
	
}
