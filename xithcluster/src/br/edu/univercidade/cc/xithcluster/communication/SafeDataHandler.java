package br.edu.univercidade.cc.xithcluster.communication;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.INonBlockingConnection;

public abstract class SafeDataHandler implements IDataHandler {
	
	protected static final String STRING_DELIMITER = "\r\n";
	
	public SafeDataHandler() {
		super();
	}
	
	protected abstract boolean onHandleData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException;
	
	protected abstract void onDataReady(INonBlockingConnection arg0) throws IOException;
	
	@Override
	public boolean onData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		boolean handleResult;
		
		arg0.markReadPosition();
		try {
			handleResult = onHandleData(arg0);
		} catch (BufferUnderflowException e) {
			arg0.resetToReadMark();
			return true;
		}
		
		onDataReady(arg0);
		
		arg0.removeReadMark();
		
		afterDataHandling(arg0);
		
		return handleResult;
	}

	protected void afterDataHandling(INonBlockingConnection arg0) throws IOException {
	}
	
}