package br.edu.univercidade.cc.xithcluster;

import org.xsocket.connection.INonBlockingConnection;

public class ConnectionStateChange {
	
	private ConnectionState connectionState;
	
	private INonBlockingConnection sourceConnection;
	
	public ConnectionStateChange(ConnectionState connectionState, INonBlockingConnection sourceConnection) {
		this.connectionState = connectionState;
		this.sourceConnection = sourceConnection;
	}
	
	public ConnectionState getConnectionState() {
		return connectionState;
	}
	
	public INonBlockingConnection getSourceConnection() {
		return sourceConnection;
	}
	
	public int getComponentId() {
		return 0;
	}
	
}