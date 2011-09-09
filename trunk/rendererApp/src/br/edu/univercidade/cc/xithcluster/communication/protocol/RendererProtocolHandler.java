package br.edu.univercidade.cc.xithcluster.communication.protocol;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.apache.log4j.Logger;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.INonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.communication.RendererNetworkManager;

public final class RendererProtocolHandler implements IDataHandler {

	private Logger log = Logger.getLogger(RendererNetworkManager.class);
	
	private RendererNetworkManager rendererNetworkManager;
	
	public RendererProtocolHandler(RendererNetworkManager rendererNetworkManager) {
		this.rendererNetworkManager = rendererNetworkManager;
	}

	@Override
	public boolean onData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		RecordType recordType;
		
		recordType = ProtocolHelper.readRecordType(arg0);
		
		if (recordType == null) {
			return true;
		}
		
		switch (recordType) {
		case START_SESSION:
			arg0.setHandler(new StartSessionContentHandler(this));
			
			return true;
		case START_FRAME:
			rendererNetworkManager.onStartFrame();
			
			return true;
		case UPDATE:
			arg0.setHandler(new UpdateContentHandler(this));
			
			return true;
		default:
			log.error("Invalid/Unknown message");
			
			return false;
		}
	}

	public void onStartSessionCompleted(INonBlockingConnection connection, int id, byte[] pointOfViewData, byte[] lightSourcesData, byte[] geometriesData) throws IOException {
		connection.setHandler(this);
		rendererNetworkManager.onStartSession(id, pointOfViewData, lightSourcesData, geometriesData);
	}

	public void onUpdateCompleted(INonBlockingConnection connection, byte[] updatesData) throws IOException {
		connection.setHandler(this);
		rendererNetworkManager.onUpdate(updatesData);
	}

	public void sendSessionStartedMessage(INonBlockingConnection masterConnection) throws BufferOverflowException, IOException {
		masterConnection.write(RecordType.SESSION_STARTED.ordinal());
		masterConnection.flush();
	}

	public void sendFrameFinishedMessage(INonBlockingConnection masterConnection) throws BufferOverflowException, IOException {
		masterConnection.write(RecordType.FRAME_FINISHED.ordinal());
		masterConnection.flush();
	}

}
