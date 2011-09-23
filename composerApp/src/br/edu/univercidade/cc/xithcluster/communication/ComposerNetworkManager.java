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

public final class ComposerNetworkManager {
	
	private final Logger log = Logger.getLogger(ComposerNetworkManager.class);
	
	private enum SessionState {
		CLOSED, STARTING, STARTED
	}
	
	private final ComposerMessageBroker composerMessageBroker = new ComposerMessageBroker();
	
	private List<INonBlockingConnection> renderersConnections = Collections.synchronizedList(new ArrayList<INonBlockingConnection>());
	
	private final Composer composer;
	
	private IServer renderersServer;
	
	private INonBlockingConnection masterConnection;
	
	private SessionState sessionState = SessionState.CLOSED;
	
	private final Map<Integer, RendererHandler> renderersHandlers = Collections.synchronizedMap(new HashMap<Integer, RendererHandler>());
	
	private final BitSet newImageMask = new BitSet();
	
	private int currentFrameIndex = -1;
	
	public ComposerNetworkManager(Composer composer) {
		this.composer = composer;
	}
	
	public void initialize() throws UnknownHostException, IOException {
		renderersServer = new Server(ComposerConfiguration.renderersConnectionAddress, ComposerConfiguration.renderersConnectionPort, composerMessageBroker);
		renderersServer.start();
		
		masterConnection = new NonBlockingConnection(ComposerConfiguration.masterListeningAddress, ComposerConfiguration.masterListeningPort, composerMessageBroker);
		masterConnection.setAutoflush(false);
	}
	
	private int getRendererIndex(INonBlockingConnection rendererConnection) {
		Integer rendererId;
		
		rendererId = (Integer) rendererConnection.getAttachment();
		
		return rendererId.intValue();
	}
	
