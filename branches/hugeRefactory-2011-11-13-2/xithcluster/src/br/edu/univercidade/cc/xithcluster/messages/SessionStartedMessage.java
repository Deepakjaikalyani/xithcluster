package br.edu.univercidade.cc.xithcluster.messages;

public class SessionStartedMessage extends Message {
	
	private int componentId;
	
	public SessionStartedMessage(int componentId) {
		super();
		this.componentId = componentId;
	}
	
	public int getComponentId() {
		return componentId;
	}
	
}
