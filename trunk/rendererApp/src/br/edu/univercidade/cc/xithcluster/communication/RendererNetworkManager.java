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
import br.edu.univercidade.cc.xithcluster.Renderer;
import br.edu.univercidade.cc.xithcluster.RendererConfiguration;
import br.edu.univercidade.cc.xithcluster.SceneDeserializer;
import br.edu.univercidade.cc.xithcluster.SceneDeserializer.DeserializationResult;

public final class RendererNetworkManager implements Observer, Updatable {
	
	private Logger log = Logger.getLogger(RendererNetworkManager.class);
	
	private enum SessionState {
		CLOSED, STARTING, STARTED
	}
	
	private RendererMessageBroker rendererMessageBroker = new RendererMessageBroker();
	
	private SceneDeserializer sceneDeserializer = new SceneDeserializer();
	
	private Renderer renderer;
	
	private INonBlockingConnection masterConnection;
	
	private INonBlockingConnection composerConnection;
	
	// private UpdatesPackager updatesPackager = new UpdatesPackager();
	
	private Thread sceneDeserializationThread;
	
	private SessionState sessionState = SessionState.CLOSED;
	
	private boolean startFrame = false;
	
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
	
	private void onUpdate(byte[] updatesData) {
		// TODO:
		// updateScene(updatesPackager.deserialize(updatesData));
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
			if (composerConnection == null) {
				log.info("Connecting to composer");
			} else {
				log.info("Re-connecting to composer");
			}
			
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
			log.info("Interrupting previous parallel scene deserialization");
			
			interruptParallelSceneDeserialization();
		}
		
		renderer.setId(id);
		renderer.setScreenSize(screenWidth, screenHeight);
		
		log.info("Starting parallel scene deserialization");
		
		startParallelSceneDeserialization(pointOfViewData, sceneData);
		
		log.info("Session started successfully");
	}
	
	private void sendCompositionOrder() {
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
	
	private void sendNewImageMessage(byte[] colorAndAlphaBuffer, byte[] depthBuffer) throws BufferOverflowException, IOException {
		composerConnection.write(MessageType.NEW_IMAGE.ordinal());
		composerConnection.flush();
		
		composerConnection.write(currentFrameIndex);
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
		Message lastUpdateMessage;
		Message lastStartFrameMessage;
		Message lastStartSessionMessage;
		byte[] updatesData;
		byte[] colorAndAlphaBuffer;
		int frameIndex;
		int rendererId;
		int screenWidth;
		int screenHeight;
		double targetFPS;
		byte[] pointOfViewData;
		byte[] sceneData;
		
		if (!masterConnection.isOpen()) {
			log.info("Master node disconnected");
			
			// TODO:
			System.exit(-1);
		}
		
		messages = MessageQueue.startReadingMessages();
		
		/*
		 * Consider only the last start session message.
		 */
		lastStartSessionMessage = null;
		iterator = messages.iterator();
		while (iterator.hasNext()) {
			message = iterator.next();
			if (message.getType() == MessageType.START_SESSION) {
				lastStartSessionMessage = message;
			} else {
				continue;
			}
			
			iterator.remove();
		}
		
		if (lastStartSessionMessage != null) {
			rendererId = (Integer) lastStartSessionMessage.getParameters()[0];
			screenWidth = (Integer) lastStartSessionMessage.getParameters()[1];
			screenHeight = (Integer) lastStartSessionMessage.getParameters()[2];
			targetFPS = (Double) lastStartSessionMessage.getParameters()[3];
			pointOfViewData = (byte[]) lastStartSessionMessage.getParameters()[4];
			sceneData = (byte[]) lastStartSessionMessage.getParameters()[5];
			
			onStartSession(rendererId, screenWidth, screenHeight, targetFPS, pointOfViewData, sceneData);
		}
		
		if (sessionState == SessionState.STARTED) {
			if (!composerConnection.isOpen()) {
				log.info("Current session was closed");
				
				sessionState = SessionState.CLOSED;
				
				MessageQueue.stopReadingMessages();
				
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
				updatesData = (byte[]) lastUpdateMessage.getParameters()[0];
				onUpdate(updatesData);
				
				log.info("Updating scene");
			}
			
			if (!startFrame) {
				/*
				 * Consider only the last start frame message received.
				 */
				lastStartFrameMessage = null;
				iterator = messages.iterator();
				while (iterator.hasNext()) {
					message = iterator.next();
					if (message.getType() == MessageType.START_FRAME) {
						lastStartFrameMessage = message;
					} else {
						continue;
					}
					
					iterator.remove();
				}
				
				if (lastStartFrameMessage != null) {
					frameIndex = (Integer) lastStartFrameMessage.getParameters()[0];
					
					startFrame = true;
					currentFrameIndex = frameIndex;
					
					log.info("Starting new frame: " + frameIndex);
				}
				// startFrame == true
			} else {
				colorAndAlphaBuffer = renderer.getColorAndAlphaBuffer();
				
				switch (RendererConfiguration.compressionMethod) {
				case PNG:
					// TODO: Deflate!
					break;
				}
				
				try {
					sendNewImageMessage(colorAndAlphaBuffer, renderer.getDepthBuffer());
					
					startFrame = false;
				} catch (IOException e) {
					log.error("Error sending image buffers to composer", e);
				}
			}
		} else if (sessionState == SessionState.STARTING && deserializationResult != null) {
			/*
			 * Scene rebuilding is an operation that should never cause "abends"
			 * so we should never prevent the cluster session to be started
			 * because of it.
			 */
			try {
				sendSessionStartedMessage();
			} catch (IOException e) {
				// TODO:
				throw new RuntimeException("Error notifying master node that session started successfully", e);
			}
			
			sessionState = SessionState.STARTED;
			
			log.info("Session started successfully");
			
			renderer.updateScene(deserializationResult.getPointOfView(), deserializationResult.getScene());
			
			log.info("Scene updated with success");
			
			deserializationResult = null;
		}
		
		MessageQueue.stopReadingMessages();
	}
	
}
