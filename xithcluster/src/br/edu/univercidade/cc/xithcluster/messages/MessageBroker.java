package br.edu.univercidade.cc.xithcluster.messages;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.Server;
import br.edu.univercidade.cc.xithcluster.ConnectionHandlingFacade;
import br.edu.univercidade.cc.xithcluster.ConnectionStateChange;
import br.edu.univercidade.cc.xithcluster.DataListener;
import br.edu.univercidade.cc.xithcluster.utils.ObjectBucket;

public abstract class MessageBroker {
	
	protected ProcessableQueue<ConnectionStateChange> connectionStateNotificationQueue = new ProcessableQueue<ConnectionStateChange>();
	
	protected ProcessableQueue<Message> messageQueue = new ProcessableQueue<Message>();
	
	private ObjectBucket<MessageType, MessageHandler> messageHandlerBucket = new ObjectBucket<MessageType, MessageHandler>();
	
	private MessageProcessor messageProcessor;
	
	protected MessageBroker(MessageProcessor messageProcessor) {
		if (messageProcessor == null) {
			throw new IllegalArgumentException();
		}
		
		this.messageProcessor = messageProcessor;
		
		ConnectionHandlingFacade.getInstance().addDataListener(new DataListener() {
			
			@Override
			public void handleData(INonBlockingConnection connection) throws IOException {
				MessageType messageType;
				
				messageType = readMessageType(connection);
				
				if (messageType == null) {
					// TODO:
					throw new AssertionError("Message type should never be null");
				}
				
				delegateMessageHandling(connection, messageType);
			}
			
		});
	}
	
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
	
	protected void enqueueMessage(Message message) {
		messageQueue.add(message);
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
	
	public void notifyMessageProcessor() {
		messageQueue.startProcessingQueue();
		
		for (Message message : messageQueue) {
			messageProcessor.processMessage(message);
		}
		
		messageQueue.stopProcessingQueue();
	}
	
}