package br.edu.univercidade.cc.xithcluster.comm;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import java.util.List;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.View;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.NonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.PendingUpdate;
import br.edu.univercidade.cc.xithcluster.RendererConfiguration;
import br.edu.univercidade.cc.xithcluster.SceneManager;
import br.edu.univercidade.cc.xithcluster.net.xSocketHelper;
import br.edu.univercidade.cc.xithcluster.serial.pack.GeometriesPackager;
import br.edu.univercidade.cc.xithcluster.serial.pack.LightSourcesPackager;
import br.edu.univercidade.cc.xithcluster.serial.pack.PointOfViewPackager;
import br.edu.univercidade.cc.xithcluster.serial.pack.UpdatesPackager;

public final class RendererNetworkManager implements IDataHandler {
	
	private class SceneDataDeserializer implements Runnable {
		
		private byte[] pointOfViewData;
		
		private byte[] lightSourcesData;
		
		private byte[] geometriesData;
		
		public void setPackagesData(byte[] pointOfViewData, byte[] lightSourcesData, byte[] geometriesData) {
			this.pointOfViewData = pointOfViewData;
			this.lightSourcesData = lightSourcesData;
			this.geometriesData = geometriesData;
		}
				
		@Override
		public void run() {
			try {
				rebuildScene(pointOfViewPackager.deserialize(pointOfViewData), lightSourcesPackager.deserialize(lightSourcesData), geometriesPackager.deserialize(geometriesData));
			} catch (IOException e) {
				// TODO:
				throw new RuntimeException("Error deserializing scene data");
			}
		}
	}
	
	private int id = -1;
	
	private SceneDataDeserializer sceneDataDeserializer = new SceneDataDeserializer();
	
	private INonBlockingConnection masterConnection;
	
	//private IBlockingConnection composerConnection;
	
	private UpdatesPackager updatesPackager = new UpdatesPackager();
	
	private PointOfViewPackager pointOfViewPackager = new PointOfViewPackager();
	
	private LightSourcesPackager lightSourcesPackager = new LightSourcesPackager();
	
	private GeometriesPackager geometriesPackager = new GeometriesPackager();
	
	private Thread dataDeserializationThread;
	
	private boolean sessionStarted = false;
	
	private boolean startFrame = false;
	
	private SceneManager sceneManager;
	
	private final Object startFrameLock = new Object();
	
	public RendererNetworkManager(SceneManager sceneManager) {
		this.sceneManager = sceneManager;
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
		sceneManager.setId(id);
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

	public void notifyFrameFinished() {
		try {
			xSocketHelper.write(masterConnection, RendererMessages.FRAME_FINISHED);
		} catch (BufferOverflowException e) {
			// TODO:
			e.printStackTrace();
		} catch (IOException e) {
			// TODO:
			e.printStackTrace();
		}
	}

	@Override
	public boolean onData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		String message;
		byte[] pointOfViewData;
		byte[] lightSourcesData;
		byte[] geometriesData;
		
		try {
			message = xSocketHelper.readString(arg0);
			
			if (ClusterMessages.SESSION_STARTED.equals(message)) {
				sessionStarted = false;
				
				// TODO: Check if this is needed!
				if (dataDeserializationThread != null) {
					dataDeserializationThread.interrupt();
					
					while (dataDeserializationThread.isInterrupted()) {
						try {
							Thread.sleep(100L);
						} catch (InterruptedException e) {
						}
					}
				}
				
				id = xSocketHelper.readInt(arg0);
				pointOfViewData = xSocketHelper.readBytes(arg0);
				lightSourcesData = xSocketHelper.readBytes(arg0);
				geometriesData = xSocketHelper.readBytes(arg0);
				
				// DEBUG:
				System.out.println("Received id: " + id);
				System.out.println("POV data: " + pointOfViewData.length + " bytes received");
				System.out.println("Light sources data: " + lightSourcesData.length + " bytes received");
				System.out.println("Geometries data: " + geometriesData.length + " bytes received");
				
				sceneDataDeserializer.setPackagesData(pointOfViewData, lightSourcesData, geometriesData);
				
				dataDeserializationThread = new Thread(sceneDataDeserializer);
				dataDeserializationThread.start();
				
				setId(id);
				
				// DEBUG:
				System.out.println("Session started successfully");
				
				sessionStarted = true;
			} else if (ClusterMessages.START_FRAME.equals(message)) {
				if (sessionStarted) {
					synchronized (startFrameLock) {
						startFrame = true;
					}
				} else {
					// DEBUG:
					System.err.println("Cannot render frame before starting a session");
				}
			} else if (ClusterMessages.UPDATE.equals(message)) {
				if (sessionStarted) {
					updateScene(updatesPackager.deserialize(xSocketHelper.readBytes(arg0)));
				} else {
					// DEBUG:
					System.err.println("Cannot update scene before starting a session");
				}
			}
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error receiving message");
		}
		
		return true;
	}
}
