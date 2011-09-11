package br.edu.univercidade.cc.xithcluster;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ComposerConfiguration {

	private static final String DEFAULT_MASTER_LISTENING_ADDRESS = "localhost";

	private static final Integer DEFAULT_MASTER_LISTENING_PORT = 20000;
	
	private static final String DEFAULT_RENDERERS_CONNECTION_ADDRESS = "localhost";

	private static final Integer DEFAULT_RENDERERS_CONNECTION_PORT = 30000;	

	public static String masterListenAddress;
	
	public static Integer masterListenPort;

	public static String renderersConnectAddress;

	public static int renderersConnectPort;
	
	static {
		Properties properties;
		InputStream in;
		
		properties = new Properties();
		
		in = ComposerConfiguration.class.getResourceAsStream("/composerApp.properties");
		
		if (in != null) {
			try {
				properties.load(in);
				masterListenAddress = properties.getProperty("master.listening.address", DEFAULT_MASTER_LISTENING_ADDRESS);
				masterListenPort = Integer.parseInt(properties.getProperty("master.listening.port", DEFAULT_MASTER_LISTENING_PORT.toString()));
				renderersConnectAddress = properties.getProperty("renderers.connection.address", DEFAULT_RENDERERS_CONNECTION_ADDRESS);
				renderersConnectPort = Integer.parseInt(properties.getProperty("renderers.connection.port", DEFAULT_RENDERERS_CONNECTION_PORT.toString()));
			} catch (IOException e) {
				// TODO:
				throw new RuntimeException("Error loading configuration file");
			}
		} else {
			masterListenAddress = DEFAULT_MASTER_LISTENING_ADDRESS;
			masterListenPort = DEFAULT_MASTER_LISTENING_PORT;
			renderersConnectAddress = DEFAULT_RENDERERS_CONNECTION_ADDRESS;
			renderersConnectPort = DEFAULT_RENDERERS_CONNECTION_PORT;
		}
	}
	
}
