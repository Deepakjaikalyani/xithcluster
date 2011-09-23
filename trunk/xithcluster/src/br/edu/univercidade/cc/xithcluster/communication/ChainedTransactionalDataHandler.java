package br.edu.univercidade.cc.xithcluster.communication;

import java.io.IOException;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.INonBlockingConnection;

public abstract class ChainedTransactionalDataHandler<NextDataHandler extends IDataHandler> extends TransactionalDataHandler {
	
	private NextDataHandler nextDataHandler;
	
	public ChainedTransactionalDataHandler(NextDataHandler nextDataHandler) {
		this.nextDataHandler = nextDataHandler;
	}
	
	public NextDataHandler getNextDataHandler() {
		return nextDataHandler;
	}

	@Override
	protected void afterDataHandling(INonBlockingConnection arg0) throws IOException {
		arg0.setHandler(nextDataHandler);
	}
	
}
