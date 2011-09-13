package br.edu.univercidade.cc.xithcluster.communication.protocol;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.INonBlockingConnection;

public final class StartSessionDataHandler extends ChainedSafeDataHandler<ComposerProtocolHandler> {
	
	private int screenWidth;
	
	private int screenHeight;
	
	private double targetFPS;
	
	public StartSessionDataHandler(ComposerProtocolHandler composerProtocolHandler) {
		super(composerProtocolHandler);
	}
	
	@Override
	protected boolean onHandleData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		screenWidth = arg0.readInt();
		screenHeight = arg0.readInt();
		targetFPS = arg0.readDouble();
		
		return true;
	}

	@Override
	protected void onDataReady(INonBlockingConnection arg0) throws IOException {
		getNextDataHandler().onStartSessionCompleted(screenWidth, screenHeight, targetFPS);
	}
	
}
