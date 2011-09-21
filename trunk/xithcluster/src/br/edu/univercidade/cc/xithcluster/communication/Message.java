package br.edu.univercidade.cc.xithcluster.communication;

import org.xsocket.connection.INonBlockingConnection;

public final class Message {
	
	private MessageType type;
	
	private INonBlockingConnection source;
	
	private Object[] parameters;
	
	Message(MessageType type, INonBlockingConnection source, Object... parameters) {
		this.type = type;
		this.source = source;
		this.parameters = parameters;
	}

	public MessageType getType() {
		return type;
	}
	
	public void setType(MessageType type) {
		this.type = type;
	}
	
	public INonBlockingConnection getSource() {
		return source;
	}
	
	public void setSource(INonBlockingConnection source) {
		this.source = source;
	}
	
	public Object[] getParameters() {
		return parameters;
	}
	
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
	
}
