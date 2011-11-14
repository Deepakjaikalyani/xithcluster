package br.edu.univercidade.cc.xithcluster.messages;

import java.io.IOException;
import org.xsocket.connection.INonBlockingConnection;

public class FinishedFrameMessageHandler extends MessageHandler {
	
	private long frameIndex;
	
	public FinishedFrameMessageHandler(MessageBroker nextDataHandler) {
		super(nextDataHandler);
	}
	
	@Override
	protected void fetchData(INonBlockingConnection arg0) throws IOException {
		frameIndex = arg0.readLong();
	}
	
	@Override
	protected Message assembleMessage() {
		return new FinishedFrameMessage(frameIndex);
	}
	
}
