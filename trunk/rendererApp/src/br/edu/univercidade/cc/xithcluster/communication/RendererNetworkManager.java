package br.edu.univercidade.cc.xithcluster.communication;

import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import org.apache.log4j.Logger;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.View;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.NonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.PendingUpdate;
import br.edu.univercidade.cc.xithcluster.RendererConfiguration;
import br.edu.univercidade.cc.xithcluster.SceneDeserializer;
import br.edu.univercidade.cc.xithcluster.SceneManager;
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
	
	private SceneManager sceneManager;
	
	private RendererProtocolHandler rendererProtocolHandler;
	
	public RendererNetworkManager(SceneManager sceneManager) {
		this.sceneManager = sceneManager;
		this.sceneDeserializer.addObserver(this);
		this.rendererProtocolHandler = new RendererProtocolHandler(this);
	}
	
	public void initialize() {
		try {
			masterConnection = new NonBlockingConnection(RendererConfiguration.masterListeningAddress, RendererConfiguration.masterListeningPort, rendererProtocolHandler);
			masterConnection.setAutoflush(false);
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error trying to connecting to the cluster", e);
		}
	}
	
	private void setId(int id) {
		this.id = id;
	}
	
	private void rebuildScene(View view, List<Light> lightSources, BranchGroup geometries) {
		synchronized (sceneManager.getSceneLock()) {
			sceneManager.setRoot(geometries);
			sceneManager.setPointOfView(view.getPosition(), view.getFacingDirection(), view.getUpDirection());
			sceneManager.addLightSources(lightSources);
			sceneManager.updateScene();
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
	
	public synchronized void onStartSession(int id, String composerHostname, int composerPort, byte[] pointOfViewData, byte[] lightSourcesData, byte[] geometriesData) throws IOException {
		
		sessionStarted = false;
		
		log.debug("***************");
		log.debug("Session started");
		log.debug("***************");
		
		composerConnection = new NonBlockingConnection(composerHostname, composerPort);
		composerConnection.setAutoflush(false);
		
		if (isParallelSceneDeserializationHappening()) {
			interruptParallelSceneDeserialization();
		}
		
		log.trace("Received id: " + id);
		log.trace("POV data size: " + pointOfViewData.length + " bytes");
		log.trace("Light sources data size: " + lightSourcesData.length + " bytes");
		log.trace("Geometries data size: " + geometriesData.length + " bytes");
		
		setId(id);
		
		notifySceneIdChange();
		
		startParallelSceneDeserialization(pointOfViewData, lightSourcesData, geometriesData);
		
		log.info("Session started successfully");
	}
	
	private void notifySceneIdChange() {
		sceneManager.setId(id);
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
		byte[] colorAndAlphaBuffer;
		byte[] depthBuffer;
		
		colorAndAlphaBuffer = BufferUtils.safeBufferRead(colorAndAlphaTexture.getImage(0).getDataBuffer());
		depthBuffer = BufferUtils.safeBufferRead(depthTexture.getImage(0).getDataBuffer());
		
		// TODO: Do re-attempts!
		try {
			rendererProtocolHandler.sendColorAlphaAndDepthBuffer(composerConnection, colorAndAlphaBuffer, depthBuffer);
		} catch (IOException e) {
			log.error("Error sending image buffers to composer", e);
		}
		
		notifyFrameFinished();
	}
}
