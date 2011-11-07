package br.edu.univercidade.cc.xithcluster.messages;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.INonBlockingConnection;


public class FinishedFrameDataHandler extends MessageHandler<MasterMessageBroker> {

	private long frameIndex;
	
	public FinishedFrameDataHandler(MasterMessageBroker nextDataHandler) {
		super(nextDataHandler);
	}

	@Override
	protected boolean onHandleData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		frameIndex = arg0.readLong();
		
		return true;
	}

	@Override
	protected void onDataReady(INonBlockingConnection arg0) throws IOException {
		getMessageBroker().onFinishedFrameCompleted(arg0, frameIndex);
	}
	
}
