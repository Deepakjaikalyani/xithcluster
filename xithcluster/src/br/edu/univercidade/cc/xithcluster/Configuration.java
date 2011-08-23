package br.edu.univercidade.cc.xithcluster;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Configuration {

	private static final String DEFAULT_LISTENING_INTERFACE = "localhost";

	private static final Integer DEFAULT_RENDERERS_LISTENING_PORT = 10000;

	private static final Integer DEFAULT_COMPOSER_LISTENING_PORT = 30000;
	
	public static String listeningInterface;
	
	public static Integer renderersListeningPort;
	
	public static Integer composerListeningPort;
	
	static {
		Properties properties;
		InputStream in;
		
		properties = new Properties();
		
		in = Configuration.class.getResourceAsStream("/xithcluster.properties");
		
		if (in != null) {
			try {
				properties.load(Configuration.class.getResourceAsStream("/xithcluster.properties"));
				listeningInterface = properties.getProperty("hostname", DEFAULT_LISTENING_INTERFACE);
				renderersListeningPort = Integer.parseInt(properties.getProperty("renderers.port", DEFAULT_RENDERERS_LISTENING_PORT.toString()));
				composerListeningPort = Integer.parseInt(properties.getProperty("composer.port", DEFAULT_COMPOSER_LISTENING_PORT.toString()));
			} catch (IOException e) {
				// TODO:
				throw new RuntimeException("Error loading configuration file");
			}
		} else {
			listeningInterface = DEFAULT_LISTENING_INTERFACE;
			renderersListeningPort = DEFAULT_RENDERERS_LISTENING_PORT;
			composerListeningPort = DEFAULT_COMPOSER_LISTENING_PORT;
		}
	}
	
}
