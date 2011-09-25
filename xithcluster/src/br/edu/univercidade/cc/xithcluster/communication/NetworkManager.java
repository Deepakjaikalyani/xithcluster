package br.edu.univercidade.cc.xithcluster.communication;

import java.util.Queue;
import org.xsocket.connection.INonBlockingConnection;

public abstract class NetworkManager {
	
	protected enum SessionState {
		CLOSED, STARTING, STARTED
	}
	
	protected INonBlockingConnection masterConnection;
	
	public NetworkManager() {
		super();
	}
	
	private void checkMasterNodeConnection() {
		if (!masterConnection.isOpen()) {
			System.err.println("Master node disconnected");
			
			// TODO:
			System.exit(-1);
		}
	}
	
	/*
	 * ================================ 
	 * Network messages processing loop
	 * ================================
	 */
	protected abstract void processMessages(Queue<Message> messages);
	
	public void update() {
		Queue<Message> messages;
		
		checkMasterNodeConnection();
		
		messages = MessageQueue.startReadingMessages();
		
		processMessages(messages);
		
		MessageQueue.stopReadingMessages();
	}
	
}