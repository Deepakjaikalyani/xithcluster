package br.edu.univercidade.cc.xithcluster.messages;


public class MasterMessageBroker extends MessageBroker {
	
	public MasterMessageBroker() {
		register(MessageType.SESSION_STARTED, SessionStartedMessageHandler.class);
		register(MessageType.FINISHED_FRAME, FinishedFrameMessageHandler.class);
	}
	
}
