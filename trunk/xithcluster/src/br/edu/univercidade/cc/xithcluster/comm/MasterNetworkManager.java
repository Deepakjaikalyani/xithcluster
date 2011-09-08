package br.edu.univercidade.cc.xithcluster.comm;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
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
	
	private enum SessionState {
		
		NOT_STARTED,
		STARTING,
		STARTED
		
	}
	
	private Logger log = Logger.getLogger(MasterNetworkManager.class);
	
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
	
	private SessionState sessionState;
	
	private final Object lock1 = new Object();
	
	private final Object lock2 = new Object();
	
	private int framesFinishedNotificationCounter = 0;
	
	private int sessionStartedNotificationCounter = 0;
	
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
	
	private boolean isComposerConnection(INonBlockingConnection arg0) {
		return arg0.getLocalPort() == Configuration.composerListeningPort;
	}
	
	public boolean hasStartedSession() {
		return sessionState == SessionState.STARTED;
	}
	
	public int getSkipNextFrames() {
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
	
	public void notifyFrameStart() {
		notifyFrameStart(true);
	}
	
	public void notifyFrameStart(boolean checkIfAllFrameNotificationsWereReceived) {
		if (!checkIfAllFrameNotificationsWereReceived || areAllFrameNotificationsReceived()) {
			synchronized (renderersConnections) {
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
			
			framesFinishedNotificationCounter = 0;
		}
	}
	
	public boolean sendPendingUpdates() {
		Map<INonBlockingConnection, List<PendingUpdate>> updatesPerRenderer;
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
				for (int i = 0; i < renderersConnections.size(); i++) {
					rendererConnection = renderersConnections.get(i);
					updates = updatesPerRenderer.get(rendererConnection);
					
					if (updates != null) {
						rendererConnection.setAutoflush(false);
						try {
							xSocketHelper.write(rendererConnection, ClusterMessages.UPDATE);
							xSocketHelper.write(rendererConnection, updatesPackager.serialize(updates));
							
							rendererConnection.flush();
							log.info(updates.size() + " update(s) were sent to renderer " + (i + 1));
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
			
			log.info("Pending updates sent successfully");
		}
		
		return true;
	}
	
	public boolean startNewSession() {
		View pointOfView;
		List<Light> lightSources;
		List<BranchGroup> geometries;
		INonBlockingConnection rendererConnection;
		BranchGroup root;
		BranchGroup rootOfARenderer;
		byte[] pointOfViewData;
		byte[] lightSourcesData;
		byte[] geometriesData;
		
		if (sessionState == SessionState.STARTING) {
			return true;
		}
		
		if (!isThereAtLeastOneRendererAndOneComposer()) {
			return true;
		}
		
		sessionState = SessionState.STARTING;
		
		// TODO: Check if this lock is needed!
		synchronized (sceneManager.getSceneLock()) {
			root = sceneManager.getRoot();
			pointOfView = sceneManager.getPointOfView();
			lightSources = sceneManager.getLightSources();
		}
		
		synchronized (renderersConnections) {
			log.info("Starting a new session");
			log.info("Executing " + distributionStrategy.getClass().getSimpleName() + "...");
			
			geometries = distributionStrategy.distribute(root, renderersConnections.size());
			
			if (geometries.size() != renderersConnections.size()) {
				// TODO:
				throw new RuntimeException("The number of distributions is not the same as the number of renderers");
			}
			
			for (int i = 0; i < renderersConnections.size(); i++) {
				rendererConnection = renderersConnections.get(i);
				rootOfARenderer = geometries.get(i);
				
				log.debug("**************");
				log.debug("Renderer " + i);
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
				
				rendererConnection.setAutoflush(false);
				try {
					xSocketHelper.write(rendererConnection, ClusterMessages.SESSION_STARTED);
					xSocketHelper.write(rendererConnection, getRendererIndex(rendererConnection));
					xSocketHelper.write(rendererConnection, pointOfViewData);
					xSocketHelper.write(rendererConnection, lightSourcesData);
					xSocketHelper.write(rendererConnection, geometriesData);
					
					rendererConnection.flush();
				} catch (IOException e) {
					log.error("Error sending distributed scene", e);
					
					return false;
				}
				rendererConnection.setAutoflush(true);
			}
		}
		
		if (composerConnection != null) {
			try {
				xSocketHelper.write(composerConnection, ClusterMessages.SESSION_STARTED);
			} catch (IOException e) {
				log.error("Error notifying composer", e);
				
				return false;
			}
		}
		
		return true;
	}

	private void sessionStartedSuccessfully() {
		sessionState = SessionState.STARTED;
		
		log.info("Session started successfully");
	}

	private int getRendererIndex(INonBlockingConnection rendererConnection) {
		Integer rendererId;
		
		rendererId = (Integer) rendererConnection.getAttachment();
		
		return rendererId.intValue();
	}

	private boolean isThereAtLeastOneRendererAndOneComposer() {
		synchronized (renderersConnections) {
			if (renderersConnections.isEmpty()) {
				return false;
			}
		}
		
		// TODO: Composer
		/*if (composerConnection == null) {
			return false;
		}*/
		
		return true;
	}

	@Override
	public boolean onConnect(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
		if (isRendererConnection(arg0)) {
			return onRendererConnected(arg0);
		} else if (isComposerConnection(arg0)) {
			return onComposerConnected(arg0);
		} else {
			log.error("Unknown connection refused");
			
			return false;
		}
	}

	private boolean onRendererConnected(INonBlockingConnection arg0) {
		addRenderer(arg0);
		
		log.info("New renderer connected");
		
		sessionState = SessionState.NOT_STARTED;
		
		return true;
	}

	private boolean onComposerConnected(INonBlockingConnection arg0) {
		if (!isThereAlreadyAConnectedComposer()) {
			composerConnection = arg0;
			
			log.info("Composer connected");
			
			sessionState = SessionState.NOT_STARTED;
			
			return true;
		} else {
			log.error("There can be only one composer");
			
			return false;
		}
	}

	private boolean isThereAlreadyAConnectedComposer() {
		return composerConnection != null;
	}

	private void addRenderer(INonBlockingConnection arg0) {
		synchronized (renderersConnections) {
			setRendererIndex(arg0);
			renderersConnections.add(arg0);
		}
	}

	private void setRendererIndex(INonBlockingConnection arg0) {
		synchronized (renderersConnections) {
			arg0.setAttachment(renderersConnections.size());
		}
	}

	@Override
	public boolean onData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		String message;
		
		message = xSocketHelper.readString(arg0);
		
		if (RendererMessages.FRAME_FINISHED.equals(message)) {
			framesFinishedNotificationCounter++;
			
			return true;
		} else if (RendererMessages.SESSION_STARTED.equals(message)) {
			sessionStartedNotificationCounter++;
			
			if (areAllSessionStartedNotificationsReceived()) {
				sessionStartedSuccessfully();
				
				notifyFrameStart(false);
			}
			
			return true;
		} else {
			log.error("Unknown message received: " + message);
			
			return false;
		}
	}

	private boolean areAllFrameNotificationsReceived() {
		synchronized (renderersConnections) {
			return framesFinishedNotificationCounter == renderersConnections.size();
		}
	}
	
	private boolean areAllSessionStartedNotificationsReceived() {
		synchronized (renderersConnections) {
			return sessionStartedNotificationCounter == renderersConnections.size();
		}
	}

	@Override
	public boolean onDisconnect(INonBlockingConnection arg0) throws IOException {
		if (isRendererConnection(arg0)) {
			removeRenderer(arg0);
			
			log.info("Renderer disconnected");
			
			sessionState = SessionState.NOT_STARTED;
			
			return true;
		} else if (isComposerConnection(arg0)) {
			composerConnection = null;
			
			log.info("Composer disconnected");
			
			sessionState = SessionState.NOT_STARTED;
			
			return true;
		} else {
			throw new AssertionError("Should never happen!");
		}
	}

	private void removeRenderer(INonBlockingConnection arg0) {
		synchronized (renderersConnections) {
			renderersConnections.remove(arg0);
			recalculateRenderersIds(arg0);
		}
	}

	private void recalculateRenderersIds(INonBlockingConnection arg0) {
		synchronized (renderersConnections) {
			for (int j = getRendererIndex(arg0); j < renderersConnections.size(); j++) {
				renderersConnections.get(j).setAttachment(j);
			}
		}
	}
	
}
