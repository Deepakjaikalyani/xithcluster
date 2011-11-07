package br.edu.univercidade.cc.xithcluster.communication;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.INonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.communication.ChainedTransactionalDataHandler;


public class StartFrameDataHandler extends ChainedTransactionalDataHandler<ComposerMessageBroker> {

	private long frameIndex;
	
	private long clockCount;
	
	public StartFrameDataHandler(ComposerMessageBroker nextDataHandler) {
		super(nextDataHandler);
	}

	@Override
	protected boolean onHandleData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		frameIndex = arg0.readLong();
		clockCount = arg0.readLong();
		
		return true;
	}

	@Override
	protected void onDataReady(INonBlockingConnection arg0) throws IOException {
		getNextDataHandler().onStartFrameCompleted(arg0, frameIndex, clockCount);
	}
	
}
