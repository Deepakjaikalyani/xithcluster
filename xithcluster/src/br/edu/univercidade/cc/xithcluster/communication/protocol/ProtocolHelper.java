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
			
			if (ordinal >= 0 && ordinal < recordTypes.length) {
				recordType = recordTypes[ordinal];
			} else {
				recordType = RecordType.UNKNOWN;
			}
			
			connection.removeReadMark();
		} catch (BufferUnderflowException e) {
			connection.resetToReadMark();
		}
		
		return recordType;
	}
	
	public static void writeRecord(INonBlockingConnection connection, RecordType recordType, Object... fields) throws IOException {
		byte[] buffer;
		
		connection.write(recordType.ordinal());
		connection.flush();
		
		for (Object field : fields) {
			if (field == null) {
				continue;
			}
			
			if (field instanceof Integer) {
				connection.write((Integer) field);
			} else if (field instanceof byte[]) {
				buffer = (byte[]) field;
				connection.write(buffer.length);
				connection.write(buffer);
			} else {
				throw new IllegalArgumentException("Field type not supported: " + field.getClass());
			}
		}
		
		connection.flush();
	}
	
	public static Object[] readRecordFields(INonBlockingConnection connection, Class<?>... fieldClasses) throws IOException {
		Object[] fields;
		Class<?> fieldClass;
		int length;
		
		if (fieldClasses.length == 0) {
			throw new IllegalArgumentException("You must inform at least one field class");
		}
		
		connection.markReadPosition();
		fields = new Object[fieldClasses.length];
		try {
			for (int i = 0; i < fields.length; i++) {
				fieldClass = fieldClasses[i];
				
				if (fieldClass == Integer.class) {
					fields[i] = connection.readInt();
				} else if (fieldClass == byte[].class) {
					length = connection.readInt();
					fields[i] = connection.readBytesByLength(length);
				}
			}
			
			connection.removeReadMark();
		} catch (BufferUnderflowException e) {
			connection.resetToReadMark();
			
			return null;
		}
		
		return fields;
	}
	
}
