package br.edu.univercidade.cc.xithcluster.messages;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IConnectHandler;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.IDisconnectHandler;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.Server;
import br.edu.univercidade.cc.xithcluster.utils.ObjectBucket;

public abstract class MessageBroker {
	
	private class MessageBrokerConnectionHandler implements IConnectHandler, IDisconnectHandler, IDataHandler {
		
		@Override
		public boolean onConnect(INonBlockingConnection connection) throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
			MessageBroker.this.handleComponentConnection(connection);
			
			return true;
		}
		
		@Override
		public boolean onDisconnect(INonBlockingConnection connection) throws IOException {
			MessageBroker.this.handleComponentDisconnection(connection);
			
			return true;
		}
		
		@Override
		public boolean onData(INonBlockingConnection connection) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
			MessageType messageType;
			
			messageType = readMessageType(connection);
			
			if (messageType == null) {
				// TODO:
				throw new AssertionError("Message type should never be null");
			}
			
			delegateMessageHandling(connection, messageType);
			
			return true;
		}
		
	}
	
	protected MessageQueue messageQueue = new MessageQueue();
	
	private MessageBrokerConnectionHandler messageBrokerConnectionHandler = new MessageBrokerConnectionHandler();
	
	private ObjectBucket<MessageType, MessageHandler> messageHandlerBucket = new ObjectBucket<MessageType, MessageHandler>();
	
	protected void register(MessageType messageType, Class<? extends MessageHandler> messageHandlerClass) {
		if (messageType == null || messageHandlerClass == null) {
			throw new IllegalArgumentException();
		}
		
		messageHandlerBucket.register(messageType, messageHandlerClass);
	}
	
	public MessageHandler getMessageHandler(MessageType messageType) {
		if (messageType == null) {
			throw new IllegalArgumentException();
		}
		
		return messageHandlerBucket.retrieveFromBucket(messageType);
	}
	
	private MessageType readMessageType(INonBlockingConnection connection) throws IOException {
		MessageType messageType;
		
		connection.markReadPosition();
		try {
			messageType = MessageType.values()[connection.readInt()];
			
			connection.removeReadMark();
			
			return messageType;
		} catch (IOException e) {
			connection.resetToReadMark();
			
			throw e;
		}
	}
	
	public void handleConnection(INonBlockingConnection connection) throws IOException {
		if (connection == null || !connection.isOpen()) {
			throw new IllegalArgumentException();
		}
		
		restoreConnectionHandlerToDefault(connection);
	}
	
	protected void enqueueMessage(Message message) {
		messageQueue.postMessage(message);
	}
	
	protected void handleComponentConnection(INonBlockingConnection connection) throws IOException {
		enqueueMessage(new ComponentConnectedMessage(connection));
	}
	
	protected void handleComponentDisconnection(INonBlockingConnection connection) throws IOException {
		enqueueMessage(new ComponentDisconnectedMessage(connection));
	}
	
	private void delegateMessageHandling(INonBlockingConnection connection, MessageType messageType) throws IOException {
		MessageHandler messageHandler = getMessageHandler(messageType);
		
		if (messageHandler == null) {
			// TODO:
			throw new AssertionError("Message handler should never be null");
		}
		
		connection.setHandler(messageHandler);
	}
	
	protected void messageHandlerCallback(MessageHandler messageHandler, INonBlockingConnection connection) throws IOException {
		Message message = messageHandler.assembleMessage();
		
		if (message == null) {
			// TODO:
			throw new AssertionError("Message should never be null");
		}
		
		enqueueMessage(message);
		
		restoreConnectionHandlerToDefault(connection);
		
		messageHandlerBucket.returnToBucket(messageHandler);
	}
	
	private void restoreConnectionHandlerToDefault(INonBlockingConnection connection) throws IOException {
		connection.setHandler(messageBrokerConnectionHandler);
	}

	public void handleServerConnection(Server server) {
		if (server == null) {
			throw new IllegalArgumentException();
		}
		
		server.setHandler(messageBrokerConnectionHandler);
	}
	
}