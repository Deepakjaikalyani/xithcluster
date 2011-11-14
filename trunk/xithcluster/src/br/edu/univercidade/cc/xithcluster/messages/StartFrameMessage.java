package br.edu.univercidade.cc.xithcluster.messages;

import br.edu.univercidade.cc.xithcluster.Component;
import br.edu.univercidade.cc.xithcluster.synchronization.SynchronizationToken;

public class StartFrameMessage extends Message {
	
	private SynchronizationToken synchronizationToken;
	
	public StartFrameMessage(SynchronizationToken synchronizationToken) {
		this.synchronizationToken = synchronizationToken;
	}
	
	@Override
	public void sendTo(Component component) {
		// connection.write(MessageType.START_FRAME.ordinal());
		// connection.flush();
		//
		// connection.write(frameIndex);
		// connection.write(clockCount);
		// connection.flush();
	}
	
}
