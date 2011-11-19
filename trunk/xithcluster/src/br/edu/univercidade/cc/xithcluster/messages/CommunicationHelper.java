package br.edu.univercidade.cc.xithcluster.messages;

import java.io.IOException;
import org.xsocket.connection.INonBlockingConnection;

public final class CommunicationHelper {
	
	private CommunicationHelper() {
	}
	
	public static MessageType safelyReadMessageType(INonBlockingConnection connection) throws IOException {
		final MessageType[] recordTypes = MessageType.values();
		MessageType recordType;
		int ordinal;
		
		recordType = null;
		
		connection.markReadPosition();
		try {
			ordinal = connection.readInt();
			
			recordType = recordTypes[ordinal];
		
			connection.removeReadMark();
		} catch (Throwable t) {
			connection.resetToReadMark();
		}
		
		return recordType;
	}
	
}
