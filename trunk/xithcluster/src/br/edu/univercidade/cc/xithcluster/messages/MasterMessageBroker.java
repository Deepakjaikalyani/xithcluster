package br.edu.univercidade.cc.xithcluster.messages;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.apache.log4j.Logger;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IConnectHandler;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.IDisconnectHandler;
import org.xsocket.connection.INonBlockingConnection;

public final class MasterMessageBroker implements IConnectHandler, IDataHandler, IDisconnectHandler {
	
	private Logger log = Logger.getLogger(MasterMessageBroker.class);
	
	@Override
	public boolean onConnect(INonBlockingConnection connection) throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
		MessageQueue.postMessage(new Message(MessageType.CONNECTED, connection));
		
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
		case SESSION_STARTED:
			MessageQueue.postMessage(new Message(MessageType.SESSION_STARTED, connection));
			
			return true;
		case FINISHED_FRAME:
			MessageHandler<MasterMessageBroker> messageHandler = new FinishedFrameDataHandler(this);
			connection.setHandler(messageHandler);
			messageHandler.onData(connection);
			
			return true;
		default:
			log.error("Invalid/Unknown record");
			
			return false;
		}
	}

	@Override
	public boolean onDisconnect(INonBlockingConnection connection) throws IOException {
		MessageQueue.postMessage(new Message(MessageType.DISCONNECTED, connection));
		
		return true;
	}
	
	void onFinishedFrameCompleted(INonBlockingConnection connection, long frameIndex) {
		MessageQueue.postMessage(new Message(MessageType.FINISHED_FRAME, connection, frameIndex));
	}
	
}
