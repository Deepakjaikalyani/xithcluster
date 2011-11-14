package br.edu.univercidade.cc.xithcluster.messages;

import br.edu.univercidade.cc.xithcluster.Component;

public class SessionStartedMessage extends Message {
	
	private int componentId;
	
	public SessionStartedMessage(int componentId) {
		super();
		this.componentId = componentId;
	}
	
	public int getComponentId() {
		return componentId;
	}
	
	@Override
	public void sendTo(Component component) {
	}
	
}
