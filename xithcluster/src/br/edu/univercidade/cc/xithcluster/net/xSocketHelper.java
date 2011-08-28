package br.edu.univercidade.cc.xithcluster.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.BufferOverflowException;
import org.xsocket.connection.IBlockingConnection;
import org.xsocket.connection.IHandler;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.IServer;
import org.xsocket.connection.Server;

public final class xSocketHelper {
	
	public static final String SYN = "SYN";
	
	public static final String ACK = "ACK";
	
	public static final String STRING_DELIMITER = "\0";
	
	public static class xSocketServerThread extends Thread {
		
		private IServer server;
		
		public xSocketServerThread(IServer server) {
			super(server);
			
			this.server = server;
			
			start();
		}
		
		public IServer getServer() {
			return server;
		}
		
		public void setServer(IServer server) {
			this.server = server;
		}
		
	}
	
	private xSocketHelper() {
	}
	
	public static void write(INonBlockingConnection arg0, String arg1) throws BufferOverflowException, IOException {
		if (arg0.isOpen()) {
			arg0.write(arg1 + STRING_DELIMITER);
		}
	}
	
	public static void write(INonBlockingConnection arg0, int arg1) throws BufferOverflowException, IOException {
		if (arg0.isOpen()) {
			arg0.write(arg1);
		}
	}
	
	public static void write(INonBlockingConnection arg0, byte[] arg1) throws BufferOverflowException, IOException {
		if (arg0.isOpen()) {
			arg0.write(arg1.length);
			arg0.write(arg1);
		}
	}
	
	public static void write(IBlockingConnection arg0, String arg1) throws BufferOverflowException, IOException {
		if (arg0.isOpen()) {
			arg0.write(arg1 + STRING_DELIMITER);
		}
	}
	
	public static String readString(INonBlockingConnection arg0) throws UnsupportedEncodingException, IOException {
		if (arg0.isOpen()) {
			return arg0.readStringByDelimiter(STRING_DELIMITER);
		} else {
			return null;
		}
	}
	
	public static byte[] readBytes(INonBlockingConnection arg0) throws IOException {
		if (arg0.isOpen()) {
			return arg0.readBytesByLength(arg0.readInt());
		} else {
			return null;
		}
	}
	
	public static String readString(IBlockingConnection arg0) throws UnsupportedEncodingException, IOException {
		if (arg0.isOpen()) {
			return arg0.readStringByDelimiter(STRING_DELIMITER);
		} else {
			return null;
		}
	}
	
	public static byte[] readBytes(IBlockingConnection arg0) throws IOException {
		if (arg0.isOpen()) {
			return arg0.readBytesByLength(arg0.readInt());
		} else {
			return null;
		}
	}
	
	public static xSocketServerThread startListening(String hostname, int port, IHandler handler) throws UnknownHostException, IOException {
		return new xSocketServerThread(new Server(hostname, port, handler));
	}
	
	public static int readInt(INonBlockingConnection arg0) throws SocketTimeoutException, IOException {
		if (arg0.isOpen()) {
			return arg0.readInt();
		} else {
			return -1;
		}
	}

	public static int readInt(IBlockingConnection arg0) throws SocketTimeoutException, IOException {
		if (arg0.isOpen()) {
			return arg0.readInt();
		} else {
			return -1;
		}
	}

}
