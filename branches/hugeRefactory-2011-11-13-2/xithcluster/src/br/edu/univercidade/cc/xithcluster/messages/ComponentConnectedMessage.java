package br.edu.univercidade.cc.xithcluster.messages;

import org.xsocket.connection.INonBlockingConnection;


public class ComponentConnectedMessage extends Message {

	private INonBlockingConnection connection;

	public ComponentConnectedMessage(INonBlockingConnection connection) {
		this.connection = connection;
	}
	
}
