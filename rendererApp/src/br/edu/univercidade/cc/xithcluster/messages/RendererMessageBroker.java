package br.edu.univercidade.cc.xithcluster.messages;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.xsocket.connection.INonBlockingConnection;

public final class RendererMessageBroker extends MessageBroker {
	
	private Logger log = Logger.getLogger(RendererMessageBroker.class);
	
	@Override
	public boolean receiveMessage(INonBlockingConnection connection, MessageType messageType) throws IOException {
		switch (messageType) {
		case START_SESSION:
			connection.setHandler(new StartSessionDataHandler(this));
			
			return true;
		case START_FRAME:
			connection.setHandler(new StartFrameDataHandler(this));
			
			return true;
		case UPDATE:
			connection.setHandler(new UpdateDataHandler(this));
			
			return true;
		default:
			log.error("Invalid/Unknown message");
			
			return false;
		}
	}
	
	void onStartSessionCompleted(INonBlockingConnection connection, int id, int screenWidth, int screenHeight, double targetFPS, byte[] pointOfViewData, byte[] sceneData) throws IOException {
		enqueueMessage(new Message(MessageType.START_SESSION, connection, id, screenWidth, screenHeight, targetFPS, pointOfViewData, sceneData));
	}
	
	void onUpdateCompleted(INonBlockingConnection connection, byte[] updatesData) throws IOException {
		enqueueMessage(new Message(MessageType.UPDATE, connection, updatesData));
	}
	
	void onStartFrameCompleted(INonBlockingConnection connection, long frameIndex, long clockCount) {
		enqueueMessage(new Message(MessageType.START_FRAME, connection, frameIndex, clockCount));
	}
	
}
