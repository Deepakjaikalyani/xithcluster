package br.edu.univercidade.cc.xithcluster.messages;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import org.apache.log4j.Logger;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.INonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.CompressionMethod;

public class ComposerMessageBroker extends MessageBroker {
	
	private Logger log = Logger.getLogger(ComposerMessageBroker.class);
	
	private int masterListeningPort;
	
	public ComposerMessageBroker(int masterListeningPort) {
		this.masterListeningPort = masterListeningPort;
	}
	
	private boolean isMyOwnConnection(INonBlockingConnection arg0) {
		return arg0.getRemotePort() == masterListeningPort;
	}
	
	@Override
	protected boolean handleComponentConnection(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
		if (!isMyOwnConnection(arg0)) {
			enqueueMessage(new Message(MessageType.CONNECTED, arg0));
		}
		
		return true;
	}
	
	@Override
	protected boolean receiveMessage(INonBlockingConnection connection, MessageType messageType) throws IOException {
		switch (messageType) {
		case START_SESSION:
			connection.setHandler(new StartSessionDataHandler(this));
			
			return true;
		case SET_COMPOSITION_ORDER:
			connection.setHandler(new SetCompositionOrderDataHandler(this));
			
			return true;
		case START_FRAME:
			connection.setHandler(new StartFrameDataHandler(this));
			
			return true;
		case NEW_IMAGE:
			connection.setHandler(new NewImageDataHandler(this));
			
			return true;
		default:
			log.error("Invalid/Unknown message");
			
			return false;
		}
	}
	
	@Override
	protected boolean handleComponentDisconnection(INonBlockingConnection connection) throws IOException {
		enqueueMessage(new Message(MessageType.DISCONNECTED, connection));
		
		return true;
	}
	
	void onStartSessionCompleted(INonBlockingConnection connection, int screenWidth, int screenHeight, double targetFPS) {
		enqueueMessage(new Message(MessageType.START_SESSION, connection, screenWidth, screenHeight, targetFPS));
	}
	
	void onNewImageCompleted(INonBlockingConnection connection, long frameIndex, CompressionMethod compressionMethod, byte[] colorAndAlphaBuffer, float[] depthBuffer) {
		enqueueMessage(new Message(MessageType.NEW_IMAGE, connection, frameIndex, compressionMethod, colorAndAlphaBuffer, depthBuffer));
	}
	
	void onSetCompositionOrderCompleted(INonBlockingConnection connection, int compositionOrder) {
		enqueueMessage(new Message(MessageType.SET_COMPOSITION_ORDER, connection, compositionOrder));
	}
	
	void onStartFrameCompleted(INonBlockingConnection connection, long frameIndex, long clockCount) {
		enqueueMessage(new Message(MessageType.START_FRAME, connection, frameIndex, clockCount));
	}
	
}
