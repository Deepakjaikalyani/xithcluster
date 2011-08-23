package br.edu.univercidade.cc.xithcluster.comm;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.View;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IConnectHandler;
import org.xsocket.connection.IDisconnectHandler;
import org.xsocket.connection.INonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.Configuration;
import br.edu.univercidade.cc.xithcluster.DistributionStrategy;
import br.edu.univercidade.cc.xithcluster.PendingUpdate;
import br.edu.univercidade.cc.xithcluster.PendingUpdate.Type;
import br.edu.univercidade.cc.xithcluster.SceneManager;
import br.edu.univercidade.cc.xithcluster.UpdateManager;
import br.edu.univercidade.cc.xithcluster.net.xSocketHelper;
import br.edu.univercidade.cc.xithcluster.net.xSocketHelper.xSocketServerThread;
import br.edu.univercidade.cc.xithcluster.serial.pack.GeometriesPackager;
import br.edu.univercidade.cc.xithcluster.serial.pack.LightSourcesPackager;
import br.edu.univercidade.cc.xithcluster.serial.pack.PointOfViewPackager;
import br.edu.univercidade.cc.xithcluster.serial.pack.UpdatesPackager;

public final class MasterNetworkManager implements IConnectHandler, IDisconnectHandler {
	
	private List<xSocketServerThread> serverThreads = new ArrayList<xSocketServerThread>();
	
	private INonBlockingConnection composerConnection;
	
	private List<INonBlockingConnection> renderersConnections = Collections.synchronizedList(new ArrayList<INonBlockingConnection>());
	
	private UpdateManager updateManager;
	
	private DistributionStrategy distributionStrategy;
	
	private UpdatesPackager updatesPackager = new UpdatesPackager();
	
	private PointOfViewPackager pointOfViewPackager = new PointOfViewPackager();
	
	private LightSourcesPackager lightSourcesPackager = new LightSourcesPackager();
	
	private GeometriesPackager geometriesPackager = new GeometriesPackager();
	
	private SceneManager sceneManager;
	
	private boolean changed = true;
	
	public MasterNetworkManager(SceneManager sceneManager, UpdateManager updateManager, DistributionStrategy distributionStrategy) {
		this.sceneManager = sceneManager;
		this.updateManager = updateManager;
		this.distributionStrategy = distributionStrategy;
	}
	
	public void initialize() throws UnknownHostException, IOException {
		serverThreads.add(xSocketHelper.startListening(Configuration.listeningInterface, Configuration.renderersListeningPort, this));
		serverThreads.add(xSocketHelper.startListening(Configuration.listeningInterface, Configuration.composerListeningPort, this));
	}
	
	@Override
	public synchronized boolean onConnect(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
		if (isRendererConnection(arg0)) {
			renderersConnections.add(arg0);
			
			// DEBUG:
			System.out.println("New renderer connected");
		} else {
			if (composerConnection != null) {
				// DEBUG:
				System.err.println("There can be only one composer");
				
				return false;
			}
			
			composerConnection = arg0;
			
			// DEBUG:
			System.out.println("Composer connected");
		}
		
		changed = true;
		
		return true;
	}
	
	private boolean isRendererConnection(INonBlockingConnection arg0) {
		return arg0.getLocalPort() == Configuration.renderersListeningPort;
	}
	
	public synchronized boolean hasChanged() {
		return changed;
	}
	
	public synchronized int getSkipNextFrames() {
		int framesToSkip = 0;
		
		if (composerConnection != null) {
			try {
				xSocketHelper.write(composerConnection, ClusterMessages.GET_FRAMES_TO_SKIP);
				framesToSkip = xSocketHelper.readInt(composerConnection);
			} catch (BufferOverflowException e) {
				// TODO:
			} catch (IOException e) {
				// TODO:
			}
		}
		
		return framesToSkip;
	}
	
	public synchronized void notifyFrameStart() {
		try {
			notifyRenderers(ClusterMessages.START_FRAME);
		} catch (IOException e) {
			// TODO:
		}
	}
	
	private void notifyRenderers(String message) throws IOException {
		for (INonBlockingConnection renderer : renderersConnections) {
			xSocketHelper.write(renderer, message);
		}
	}
	
