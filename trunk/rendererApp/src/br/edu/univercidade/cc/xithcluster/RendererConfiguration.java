package br.edu.univercidade.cc.xithcluster;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class RendererConfiguration {

	private static final String DEFAULT_MASTER_LISTENING_ADDRESS = "localhost";

	private static final Integer DEFAULT_MASTER_LISTENING_PORT = 11111;

	public static String masterListeningAddress;
	
	public static Integer masterListeningPort;
	
	static {
		Properties properties;
		InputStream in;
		
		properties = new Properties();
		
		in = RendererConfiguration.class.getResourceAsStream("/rendererApp.properties");
		
		if (in != null) {
			try {
				properties.load(in);
				masterListeningAddress = properties.getProperty("master.listening.address", DEFAULT_MASTER_LISTENING_ADDRESS);
				masterListeningPort = Integer.parseInt(properties.getProperty("master.listening.port", DEFAULT_MASTER_LISTENING_PORT.toString()));
			} catch (IOException e) {
				// TODO:
				throw new RuntimeException("Error loading configuration file");
			}
		} else {
			masterListeningAddress = DEFAULT_MASTER_LISTENING_ADDRESS;
			masterListeningPort = DEFAULT_MASTER_LISTENING_PORT;
		}
	}
	
}
