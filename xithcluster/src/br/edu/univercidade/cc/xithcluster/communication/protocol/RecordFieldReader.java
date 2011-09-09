package br.edu.univercidade.cc.xithcluster.communication.protocol;

import java.nio.BufferUnderflowException;
import org.xsocket.connection.INonBlockingConnection;


public interface RecordFieldReader {
	
	void readField(INonBlockingConnection connection) throws BufferUnderflowException;
	
}
