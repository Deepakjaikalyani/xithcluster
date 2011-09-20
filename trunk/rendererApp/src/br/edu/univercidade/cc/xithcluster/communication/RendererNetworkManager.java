package br.edu.univercidade.cc.xithcluster.communication;

import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import org.apache.log4j.Logger;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.View;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.NonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.PendingUpdate;
import br.edu.univercidade.cc.xithcluster.Renderer;
import br.edu.univercidade.cc.xithcluster.RendererConfiguration;
import br.edu.univercidade.cc.xithcluster.SceneDeserializer;
import br.edu.univercidade.cc.xithcluster.communication.protocol.RendererProtocolHandler;
import br.edu.univercidade.cc.xithcluster.serialization.packagers.UpdatesPackager;

public final class RendererNetworkManager implements Observer {
	
	private Logger log = Logger.getLogger(RendererNetworkManager.class);
	
	private SceneDeserializer sceneDeserializer = new SceneDeserializer();
	
	private INonBlockingConnection masterConnection;
	
	private INonBlockingConnection composerConnection;
	
	private UpdatesPackager updatesPackager = new UpdatesPackager();
	
	private Thread sceneDeserializationThread;
	
	private boolean sessionStarted = false;
	
	private boolean startFrame = false;
	
	private Renderer renderer;
	
	private RendererProtocolHandler rendererProtocolHandler;
	
	private int currentFrameIndex = -1;
	
	public RendererNetworkManager(Renderer renderer) {
		this.renderer = renderer;
		this.sceneDeserializer.addObserver(this);
		this.rendererProtocolHandler = new RendererProtocolHandler(this);
	}
	
	public void initialize() throws IOException {
		masterConnection = new NonBlockingConnection(RendererConfiguration.masterListeningAddress, RendererConfiguration.masterListeningPort, rendererProtocolHandler);
		masterConnection.setAutoflush(false);
	}
	
	private void rebuildScene(View pointOfView, BranchGroup scene) {
		synchronized (renderer.getSceneLock()) {
			renderer.updateScene(pointOfView, scene);
		}
	}
	
	private void updateScene(List<PendingUpdate> pendingUpdates) {
		// TODO:
	}
	
	public synchronized boolean startFrame() {
		boolean param0;
		
		param0 = startFrame;
		startFrame = false;
		
		return param0;
	}
	
	public void notifySessionStarted() {
		try {
			rendererProtocolHandler.sendSessionStartedMessage(masterConnection);
		} catch (IOException e) {
			log.error("Error notifying master node that session started successfully", e);
		}
	}
	
	public synchronized boolean onUpdate(byte[] updatesData) throws IOException {
		if (sessionStarted) {
			updateScene(updatesPackager.deserialize(updatesData));
			
			return true;
		} else {
			log.error("Cannot update scene before starting a session");
			
			return false;
		}
	}
	
	public synchronized boolean onStartFrame(int frameIndex) {
		if (sessionStarted) {
			startFrame = true;
			currentFrameIndex = frameIndex;
			
			return true;
		} else {
			log.error("Cannot render frame before starting a session");
			
			return false;
		}
	}
	
	public synchronized void onStartSession(int id, int screenWidth, int screenHeight, double targetFPS, byte[] pointOfViewData, byte[] sceneData) throws IOException {
		sessionStarted = false;
		
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
			rendererProtocolHandler.sendSetCompositionOrderMessage(composerConnection, RendererConfiguration.compositionOrder);
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
	
	@Override
	public void update(Observable o, Object arg) {
		SceneDeserializer.DeserializationResult result;
		
		if (o == sceneDeserializer) {
			/*
			 * Scene rebuilding is an operation that should never cause "abends"
			 * so we should never prevent the cluster session to be started
			 * because of it.
			 */
			notifySessionStarted();
			
			sessionStarted = true;
			
			result = (SceneDeserializer.DeserializationResult) arg;
			
			rebuildScene(result.getPointOfView(), result.getScene());
		}
	}
	
	public void sendColorAlphaAndDepthBuffers(byte[] colorAndAlphaBuffer, byte[] depthBuffer) {
		switch (RendererConfiguration.compressionMethod) {
		case PNG:
			// TODO: Deflate!
			break;
		}
		
		// DEBUG:
		checkDepthBuffer(depthBuffer);
		
		// TODO: Do re-attempts!
		try {
			rendererProtocolHandler.sendNewImageMessage(composerConnection, currentFrameIndex, RendererConfiguration.compressionMethod, colorAndAlphaBuffer, depthBuffer);
		} catch (IOException e) {
			log.error("Error sending image buffers to composer", e);
		}
	}
	
	private void checkDepthBuffer(byte[] depthBuffer) {
		for (int i = 0; i < depthBuffer.length; i++) {
			if (depthBuffer[i] != 0) {
				System.out.println("depth[" + i + "]=" + depthBuffer[i]);
			}
		}
	}
	
}
