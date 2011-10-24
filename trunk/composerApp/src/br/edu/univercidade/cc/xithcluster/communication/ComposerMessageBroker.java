package br.edu.univercidade.cc.xithcluster.communication;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.apache.log4j.Logger;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IConnectHandler;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.IDisconnectHandler;
import org.xsocket.connection.INonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.CompressionMethod;

public class ComposerMessageBroker implements IConnectHandler, IDataHandler, IDisconnectHandler {
	
	private final Logger log = Logger.getLogger(ComposerMessageBroker.class);
	
	private int masterListeningPort;
	
	public ComposerMessageBroker(int masterListeningPort) {
		this.masterListeningPort = masterListeningPort;
	}

	private boolean isMyOwnConnection(INonBlockingConnection arg0) {
		return arg0.getRemotePort() == masterListeningPort;
	}
	
	@Override
	public boolean onConnect(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
		if (!isMyOwnConnection(arg0)) {
			MessageQueue.postMessage(new Message(MessageType.CONNECTED, arg0));
		}
		
		return true;
	}
	
	@Override
	public boolean onData(INonBlockingConnection connection) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		MessageType messageType;
		
		messageType = CommunicationHelper.safelyReadMessageType(connection);
		
		if (messageType == null) {
			return true;
		}
		
		switch (messageType) {
		case START_SESSION:
			connection.setHandler(new StartSessionDataHandler(this));
			
			return true;
		case SET_COMPOSITION_ORDER:
			connection.setHandler(new SetCompositionOrderDataHandler(this));
			
			return true;
		case START_FRAME:
			connection.setHandler(new StartFrameDataHandler(this));
			
			return true;
		case NEW_IMAGE:
			connection.setHandler(new NewImageDataHandler(this));
			
			return true;
		default:
			log.error("Invalid/Unknown message");
			
			return false;
		}
	}
	
	@Override
	public boolean onDisconnect(INonBlockingConnection connection) throws IOException {
		MessageQueue.postMessage(new Message(MessageType.DISCONNECTED, connection));
		
		return true;
	}
	
	void onStartSessionCompleted(INonBlockingConnection connection, int screenWidth, int screenHeight, double targetFPS) {
		MessageQueue.postMessage(new Message(MessageType.START_SESSION, connection, screenWidth, screenHeight, targetFPS));
	}
	
	void onNewImageCompleted(INonBlockingConnection connection, int frameIndex, CompressionMethod compressionMethod, byte[] colorAndAlphaBuffer, float[] depthBuffer) {
		MessageQueue.postMessage(new Message(MessageType.NEW_IMAGE, connection, frameIndex, compressionMethod, colorAndAlphaBuffer, depthBuffer));
	}

	void onSetCompositionOrderCompleted(INonBlockingConnection connection, int compositionOrder) {
		MessageQueue.postMessage(new Message(MessageType.SET_COMPOSITION_ORDER, connection, compositionOrder));
	}
	
	void onStartFrameCompleted(INonBlockingConnection connection, int frameIndex) {
		MessageQueue.postMessage(new Message(MessageType.START_FRAME, connection, frameIndex));
	}

}
