package br.edu.univercidade.cc.xithcluster;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class XithClusterConfiguration {

	private static final String DEFAULT_LISTENING_ADDRESS = "localhost";

	private static final Integer DEFAULT_RENDERERS_CONNECTION_PORT = 10000;

	private static final Integer DEFAULT_COMPOSER_CONNECTION_PORT = 30000;
	
	public static String listeningAddress;
	
	public static Integer renderersConnectionPort;
	
	public static Integer composerConnectionPort;
	
	static {
		Properties properties;
		InputStream in;
		
		properties = new Properties();
		
		in = XithClusterConfiguration.class.getResourceAsStream("/xithcluster.properties");
		
		if (in != null) {
			try {
				properties.load(XithClusterConfiguration.class.getResourceAsStream("/xithcluster.properties"));
				listeningAddress = properties.getProperty("listening.address", DEFAULT_LISTENING_ADDRESS);
				renderersConnectionPort = Integer.parseInt(properties.getProperty("renderers.connection.port", DEFAULT_RENDERERS_CONNECTION_PORT.toString()));
				composerConnectionPort = Integer.parseInt(properties.getProperty("composer.connection.port", DEFAULT_COMPOSER_CONNECTION_PORT.toString()));
			} catch (IOException e) {
				// TODO:
				throw new RuntimeException("Error loading configuration file");
			}
		} else {
			listeningAddress = DEFAULT_LISTENING_ADDRESS;
			renderersConnectionPort = DEFAULT_RENDERERS_CONNECTION_PORT;
			composerConnectionPort = DEFAULT_COMPOSER_CONNECTION_PORT;
		}
	}
	
}
