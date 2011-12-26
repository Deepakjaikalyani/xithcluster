package br.edu.univercidade.cc.xithcluster.messages;

import org.xsocket.connection.INonBlockingConnection;

public final class Message implements Comparable<Message> {
	
	private Long creationTime;
	
	private MessageType type;
	
	private INonBlockingConnection source;
	
	private Object[] parameters;
	
	public Message(MessageType type, INonBlockingConnection source, Object... parameters) {
		creationTime = System.nanoTime();
		
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

	@Override
	public int compareTo(Message o) {
		return creationTime.compareTo(o.creationTime);
	}
	
}
