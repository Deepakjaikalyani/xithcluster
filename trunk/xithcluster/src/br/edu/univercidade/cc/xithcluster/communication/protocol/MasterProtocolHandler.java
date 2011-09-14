package br.edu.univercidade.cc.xithcluster.communication.protocol;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.apache.log4j.Logger;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IConnectHandler;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.IDisconnectHandler;
import org.xsocket.connection.INonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.communication.MasterNetworkManager;

public final class MasterProtocolHandler implements IConnectHandler, IDataHandler, IDisconnectHandler {

	private Logger log = Logger.getLogger(MasterNetworkManager.class);
	
	private MasterNetworkManager masterNetworkManager;
	
	public MasterProtocolHandler(MasterNetworkManager networkManager) {
		this.masterNetworkManager = networkManager;
	}
	
	public void sendStartSessionMessage(
			INonBlockingConnection rendererConnection, 
			int rendererIndex, 
			int screenWidth, 
			int screenHeight,
			double targetFPS,
			byte[] pointOfViewData, 
			byte[] lightSourcesData, 
			byte[] geometriesData) 
	throws BufferOverflowException, ClosedChannelException, SocketTimeoutException, IOException {
		rendererConnection.write(MessageType.START_SESSION.ordinal());
		rendererConnection.flush();

		rendererConnection.write(rendererIndex);
		rendererConnection.write(screenWidth);
		rendererConnection.write(screenHeight);
		rendererConnection.write(targetFPS);
		rendererConnection.write(pointOfViewData.length);
		rendererConnection.write(pointOfViewData);
		rendererConnection.write(lightSourcesData.length);
		rendererConnection.write(lightSourcesData);
		rendererConnection.write(geometriesData.length);
		rendererConnection.write(geometriesData);
		rendererConnection.flush();
	}
	
	public void sendStartSessionMessage(
			INonBlockingConnection composerConnection, 
			int screenWidth, 
			int screenHeight,
			double targetFPS) 
	throws BufferOverflowException, ClosedChannelException, SocketTimeoutException, IOException {
		composerConnection.write(MessageType.START_SESSION.ordinal());
		composerConnection.flush();
		
		composerConnection.write(screenWidth);
		composerConnection.write(screenHeight);
		composerConnection.write(targetFPS);
		composerConnection.flush();
	}
	
	@Override
	public boolean onConnect(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
		return masterNetworkManager.onConnected(arg0);
	}
	
	@Override
	public boolean onData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		MessageType messageType;
		
		messageType = ProtocolHelper.readMessageType(arg0);
		
		if (messageType == null) {
			return true;
		}
		
		switch (messageType) {
		case SESSION_STARTED:
			return masterNetworkManager.onSessionStarted(arg0);
		case FINISHED_FRAME:
			arg0.setHandler(new FinishedFrameDataHandler(this));
			
			return true;
		default:
			log.error("Invalid/Unknown record");
			
			return false;
		}
	}

	@Override
	public boolean onDisconnect(INonBlockingConnection arg0) throws IOException {
		return masterNetworkManager.onDisconnected(arg0);
	}
	
	void onFinishedFrameCompleted(int frameIndex) {
		masterNetworkManager.onFinishedFrame(frameIndex);
	}

	public void sendUpdateMessage(INonBlockingConnection rendererConnection, byte[] updateData) throws BufferOverflowException, IOException {
		rendererConnection.write(MessageType.UPDATE.ordinal());
		rendererConnection.flush();
		
		rendererConnection.write(updateData);
		rendererConnection.flush();
	}

	public void sendGetFramesToSkipMessage(INonBlockingConnection composerConnection) throws BufferOverflowException, IOException {
		composerConnection.write(MessageType.GET_FRAMES_TO_SKIP.ordinal());
		composerConnection.flush();
	}

	public void sendStartFrameMessage(INonBlockingConnection connection, int frameIndex) throws BufferOverflowException, IOException {
		connection.write(MessageType.START_FRAME.ordinal());
		connection.flush();
		
		connection.write(frameIndex);
		connection.flush();
	}

}
