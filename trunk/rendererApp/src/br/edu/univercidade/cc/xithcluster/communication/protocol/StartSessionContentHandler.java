package br.edu.univercidade.cc.xithcluster.communication.protocol;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.INonBlockingConnection;

public final class StartSessionContentHandler implements IDataHandler {
	
	private RendererProtocolHandler rendererProtocolHandler;
	
	public StartSessionContentHandler(RendererProtocolHandler rendererProtocolHandler) {
		this.rendererProtocolHandler = rendererProtocolHandler;
	}
	
	@Override
	public boolean onData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		int id;
		byte[] pointOfViewData;
		byte[] lightSourcesData;
		byte[] geometriesData;
		
		arg0.markReadPosition();
		try {
			id = arg0.readInt();
			pointOfViewData = arg0.readBytesByLength(arg0.readInt());
			lightSourcesData = arg0.readBytesByLength(arg0.readInt());
			geometriesData = arg0.readBytesByLength(arg0.readInt());
			
			arg0.removeReadMark();
			
			rendererProtocolHandler.onStartSessionCompleted(arg0, id, pointOfViewData, lightSourcesData, geometriesData);
		} catch (BufferUnderflowException e) {
			arg0.resetToReadMark();
		}
		
		return true;
	}
	
}
