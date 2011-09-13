package br.edu.univercidade.cc.xithcluster.communication;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.View;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.IServer;
import org.xsocket.connection.Server;

import br.edu.univercidade.cc.xithcluster.DistributedRenderLoop;
import br.edu.univercidade.cc.xithcluster.DistributionStrategy;
import br.edu.univercidade.cc.xithcluster.PendingUpdate;
import br.edu.univercidade.cc.xithcluster.PendingUpdate.Type;
import br.edu.univercidade.cc.xithcluster.UpdateManager;
import br.edu.univercidade.cc.xithcluster.XithClusterConfiguration;
import br.edu.univercidade.cc.xithcluster.communication.protocol.MasterProtocolHandler;
import br.edu.univercidade.cc.xithcluster.serialization.packagers.GeometriesPackager;
import br.edu.univercidade.cc.xithcluster.serialization.packagers.LightSourcesPackager;
import br.edu.univercidade.cc.xithcluster.serialization.packagers.PointOfViewPackager;
import br.edu.univercidade.cc.xithcluster.serialization.packagers.UpdatesPackager;

public final class MasterNetworkManager {
	
	private Logger log = Logger.getLogger(MasterNetworkManager.class);
	
	private List<INonBlockingConnection> renderersConnections = Collections.synchronizedList(new ArrayList<INonBlockingConnection>());
	
	private final UpdatesPackager updatesPackager = new UpdatesPackager();
	
	private final PointOfViewPackager pointOfViewPackager = new PointOfViewPackager();
	
	private final LightSourcesPackager lightSourcesPackager = new LightSourcesPackager();
	
	private final GeometriesPackager geometriesPackager = new GeometriesPackager();
	
	private final DistributedRenderLoop distributedRenderLoop;
	
	private final UpdateManager updateManager;
	
	private DistributionStrategy distributionStrategy;
	
	private final MasterProtocolHandler masterProtocolHandler;
	
	private IServer composerServer;
	
	private IServer renderersServer;
	
	private INonBlockingConnection composerConnection;
	
	private boolean sessionStarted = false;
	
	private boolean sessionStarting = false;
	
	private final BitSet framesFinishedMask = new BitSet();
	
	private final BitSet sessionStartedMask = new BitSet();
	
	public MasterNetworkManager(DistributedRenderLoop distributedRenderLoop, UpdateManager updateManager, DistributionStrategy distributionStrategy) {
		this.distributedRenderLoop = distributedRenderLoop;
		this.updateManager = updateManager;
		this.distributionStrategy = distributionStrategy;
		this.masterProtocolHandler = new MasterProtocolHandler(this);
	}
	
	public void initialize() throws UnknownHostException, IOException {
		renderersServer = new Server(XithClusterConfiguration.listeningAddress, XithClusterConfiguration.renderersConnectionPort, masterProtocolHandler);
		composerServer = new Server(XithClusterConfiguration.listeningAddress, XithClusterConfiguration.composerConnectionPort, masterProtocolHandler);
		
		renderersServer.start();
		composerServer.start();
	}
	
	public boolean isSessionStarted() {
		return sessionStarted;
	}
	
	public synchronized int getSkipNextFrames() {
		int framesToSkip = 0;
		
		if (composerConnection != null) {
			try {
				masterProtocolHandler.sendGetFramesToSkipMessage(composerConnection);
				// TODO: Implement async read!
			} catch (IOException e) {
				log.error("Error getting frames to skip", e);
			}
		}
		
		return framesToSkip;
	}
	
	public synchronized void notifyFrameStart() {
		if (framesFinishedMask.cardinality() == renderersConnections.size()) {
			internalNotifyFrameStart();
		}
	}
	
	private void internalNotifyFrameStart() {
		Iterator<INonBlockingConnection> i;
		
		synchronized (renderersConnections) {
			try {
				i = renderersConnections.iterator();
				while (i.hasNext()) {
					masterProtocolHandler.sendStartFrameMessage(i.next());
				}
			} catch (IOException e) {
				log.error("Error notifying frame start", e);
			}
		}
		
		framesFinishedMask.clear();
	}
	
	public synchronized boolean sendPendingUpdates() {
		Map<INonBlockingConnection, List<PendingUpdate>> updatesPerRenderer;
		Iterator<INonBlockingConnection> i;
		INonBlockingConnection rendererConnection;
		List<PendingUpdate> updates;
		
		if (updateManager.hasPendingUpdates()) {
			log.info("Sending " + updateManager.getPendingUpdates().size() + " pending update(s)");
			
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
				i = renderersConnections.iterator();
				while (i.hasNext()) {
					rendererConnection = i.next();
					updates = updatesPerRenderer.get(rendererConnection);
					
					if (updates != null) {
						try {
							masterProtocolHandler.sendUpdateMessage(rendererConnection, updatesPackager.serialize(updates));
							
							log.info(updates.size() + " update(s) were sent to renderer " + getRendererIndex(rendererConnection));
						} catch (IOException e) {
							log.error("Error sending pending updates", e);
							
							return false;
						}
					}
				}
			}
			
			log.info("Pending updates sent successfully");
		}
		
