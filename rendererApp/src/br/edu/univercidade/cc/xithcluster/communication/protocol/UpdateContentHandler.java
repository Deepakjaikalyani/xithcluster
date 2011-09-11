package br.edu.univercidade.cc.xithcluster.communication.protocol;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.INonBlockingConnection;

public final class UpdateContentHandler extends ContentHandler {
	
	private byte[] updatesData;
	
	public UpdateContentHandler(RendererProtocolHandler rendererProtocolHandler) {
		super(rendererProtocolHandler);
	}
	
	@Override
	protected boolean onHandleContent(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		updatesData = arg0.readBytesByLength(arg0.readInt());
		
		return true;
	}

	@Override
	protected void onContentReady(INonBlockingConnection arg0) throws IOException {
		((RendererProtocolHandler) getPreviousHandler()).onUpdateCompleted(arg0, updatesData);
	}
	
}
