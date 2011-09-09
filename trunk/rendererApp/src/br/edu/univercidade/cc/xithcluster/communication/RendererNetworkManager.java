package br.edu.univercidade.cc.xithcluster.communication;

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
import br.edu.univercidade.cc.xithcluster.communication.protocol.ProtocolHelper;
import br.edu.univercidade.cc.xithcluster.communication.protocol.RecordType;
import br.edu.univercidade.cc.xithcluster.serialization.packagers.UpdatesPackager;

public final class RendererNetworkManager implements IDataHandler, Observer {
	
	private Logger log = Logger.getLogger(RendererNetworkManager.class);
	
	private int id = -1;
	
	private SceneDeserializer sceneDeserializer;
	
	private INonBlockingConnection masterConnection;
	
	//private INonBlockingConnection composerConnection;
	
	private UpdatesPackager updatesPackager = new UpdatesPackager();
	
	private Thread sceneDeserializationThread;
	
	private boolean sessionStarted = false;
	
	private boolean startFrame = false;
	
	private SceneManager sceneManager;
	
	public RendererNetworkManager(SceneManager sceneManager) {
		this.sceneManager = sceneManager;
		
		sceneDeserializer = new SceneDeserializer();
		sceneDeserializer.addObserver(this);
	}
	
	public void initialize() {
		try {
			masterConnection = new NonBlockingConnection(RendererConfiguration.masterHostname, RendererConfiguration.masterPort, this);
			masterConnection.setAutoflush(false);
			
			//composerConnection = new NonBlockingConnection(RendererConfiguration.composerHostname, RendererConfiguration.composerPort);
			//composerConnection.setAutoflush(false);
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
	}
	
	public synchronized boolean startFrame() {
		boolean param0;
		
		param0 = startFrame;
		startFrame = false;
		
		return param0;
	}
	
	public void notifySessionStarted() {
		try {
			ProtocolHelper.writeRecord(masterConnection, RecordType.SESSION_STARTED);
		} catch (IOException e) {
			log.error("Error notifying master node that session started successfully", e);
		}
	}

	public void notifyFrameFinished() {
		try {
			ProtocolHelper.writeRecord(masterConnection, RecordType.FRAME_FINISHED);
		} catch (IOException e) {
			log.error("Error notifying master node that thet current frame was finished", e);
		}
	}

	@Override
	public boolean onData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		RecordType recordType;
		
		recordType = ProtocolHelper.readRecordType(arg0);
		
		if (recordType == null) {
			return true;
		}
		
		switch (recordType) {
		case START_SESSION:
			return onStartSession(arg0);
		case START_FRAME:
			return onStartFrame();
		case UPDATE:
			return onUpdate(arg0);
		default:
			log.error("Invalid/Unknown record");
			
			return false;
		}
	}

	private synchronized boolean onUpdate(INonBlockingConnection arg0) throws IOException {
		Object[] fields;
		
		fields = ProtocolHelper.readRecordFields(arg0, byte[].class);
		
		if (fields != null) {
			if (sessionStarted) {
				updateScene(updatesPackager.deserialize((byte[]) fields[0]));
				
				return true;
			} else {
				log.error("Cannot update scene before starting a session");
				
				return false;
			}
		} else {
			return true;
		}
	}

	private synchronized boolean onStartFrame() {
		if (sessionStarted) {
			startFrame = true;
			
			return true;
		} else {
			log.error("Cannot render frame before starting a session");
			
			return false;
		}
	}

	private synchronized boolean onStartSession(INonBlockingConnection arg0) throws IOException {
		Object[] fields;
		int id;
		byte[] pointOfViewData;
		byte[] lightSourcesData;
		byte[] geometriesData;
		
		fields = ProtocolHelper.readRecordFields(arg0, Integer.class, byte[].class, byte[].class, byte[].class);
		
		if (fields != null) {
			sessionStarted = false;
			
			log.debug("***************");
			log.debug("Session started");
			log.debug("***************");
			
			if (isParallelSceneDeserializationHappening()) {
				interruptParallelSceneDeserialization();
			}
			
			id = (Integer) fields[0];
			pointOfViewData = (byte[]) fields[1];
			lightSourcesData = (byte[]) fields[2];
			geometriesData = (byte[]) fields[3];
			
			log.trace("Received id: " + id);
			log.trace("POV data size: " + pointOfViewData.length + " bytes");
			log.trace("Light sources data size: " + lightSourcesData.length + " bytes");
			log.trace("Geometries data size: " + geometriesData.length + " bytes");
			
			setId(id);
			
			notifySceneIdChange();
			
			startParallelSceneDeserialization(pointOfViewData, lightSourcesData, geometriesData);
			
			log.info("Session started successfully");
		}
		
		return true;
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
