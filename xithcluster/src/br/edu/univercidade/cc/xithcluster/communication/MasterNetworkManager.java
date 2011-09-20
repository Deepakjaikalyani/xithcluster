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
import br.edu.univercidade.cc.xithcluster.serialization.packagers.PointOfViewPackager;
import br.edu.univercidade.cc.xithcluster.serialization.packagers.ScenePackager;
import br.edu.univercidade.cc.xithcluster.serialization.packagers.UpdatesPackager;

public final class MasterNetworkManager {
	
	private Logger log = Logger.getLogger(MasterNetworkManager.class);
	
	private List<INonBlockingConnection> renderersConnections = Collections.synchronizedList(new ArrayList<INonBlockingConnection>());
	
	private final UpdatesPackager updatesPackager = new UpdatesPackager();
	
	private final PointOfViewPackager pointOfViewPackager = new PointOfViewPackager();
	
	private final ScenePackager scenePackager = new ScenePackager();
	
	private final DistributedRenderLoop distributedRenderLoop;
	
	private final UpdateManager updateManager;
	
	private DistributionStrategy distributionStrategy;
	
	private final MasterProtocolHandler masterProtocolHandler;
	
	private IServer composerServer;
	
	private IServer renderersServer;
	
	private INonBlockingConnection composerConnection;
	
	private boolean sessionStarted = false;
	
	private boolean sessionStarting = false;
	
	private boolean composerSessionStarted = false; 
	
	private final BitSet renderersSessionStartedMask = new BitSet();
	
	private boolean finishedFrame = false;
	
	private int currentFrameIndex = 0;
	
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
		
		// TODO:
		/*
		 * if (composerConnection != null) { try {
		 * masterProtocolHandler.sendGetFramesToSkipMessage(composerConnection);
		 * // TODO: Implement async read! } catch (IOException e) {
		 * log.error("Error getting frames to skip", e); } }
		 */

