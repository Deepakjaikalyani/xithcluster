package br.edu.univercidade.cc.xithcluster.communication.protocol;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.INonBlockingConnection;

public abstract class ContentHandler implements IDataHandler {
	
	protected static final String STRING_DELIMITER = "\n\r";
	private IDataHandler previousHandler;
	
	public ContentHandler(IDataHandler previousHandler) {
		this.previousHandler = previousHandler;
	}
	
	public IDataHandler getPreviousHandler() {
		return previousHandler;
	}

	protected abstract boolean onHandleContent(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException;
	
	protected abstract void onContentReady(INonBlockingConnection arg0) throws IOException;
	
	@Override
	public boolean onData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		boolean handleResult;
		
		arg0.markReadPosition();
		try {
			handleResult = onHandleContent(arg0);
		} catch (BufferUnderflowException e) {
			arg0.resetToReadMark();
			return true;
		}
		
		onContentReady(arg0);
		
		arg0.removeReadMark();
		
		arg0.setHandler(previousHandler);
		
		return handleResult;
	}
	
}
