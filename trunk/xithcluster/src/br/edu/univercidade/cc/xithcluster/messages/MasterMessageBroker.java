package br.edu.univercidade.cc.xithcluster.messages;

import br.edu.univercidade.cc.xithcluster.NetworkManager;

public class MasterMessageBroker extends MessageBroker {
	
	public MasterMessageBroker(NetworkManager networkManager) {
		register(MessageType.SESSION_STARTED, SessionStartedMessageHandler.class);
		register(MessageType.FINISHED_FRAME, FinishedFrameMessageHandler.class);
	}
	
}
