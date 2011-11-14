package br.edu.univercidade.cc.xithcluster.messages;

import org.xsocket.connection.INonBlockingConnection;

public class ComponentDisconnectedMessage extends Message {

	private INonBlockingConnection connection;

	public ComponentDisconnectedMessage(INonBlockingConnection connection) {
		this.connection = connection;
	}
	
}
