package br.edu.univercidade.cc.xithcluster.comm;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.BitSet;
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
import org.xsocket.connection.IDataHandler;
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

public final class MasterNetworkManager implements IConnectHandler, IDataHandler, IDisconnectHandler {
	
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
	
	private BitSet framesFinished = new BitSet();
	
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
		synchronized (renderersConnections) {
			if (framesFinished.cardinality() == renderersConnections.size()) {
				try {
					for (INonBlockingConnection renderer : renderersConnections) {
						xSocketHelper.write(renderer, ClusterMessages.START_FRAME);
					}
				} catch (IOException e) {
					// TODO:
					System.err.println("Error notifying frame start");
					e.printStackTrace();
				}
			}
		}
	}
	
	public synchronized boolean sendPendingUpdates() {
		Map<INonBlockingConnection, List<PendingUpdate>> updatesPerRenderer;
		INonBlockingConnection rendererConnection;
		List<PendingUpdate> updates;
		
		if (updateManager.hasPendingUpdates()) {
			// DEBUG:
			System.out.println("Sending " + updateManager.getPendingUpdates().size() + " pending update(s)");
			
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
			
			synchronized (renderersConnections) {
				for (int i = 0; i < renderersConnections.size(); i++) {
					rendererConnection = renderersConnections.get(i);
					updates = updatesPerRenderer.get(rendererConnection);
					
					if (updates != null) {
						rendererConnection.setAutoflush(false);
						try {
							xSocketHelper.write(rendererConnection, ClusterMessages.UPDATE);
							xSocketHelper.write(rendererConnection, updatesPackager.serialize(updates));
							
							rendererConnection.flush();
							// DEBUG:
							System.out.println(updates.size() + " update(s) were sent to renderer " + (i + 1));
						} catch (IOException e) {
							// TODO:
							System.err.println("Error sending pending updates");
							e.printStackTrace();
							
							return false;
						}
						rendererConnection.setAutoflush(true);
					}
				}
			}
			
			// DEBUG:
			System.out.println("Pending updates sent successfully");
		}
		
		return true;
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
		
		// TODO: Check if this lock is needed!
		synchronized (sceneManager.getSceneLock()) {
			root = sceneManager.getRoot();
			pointOfView = sceneManager.getPointOfView();
			lightSources = sceneManager.getLightSources();
		}
		
		synchronized (renderersConnections) {
			if (renderersConnections.isEmpty()) {
				return true;
			}
			
			// DEBUG:
			System.out.println("Starting a new session");
			
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
				
				// DEBUG:
				System.out.println(">> Renderer " + i);
				
				ConnectionSetter.setConnection(rendererRoot, rendererConnection);
				
				try {
					pointOfViewData = pointOfViewPackager.serialize(pointOfView);
					lightSourcesData = lightSourcesPackager.serialize(lightSources);
					geometriesData = geometriesPackager.serialize(rendererRoot);
				} catch (IOException e) {
					// TODO:
					System.err.println("Error serializing the scene");
					e.printStackTrace();
					
					return false;
				}
				
				// DEBUG:
				System.out.println("POV data: " + pointOfViewData.length + " bytes sent");
				System.out.println("Light sources data: " + lightSourcesData.length + " bytes sent");
				System.out.println("Geometries data: " + geometriesData.length + " bytes sent");
				
				rendererConnection.setAutoflush(false);
				try {
					xSocketHelper.write(rendererConnection, ClusterMessages.SESSION_STARTED);
					xSocketHelper.write(rendererConnection, (Integer) rendererConnection.getAttachment());
					xSocketHelper.write(rendererConnection, pointOfViewData);
					xSocketHelper.write(rendererConnection, lightSourcesData);
					xSocketHelper.write(rendererConnection, geometriesData);
					
					rendererConnection.flush();
				} catch (IOException e) {
					// TODO:
					System.err.println("Error sending distributed scene");
					e.printStackTrace();
					
					return false;
				}
				rendererConnection.setAutoflush(true);
				
				framesFinished.set(i);
			}
		}
		
		if (composerConnection != null) {
			try {
				xSocketHelper.write(composerConnection, ClusterMessages.SESSION_STARTED);
			} catch (IOException e) {
				// TODO:
				System.err.println("Error notifying composer");
				e.printStackTrace();
				
				return false;
			}
		}
		
		changed = false;
		
		// DEBUG:
		System.out.println("New session started successfully");
		
		return true;
	}

	@Override
	public synchronized boolean onConnect(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
		BitSet newMask;
		
		if (isRendererConnection(arg0)) {
			synchronized (renderersConnections) {
				arg0.setAttachment(renderersConnections.size());
				renderersConnections.add(arg0);
				
				newMask = new BitSet(renderersConnections.size());
				newMask.or(framesFinished);
				framesFinished = newMask;
			}
			
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
	
	@Override
	public boolean onData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		synchronized (renderersConnections) {
			framesFinished.set((Integer) arg0.getAttachment());
		}
		
		return true;
	}
	
	@Override
	public boolean onDisconnect(INonBlockingConnection arg0) throws IOException {
		BitSet newMask;
		int i;
		
		if (isRendererConnection(arg0)) {
			synchronized (renderersConnections) {
				renderersConnections.remove(arg0);
				
				i = (Integer) arg0.getAttachment();
				for (int j = i; j < renderersConnections.size(); j++) {
					renderersConnections.get(j).setAttachment(j);
				}
				
				if (i == 0) {
					framesFinished = framesFinished.get(1, framesFinished.size() - 1);
				} else if (i == framesFinished.size() - 1) {
					framesFinished = framesFinished.get(0, framesFinished.size() - 2);
				} else {
					newMask = new BitSet(renderersConnections.size());
					newMask.or(framesFinished.get(0, i - 1));
					
					for (int j = i + 1; j < framesFinished.size(); j++) {
						newMask.set(i++, framesFinished.get(j));
					}
					
					framesFinished = newMask;
				}
			}
			
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
