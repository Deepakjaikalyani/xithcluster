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

public final class MasterMessageBroker implements IConnectHandler, IDataHandler, IDisconnectHandler {
	
	private Logger log = Logger.getLogger(MasterMessageBroker.class);
	
	@Override
	public boolean onConnect(INonBlockingConnection connection) throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
		MessageQueue.getInstance().postMessage(new Message(MessageType.CONNECTED, connection));
		
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
			MessageQueue.getInstance().postMessage(new Message(MessageType.SESSION_STARTED, connection));
		case FINISHED_FRAME:
			connection.setHandler(new FinishedFrameDataHandler(this));
			
			return true;
		default:
			log.error("Invalid/Unknown record");
			
			return false;
		}
	}

	@Override
	public boolean onDisconnect(INonBlockingConnection connection) throws IOException {
		MessageQueue.getInstance().postMessage(new Message(MessageType.DISCONNECTED, connection));
		
		return true;
	}
	
	void onFinishedFrameCompleted(INonBlockingConnection connection, int frameIndex) {
		MessageQueue.getInstance().postMessage(new Message(MessageType.FINISHED_FRAME, connection, frameIndex));
	}
	
}
