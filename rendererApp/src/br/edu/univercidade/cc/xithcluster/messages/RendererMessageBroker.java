package br.edu.univercidade.cc.xithcluster.messages;

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
			setMessageHandler(connection, new RendererStartSessionDataHandler(this));
			
			return true;
		case START_FRAME:
			setMessageHandler(connection, new RendererStartFrameDataHandler(this));
			
			return true;
		case UPDATE:
			setMessageHandler(connection, new UpdateDataHandler(this));
			
			return true;
		default:
			log.error("Invalid/Unknown message");
			
			return false;
		}
	}

	private void setMessageHandler(INonBlockingConnection connection, MessageHandler<?> messageHandler) throws IOException, ClosedChannelException, MaxReadSizeExceededException {
		connection.setHandler(messageHandler);
		messageHandler.onData(connection);
	}

	void onStartSessionCompleted(INonBlockingConnection connection, int id, int screenWidth, int screenHeight, double targetFPS, byte[] pointOfViewData, byte[] sceneData) throws IOException {
		MessageQueue.postMessage(new Message(MessageType.START_SESSION, connection, id, screenWidth, screenHeight, targetFPS, pointOfViewData, sceneData));
	}

	void onUpdateCompleted(INonBlockingConnection connection, byte[] updatesData) throws IOException {
		MessageQueue.postMessage(new Message(MessageType.UPDATE, connection, updatesData));
	}
	
	void onStartFrameCompleted(INonBlockingConnection connection, long frameIndex, long clockCount) {
		MessageQueue.postMessage(new Message(MessageType.START_FRAME, connection, frameIndex, clockCount));
	}

}
