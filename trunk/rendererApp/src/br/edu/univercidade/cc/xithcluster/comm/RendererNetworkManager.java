package br.edu.univercidade.cc.xithcluster.comm;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import org.apache.log4j.Logger;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.View;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.NonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.PendingUpdate;
import br.edu.univercidade.cc.xithcluster.RendererConfiguration;
import br.edu.univercidade.cc.xithcluster.SceneDeserializer;
import br.edu.univercidade.cc.xithcluster.SceneManager;
import br.edu.univercidade.cc.xithcluster.net.xSocketHelper;
import br.edu.univercidade.cc.xithcluster.serial.pack.UpdatesPackager;

public final class RendererNetworkManager implements IDataHandler, Observer {
	
	private Logger log = Logger.getLogger(RendererNetworkManager.class);
	
	private int id = -1;
	
	private SceneDeserializer sceneDeserializer;
	
	private INonBlockingConnection masterConnection;
	
	//private IBlockingConnection composerConnection;
	
	private UpdatesPackager updatesPackager = new UpdatesPackager();
	
	private Thread sceneDeserializationThread;
	
	private boolean sessionStarted = false;
	
	private boolean startFrame = false;
	
	private SceneManager sceneManager;
	
	private final Object startFrameLock = new Object();
	
	public RendererNetworkManager(SceneManager sceneManager) {
		this.sceneManager = sceneManager;
		
		sceneDeserializer = new SceneDeserializer();
		sceneDeserializer.addObserver(this);
	}
	
	public void initialize() {
		try {
			masterConnection = new NonBlockingConnection(RendererConfiguration.masterHostname, RendererConfiguration.masterPort, this);
			//composerConnection = new BlockingConnection(RendererConfiguration.composerHostname, RendererConfiguration.composerPort);
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
			sceneManager.updateModifications();
		}
	}
	
	private void updateScene(List<PendingUpdate> pendingUpdates) {
		// TODO:
		synchronized (startFrameLock) {
		}
	}
	
	public boolean startFrame() {
		boolean param0;
		
		synchronized (startFrameLock) {
			param0 = startFrame;
			startFrame = false;
		}
		
		return param0;
	}
	
	public void notifySessionStarted() {
		try {
			xSocketHelper.write(masterConnection, RendererMessages.SESSION_STARTED);
		} catch (IOException e) {
			// TODO:
			e.printStackTrace();
		}
	}

	public void notifyFrameFinished() {
		try {
			xSocketHelper.write(masterConnection, RendererMessages.FRAME_FINISHED);
		} catch (IOException e) {
			// TODO:
			e.printStackTrace();
		}
	}

	@Override
	public boolean onData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		String message;
		int id;
		byte[] pointOfViewData;
		byte[] lightSourcesData;
		byte[] geometriesData;
		
		message = xSocketHelper.readString(arg0);
		
		if (ClusterMessages.SESSION_STARTED.equals(message)) {
			sessionStarted = false;
			
			log.debug("***************");
			log.debug("Session started");
			log.debug("***************");
			
			if (isParallelSceneDeserializationHappening()) {
				interruptParallelSceneDeserialization();
			}
			
			id = xSocketHelper.readInt(arg0);
			pointOfViewData = xSocketHelper.readBytes(arg0);
			lightSourcesData = xSocketHelper.readBytes(arg0);
			geometriesData = xSocketHelper.readBytes(arg0);
			
			log.trace("Received id: " + id);
			log.trace("POV data size: " + pointOfViewData.length + " bytes");
			log.trace("Light sources data size: " + lightSourcesData.length + " bytes");
			log.trace("Geometries data size: " + geometriesData.length + " bytes");
			
			setId(id);
			
			notifySceneIdChange();
			
			startParallelSceneDeserialization(pointOfViewData, lightSourcesData, geometriesData);
			
			log.info("Session started successfully");
			
			return true;
		} else if (ClusterMessages.START_FRAME.equals(message)) {
			if (sessionStarted) {
				synchronized (startFrameLock) {
					startFrame = true;
				}
				
				return true;
			} else {
				log.error("Cannot render frame before starting a session");
				
				return false;
			}
		} else if (ClusterMessages.UPDATE.equals(message)) {
			if (sessionStarted) {
				updateScene(updatesPackager.deserialize(xSocketHelper.readBytes(arg0)));
				
				return true;
			} else {
				log.error("Cannot update scene before starting a session");
				
				return false;
			}
		} else {
			log.error("Unknown message received: " + message);
			
			return false;
		}
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
			/* Scene rebuilding is an operation that should never cause "abends" 
			 * so we should never prevent the cluster session to be started because of it. 
			 */
			notifySessionStarted();
			
			sessionStarted = true;
			
			result = (SceneDeserializer.DeserializationResult) arg;
			
			rebuildScene(result.getView(), result.getLightSources(), result.getGeometries());
		}
	}
}
