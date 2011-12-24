package br.edu.univercidade.cc.xithcluster.messages;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.INonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.messages.MessageHandler;


public class RendererStartFrameDataHandler extends MessageHandler<RendererMessageBroker> {

	private long frameIndex;
	
	private long clockCount;

	public RendererStartFrameDataHandler(RendererMessageBroker nextDataHandler) {
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
		getMessageBroker().onStartFrameCompleted(arg0, frameIndex, clockCount);
	}
	
}
