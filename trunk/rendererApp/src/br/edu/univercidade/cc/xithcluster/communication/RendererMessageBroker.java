package br.edu.univercidade.cc.xithcluster.communication;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.apache.log4j.Logger;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.INonBlockingConnection;

public final class RendererMessageBroker implements IDataHandler {

	private final Logger log = Logger.getLogger(RendererMessageBroker.class);
	
	@Override
	public boolean onData(INonBlockingConnection connection) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		MessageType messageType;
		
		messageType = CommunicationHelper.safelyReadMessageType(connection);
		
		if (messageType == null) {
			return true;
		}
		
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
		MessageQueue.postMessage(new Message(MessageType.START_SESSION, connection, id, screenWidth, screenHeight, targetFPS, pointOfViewData, sceneData));
	}

	void onUpdateCompleted(INonBlockingConnection connection, byte[] updatesData) throws IOException {
		MessageQueue.postMessage(new Message(MessageType.UPDATE, connection, updatesData));
	}
	
	void onStartFrameCompleted(INonBlockingConnection connection, int frameIndex) {
		MessageQueue.postMessage(new Message(MessageType.START_FRAME, connection, frameIndex));
	}

}
