package br.edu.univercidade.cc.xithcluster.communication.protocol;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import org.xsocket.connection.INonBlockingConnection;

public final class ProtocolHelper {
	
	private ProtocolHelper() {
	}
	
	public static MessageType readMessageType(INonBlockingConnection connection) throws IOException {
		final MessageType[] recordTypes = MessageType.values();
		MessageType recordType;
		int ordinal;
		
		recordType = null;
		
		connection.markReadPosition();
		try {
			ordinal = connection.readInt();
			
			recordType = recordTypes[ordinal];
		
			connection.removeReadMark();
		} catch (BufferUnderflowException e) {
			connection.resetToReadMark();
		}
		
		return recordType;
	}
	
}
