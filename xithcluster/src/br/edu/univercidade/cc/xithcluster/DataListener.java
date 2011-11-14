package br.edu.univercidade.cc.xithcluster;

import java.io.IOException;
import org.xsocket.connection.INonBlockingConnection;

public interface DataListener {

	void handleData(INonBlockingConnection connection) throws IOException;
	
}