		return framesToSkip;
	}
	
	public synchronized void notifyFrameStart(int frameIndex) {
		if (sessionStarted && finishedFrame) {
			internalNotifyFrameStart(frameIndex);
		}
	}
	
	private void internalNotifyFrameStart(int frameIndex) {
		Iterator<INonBlockingConnection> i;
		
		try {
			masterProtocolHandler.sendStartFrameMessage(composerConnection, frameIndex);
			
			synchronized (renderersConnections) {
				i = renderersConnections.iterator();
				while (i.hasNext()) {
					masterProtocolHandler.sendStartFrameMessage(i.next(), frameIndex);
				}
			}
			
			currentFrameIndex = frameIndex;
			finishedFrame = false;
		} catch (IOException e) {
			log.error("Error sending frame start notification: " + frameIndex, e);
		}
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
	
	public synchronized void startNewSession() {
		BranchGroup scene;
		View pointOfView;
		List<BranchGroup> distributedScenes;
		Iterator<INonBlockingConnection> i;
		INonBlockingConnection rendererConnection;
		BranchGroup rendererScene;
		byte[] pointOfViewData;
		byte[] sceneData;
		int rendererIndex;
		
		if (sessionStarted || sessionStarting || !isThereAtLeastOneRendererAndOneComposer()) {
			return;
		}
		
		sessionStarting = true;
		
		synchronized (distributedRenderLoop.getSceneLock()) {
			scene = distributedRenderLoop.getScene();
			pointOfView = distributedRenderLoop.getPointOfView();
		}
		
		log.info("Starting a new session");
		log.info("Executing " + distributionStrategy.getClass().getSimpleName() + "...");
		
		distributedScenes = distributionStrategy.distribute(scene, renderersConnections.size());
		
		if (distributedScenes.size() != renderersConnections.size()) {
			// TODO:
			throw new RuntimeException("The number of distributions is not the same as the number of renderers");
		}
		
		synchronized (renderersConnections) {
			i = renderersConnections.iterator();
			while (i.hasNext()) {
				rendererConnection = i.next();
				rendererIndex = getRendererIndex(rendererConnection);
				rendererScene = distributedScenes.get(rendererIndex);
				
				log.debug("**************");
				log.debug("Renderer " + rendererIndex);
				log.debug("**************");
				
				ConnectionSetter.setConnection(rendererScene, rendererConnection);
				
				try {
					pointOfViewData = pointOfViewPackager.serialize(pointOfView);
					sceneData = scenePackager.serialize(rendererScene);
				} catch (IOException e) {
					log.error("Error serializing the scene", e);
					sessionStarted = false;
					return;
				}
				
				log.trace("POV data size: " + pointOfViewData.length + " bytes");
				log.trace("Scene data size: " + sceneData.length + " bytes");
				
				try {
					masterProtocolHandler.sendStartSessionMessage(
							rendererConnection, 
							getRendererIndex(rendererConnection),
							XithClusterConfiguration.screenWidth, 
							XithClusterConfiguration.screenHeight,
							XithClusterConfiguration.targetFPS,
							pointOfViewData, 
							sceneData);
				} catch (IOException e) {
					log.error("Error sending distributed scene", e);
					sessionStarted = false;
					return;
				}
			}
		}
		
		if (composerConnection != null) {
			try {
				masterProtocolHandler.sendStartSessionMessage(
						composerConnection, 
						XithClusterConfiguration.screenWidth, 
						XithClusterConfiguration.screenHeight,
						XithClusterConfiguration.targetFPS);
			} catch (IOException e) {
				log.error("Error notifying composer", e);
				sessionStarted = false;
				return;
			}
		}
	}
	
	public synchronized void closeCurrentSession() {
		sessionStarting = false;
		sessionStarted = false;
		
		composerSessionStarted = false;
		synchronized (renderersSessionStartedMask) {
			renderersSessionStartedMask.clear();
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
	
	public boolean onConnected(INonBlockingConnection arg0) {
		if (isRendererConnection(arg0)) {
			return onRendererConnected(arg0);
		} else if (isComposerConnection(arg0)) {
			return onComposerConnected(arg0);
		} else {
			log.error("Unknown connection refused");
			
			return false;
		}
	}
	
	private synchronized boolean onRendererConnected(INonBlockingConnection arg0) {
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
	
	private synchronized boolean onComposerConnected(INonBlockingConnection arg0) {
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
	
	private boolean isRendererConnection(INonBlockingConnection arg0) {
		return arg0.getLocalPort() == XithClusterConfiguration.renderersConnectionPort;
	}
	
	private boolean isComposerConnection(INonBlockingConnection arg0) {
		return arg0.getLocalPort() == XithClusterConfiguration.composerConnectionPort;
	}
	
	public synchronized boolean onSessionStarted(INonBlockingConnection arg0) throws IOException {
		if (isRendererConnection(arg0)) {
			renderersSessionStartedMask.set(getRendererIndex(arg0));
			
			evaluateSessionStart();
			
			return true;
		} else if (isComposerConnection(arg0)) {
			composerSessionStarted = true;
			
			evaluateSessionStart();
			
			return true;
		} else {
			return false;
		}
	}

	private void evaluateSessionStart() throws IOException {
		if (!sessionStarting || sessionStarted) {
			return;
		}
		
		if (composerSessionStarted && renderersSessionStartedMask.cardinality() == renderersConnections.size()) {
			sessionStarting = false;
			sessionStarted = true;
			
			log.info("Session started successfully");
			
			internalNotifyFrameStart(currentFrameIndex);
		}
	}
	
	public synchronized boolean onFinishedFrame(int frameIndex) {
		finishedFrame = (currentFrameIndex == frameIndex);
		
		return true;
	}
	
	public boolean onDisconnected(INonBlockingConnection arg0) {
		if (isRendererConnection(arg0)) {
			return onRendererDisconnect(arg0);
		} else if (isComposerConnection(arg0)) {
			return onComposerDisconnect();
		} else {
			throw new AssertionError("Should never happen!");
		}
	}

	
	private synchronized boolean onComposerDisconnect() {
		finishedFrame = false;
		
		composerConnection = null;
		
		log.info("Composer disconnected");
		
		closeCurrentSession();
		
		return true;
	}
	
	private synchronized boolean onRendererDisconnect(INonBlockingConnection arg0) {
		int rendererIndex;
		
		rendererIndex = getRendererIndex(arg0);
		
		finishedFrame = true;
		renderersSessionStartedMask.clear(rendererIndex);
		
		renderersConnections.remove(rendererIndex);
		synchronized (renderersConnections) {
			for (int j = rendererIndex; j < renderersConnections.size(); j++) {
				renderersConnections.get(j).setAttachment(j);
			}
		}
		
		log.info("Renderer disconnected");
		
		closeCurrentSession();
		
		return true;
	}

}