package br.edu.univercidade.cc.xithcluster.communication;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.INonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.communication.ChainedSafeDataHandler;

public final class StartSessionDataHandler extends ChainedSafeDataHandler<RendererMessageBroker> {
	
	private int id;
	
	private int screenWidth;
	
	private int screenHeight;
	
	private double targetFPS;
	
	private byte[] pointOfViewData;
	
	private byte[] sceneData;
	
	public StartSessionDataHandler(RendererMessageBroker nextDataHandler) {
		super(nextDataHandler);
	}
	
	@Override
	protected boolean onHandleData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		id = arg0.readInt();
		screenWidth = arg0.readInt();
		screenHeight = arg0.readInt();
		targetFPS = arg0.readDouble();
		pointOfViewData = arg0.readBytesByLength(arg0.readInt());
		sceneData = arg0.readBytesByLength(arg0.readInt());
		
		return true;
	}

	@Override
	protected void onDataReady(INonBlockingConnection arg0) throws IOException {
		getNextDataHandler().onStartSessionCompleted(arg0, id, screenWidth, screenHeight, targetFPS, pointOfViewData, sceneData);
	}
	
}
