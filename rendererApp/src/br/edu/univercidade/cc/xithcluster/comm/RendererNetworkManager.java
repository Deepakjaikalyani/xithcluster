package br.edu.univercidade.cc.xithcluster.comm;

import java.io.IOException;
import java.util.List;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.View;
import org.xsocket.connection.BlockingConnection;
import org.xsocket.connection.IBlockingConnection;
import br.edu.univercidade.cc.xithcluster.PendingUpdate;
import br.edu.univercidade.cc.xithcluster.RendererConfiguration;
import br.edu.univercidade.cc.xithcluster.SceneManager;
import br.edu.univercidade.cc.xithcluster.net.xSocketHelper;
import br.edu.univercidade.cc.xithcluster.serial.pack.GeometriesPackager;
import br.edu.univercidade.cc.xithcluster.serial.pack.LightSourcesPackager;
import br.edu.univercidade.cc.xithcluster.serial.pack.PointOfViewPackager;
import br.edu.univercidade.cc.xithcluster.serial.pack.UpdatesPackager;
import br.edu.univercidade.cc.xithcluster.sync.SimpleWorker;

public final class RendererNetworkManager {
	
	private class SceneDataDeserializer extends SimpleWorker {
		
		private byte[] pointOfViewData;
		
		private byte[] lightSourcesData;
		
		private byte[] geometriesData;
		
		public void setPackagesData(byte[] pointOfViewData, byte[] lightSourcesData, byte[] geometriesData) {
			this.pointOfViewData = pointOfViewData;
			this.lightSourcesData = lightSourcesData;
			this.geometriesData = geometriesData;
		}
				
		protected void beforeWork() {
			// DEBUG:
			System.out.println("Scene deserialization started");
		}

		@Override
		protected void doWork() {
			try {
				rebuildScene(pointOfViewPackager.deserialize(pointOfViewData), lightSourcesPackager.deserialize(lightSourcesData), geometriesPackager.deserialize(geometriesData));
			} catch (IOException e) {
				// TODO:
				throw new RuntimeException("Error deserializing scene data");
			}
		}

		@Override
		protected void afterWork() {
			// DEBUG:
			System.out.println("Scene deserialization finished");
		}
	}
	
	private SceneDataDeserializer sceneDataDeserializer = new SceneDataDeserializer();
	
	private SimpleWorker connectionPollingWorker = new SimpleWorker() {
		
		@Override
		protected void beforeWork() {
			setInterval(100L);
		}
		
		@Override
		protected void doWork() {
			String message;
			byte[] pointOfViewData;
			byte[] lightSourcesData;
			byte[] geometriesData;
			
			try {
				message = xSocketHelper.readString(masterConnection);
				
				if (ClusterMessages.SESSION_STARTED.equals(message)) {
					sessionStarted = false;
					
					if (dataDeserializationThread != null) {
						dataDeserializationThread.interrupt();
						
						while (dataDeserializationThread.isInterrupted()) {
							try {
								Thread.sleep(100L);
							} catch (InterruptedException e) {
							}
						}
					}
					
					pointOfViewData = xSocketHelper.readBytes(masterConnection);
					lightSourcesData = xSocketHelper.readBytes(masterConnection);
					geometriesData = xSocketHelper.readBytes(masterConnection);
					
					// DEBUG:
					System.out.println("POV data: " + pointOfViewData.length + " bytes received");
					System.out.println("Light sources data: " + lightSourcesData.length + " bytes received");
					System.out.println("Geometries data: " + geometriesData.length + " bytes received");
					
					sceneDataDeserializer.setPackagesData(pointOfViewData, lightSourcesData, geometriesData);
					
					dataDeserializationThread = new Thread(sceneDataDeserializer);
					dataDeserializationThread.start();
					
					// DEBUG:
					System.out.println("Session started successfully");
					
					sessionStarted = true;
					
				} else if (ClusterMessages.START_FRAME.equals(message)) {
					if (sessionStarted) {
						synchronized (stateLock) {
							newFrameStarted = true;
						}
					} else {
						// DEBUG:
						System.err.println("Cannot render frame before starting a session");
					}
				} else if (ClusterMessages.UPDATE.equals(message)) {
					if (sessionStarted) {
						updateScene(updatesPackager.deserialize(xSocketHelper.readBytes(masterConnection)));
					} else {
						// DEBUG:
						System.err.println("Cannot update scene before starting a session");
					}
				}
			} catch (IOException e) {
				// TODO:
				throw new RuntimeException("Error receiving message");
			}
		}
		
	};
	
	private IBlockingConnection masterConnection;
	
	//private IBlockingConnection composerConnection;
	
	private UpdatesPackager updatesPackager = new UpdatesPackager();
	
	private PointOfViewPackager pointOfViewPackager = new PointOfViewPackager();
	
	private LightSourcesPackager lightSourcesPackager = new LightSourcesPackager();
	
	private GeometriesPackager geometriesPackager = new GeometriesPackager();
	
	private Thread dataDeserializationThread;
	
	private Thread connectionPollingThread;
	
	private boolean sessionStarted = false;
	
	private boolean newFrameStarted = false;
	
	private SceneManager sceneManager;
	
	private final Object stateLock = new Object();
	
	public RendererNetworkManager(SceneManager sceneManager) {
		this.sceneManager = sceneManager;
	}
	
	public void initialize() {
		try {
			masterConnection = new BlockingConnection(RendererConfiguration.masterHostname, RendererConfiguration.masterPort);
			//composerConnection = new BlockingConnection(RendererConfiguration.composerHostname, RendererConfiguration.composerPort);
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error trying to connecting to the cluster", e);
		}
		
		connectionPollingThread = new Thread(connectionPollingWorker);
		connectionPollingThread.start();
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
		synchronized (stateLock) {
		}
	}
	
	public boolean renderNewFrame() {
		boolean param0;
		
		synchronized (stateLock) {
			param0 = newFrameStarted;
			newFrameStarted = false;
		}
		
		return param0;
	}
}
