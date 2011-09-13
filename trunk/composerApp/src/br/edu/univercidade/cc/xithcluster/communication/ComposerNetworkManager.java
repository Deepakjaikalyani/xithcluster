package br.edu.univercidade.cc.xithcluster.communication;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
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
	
	private final BitSet newImageMask = new BitSet();
	
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
		masterConnection = new NonBlockingConnection(ComposerConfiguration.masterListeningAddress, ComposerConfiguration.masterListeningPort, composerProtocolHandler);
		masterConnection.setAutoflush(false);
		
		renderersServer = new Server(ComposerConfiguration.renderersConnectionAddress, ComposerConfiguration.renderersConnectionPort, composerProtocolHandler);
		renderersServer.start();
	}

	public synchronized boolean hasAllSubImages() {
		return false;
	}

	public synchronized byte[][] getColorAndAlphaBuffers() {
		byte[][] colorAndAlphaBuffers;
		INonBlockingConnection rendererConnection;
		
		colorAndAlphaBuffers = new byte[renderersConnections.size()][];
		synchronized (renderersConnections) {
			for (int i = 0; i < renderersConnections.size(); i++) {
				rendererConnection = renderersConnections.get(i);
				colorAndAlphaBuffers[i] = ((RendererHandler) rendererConnection.getAttachment()).getColorAndAlphaBuffer();
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
				depthBuffers[i] = ((RendererHandler) rendererConnection.getAttachment()).getDepthBuffer();
			}
		}
		
		return depthBuffers;
	}

	public void onStartFrame() {
		newImageMask.clear();
	}

	public void onStartSession(int screenWidth, int screenHeight, double targetFPS) {
		log.debug("***************");
		log.debug("Session started");
		log.debug("***************");
		
		log.trace("Screen width: " + screenWidth);
		log.trace("Screen height: " + screenHeight);
		log.trace("Target FPS: " + targetFPS);
		
		composer.setScreenSize(screenWidth, screenHeight);
		
		// TODO: set target FPS!
	}

	public void onNewImage(INonBlockingConnection arg0, CompressionMethod compressionMethod, byte[] colorAndAlphaBuffer, byte[] depthBuffer) {
		RendererHandler handler;
		
		handler = (RendererHandler) arg0.getAttachment();
		
		if (handler != null) {
			switch (compressionMethod) {
			case PNG:
				// TODO: Inflate!
				break;
			}
			
			handler.setColorAndAlphaBuffer(colorAndAlphaBuffer);
			handler.setDepthBuffer(depthBuffer);
			
			newImageMask.set(getRendererIndex(arg0));
		} else {
			log.error("Trying to set a new image on a renderer that hasn't informed his composition order");
		}
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
		
		return true;
	}

	public synchronized boolean onRendererDisconnect(INonBlockingConnection arg0) {
		newImageMask.clear(getRendererIndex(arg0));
		
		renderersConnections.remove(arg0);
		synchronized (renderersConnections) {
			for (int j = getRendererIndex(arg0); j < renderersConnections.size(); j++) {
				renderersConnections.get(j).setAttachment(j);
			}
		}
		
		log.info("Renderer disconnected");
		
		return true;
	}

	public synchronized void onSetCompositionOrder(INonBlockingConnection arg0, int compositionOrder) {
		if (arg0.getAttachment() == null) {
			arg0.setAttachment(new RendererHandler(compositionOrder));
		} else {
			log.info("Trying to set the composition order repeatedly for the same renderer: " + getRendererIndex(arg0));
		}
	}
	
}
