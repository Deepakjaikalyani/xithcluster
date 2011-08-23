package br.edu.univercidade.cc.xithcluster;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class RendererConfiguration {

	private static final String DEFAULT_MASTER_HOSTNAME = "localhost";

	private static final String DEFAULT_COMPOSER_HOSTNAME = "localhost";

	private static final Integer DEFAULT_MASTER_PORT = 10000;

	private static final Integer DEFAULT_COMPOSER_PORT = 30000;
	
	public static String masterHostname;
	
	public static String composerHostname;
	
	public static Integer masterPort;
	
	public static Integer composerPort;
	
	static {
		Properties properties;
		InputStream in;
		
		properties = new Properties();
		
		in = Configuration.class.getResourceAsStream("/rendererApp.properties");
		
		if (in != null) {
			try {
				properties.load(in);
				masterHostname = properties.getProperty("master.hostname", DEFAULT_MASTER_HOSTNAME);
				composerHostname = properties.getProperty("composer.hostname", DEFAULT_COMPOSER_HOSTNAME);
				masterPort = Integer.parseInt(properties.getProperty("master.port", DEFAULT_MASTER_PORT.toString()));
				composerPort = Integer.parseInt(properties.getProperty("master.port", DEFAULT_COMPOSER_PORT.toString()));
			} catch (IOException e) {
				// TODO:
				throw new RuntimeException("Error loading configuration file");
			}
		} else {
			masterHostname = DEFAULT_MASTER_HOSTNAME;
			composerHostname = DEFAULT_COMPOSER_HOSTNAME;
			masterPort = DEFAULT_MASTER_PORT;
			composerPort = DEFAULT_COMPOSER_PORT;
		}
	}
	
}
