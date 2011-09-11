package br.edu.univercidade.cc.xithcluster.communication.protocol;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.INonBlockingConnection;

public final class StartSessionContentHandler extends ContentHandler {
	
	private int id;
	
	private byte[] pointOfViewData;
	
	private byte[] lightSourcesData;
	
	private byte[] geometriesData;
	
	public StartSessionContentHandler(RendererProtocolHandler rendererProtocolHandler) {
		super(rendererProtocolHandler);
	}
	
	@Override
	protected boolean onHandleContent(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		id = arg0.readInt();
		pointOfViewData = arg0.readBytesByLength(arg0.readInt());
		lightSourcesData = arg0.readBytesByLength(arg0.readInt());
		geometriesData = arg0.readBytesByLength(arg0.readInt());
		
		return true;
	}

	@Override
	protected void onContentReady(INonBlockingConnection arg0) throws IOException {
		((RendererProtocolHandler) getPreviousHandler()).onStartSessionCompleted(arg0, id, pointOfViewData, lightSourcesData, geometriesData);
	}
	
}
