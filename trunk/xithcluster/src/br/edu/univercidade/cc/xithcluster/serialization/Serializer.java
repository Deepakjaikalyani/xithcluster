package br.edu.univercidade.cc.xithcluster.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Serializer<T> {
	
	public byte[] serialize(T data) throws IOException {
		ByteArrayOutputStream buffer;
		DataOutputStream out;
		
		buffer = new ByteArrayOutputStream();
		out = new DataOutputStream(buffer);
		
		doSerialization(data, out);
		
		return buffer.toByteArray();
	}
	
	public T deserialize(byte[] arg0) throws IOException {
		return doDeserialization(new DataInputStream(new ByteArrayInputStream(arg0)));
	}
	
	protected abstract void doSerialization(T data, DataOutputStream out) throws IOException;
	
	protected abstract T doDeserialization(DataInputStream in) throws IOException;
}
