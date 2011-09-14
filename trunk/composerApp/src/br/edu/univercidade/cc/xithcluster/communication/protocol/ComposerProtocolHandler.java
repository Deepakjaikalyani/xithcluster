package br.edu.univercidade.cc.xithcluster.communication.protocol;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.apache.log4j.Logger;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IConnectHandler;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.IDisconnectHandler;
import org.xsocket.connection.INonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.CompressionMethod;
import br.edu.univercidade.cc.xithcluster.communication.ComposerNetworkManager;

public class ComposerProtocolHandler implements IConnectHandler, IDataHandler, IDisconnectHandler {
	
	private final Logger log = Logger.getLogger(ComposerProtocolHandler.class);
	
	private final ComposerNetworkManager composerNetworkManager;
	
	public ComposerProtocolHandler(ComposerNetworkManager composerNetworkManager) {
		this.composerNetworkManager = composerNetworkManager;
	}
	
	@Override
	public boolean onConnect(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
		return composerNetworkManager.onConnected(arg0);
	}
	
	@Override
	public boolean onData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		MessageType messageType;
		
		messageType = ProtocolHelper.readMessageType(arg0);
		
		if (messageType == null) {
			return true;
		}
		
		// TODO: Reduce object instatiation by creating an object pool... or redesigning the classes.
		switch (messageType) {
		case START_SESSION:
			arg0.setHandler(new StartSessionDataHandler(this));
			
			return true;
		case SET_COMPOSITION_ORDER:
			arg0.setHandler(new SetCompositionOrderDataHandler(this));
			
			return true;
		case START_FRAME:
			arg0.setHandler(new StartFrameDataHandler(this));
			
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
		return composerNetworkManager.onRendererDisconnect(arg0);
	}
	
	void onStartSessionCompleted(int screenWidth, int screenHeight, double targetFPS) {
		composerNetworkManager.onStartSession(screenWidth, screenHeight, targetFPS);
	}
	
	void onNewImageCompleted(INonBlockingConnection arg0, int frameIndex, CompressionMethod compressionMethod, byte[] colorAndAlphaBuffer, byte[] depthBuffer) {
		composerNetworkManager.onNewImage(arg0, frameIndex, compressionMethod, colorAndAlphaBuffer, depthBuffer);
	}

	void onSetCompositionOrderCompleted(INonBlockingConnection arg0, int compositionOrder) {
		composerNetworkManager.onSetCompositionOrder(arg0, compositionOrder);
	}
	
	void onStartFrameCompleted(int frameIndex) {
		composerNetworkManager.onStartFrame(frameIndex);
	}

	public void sendSessionStartedMessage(INonBlockingConnection masterConnection) throws BufferOverflowException, IOException {
		masterConnection.write(MessageType.SESSION_STARTED.ordinal());
		masterConnection.flush();
	}

	public void sendFinishedFrameMessage(INonBlockingConnection masterConnection, int frameIndex) throws BufferOverflowException, IOException {
		masterConnection.write(MessageType.FINISHED_FRAME.ordinal());
		masterConnection.flush();
		
		masterConnection.write(frameIndex);
		masterConnection.flush();
	}

}
