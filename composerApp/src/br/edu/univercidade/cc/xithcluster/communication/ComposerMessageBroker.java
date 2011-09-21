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
	
	@Override
	public boolean onConnect(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
		MessageQueue.getInstance().postMessage(new Message(MessageType.CONNECTED, arg0));
		
		return true;
	}
	
	@Override
	public boolean onData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		MessageType messageType;
		
		messageType = CommunicationHelper.safelyReadMessageType(arg0);
		
		if (messageType == null) {
			return true;
		}
		
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
	public boolean onDisconnect(INonBlockingConnection connection) throws IOException {
		MessageQueue.getInstance().postMessage(new Message(MessageType.DISCONNECTED, connection));
		
		return true;
	}
	
	void onStartSessionCompleted(INonBlockingConnection connection, int screenWidth, int screenHeight, double targetFPS) {
		MessageQueue.getInstance().postMessage(new Message(MessageType.START_SESSION, connection, screenHeight, screenWidth, targetFPS));
	}
	
	void onNewImageCompleted(INonBlockingConnection connection, int frameIndex, CompressionMethod compressionMethod, byte[] colorAndAlphaBuffer, byte[] depthBuffer) {
		MessageQueue.getInstance().postMessage(new Message(MessageType.NEW_IMAGE, connection, frameIndex, compressionMethod, colorAndAlphaBuffer, depthBuffer));
	}

	void onSetCompositionOrderCompleted(INonBlockingConnection connection, int compositionOrder) {
		MessageQueue.getInstance().postMessage(new Message(MessageType.SET_COMPOSITION_ORDER, connection, compositionOrder));
	}
	
	void onStartFrameCompleted(INonBlockingConnection connection, int frameIndex) {
		MessageQueue.getInstance().postMessage(new Message(MessageType.START_FRAME, connection, frameIndex));
	}

}
