package br.edu.univercidade.cc.xithcluster.communication;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.INonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.communication.ChainedTransactionalDataHandler;


public class StartFrameDataHandler extends ChainedTransactionalDataHandler<RendererMessageBroker> {

	private int frameIndex;

	public StartFrameDataHandler(RendererMessageBroker nextDataHandler) {
		super(nextDataHandler);
	}

	@Override
	protected boolean onHandleData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		frameIndex = arg0.readInt();
		
		return true;
	}

	@Override
	protected void onDataReady(INonBlockingConnection arg0) throws IOException {
		getNextDataHandler().onStartFrameCompleted(arg0, frameIndex);
	}
	
}
