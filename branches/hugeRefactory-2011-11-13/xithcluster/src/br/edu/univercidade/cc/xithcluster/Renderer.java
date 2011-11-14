package br.edu.univercidade.cc.xithcluster;

import org.xsocket.connection.INonBlockingConnection;

public class Renderer extends Component {

	public Renderer(INonBlockingConnection connection) {
		super(connection); 
	}

	@Override
	public void startSession() {
	}

	@Override
	public void closeSession() {
	}
	
}
