package br.edu.univercidade.cc.xithcluster;

import org.xsocket.connection.INonBlockingConnection;

public class Composer extends Component {

	protected Composer(INonBlockingConnection connection) {
		super(connection);
	}

	@Override
	public void startSession() {
	}

	@Override
	public void closeSession() {
	}
	
}