		return true;
	}
	
	public synchronized boolean startNewSession() {
		View pointOfView;
		List<Light> lightSources;
		List<BranchGroup> geometries;
		Iterator<INonBlockingConnection> i;
		INonBlockingConnection rendererConnection;
		BranchGroup root;
		BranchGroup rootOfARenderer;
		byte[] pointOfViewData;
		byte[] lightSourcesData;
		byte[] geometriesData;
		int rendererIndex;
		
		if (sessionStarted || sessionStarting || !isThereAtLeastOneRendererAndOneComposer()) {
			return true;
		}
		
		sessionStarting = true;
		
		synchronized (distributedRenderLoop.getSceneLock()) {
			root = distributedRenderLoop.getRoot();
			pointOfView = distributedRenderLoop.getPointOfView();
			lightSources = distributedRenderLoop.getLightSources();
		}
		
		log.info("Starting a new session");
		log.info("Executing " + distributionStrategy.getClass().getSimpleName() + "...");
		
		geometries = distributionStrategy.distribute(root, renderersConnections.size());
		
		if (geometries.size() != renderersConnections.size()) {
			// TODO:
			throw new RuntimeException("The number of distributions is not the same as the number of renderers");
		}
		
		synchronized (renderersConnections) {
			i = renderersConnections.iterator();
			while (i.hasNext()) {
				rendererConnection = i.next();
				rendererIndex = getRendererIndex(rendererConnection);
				rootOfARenderer = geometries.get(rendererIndex);
				
				log.debug("**************");
				log.debug("Renderer " + rendererIndex);
				log.debug("**************");
				
				ConnectionSetter.setConnection(rootOfARenderer, rendererConnection);
				
				try {
					pointOfViewData = pointOfViewPackager.serialize(pointOfView);
					lightSourcesData = lightSourcesPackager.serialize(lightSources);
					geometriesData = geometriesPackager.serialize(rootOfARenderer);
				} catch (IOException e) {
					log.error("Error serializing the scene", e);
					
					return false;
				}
				
				log.trace("POV data size: " + pointOfViewData.length + " bytes");
				log.trace("Light sources data size: " + lightSourcesData.length + " bytes");
				log.trace("Geometries data size: " + geometriesData.length + " bytes");
				
				try {
					masterProtocolHandler.sendStartSessionMessage(rendererConnection, 
							getRendererIndex(rendererConnection),
							composerConnection.getRemoteAddress().getHostAddress(),
							XithClusterConfiguration.composerConnectionPort,
							pointOfViewData, 
							lightSourcesData, 
							geometriesData);
				} catch (IOException e) {
					log.error("Error sending distributed scene", e);
					
					return false;
				}
			}
		}
		
		if (composerConnection != null) {
			try {
				masterProtocolHandler.sendStartSessionMessage(composerConnection, 
						XithClusterConfiguration.screenWidth, 
						XithClusterConfiguration.screenHeight);
			} catch (IOException e) {
				log.error("Error notifying composer", e);
				
				return false;
			}
		}
		
		return true;
	}
	
	public synchronized void closeCurrentSession() {
		sessionStarted = false;
		
		synchronized (sessionStartedMask) {
			sessionStartedMask.clear();
		}
	}

	private int getRendererIndex(INonBlockingConnection rendererConnection) {
		Integer rendererId;
		
		rendererId = (Integer) rendererConnection.getAttachment();
		
		return rendererId.intValue();
	}
	
	private void setRendererIndex(INonBlockingConnection arg0) {
		arg0.setAttachment(renderersConnections.size());
	}

	private boolean isThereAtLeastOneRendererAndOneComposer() {
		return !renderersConnections.isEmpty() && composerConnection != null;
	}

	private boolean isThereAlreadyAConnectedComposer() {
		return composerConnection != null;
	}

	public synchronized boolean onRendererConnected(INonBlockingConnection arg0) {
		INonBlockingConnection rendererConnection;
		
		rendererConnection = arg0;
		
		synchronized (renderersConnections) {
			setRendererIndex(rendererConnection);
			renderersConnections.add(rendererConnection);
		}
		
		rendererConnection.setAutoflush(false);
		
		log.info("New renderer connected");
		
		closeCurrentSession();
		
		return true;
	}
	
	public synchronized boolean onComposerConnected(INonBlockingConnection arg0) {
		if (!isThereAlreadyAConnectedComposer()) {
			composerConnection = arg0;
			
			composerConnection.setAutoflush(false);
			
			log.info("Composer connected");
			
			closeCurrentSession();
			
			return true;
		} else {
			log.error("There can be only one composer");
			
			return false;
		}
	}
	
	public synchronized boolean onSessionStarted(INonBlockingConnection arg0) {
		sessionStartedMask.set(getRendererIndex(arg0));
		
		if (sessionStartedMask.cardinality() == renderersConnections.size()) {
			sessionStartedMask.clear();
			
			sessionStarting = false;
			sessionStarted = true;
			
			log.info("Session started successfully");
			
			internalNotifyFrameStart();
		}
		
		return true;
	}

	public synchronized boolean onFrameFinished(INonBlockingConnection arg0) {
		framesFinishedMask.set(getRendererIndex(arg0));
		
		return true;
	}
	
	public synchronized boolean onComposerDisconnect() {
		composerConnection = null;
		
		log.info("Composer disconnected");
		
		closeCurrentSession();
		
		return true;
	}

	public synchronized boolean onRendererDisconnect(INonBlockingConnection arg0) {
		framesFinishedMask.clear(getRendererIndex(arg0));
		sessionStartedMask.clear(getRendererIndex(arg0));
		
		renderersConnections.remove(arg0);
		synchronized (renderersConnections) {
			for (int j = getRendererIndex(arg0); j < renderersConnections.size(); j++) {
				renderersConnections.get(j).setAttachment(j);
			}
		}
		
		log.info("Renderer disconnected");
		
		closeCurrentSession();
		
		return true;
	}
	
}
