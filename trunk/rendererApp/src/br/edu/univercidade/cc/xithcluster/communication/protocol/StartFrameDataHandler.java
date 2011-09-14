package br.edu.univercidade.cc.xithcluster.communication.protocol;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.INonBlockingConnection;


public class StartFrameDataHandler extends ChainedSafeDataHandler<RendererProtocolHandler> {

	private int frameIndex;

	public StartFrameDataHandler(RendererProtocolHandler nextDataHandler) {
		super(nextDataHandler);
	}

	@Override
	protected boolean onHandleData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		frameIndex = arg0.readInt();
		
		return true;
	}

	@Override
	protected void onDataReady(INonBlockingConnection arg0) throws IOException {
		getNextDataHandler().onStartFrameCompleted(frameIndex);
	}
	
}
