package br.edu.univercidade.cc.xithcluster.communication;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.BufferOverflowException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.apache.log4j.Logger;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.IServer;
import org.xsocket.connection.NonBlockingConnection;
import org.xsocket.connection.Server;
import br.edu.univercidade.cc.xithcluster.CompressionMethod;
import br.edu.univercidade.cc.xithcluster.Rasterizer;
import br.edu.univercidade.cc.xithcluster.composition.ColorAndAlphaBuffer;
import br.edu.univercidade.cc.xithcluster.composition.ColorAndAlphaBufferList;
import br.edu.univercidade.cc.xithcluster.composition.DepthBuffer;
import br.edu.univercidade.cc.xithcluster.composition.DepthBufferList;
import br.edu.univercidade.cc.xithcluster.hud.components.FPSCounter;
import br.edu.univercidade.cc.xithcluster.utils.Timer;

public final class ComposerNetworkManager {
	
	private Logger log = Logger.getLogger(ComposerNetworkManager.class);
	
	private boolean trace = log.isTraceEnabled();
	
	private Rasterizer rasterizer;
	
	private ComposerMessageBroker composerMessageBroker;
	
	protected INonBlockingConnection masterConnection;
	
	private List<INonBlockingConnection> renderersConnections = new ArrayList<INonBlockingConnection>();
	
	private FPSCounter fpsCounter;
	
	private IServer renderersServer;
	
	private SessionState sessionState = SessionState.CLOSED;
	
	private Map<Integer, CompositionOrder> compositionOrderMap = new HashMap<Integer, CompositionOrder>();
	
	private ColorAndAlphaBufferList colorAndAlphaBuffers = ColorAndAlphaBufferList.emptyList();
	
	private DepthBufferList depthBuffers = DepthBufferList.emptyList();
	
	private String renderersConnectionAddress;
	
	private int renderersConnectionPort;
	
	private String masterListeningAddress;
	
	private int masterListeningPort;
	
	private BitSet newImageMask = new BitSet();
	
	private int currentFrame = -1;

	private boolean started = false;
	
	private long lastGameTime;
	
	public ComposerNetworkManager(String masterListeningAddress, int masterListeningPort, String renderersConnectionAddress, int renderersConnectionPort) {
		if (masterListeningAddress == null || masterListeningAddress.isEmpty() ||
				renderersConnectionAddress == null || renderersConnectionAddress.isEmpty()) {
			throw new IllegalArgumentException();
		}
		
		this.masterListeningAddress = masterListeningAddress;
		this.masterListeningPort = masterListeningPort;
		this.renderersConnectionAddress = renderersConnectionAddress;
		this.renderersConnectionPort = renderersConnectionPort;
		
		composerMessageBroker = new ComposerMessageBroker(this.masterListeningPort);
	}
	
	public void setRasterizer(Rasterizer rasterizer) {
		if (rasterizer == null) {
			throw new IllegalArgumentException();
		}
		
		this.rasterizer = rasterizer;
	}
	
	public void setFpsCounter(FPSCounter fpsCounter) {
		if (fpsCounter == null) {
			throw new IllegalArgumentException();
		}
		
		this.fpsCounter = fpsCounter;
	}
	
	public void start() throws UnknownHostException, IOException {
		if (rasterizer == null) {
			throw new RuntimeException("Rasterizer must be set");
		}
		
		renderersServer = new Server(renderersConnectionAddress, renderersConnectionPort, composerMessageBroker);
		renderersServer.start();
		
		masterConnection = new NonBlockingConnection(masterListeningAddress, masterListeningPort, composerMessageBroker);
		masterConnection.setAutoflush(false);
		
		started = true;
	}
	
	private boolean isSessionReadyToStart() {
		return !renderersConnections.isEmpty() && renderersConnections.size() == compositionOrderMap.size();
	}
	
	private void startNewSession() {
		try {
			sendSessionStartedMessage();
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error notifying master node that session started successfully", e);
		}
		
		sessionState = SessionState.STARTED;
		
		log.info("Session started successfully");
	}
	
	private void closeCurrentSession() {
		compositionOrderMap.clear();
		newImageMask.clear();
		
		sessionState = SessionState.CLOSED;
		
		log.info("Current session was closed");
	}
	
	private boolean areAllSubImagesReceived() {
		return !compositionOrderMap.isEmpty() && newImageMask.cardinality() == compositionOrderMap.size();
	}
	
