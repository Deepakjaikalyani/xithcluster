package br.edu.univercidade.cc.xithcluster.communication;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
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
import br.edu.univercidade.cc.xithcluster.communication.protocol.ComposerProtocolHandler;

public final class ComposerNetworkManager {
	
	private final Logger log = Logger.getLogger(ComposerNetworkManager.class);
	
	private List<INonBlockingConnection> renderersConnections = Collections.synchronizedList(new ArrayList<INonBlockingConnection>());
	
	private final Composer composer;
	
	private final ComposerProtocolHandler composerProtocolHandler;
	
	private INonBlockingConnection masterConnection;
	
	private IServer renderersServer;

	private final Map<Integer, RendererHandler> renderersHandlers = new HashMap<Integer, RendererHandler>();
	
	private final BitSet newImageMask = new BitSet();

	private int currentFrameIndex = -1;
	
	public ComposerNetworkManager(Composer composer) {
		this.composer = composer;
		this.composerProtocolHandler = new ComposerProtocolHandler(this);
	}
	
	private int getRendererIndex(INonBlockingConnection rendererConnection) {
		Integer rendererId;
		
		rendererId = (Integer) rendererConnection.getAttachment();
		
		return rendererId.intValue();
	}
	
	private void setRendererIndex(INonBlockingConnection arg0) {
		arg0.setAttachment(renderersConnections.size());
	}

	public void initialize() throws UnknownHostException, IOException {
		renderersServer = new Server(ComposerConfiguration.renderersConnectionAddress, ComposerConfiguration.renderersConnectionPort, composerProtocolHandler);
		renderersServer.start();
		
		masterConnection = new NonBlockingConnection(ComposerConfiguration.masterListeningAddress, ComposerConfiguration.masterListeningPort, composerProtocolHandler);
		masterConnection.setAutoflush(false);
	}

	public synchronized boolean hasAllSubImages() {
		return !renderersConnections.isEmpty() && newImageMask.cardinality() == renderersConnections.size();
	}
	
	public synchronized int getNumberOfSubImages() {
		return renderersConnections.size();
	}
	
	private void notifySessionStarted() {
		try {
			composerProtocolHandler.sendSessionStartedMessage(masterConnection); 
		} catch (IOException e) {
			log.error("Error notifying master node that session started successfully", e);
		}
	}

	public synchronized byte[][] getColorAndAlphaBuffers() {
		byte[][] colorAndAlphaBuffers;
		INonBlockingConnection rendererConnection;
		
		colorAndAlphaBuffers = new byte[renderersConnections.size()][];
		synchronized (renderersConnections) {
			for (int i = 0; i < renderersConnections.size(); i++) {
				rendererConnection = renderersConnections.get(i);
				colorAndAlphaBuffers[i] = renderersHandlers.get(getRendererIndex(rendererConnection)).getColorAndAlphaBuffer();
			}
		}
		
		return colorAndAlphaBuffers;
	}

	public synchronized byte[][] getDepthBuffers() {
		byte[][] depthBuffers;
		INonBlockingConnection rendererConnection;
		
		depthBuffers = new byte[renderersConnections.size()][];
		synchronized (renderersConnections) {
			for (int i = 0; i < renderersConnections.size(); i++) {
				rendererConnection = renderersConnections.get(i);
				depthBuffers[i] = renderersHandlers.get(getRendererIndex(rendererConnection)).getDepthBuffer();
			}
		}
		
		return depthBuffers;
	}
	
	public void onStartFrame(int frameIndex) {
		newImageMask.clear();
		currentFrameIndex  = frameIndex;
	}

	public void onStartSession(int screenWidth, int screenHeight, double targetFPS) {
		log.debug("****************");
		log.debug("Session starting");
		log.debug("****************");
		
		log.trace("Screen width: " + screenWidth);
		log.trace("Screen height: " + screenHeight);
		log.trace("Target FPS: " + targetFPS);
		
		composer.setScreenSize(screenWidth, screenHeight);
		
		// TODO: set target FPS!
		
		notifySessionStarted();
		
		log.info("Session started successfully");
	}

	public synchronized void onNewImage(INonBlockingConnection arg0, int frameIndex, CompressionMethod compressionMethod, byte[] colorAndAlphaBuffer, byte[] depthBuffer) {
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
			notifyFinishedFrame();
		}
	}

	private void notifyFinishedFrame() {
		try {
			composerProtocolHandler.sendFinishedFrameMessage(masterConnection, currentFrameIndex); 
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

	public synchronized boolean onConnected(INonBlockingConnection arg0) {
		if (isRendererConnection(arg0)) {
			return onRendererConnected(arg0);
		} else if (!isMyOwnConnection(arg0)) {
			return true;
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
		
		return true;
	}

	public synchronized boolean onRendererDisconnect(INonBlockingConnection arg0) {
		int rendererIndex;
		
		rendererIndex = getRendererIndex(arg0);
		
		newImageMask.clear(rendererIndex);
		
		renderersConnections.remove(rendererIndex);
		synchronized (renderersConnections) {
			for (int j = rendererIndex; j < renderersConnections.size(); j++) {
				renderersConnections.get(j).setAttachment(j);
			}
		}
		
		renderersHandlers.remove(rendererIndex);
		
		log.info("Renderer disconnected");
		
		return true;
	}

	public synchronized void onSetCompositionOrder(INonBlockingConnection arg0, int compositionOrder) {
		int rendererIndex;
		
		rendererIndex = getRendererIndex(arg0);
		if (!renderersHandlers.containsKey(rendererIndex)) {
			renderersHandlers.put(rendererIndex, new RendererHandler(compositionOrder));
			
			log.info("Renderer " + rendererIndex + " has composition order " + compositionOrder);
		} else {
			log.info("Trying to set the composition order repeatedly for the same renderer: " + rendererIndex);
		}
	}

}
