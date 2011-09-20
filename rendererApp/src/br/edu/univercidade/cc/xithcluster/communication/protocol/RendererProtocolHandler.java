package br.edu.univercidade.cc.xithcluster.communication.protocol;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.apache.log4j.Logger;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.INonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.CompressionMethod;
import br.edu.univercidade.cc.xithcluster.communication.RendererNetworkManager;

public final class RendererProtocolHandler implements IDataHandler {

	private final Logger log = Logger.getLogger(RendererProtocolHandler.class);
	
	private final RendererNetworkManager rendererNetworkManager;
	
	public RendererProtocolHandler(RendererNetworkManager rendererNetworkManager) {
		this.rendererNetworkManager = rendererNetworkManager;
	}

	@Override
	public boolean onData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		MessageType messageType;
		
		messageType = ProtocolHelper.readMessageType(arg0);
		
		if (messageType == null) {
			return true;
		}
		
		switch (messageType) {
		case START_SESSION:
			arg0.setHandler(new StartSessionDataHandler(this));
			
			return true;
		case START_FRAME:
			arg0.setHandler(new StartFrameDataHandler(this));
			
			return true;
		case UPDATE:
			arg0.setHandler(new UpdateDataHandler(this));
			
			return true;
		default:
			log.error("Invalid/Unknown message");
			
			return false;
		}
	}

	void onStartSessionCompleted(int id, int screenWidth, int screenHeight, double targetFPS, byte[] pointOfViewData, byte[] sceneData) throws IOException {
		rendererNetworkManager.onStartSession(id, screenWidth, screenHeight, targetFPS, pointOfViewData, sceneData);
	}

	void onUpdateCompleted(byte[] updatesData) throws IOException {
		rendererNetworkManager.onUpdate(updatesData);
	}
	
	void onStartFrameCompleted(int frameIndex) {
		rendererNetworkManager.onStartFrame(frameIndex);
	}

	public void sendSessionStartedMessage(INonBlockingConnection masterConnection) throws BufferOverflowException, IOException {
		masterConnection.write(MessageType.SESSION_STARTED.ordinal());
		masterConnection.flush();
	}

	public void sendFrameFinishedMessage(INonBlockingConnection masterConnection) throws BufferOverflowException, IOException {
		masterConnection.write(MessageType.FINISHED_FRAME.ordinal());
		masterConnection.flush();
	}

	public void sendNewImageMessage(INonBlockingConnection composerConnection, int frameIndex, CompressionMethod compressionMethod, byte[] colorAndAlphaBuffer, byte[] depthBuffer) throws BufferOverflowException, IOException {
		composerConnection.write(MessageType.NEW_IMAGE.ordinal());
		composerConnection.flush();
		
		composerConnection.write(frameIndex);
		composerConnection.write(compressionMethod.ordinal());
		composerConnection.write(colorAndAlphaBuffer.length);
		composerConnection.write(colorAndAlphaBuffer);
		composerConnection.write(depthBuffer.length);
		composerConnection.write(depthBuffer);
		composerConnection.flush();
	}

	public void sendSetCompositionOrderMessage(INonBlockingConnection composerConnection, int compositionOrder) throws BufferOverflowException, IOException {
		composerConnection.write(MessageType.SET_COMPOSITION_ORDER.ordinal());
		composerConnection.flush();
		
		composerConnection.write(compositionOrder);
		composerConnection.flush();
	}

}
