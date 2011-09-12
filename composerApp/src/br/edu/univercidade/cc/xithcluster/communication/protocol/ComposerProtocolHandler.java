package br.edu.univercidade.cc.xithcluster.communication.protocol;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.apache.log4j.Logger;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IConnectHandler;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.IDisconnectHandler;
import org.xsocket.connection.INonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.XithClusterConfiguration;
import br.edu.univercidade.cc.xithcluster.communication.ComposerNetworkManager;

public class ComposerProtocolHandler implements IConnectHandler, IDataHandler, IDisconnectHandler {

	private final Logger log = Logger.getLogger(ComposerProtocolHandler.class);
	
	private final ComposerNetworkManager composerNetworkManager;

	public ComposerProtocolHandler(ComposerNetworkManager composerNetworkManager) {
		this.composerNetworkManager = composerNetworkManager;
	}
	
	private boolean isRendererConnection(INonBlockingConnection arg0) {
		return arg0.getLocalPort() == XithClusterConfiguration.renderersConnectionPort;
	}

	@Override
	public boolean onConnect(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
		if (isRendererConnection(arg0)) {
			return composerNetworkManager.onRendererConnected(arg0);
		} else {
			log.error("Unknown connection refused");
			
			return false;
		}
	}
	
	@Override
	public boolean onData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		RecordType recordType;

		recordType = ProtocolHelper.readRecordType(arg0);
		
		if (recordType == null) {
			return true;
		}
		
		switch (recordType) {
		case START_SESSION:
			arg0.setHandler(new StartSessionDataHandler(this));
			
			return true;
		case START_FRAME:
			composerNetworkManager.onStartFrame();
			
			return true;
		case NEW_IMAGE:
			arg0.setHandler(new NewImageDataHandler(this));
			
			return true;
		default:
			log.error("Invalid/Unknown message");
			
			return false;
		}
	}
	
	@Override
	public boolean onDisconnect(INonBlockingConnection arg0) throws IOException {
		if (isRendererConnection(arg0)) {
			return composerNetworkManager.onRendererDisconnect(arg0);
		} else {
			throw new AssertionError("Should never happen!");
		}
	}

	void onStartSessionCompleted(INonBlockingConnection arg0, int screenWidth, int screenHeight) {
		composerNetworkManager.onStartSession(screenWidth, screenHeight);
	}

	void onNewImageCompleted(INonBlockingConnection arg0, byte[] colorAndAlphaBuffer, byte[] depthBuffer) {
		composerNetworkManager.onNewImage(arg0, colorAndAlphaBuffer, depthBuffer);
	}

}
