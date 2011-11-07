package br.edu.univercidade.cc.xithcluster.messages;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.INonBlockingConnection;

public abstract class MessageHandler<T extends IDataHandler> implements IDataHandler {
	
	protected static final String STRING_DELIMITER = "\r\n";
	
	private T messageBroker;
	
	public MessageHandler(T nextDataHandler) {
		this.messageBroker = nextDataHandler;
	}
	
	public T getMessageBroker() {
		return messageBroker;
	}
	
	protected abstract boolean onHandleData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException;
	
	protected abstract void onDataReady(INonBlockingConnection arg0) throws IOException;
	
	@Override
	public boolean onData(INonBlockingConnection connection) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		boolean handleResult;
		
		connection.markReadPosition();
		try {
			handleResult = onHandleData(connection);
			
			connection.removeReadMark();
		} catch (BufferUnderflowException e) {
			connection.resetToReadMark();
			return true;
		}
		
		onDataReady(connection);
		
		restoreConnectionHandlerToMessageBroker(connection);
		
		return handleResult;
	}
	
	protected void restoreConnectionHandlerToMessageBroker(INonBlockingConnection connection) throws IOException {
		connection.setHandler(messageBroker);
	}
	
}
