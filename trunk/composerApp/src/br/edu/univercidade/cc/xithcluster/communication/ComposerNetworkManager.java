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
import br.edu.univercidade.cc.xithcluster.XithClusterConfiguration;
import br.edu.univercidade.cc.xithcluster.communication.protocol.ComposerProtocolHandler;

public final class ComposerNetworkManager {
	
	private final Logger log = Logger.getLogger(ComposerNetworkManager.class);
	
	private List<INonBlockingConnection> renderersConnections = Collections.synchronizedList(new ArrayList<INonBlockingConnection>());
	
	private final Composer composer;
	
	private final ComposerProtocolHandler composerProtocolHandler;
	
	private INonBlockingConnection masterConnection;
	
	private IServer renderersServer;
	
	private byte[] colorAndAlphaBuffer;
	
	private byte[] depthBuffer;
	
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
		
		renderersServer = new Server(XithClusterConfiguration.listeningAddress, XithClusterConfiguration.renderersConnectionPort, composerProtocolHandler);
		renderersServer.start();
	}

	public synchronized boolean hasAllSubImages() {
		return false;
	}

	public synchronized byte[] getColorAndAlphaBuffer() {
		return colorAndAlphaBuffer;
	}

	public synchronized byte[] getDepthBuffer() {
		return depthBuffer;
	}

	public void onStartFrame() {
		newImageMask.clear();
	}

	public void onStartSession(int screenWidth, int screenHeight) {
		composer.setScreenSize(screenWidth, screenHeight);
	}

	public void onNewImage(INonBlockingConnection arg0, byte[] colorAndAlphaBuffer, byte[] depthBuffer) {
		newImageMask.set(getRendererIndex(arg0));
		
		this.colorAndAlphaBuffer = colorAndAlphaBuffer;
		this.depthBuffer = depthBuffer;
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
	
}