	public synchronized boolean sendPendingUpdates() {
		Map<INonBlockingConnection, List<PendingUpdate>> updatesPerRenderer;
		INonBlockingConnection rendererConnection;
		List<PendingUpdate> updates;
		
		if (updateManager.hasPendingUpdates()) {
			// DEBUG:
			System.out.println(updateManager.getPendingUpdates().size() + " pending update(s)");
			
			// FIXME: Optimize
			updatesPerRenderer = new HashMap<INonBlockingConnection, List<PendingUpdate>>();
			for (PendingUpdate pendingUpdate : updateManager.getPendingUpdates()) {
				// TODO:
				if (pendingUpdate.getType() == Type.NODE_ADDED || pendingUpdate.getType() == Type.NODE_REMOVED) {
					rendererConnection = (INonBlockingConnection) ((Node) pendingUpdate.getTarget()).getUserData(ConnectionSetter.CONNECTION_USER_DATA);
				} else {
					rendererConnection = null;
				}
				
				if (rendererConnection != null) {
					updates = updatesPerRenderer.get(rendererConnection);
					
					if (updates == null) {
						updates = new ArrayList<PendingUpdate>();
						updatesPerRenderer.put(rendererConnection, updates);
					}
					
					updates.add(pendingUpdate);
				}
			}
			
			for (int i = 0; i < renderersConnections.size(); i++) {
				rendererConnection = renderersConnections.get(i);
				updates = updatesPerRenderer.get(rendererConnection);
				
				if (updates != null) {
					try {
						xSocketHelper.write(rendererConnection, ClusterMessages.UPDATE);
						xSocketHelper.write(rendererConnection, updatesPackager.serialize(updates));
						
						// DEBUG:
						System.out.println(updates.size() + " update(s) were sent to renderer " + (i + 1));
					} catch (IOException e) {
						// TODO:
						return false;
					} catch (BufferOverflowException e) {
						// TODO:
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	private synchronized void notifySessionStart() {
		try {
			notifyRenderers(ClusterMessages.SESSION_STARTED);
			
			if (composerConnection != null) {
				xSocketHelper.write(composerConnection, ClusterMessages.SESSION_STARTED);
			}
		} catch (IOException e) {
			// TODO:
		}
	}
	
	public synchronized boolean startNewSession() {
		View pointOfView;
		List<Light> lightSources;
		List<BranchGroup> geometries;
		INonBlockingConnection rendererConnection;
		BranchGroup root;
		BranchGroup rendererRoot;
		byte[] pointOfViewData;
		byte[] lightSourcesData;
		byte[] geometriesData;
		
		if (renderersConnections.isEmpty()) {
			return true;
		}
		
		// DEBUG:
		System.out.println("Starting a new session");
		
		notifySessionStart();
		
		synchronized (sceneManager.getSceneLock()) {
			root = sceneManager.getRoot();
			pointOfView = sceneManager.getPointOfView();
			lightSources = sceneManager.getLightSources();
		}
		
		// DEBUG:
		System.out.println("Executing " + distributionStrategy.getClass().getSimpleName() + "...");
		
		geometries = distributionStrategy.distribute(root, renderersConnections.size());
		
		if (geometries.size() != renderersConnections.size()) {
			// TODO:
			throw new RuntimeException("The number of distributions is not the same as the number of renderers");
		}
		
		for (int i = 0; i < renderersConnections.size(); i++) {
			rendererConnection = renderersConnections.get(i);
			rendererRoot = geometries.get(i);
			
			System.out.println(">> Renderer " + i);
			
			ConnectionSetter.setConnection(rendererRoot, rendererConnection);
			
			try {
				pointOfViewData = pointOfViewPackager.serialize(pointOfView);
				lightSourcesData = lightSourcesPackager.serialize(lightSources);
				geometriesData = geometriesPackager.serialize(rendererRoot);
				
				// DEBUG:
				System.out.println("POV data: " + pointOfViewData.length + " bytes sent");
				System.out.println("Light sources data: " + lightSourcesData.length + " bytes sent");
				System.out.println("Geometries data: " + geometriesData.length + " bytes sent");
				
				xSocketHelper.write(rendererConnection, pointOfViewData);
				xSocketHelper.write(rendererConnection, lightSourcesData);
				xSocketHelper.write(rendererConnection, geometriesData);
			} catch (IOException e) {
				// TODO:
				System.err.println("Error sending distributed scene");
				
				return false;
			} catch (BufferOverflowException e) {
				// TODO:
				System.err.println("Error sending distributed scene");
				
				return false;
			}
		}
		
		changed = false;
		
		// DEBUG:
		System.out.println("New session started successfully");
		
		return true;
	}
	
	@Override
	public boolean onDisconnect(INonBlockingConnection arg0) throws IOException {
		if (isRendererConnection(arg0)) {
			renderersConnections.remove(arg0);
			// DEBUG:
			System.err.println("Renderer disconnected");
		} else {
			composerConnection = null;
			
			// DEBUG:
			System.err.println("Composer disconnected");
		}
		
		changed = true;
		
		return true;
	}
	
}
