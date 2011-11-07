package br.edu.univercidade.cc.xithcluster.messages;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.INonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.messages.TransactionalDataHandler;

public class SetCompositionOrderDataHandler extends TransactionalDataHandler<ComposerMessageBroker> {
	
	private int compositionOrder;

	public SetCompositionOrderDataHandler(ComposerMessageBroker nextDataHandler) {
		super(nextDataHandler);
	}
	
	@Override
	protected boolean onHandleData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		compositionOrder = arg0.readInt();
		
		return true;
	}
	
	@Override
	protected void onDataReady(INonBlockingConnection arg0) throws IOException {
		getMessageBroker().onSetCompositionOrderCompleted(arg0, compositionOrder);
	}
	
}
