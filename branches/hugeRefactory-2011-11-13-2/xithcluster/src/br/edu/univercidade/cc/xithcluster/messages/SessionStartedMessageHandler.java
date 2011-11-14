package br.edu.univercidade.cc.xithcluster.messages;

import java.io.IOException;
import org.xsocket.connection.INonBlockingConnection;

public class SessionStartedMessageHandler extends MessageHandler {
	
	public SessionStartedMessageHandler(MessageBroker messageBroker) {
		super(messageBroker);
	}
	
	@Override
	protected void fetchData(INonBlockingConnection arg0) throws IOException {
	}
	
	@Override
	protected Message assembleMessage() {
		return null;
	}
	
}