	private void setRendererIndex(INonBlockingConnection arg0) {
		arg0.setAttachment(renderersConnections.size());
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
	
	private void onStartSession(int screenWidth, int screenHeight, double targetFPS) {
		log.debug("****************");
		log.debug("Session starting");
		log.debug("****************");
		
		log.trace("Screen width: " + screenWidth);
		log.trace("Screen height: " + screenHeight);
		log.trace("Target FPS: " + targetFPS);
		
		composer.setScreenSize(screenWidth, screenHeight);
		
		// TODO: set target FPS!
	}
	
	private void onNewImage(INonBlockingConnection arg0, int frameIndex, CompressionMethod compressionMethod, byte[] colorAndAlphaBuffer, byte[] depthBuffer) {
		INonBlockingConnection rendererConnection;
		RendererHandler rendererHandler;
		
		if (frameIndex != currentFrameIndex) {
			log.error("Discarting out-of-sync image: " + frameIndex);
			return;
		}
		
		rendererConnection = arg0;
		
		rendererHandler = renderersHandlers.get(getRendererIndex(rendererConnection));
		if (rendererHandler == null) {
			log.error("Trying to set a new image on a renderer that hasn't informed his composition order");
		}
		
		switch (compressionMethod) {
		case PNG:
			// TODO: Inflate!
			break;
		}
		
		rendererHandler.setColorAndAlphaBuffer(colorAndAlphaBuffer);
		rendererHandler.setDepthBuffer(depthBuffer);
		
		newImageMask.set(getRendererIndex(rendererConnection));
	}
	
	private boolean isRendererConnection(INonBlockingConnection arg0) {
		return arg0.getLocalPort() == ComposerConfiguration.renderersConnectionPort;
	}
	
	private boolean isMyOwnConnection(INonBlockingConnection arg0) {
		return arg0.getLocalPort() == ComposerConfiguration.masterListeningPort;
	}
	
	private void onConnected(INonBlockingConnection arg0) {
		if (isRendererConnection(arg0)) {
			onRendererConnected(arg0);
		} else if (!isMyOwnConnection(arg0)) {
			// TODO: Do nothing!
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
	
	private void onSetCompositionOrder(INonBlockingConnection arg0, int compositionOrder) {
		int rendererIndex;
		
		rendererIndex = getRendererIndex(arg0);
		
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
		
		masterConnection.write(currentFrameIndex);
		masterConnection.flush();
	}
	
	/*
	 * ================================ 
	 * Network messages processing loop
	 * ================================
	 */
	public void update() {
		Queue<Message> messages;
		Message message;
		Message lastStartSessionMessage;
		Iterator<Message> iterator;
		int frameIndex;
		CompressionMethod compressionMethod;
		byte[] colorAndAlphaBuffer;
		byte[] depthBuffer;
		int compositionOrder;
		int screenWidth;
		int screenHeight;
		double targetFPS;
		boolean clusterConfigurationChanged;
		
		if (!masterConnection.isOpen()) {
			log.info("Master node disconnected");
			
			// TODO:
			System.exit(-1);
		}
		
		messages = MessageQueue.startReadingMessages();
		
		clusterConfigurationChanged = false;
		iterator = messages.iterator();
		while (iterator.hasNext()) {
			message = iterator.next();
			if (message.getType() == MessageType.CONNECTED) {
				onConnected(message.getSource());
			} else if (message.getType() == MessageType.DISCONNECTED) {
				// TODO: onDisconnected(..)
				onRendererDisconnected(message.getSource());
			} else {
				continue;
			}
			
			clusterConfigurationChanged = true;
			iterator.remove();
		}
		
		if (clusterConfigurationChanged) {
			newImageMask.clear();
			sessionState = SessionState.CLOSED;
		}
		
		if (sessionState == SessionState.STARTED) {
			iterator = messages.iterator();
			while (iterator.hasNext()) {
				message = iterator.next();
				if (message.getType() == MessageType.START_FRAME) {
					frameIndex = (Integer) message.getParameters()[0];
					
					log.info("Start frame received: " + frameIndex);
					
					currentFrameIndex = frameIndex;
					newImageMask.clear();
				} else if (message.getType() == MessageType.NEW_IMAGE) {
					frameIndex = (Integer) message.getParameters()[0];
					compressionMethod = (CompressionMethod) message.getParameters()[1];
					colorAndAlphaBuffer = (byte[]) message.getParameters()[2];
					depthBuffer = (byte[]) message.getParameters()[3];
					
					log.info("New image received: " + frameIndex);
					
					onNewImage(message.getSource(), frameIndex, compressionMethod, colorAndAlphaBuffer, depthBuffer);
					
					if (!renderersHandlers.isEmpty() && newImageMask.cardinality() == renderersHandlers.size()) {
						log.info("Finished frame: " + currentFrameIndex);
						
						composer.setFrameData(renderersHandlers.size(), getColorAndAlphaBuffers(), getDepthBuffers());
						
						try {
							sendFinishedFrameMessage();
						} catch (IOException e) {
							log.error("Error notifying master node that the frame was finished", e);
						}
					}
				} else {
					continue;
				}
				
				iterator.remove();
			}
		}
		else if (sessionState == SessionState.CLOSED) {
			/*
			 * Consider only the last start session message.
			 */
			lastStartSessionMessage = null;
			iterator = messages.iterator();
			while (iterator.hasNext()) {
				message = iterator.next();
				if (message.getType() == MessageType.START_SESSION) {
					lastStartSessionMessage = message;
				} else {
					continue;
				}
				
				iterator.remove();
			}
			
			if (lastStartSessionMessage != null) {
				log.info("Start session received");
				
				screenWidth = (Integer) lastStartSessionMessage.getParameters()[0];
				screenHeight = (Integer) lastStartSessionMessage.getParameters()[1];
				targetFPS = (Double) lastStartSessionMessage.getParameters()[2];
				
				onStartSession(screenWidth, screenHeight, targetFPS);
				
				sessionState = SessionState.STARTING;
				
				log.info("Waiting for renderer's composition order");
			}
		// sessionState == SessionState.STARTING
		} else {
			iterator = messages.iterator();
			while (iterator.hasNext()) {
				message = iterator.next();
				if (message.getType() == MessageType.SET_COMPOSITION_ORDER) {
					log.info("Composition order received");
					
					compositionOrder = (Integer) message.getParameters()[0];
					
					onSetCompositionOrder(message.getSource(), compositionOrder);
				} else {
					continue;
				}
				
				iterator.remove();
			}
			
			if (renderersConnections.size() == renderersHandlers.size()) {
				try {
					sendSessionStartedMessage();
				} catch (IOException e) {
					// TODO:
					throw new RuntimeException("Error notifying master node that session started successfully", e);
				}
				
				sessionState = SessionState.STARTED;
			
				log.info("Session started successfully");
			}
		}
		
		MessageQueue.stopReadingMessages();
	}
	
}
