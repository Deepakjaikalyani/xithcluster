package br.edu.univercidade.cc.xithcluster.communication.protocol;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.INonBlockingConnection;

public class SetCompositionOrderDataHandler extends ChainedSafeDataHandler<ComposerProtocolHandler> {
	
	private int compositionOrder;

	public SetCompositionOrderDataHandler(ComposerProtocolHandler nextDataHandler) {
		super(nextDataHandler);
	}
	
	@Override
	protected boolean onHandleData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		compositionOrder = arg0.readInt();
		
		return true;
	}
	
	@Override
	protected void onDataReady(INonBlockingConnection arg0) throws IOException {
		getNextDataHandler().onSetCompositionOrderCompleted(arg0, compositionOrder);
	}
	
}
