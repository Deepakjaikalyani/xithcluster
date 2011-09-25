package br.edu.univercidade.cc.xithcluster.communication;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.BufferOverflowException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
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
import br.edu.univercidade.cc.xithcluster.Composer;
import br.edu.univercidade.cc.xithcluster.ComposerConfiguration;
import br.edu.univercidade.cc.xithcluster.CompressionMethod;

public final class ComposerNetworkManager extends NetworkManager {
	
	final Logger log = Logger.getLogger(ComposerNetworkManager.class);
	
	private final ComposerMessageBroker composerMessageBroker = new ComposerMessageBroker();
	
	List<INonBlockingConnection> renderersConnections = Collections.synchronizedList(new ArrayList<INonBlockingConnection>());
	
	private final Composer composer;
	
	private IServer renderersServer;
	
	SessionState sessionState = SessionState.CLOSED;
	
	private final Map<Integer, RendererHandler> renderersHandlers = Collections.synchronizedMap(new HashMap<Integer, RendererHandler>());
	
	final BitSet newImageMask = new BitSet();
	
	private int currentFrame = -1;
	
	public ComposerNetworkManager(Composer composer) {
		this.composer = composer;
	}
	
	public void initialize() throws UnknownHostException, IOException {
		renderersServer = new Server(ComposerConfiguration.renderersConnectionAddress, ComposerConfiguration.renderersConnectionPort, composerMessageBroker);
		renderersServer.start();
		
		masterConnection = new NonBlockingConnection(ComposerConfiguration.masterListeningAddress, ComposerConfiguration.masterListeningPort, composerMessageBroker);
		masterConnection.setAutoflush(false);
	}
	
	private byte[][] getColorAndAlphaBuffers() {
		byte[][] colorAndAlphaBuffers;
		int i;
		
		colorAndAlphaBuffers = new byte[renderersHandlers.size()][];
		i = 0;
		for (RendererHandler rendererHandler : renderersHandlers.values()) {
			colorAndAlphaBuffers[i++] = rendererHandler.getColorAndAlphaBuffer();
		}
		
		return colorAndAlphaBuffers;
	}
	
	private byte[][] getDepthBuffers() {
		byte[][] depthBuffers;
		int i;
		
		depthBuffers = new byte[renderersHandlers.size()][];
		i = 0;
		for (RendererHandler rendererHandler : renderersHandlers.values()) {
			depthBuffers[i++] = rendererHandler.getDepthBuffer();
		}
		
		return depthBuffers;
	}
	
	private boolean isSessionReadyToStart() {
		return !renderersConnections.isEmpty() && renderersConnections.size() == renderersHandlers.size();
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
		renderersHandlers.clear();
		newImageMask.clear();
		
		sessionState = SessionState.CLOSED;
		
		log.info("Current session was closed");
	}
	
	private boolean areAllSubImagesReceived() {
		return !renderersHandlers.isEmpty() && newImageMask.cardinality() == renderersHandlers.size();
	}
	
	private void finishCurrentFrame() {
		log.info("Finishing current frame");
		log.trace("currentFrame=" + currentFrame);
		
		composer.setFrameData(renderersHandlers.size(), getColorAndAlphaBuffers(), getDepthBuffers());
		
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
		return arg0.getLocalPort() == ComposerConfiguration.renderersConnectionPort;
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
		
		renderersHandlers.remove(rendererIndex);
		
		log.info("Renderer disconnected");
	}
	
	private void onNewImage(Message message) {
		RendererHandler rendererHandler;
		int rendererIndex;
		CompressionMethod compressionMethod;
		byte[] colorAndAlphaBuffer;
		byte[] depthBuffer;
		
		compressionMethod = (CompressionMethod) message.getParameters()[1];
		colorAndAlphaBuffer = (byte[]) message.getParameters()[2];
		depthBuffer = (byte[]) message.getParameters()[3];
		
		log.info("New image received");
		log.trace("currentFrame=" + currentFrame);
		
		rendererIndex = getRendererIndex(message.getSource());
		rendererHandler = renderersHandlers.get(rendererIndex);
		
		if (rendererHandler == null) {
			// TODO:
			throw new RuntimeException("Trying to set a new image on a renderer that hasn't informed his composition order");
		}
		
		switch (compressionMethod) {
		case PNG:
			// TODO: Inflate!
			break;
		}
		
		rendererHandler.setColorAndAlphaBuffer(colorAndAlphaBuffer);
		rendererHandler.setDepthBuffer(depthBuffer);
		
		newImageMask.set(rendererIndex);
	}
	
	private void onStartFrame(Message message) {
		log.info("Start frame received");
		
		currentFrame = (Integer) message.getParameters()[0];
		
		log.trace("currentFrame=" + currentFrame);
		
		newImageMask.clear();
	}
	
	private void onStartSession(Message message) {
		int screenWidth;
		int screenHeight;
		double targetFPS;
		
		log.info("Start session received");
		
		screenWidth = (Integer) message.getParameters()[0];
		screenHeight = (Integer) message.getParameters()[1];
		targetFPS = (Double) message.getParameters()[2];
		
		log.debug("****************");
		log.debug("Session starting");
		log.debug("****************");
		
		log.trace("Screen width: " + screenWidth);
		log.trace("Screen height: " + screenHeight);
		log.trace("Target FPS: " + targetFPS);
		
		composer.setScreenSize(screenWidth, screenHeight);
		
		// TODO: Configure target FPS!
		
		sessionState = SessionState.STARTING;
		
		log.info("Waiting for renderer's composition order");
	}
	
	private void onSetCompositionOrder(Message message) {
		int compositionOrder;
		int rendererIndex;
		
		log.info("Composition order received");
		
		compositionOrder = (Integer) message.getParameters()[0];
		
		rendererIndex = getRendererIndex(message.getSource());
		
		if (!renderersHandlers.containsKey(rendererIndex)) {
			renderersHandlers.put(rendererIndex, new RendererHandler(compositionOrder));
			
			log.info("Renderer " + rendererIndex + " has composition order " + compositionOrder);
		} else {
			log.info("Trying to set the composition order repeatedly for the same renderer: " + rendererIndex);
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

	@Override
	protected void processMessages(Queue<Message> messages) {
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
						// TODO:
						throw new RuntimeException("Image from past frame received!");
					}
				}
			}
		
			if (areAllSubImagesReceived()) {
				finishCurrentFrame();
			}
		} else if (sessionState == SessionState.CLOSED) {
			/*
			 * Consider only the last start session message, throwing away all the rest.
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

}
