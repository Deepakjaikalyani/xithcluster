package br.edu.univercidade.cc.xithcluster.communication;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.BufferOverflowException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
	
	private final ComposerMessageBroker composerMessageBroker = new ComposerMessageBroker();
	
	private List<INonBlockingConnection> renderersConnections = Collections.synchronizedList(new ArrayList<INonBlockingConnection>());
	
	private final Composer composer;
	
	private IServer renderersServer;
	
	private INonBlockingConnection masterConnection;

	private boolean sessionStarted = false;
	
	private final Map<Integer, RendererHandler> renderersHandlers = Collections.synchronizedMap(new HashMap<Integer, RendererHandler>());
	
	private final BitSet newImageMask = new BitSet();

	private int currentFrameIndex = -1;
	
	public ComposerNetworkManager(Composer composer) {
		this.composer = composer;
	}
	
	private int getRendererIndex(INonBlockingConnection rendererConnection) {
		Integer rendererId;
		
		rendererId = (Integer) rendererConnection.getAttachment();
		
		return rendererId.intValue();
	}
	
	public void initialize() throws UnknownHostException, IOException {
		renderersServer = new Server(ComposerConfiguration.renderersConnectionAddress, ComposerConfiguration.renderersConnectionPort, composerMessageBroker);
		renderersServer.start();
		
		masterConnection = new NonBlockingConnection(ComposerConfiguration.masterListeningAddress, ComposerConfiguration.masterListeningPort, composerMessageBroker);
		masterConnection.setAutoflush(false);
	}
	
	private void setRendererIndex(INonBlockingConnection arg0) {
		arg0.setAttachment(renderersConnections.size());
	}

	private boolean hasAllSubImages() {
		return !renderersHandlers.isEmpty() && newImageMask.cardinality() == renderersHandlers.size();
	}
	
	private void notifySessionStarted() {
		try {
			sendSessionStartedMessage(); 
		} catch (IOException e) {
			log.error("Error notifying master node that session started successfully", e);
		}
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
	
	private void onStartFrame(int frameIndex) {
		if (currentFrameIndex != -1 && currentFrameIndex != frameIndex) {
			log.error("Frame lost: " + currentFrameIndex);
		}
		
		newImageMask.clear();
		
		currentFrameIndex = frameIndex;
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
		
		notifySessionStarted();
		
		sessionStarted = true;
		
		log.info("Session started successfully");
	}

	private void onNewImage(INonBlockingConnection arg0, int frameIndex, CompressionMethod compressionMethod, byte[] colorAndAlphaBuffer, byte[] depthBuffer) {
		INonBlockingConnection rendererConnection;
		RendererHandler handler;
		
		if (frameIndex != currentFrameIndex) {
			log.error("Discarting out-of-sync frame: " + frameIndex);
			return;
		}
		
		rendererConnection = arg0;
		
		handler = renderersHandlers.get(getRendererIndex(rendererConnection));
		if (handler == null) {
			log.error("Trying to set a new image on a renderer that hasn't informed his composition order");
		}
		
		switch (compressionMethod) {
		case PNG:
			// TODO: Inflate!
			break;
		}
		
		handler.setColorAndAlphaBuffer(colorAndAlphaBuffer);
		handler.setDepthBuffer(depthBuffer);
		
		newImageMask.set(getRendererIndex(rendererConnection));
		
		if (hasAllSubImages()) {
			composer.setFrameData(renderersHandlers.size(), getColorAndAlphaBuffers(), getDepthBuffers());
			
			notifyFinishedFrame();
		}
	}

	private void notifyFinishedFrame() {
		try {
			sendFinishedFrameMessage(currentFrameIndex); 
		} catch (IOException e) {
			log.error("Error notifying master node that the frame was finished", e);
		}
	}
	
	private boolean isRendererConnection(INonBlockingConnection arg0) {
		return arg0.getLocalPort() == ComposerConfiguration.renderersConnectionPort;
	}
	
	private boolean isMyOwnConnection(INonBlockingConnection arg0) {
		return arg0.getLocalPort() == ComposerConfiguration.masterListeningPort;
	}

	public void onConnected(INonBlockingConnection arg0) {
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

	private void sendFinishedFrameMessage(int frameIndex) throws BufferOverflowException, IOException {
		masterConnection.write(MessageType.FINISHED_FRAME.ordinal());
		masterConnection.flush();
		
		masterConnection.write(frameIndex);
		masterConnection.flush();
	}
	
	public void update() {
		Deque<Message> messages;
		Message message;
		Message lastStartSessionMessage;
		Iterator<Message> iterator;
		Iterator<Message> descendingIterator;
		
		messages = MessageQueue.getInstance().retrieveMessages();
		
		descendingIterator = messages.descendingIterator();
		while (descendingIterator.hasNext()) {
			message = descendingIterator.next();
			if (message.getType() == MessageType.CONNECTED) {
				onConnected(message.getSource());
			} else if (message.getType() == MessageType.DISCONNECTED) {
				// TODO: onDisconnected(..)
				onRendererDisconnected(message.getSource());
			} else {
				continue;
			}
			descendingIterator.remove();
		}
		
		iterator = messages.iterator();
		while (iterator.hasNext()) {
			message = iterator.next();
			if (message.getType() == MessageType.SET_COMPOSITION_ORDER) {
				onSetCompositionOrder(message.getSource(), (Integer) message.getParameters()[0]);
				iterator.remove();
			}
		}
		
		if (sessionStarted) {
			descendingIterator = messages.descendingIterator();
			while (descendingIterator.hasNext()) {
				message = descendingIterator.next();
				if (message.getType() == MessageType.START_FRAME) {
					onStartFrame((Integer) message.getParameters()[0]);
				} else if (message.getType() == MessageType.NEW_IMAGE) {
					onNewImage(message.getSource(), (Integer) message.getParameters()[0], (CompressionMethod) message.getParameters()[1], (byte[]) message.getParameters()[2], (byte[]) message.getParameters()[2]);
				}
			}
		} else {
			/*
			 * Consider only the last start session message.
			 */
			lastStartSessionMessage = null;
			descendingIterator = messages.descendingIterator();
			while (descendingIterator.hasNext()) {
				message = descendingIterator.next();
				if (lastStartSessionMessage != null && message.getType() == MessageType.START_SESSION) {
					lastStartSessionMessage = message;
				} else {
					MessageQueue.getInstance().postMessage(message);
				}
			}
			
			if (lastStartSessionMessage != null) {
				onStartSession((Integer) lastStartSessionMessage.getParameters()[0], (Integer) lastStartSessionMessage.getParameters()[1], (Double) lastStartSessionMessage.getParameters()[2]);
			}
		}
	}

}
