package br.edu.univercidade.cc.xithcluster.communication;

import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.TextureImage;
import org.xith3d.scenegraph.View;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.NonBlockingConnection;

import br.edu.univercidade.cc.xithcluster.PendingUpdate;
import br.edu.univercidade.cc.xithcluster.Renderer;
import br.edu.univercidade.cc.xithcluster.RendererConfiguration;
import br.edu.univercidade.cc.xithcluster.SceneDeserializer;
import br.edu.univercidade.cc.xithcluster.communication.protocol.RendererProtocolHandler;
import br.edu.univercidade.cc.xithcluster.serialization.packagers.UpdatesPackager;
import br.edu.univercidade.cc.xithcluster.util.BufferUtils;

public final class RendererNetworkManager implements Observer {
	
	private Logger log = Logger.getLogger(RendererNetworkManager.class);
	
	private int id = -1;
	
	private SceneDeserializer sceneDeserializer = new SceneDeserializer();
	
	private INonBlockingConnection masterConnection;
	
	private INonBlockingConnection composerConnection;
	
	private UpdatesPackager updatesPackager = new UpdatesPackager();
	
	private Thread sceneDeserializationThread;
	
	private boolean sessionStarted = false;
	
	private boolean startFrame = false;
	
	private Renderer renderer;
	
	private RendererProtocolHandler rendererProtocolHandler;
	
	public RendererNetworkManager(Renderer renderer) {
		this.renderer = renderer;
		this.sceneDeserializer.addObserver(this);
		this.rendererProtocolHandler = new RendererProtocolHandler(this);
	}
	
	public void initialize() throws IOException {
		masterConnection = new NonBlockingConnection(RendererConfiguration.masterListeningAddress, RendererConfiguration.masterListeningPort, rendererProtocolHandler);
		masterConnection.setAutoflush(false);
	}
	
	private void setId(int id) {
		this.id = id;
	}
	
	private void rebuildScene(View view, List<Light> lightSources, BranchGroup geometries) {
		synchronized (renderer.getSceneLock()) {
			renderer.updateScene(view, lightSources, geometries);
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
	
	public void notifyFrameFinished() {
		try {
			rendererProtocolHandler.sendFrameFinishedMessage(masterConnection);
		} catch (IOException e) {
			log.error("Error notifying master node that thet current frame was finished", e);
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
	
	public synchronized boolean onStartFrame() {
		if (sessionStarted) {
			startFrame = true;
			
			return true;
		} else {
			log.error("Cannot render frame before starting a session");
			
			return false;
		}
	}
	
	public synchronized void onStartSession(
			int id, 
			int screenWidth, 
			int screenHeight, 
			double targetFPS, 
			String composerHostname, 
			int composerPort, 
			byte[] pointOfViewData, 
			byte[] lightSourcesData, 
			byte[] geometriesData) 
	throws IOException {
		sessionStarted = false;
		
		log.debug("***************");
		log.debug("Session started");
		log.debug("***************");
		
		composerConnection = new NonBlockingConnection(composerHostname, composerPort);
		composerConnection.setAutoflush(false);
		
		notifyCompositionOrder();
		
		if (isParallelSceneDeserializationHappening()) {
			interruptParallelSceneDeserialization();
		}
		
		log.trace("Received id: " + id);
		log.trace("Screen width: " + screenWidth);
		log.trace("Screen height: " + screenHeight);
		log.trace("Target FPS: " + targetFPS);
		log.trace("POV data size: " + pointOfViewData.length + " bytes");
		log.trace("Light sources data size: " + lightSourcesData.length + " bytes");
		log.trace("Geometries data size: " + geometriesData.length + " bytes");
		
		setId(id);
		
		notifySceneIdChange();
		
		startParallelSceneDeserialization(pointOfViewData, lightSourcesData, geometriesData);
		
		log.info("Session started successfully");
	}
	
	private void notifyCompositionOrder() {
		try {
			rendererProtocolHandler.sendSetCompositionOrderMessage(composerConnection, RendererConfiguration.compositionOrder); 
		} catch (IOException e) {
			log.error("Error notifying composer node the renderer's composition order", e);
		}
	}

	private void notifySceneIdChange() {
		renderer.setId(id);
	}
	
	private void startParallelSceneDeserialization(byte[] pointOfViewData, byte[] lightSourcesData, byte[] geometriesData) {
		sceneDeserializer.setPackagesData(pointOfViewData, lightSourcesData, geometriesData);
		
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
			
			rebuildScene(result.getView(), result.getLightSources(), result.getGeometries());
		}
	}

	public void sendColorAlphaAndDepthBuffer(Texture2D colorAndAlphaTexture, Texture2D depthTexture) {
		TextureImage textureImage1, textureImage2;
		byte[] colorAndAlphaBuffer;
		byte[] depthBuffer;
		
		textureImage1 = colorAndAlphaTexture.getImage(0);
		textureImage2 = depthTexture.getImage(0);
		if (textureImage1.getDataBuffer() != null && textureImage2.getDataBuffer() != null) {
			colorAndAlphaBuffer = BufferUtils.safeBufferRead(textureImage1.getDataBuffer());
			depthBuffer = BufferUtils.safeBufferRead(depthTexture.getImage(0).getDataBuffer());
			
			switch (RendererConfiguration.compressionMethod) {
			case PNG:
				// TODO: Deflate!
				break;
			}
			
			// TODO: Do re-attempts!
			try {
				rendererProtocolHandler.sendNewImageMessage(composerConnection, RendererConfiguration.compressionMethod, colorAndAlphaBuffer, depthBuffer);
			} catch (IOException e) {
				log.error("Error sending image buffers to composer", e);
			}
		}
		
		notifyFrameFinished();
	}
}
