package br.edu.univercidade.cc.xithcluster;

import org.xsocket.connection.INonBlockingConnection;

public abstract class Component {
	
	protected INonBlockingConnection connection;
	
	protected Component(INonBlockingConnection connection) {
		if (connection == null) {
			throw new IllegalArgumentException();
		}
		
		this.connection = connection;
		
		decorateConnection();
		
		ConnectionHandlingFacade.getInstance().handleConnection(this.connection);
	}
	
	private void decorateConnection() {
		connection.setAutoflush(false);
	}

	public abstract void startSession();
	
	public abstract void closeSession();
	
}