	private void finishCurrentFrame() {
		if (trace) {
			log.trace("Finishing current frame: " + currentFrame);
		}
		
		rasterizer.setNewImageData(colorAndAlphaBuffers, depthBuffers);
		
		try {
			sendFinishedFrameMessage();
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error notifying master node that the frame was finished", e);
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
	
	private boolean isRendererConnection(INonBlockingConnection arg0) {
		return arg0.getLocalPort() == renderersConnectionPort;
	}
	
	private void onConnected(INonBlockingConnection arg0) {
		if (isRendererConnection(arg0)) {
			onRendererConnected(arg0);
		} else {
			log.error("Unknown connection refused");
		}
	}
	
	private void onRendererConnected(INonBlockingConnection arg0) {
		INonBlockingConnection rendererConnection;
		
		rendererConnection = arg0;
		
		setRendererIndex(rendererConnection);
		renderersConnections.add(rendererConnection);
		
		rendererConnection.setAutoflush(false);
		
		log.info("New renderer connected");
	}
	
	private void onDisconnected(INonBlockingConnection arg0) {
		if (isRendererConnection(arg0)) {
			onRendererDisconnected(arg0);
		} else {
			log.error("Unknown connection refused");
		}
	}
	
	private void onRendererDisconnected(INonBlockingConnection arg0) {
		int rendererIndex;
		
		rendererIndex = getRendererIndex(arg0);
		
		newImageMask.clear(rendererIndex);
		
		renderersConnections.remove(rendererIndex);
		for (int j = rendererIndex; j < renderersConnections.size(); j++) {
			renderersConnections.get(j).setAttachment(j);
		}
		
		compositionOrderMap.remove(rendererIndex);
		colorAndAlphaBuffers.remove(rendererIndex);
		depthBuffers.remove(rendererIndex);
		
		log.info("Renderer disconnected");
	}
	
	private void onNewImage(Message message) {
		CompositionOrder compositionOrder;
		int rendererIndex;
		CompressionMethod compressionMethod;
		byte[] colorAndAlphaBuffer;
		float[] depthBuffer;
		
		compressionMethod = (CompressionMethod) message.getParameters()[1];
		colorAndAlphaBuffer = (byte[]) message.getParameters()[2];
		depthBuffer = (float[]) message.getParameters()[3];
		
		if (trace) {
			log.trace("New image received: " + currentFrame);
		}
		
		rendererIndex = getRendererIndex(message.getSource());
		compositionOrder = compositionOrderMap.get(rendererIndex);
		
		if (compositionOrder == null) {
			// TODO:
			throw new RuntimeException("Trying to set a new image on a renderer didn't send his composition order yet");
		}
		
		switch (compressionMethod) {
		case PNG:
			// TODO: Inflate!
			break;
		}
		
		colorAndAlphaBuffers.add(rendererIndex, ColorAndAlphaBuffer.wrap(colorAndAlphaBuffer));
		depthBuffers.add(rendererIndex, DepthBuffer.wrap(depthBuffer));
		
		newImageMask.set(rendererIndex);
	}
	
	private void onStartFrame(Message message) {
		currentFrame = (Integer) message.getParameters()[0];
		
		if (trace) {
			log.trace("Start frame received: " + currentFrame);
		}
		
		newImageMask.clear();
	}
	
	private void onStartSession(Message message) {
		int screenWidth;
		int screenHeight;
		double targetFPS;
		
		if (trace) {
			log.trace("Start session received");
		}
		
		screenWidth = (Integer) message.getParameters()[0];
		screenHeight = (Integer) message.getParameters()[1];
		targetFPS = (Double) message.getParameters()[2];
		
		if (trace) {
			log.trace("targetScreenWidth=" + screenWidth);
			log.trace("targetScreenHeight=" + screenHeight);
			log.trace("targetFPS=" + targetFPS);
		}
		
		rasterizer.setScreenSize(screenWidth, screenHeight);
		
		// TODO: Configure target FPS!
		
		sessionState = SessionState.STARTING;
		
		log.info("Waiting for renderer's composition order");
	}
	
	private void onSetCompositionOrder(Message message) {
		int compositionOrder;
		int rendererIndex;
		
		if (trace) {
			log.trace("Composition order received");
		}
		
		compositionOrder = (Integer) message.getParameters()[0];
		
		rendererIndex = getRendererIndex(message.getSource());
		
		if (!compositionOrderMap.containsKey(rendererIndex)) {
			compositionOrderMap.put(rendererIndex, new CompositionOrder(compositionOrder));
			
			if (trace) {
				log.trace("Renderer " + rendererIndex + " has composition order " + compositionOrder);
			}
		} else {
			log.error("Trying to set the composition order repeatedly for the same renderer: " + rendererIndex);
		}
	}
	
	private void sendSessionStartedMessage() throws BufferOverflowException, IOException {
		masterConnection.write(MessageType.SESSION_STARTED.ordinal());
		masterConnection.flush();
	}
	
	private void sendFinishedFrameMessage() throws BufferOverflowException, IOException {
		masterConnection.write(MessageType.FINISHED_FRAME.ordinal());
		masterConnection.flush();
		
		masterConnection.write(currentFrame);
		masterConnection.flush();
	}
	
	public void update(long startingTime, long elapsedTime) {
		Queue<Message> messages;
		
		if (!started) {
			throw new IllegalStateException();
		}
		
		checkMasterNodeConnection();
		
		messages = MessageQueue.startReadingMessages();
		
		processMessages(startingTime, elapsedTime, messages);
		
		MessageQueue.stopReadingMessages();
	}
	
	private void checkMasterNodeConnection() {
		if (!masterConnection.isOpen()) {
			System.err.println("Master node disconnected");
			
			// TODO:
			System.exit(-1);
		}
	}
	
	/*
	 * ================================ 
	 * Network messages processing loop
	 * ================================
	 */
	protected void processMessages(long startingTime, long elapsedTime, Queue<Message> messages) {
		Message message;
		Message firstStartFrameMessage;
		Message lastStartSessionMessage;
		Iterator<Message> iterator;
		boolean clusterConfigurationChanged;
		int frameIndex;
		
		clusterConfigurationChanged = false;
		iterator = messages.iterator();
		while (iterator.hasNext()) {
			message = iterator.next();
			if (message.getType() == MessageType.CONNECTED) {
				onConnected(message.getSource());
			} else if (message.getType() == MessageType.DISCONNECTED) {
				onDisconnected(message.getSource());
			} else {
				continue;
			}
			
			clusterConfigurationChanged = true;
			iterator.remove();
		}
		
		if (sessionState == SessionState.STARTED) {
			if (clusterConfigurationChanged) {
				closeCurrentSession();
				return;
			}
			
			/*
			 * Consider only the first start frame message received.
			 */
			firstStartFrameMessage = null;
			iterator = messages.iterator();
			while (iterator.hasNext()) {
				message = iterator.next();
				if (message.getType() == MessageType.START_FRAME) {
					firstStartFrameMessage = message;
					iterator.remove();
					break;
				}
			}
			
			if (firstStartFrameMessage != null) {
				onStartFrame(firstStartFrameMessage);
			}
			
			iterator = messages.iterator();
			while (iterator.hasNext()) {
				message = iterator.next();
				if (message.getType() == MessageType.NEW_IMAGE) {
					frameIndex = (Integer) message.getParameters()[0];
					
					if (frameIndex == currentFrame) {
						onNewImage(message);
						iterator.remove();
					} else if (frameIndex < currentFrame) {
						iterator.remove();
					}
				}
			}
			
			if (areAllSubImagesReceived()) {
				finishCurrentFrame();
				
				updateFPS(startingTime, elapsedTime);
			}
		} else if (sessionState == SessionState.CLOSED) {
			/*
			 * Consider only the last start session message, throwing away all
			 * the rest.
			 */
			lastStartSessionMessage = null;
			iterator = messages.iterator();
			while (iterator.hasNext()) {
				message = iterator.next();
				if (message.getType() == MessageType.START_SESSION) {
					lastStartSessionMessage = message;
					iterator.remove();
				}
			}
			
			if (lastStartSessionMessage != null) {
				onStartSession(lastStartSessionMessage);
			}
		} else if (sessionState == SessionState.STARTING) {
			iterator = messages.iterator();
			while (iterator.hasNext()) {
				message = iterator.next();
				if (message.getType() == MessageType.SET_COMPOSITION_ORDER) {
					onSetCompositionOrder(message);
					iterator.remove();
				}
			}
			
			if (isSessionReadyToStart()) {
				startNewSession();
			}
		}
	}

	private void updateFPS(long startingTime, long elapsedTime) {
		double fps;
		
		if (fpsCounter == null) {
			return;
		}
		
		if (lastGameTime > 0) {
			fps = Timer.getTimeDivisor() / (startingTime - lastGameTime);
			
			fpsCounter.update(fps);
		}
		
		lastGameTime = startingTime;		
	}

}
