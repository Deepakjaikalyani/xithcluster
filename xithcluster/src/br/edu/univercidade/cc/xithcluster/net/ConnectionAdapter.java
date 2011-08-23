package br.edu.univercidade.cc.xithcluster.net;

import java.io.IOException;
import org.xsocket.connection.BlockingConnection;
import org.xsocket.connection.IBlockingConnection;
import org.xsocket.connection.INonBlockingConnection;

public class ConnectionAdapter {
	
	private INonBlockingConnection nonBlockingConnection;
	
	private IBlockingConnection blockingConnection;
	
	public ConnectionAdapter(INonBlockingConnection nonBlockingConnection) throws IOException {
		this.nonBlockingConnection = nonBlockingConnection;
		blockingConnection = new BlockingConnection(this.nonBlockingConnection);
	}
	
	public INonBlockingConnection getNonBlockingConnection() {
		return nonBlockingConnection;
	}
	
	public IBlockingConnection getBlockingConnection() {
		return blockingConnection;
	}
	
}
