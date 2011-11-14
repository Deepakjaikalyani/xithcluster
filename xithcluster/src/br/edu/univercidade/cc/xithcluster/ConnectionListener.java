package br.edu.univercidade.cc.xithcluster;

import org.xsocket.connection.INonBlockingConnection;

public interface ConnectionListener {

	void handleConnection(INonBlockingConnection connection);

	void handleDisconnection(INonBlockingConnection connection);

}
