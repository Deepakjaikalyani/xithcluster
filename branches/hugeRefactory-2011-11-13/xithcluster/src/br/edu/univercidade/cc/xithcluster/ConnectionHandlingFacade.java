package br.edu.univercidade.cc.xithcluster;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.List;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IConnectHandler;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.IDisconnectHandler;
import org.xsocket.connection.INonBlockingConnection;

public class ConnectionHandlingFacade implements IConnectHandler, IDisconnectHandler, IDataHandler {
	
	private static ConnectionHandlingFacade instance = null;
	
	private List<ConnectionListener> connectionListeners = new ArrayList<ConnectionListener>();
	
	private List<DataListener> dataListeners = new ArrayList<DataListener>();
	
	public static ConnectionHandlingFacade getInstance() {
		if (instance == null) {
			instance = new ConnectionHandlingFacade();
		}
		
		return instance;
	}
	
	public void handleConnection(INonBlockingConnection connection) {
		try {
			connection.setHandler(this);
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error setting the global connection handler", e);
		}
	}
	
	public void addConnectionListener(ConnectionListener connectionListener) {
		connectionListeners.add(connectionListener);
	}
	
	public void addDataListener(DataListener dataListener) {
		dataListeners.add(dataListener);
	}
	
	@Override
	public boolean onConnect(INonBlockingConnection connection) throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
		for (ConnectionListener connectionListener : connectionListeners) {
			connectionListener.handleConnection(connection);
		}
		
		return true;
	}
	
	@Override
	public boolean onDisconnect(INonBlockingConnection connection) throws IOException {
		for (ConnectionListener connectionListener : connectionListeners) {
			connectionListener.handleDisconnection(connection);
		}
		
		return true;
	}
	
	@Override
	public boolean onData(INonBlockingConnection connection) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		for (DataListener dataListener : dataListeners) {
			dataListener.handleData(connection);
		}
		
		return true;
	}
	
}
