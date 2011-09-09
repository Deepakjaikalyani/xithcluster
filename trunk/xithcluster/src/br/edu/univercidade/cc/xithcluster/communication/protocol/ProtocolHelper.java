package br.edu.univercidade.cc.xithcluster.communication.protocol;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import org.xsocket.connection.INonBlockingConnection;

public final class ProtocolHelper {
	
	private ProtocolHelper() {
	}
	
	public static RecordType readRecordType(INonBlockingConnection connection) throws IOException {
		final RecordType[] recordTypes = RecordType.values();
		RecordType recordType;
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
