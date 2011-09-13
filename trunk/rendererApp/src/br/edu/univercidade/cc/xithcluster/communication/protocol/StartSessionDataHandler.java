package br.edu.univercidade.cc.xithcluster.communication.protocol;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.INonBlockingConnection;

public final class StartSessionDataHandler extends ChainedSafeDataHandler<RendererProtocolHandler> {
	
	private int id;
	
	private String composerHostname;
	
	private int composerPort;
	
	private byte[] pointOfViewData;
	
	private byte[] lightSourcesData;
	
	private byte[] geometriesData;
	
	public StartSessionDataHandler(RendererProtocolHandler rendererProtocolHandler) {
		super(rendererProtocolHandler);
	}
	
	@Override
	protected boolean onHandleData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		id = arg0.readInt();
		composerHostname = arg0.readStringByDelimiter(STRING_DELIMITER);
		composerPort = arg0.readInt();
		pointOfViewData = arg0.readBytesByLength(arg0.readInt());
		lightSourcesData = arg0.readBytesByLength(arg0.readInt());
		geometriesData = arg0.readBytesByLength(arg0.readInt());
		
		return true;
	}

	@Override
	protected void onDataReady(INonBlockingConnection arg0) throws IOException {
		getNextDataHandler().onStartSessionCompleted(id, composerHostname, composerPort, pointOfViewData, lightSourcesData, geometriesData);
	}
	
}