package br.edu.univercidade.cc.xithcluster.messages;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.INonBlockingConnection;

public abstract class MessageHandler implements IDataHandler {
	
	private MessageBroker messageBroker;
	
	public MessageHandler(MessageBroker messageBroker) {
		this.messageBroker = messageBroker;
	}
	
	protected abstract void fetchData(INonBlockingConnection arg0) throws IOException;
	
	protected abstract Message assembleMessage();
	
	@Override
	public boolean onData(INonBlockingConnection connection) throws IOException {
		fetchDataTransactionally(connection);
		
		notifyMessageBroker(connection);
		
		return true;
	}
	
	private void fetchDataTransactionally(INonBlockingConnection connection) throws IOException {
		connection.markReadPosition();
		try {
			fetchData(connection);
			
			connection.removeReadMark();
		} catch (BufferUnderflowException e) {
			connection.resetToReadMark();
			
			throw e;
		}
	}
	
	private void notifyMessageBroker(INonBlockingConnection connection) throws IOException {
		messageBroker.messageHandlerCallback(this, connection);
	}
	
}
