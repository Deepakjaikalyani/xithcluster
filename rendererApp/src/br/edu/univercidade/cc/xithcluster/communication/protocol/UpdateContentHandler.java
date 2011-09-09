package br.edu.univercidade.cc.xithcluster.communication.protocol;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.INonBlockingConnection;

public final class UpdateContentHandler implements IDataHandler {
	
	private RendererProtocolHandler rendererProtocolHandler;
	
	public UpdateContentHandler(RendererProtocolHandler rendererProtocolHandler) {
		this.rendererProtocolHandler = rendererProtocolHandler;
	}
	
	@Override
	public boolean onData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		byte[] updatesData;
		
		arg0.markReadPosition();
		try {
			updatesData = arg0.readBytesByLength(arg0.readInt());
			
			arg0.removeReadMark();
			
			rendererProtocolHandler.onUpdateCompleted(arg0, updatesData);
		} catch (BufferUnderflowException e) {
			arg0.resetToReadMark();
		}
		
		return true;
	}
	
}
