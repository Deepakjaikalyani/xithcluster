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
import br.edu.univercidade.cc.xithcluster.Configuration;
import br.edu.univercidade.cc.xithcluster.communication.MasterNetworkManager;

public final class MasterProtocolHandler implements IConnectHandler, IDataHandler, IDisconnectHandler {

	private Logger log = Logger.getLogger(MasterNetworkManager.class);
	
	private MasterNetworkManager masterNetworkManager;
	
	public MasterProtocolHandler(MasterNetworkManager networkManager) {
		this.masterNetworkManager = networkManager;
	}
	
	private boolean isRendererConnection(INonBlockingConnection arg0) {
		return arg0.getLocalPort() == Configuration.renderersListeningPort;
	}
	
	private boolean isComposerConnection(INonBlockingConnection arg0) {
		return arg0.getLocalPort() == Configuration.composerListeningPort;
	}
	
	public void sendStartSessionMessage(INonBlockingConnection rendererConnection, byte[] pointOfViewData, byte[] lightSourcesData, byte[] geometriesData) throws IOException, ClosedChannelException, SocketTimeoutException {
		rendererConnection.write(RecordType.START_SESSION.ordinal());
		rendererConnection.flush();
		
		rendererConnection.write(pointOfViewData);
		rendererConnection.write(lightSourcesData);
		rendererConnection.write(geometriesData);
		rendererConnection.flush();
	}
	
	@Override
	public boolean onDisconnect(INonBlockingConnection arg0) throws IOException {
		if (isRendererConnection(arg0)) {
			return masterNetworkManager.onRendererDisconnect(arg0);
		} else if (isComposerConnection(arg0)) {
			return masterNetworkManager.onComposerDisconnect();
		} else {
			throw new AssertionError("Should never happen!");
		}
	}

	@Override
	public boolean onData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		RecordType recordType;
		
		recordType = ProtocolHelper.readRecordType(arg0);
		
		if (recordType == null) {
			return true;
		}
		
		switch (recordType) {
		case SESSION_STARTED:
			return masterNetworkManager.onSessionStarted(arg0);
		case FRAME_FINISHED:
			return masterNetworkManager.onFrameFinished(arg0);
		default:
			log.error("Invalid/Unknown record");
			
			return false;
		}
	}

	@Override
	public boolean onConnect(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
		if (isRendererConnection(arg0)) {
			return masterNetworkManager.onRendererConnected(arg0);
		} else if (isComposerConnection(arg0)) {
			return masterNetworkManager.onComposerConnected(arg0);
		} else {
			log.error("Unknown connection refused");
			
			return false;
		}
	}

	public void sendStartSessionMessage(INonBlockingConnection composerConnection) throws BufferOverflowException, IOException {
		composerConnection.write(RecordType.START_SESSION.ordinal());
		composerConnection.flush();
	}

	public void sendUpdateMessage(INonBlockingConnection rendererConnection, byte[] updateData) throws BufferOverflowException, IOException {
		rendererConnection.write(RecordType.UPDATE.ordinal());
		rendererConnection.flush();
		
		rendererConnection.write(updateData);
		rendererConnection.flush();
	}

	public void sendGetFramesToSkipMessage(INonBlockingConnection composerConnection) throws BufferOverflowException, IOException {
		composerConnection.write(RecordType.GET_FRAMES_TO_SKIP.ordinal());
		composerConnection.flush();
	}

	public void sendStartFrameMessage(INonBlockingConnection rendererConnection) throws BufferOverflowException, IOException {
		rendererConnection.write(RecordType.START_FRAME.ordinal());
		rendererConnection.flush();
	}
	
}
