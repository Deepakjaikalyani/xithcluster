package br.edu.univercidade.cc.xithcluster.messages;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.INonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.messages.MessageHandler;

public final class UpdateDataHandler extends MessageHandler<RendererMessageBroker> {
	
	private byte[] updatesData;
	
	public UpdateDataHandler(RendererMessageBroker nextDataHandler) {
		super(nextDataHandler);
	}
	
	@Override
	protected boolean fetchData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		updatesData = arg0.readBytesByLength(arg0.readInt());
		
		return true;
	}

	@Override
	protected void onDataReady(INonBlockingConnection arg0) throws IOException {
		getMessageBroker().onUpdateCompleted(arg0, updatesData);
	}
	
}
