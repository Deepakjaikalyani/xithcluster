package br.edu.univercidade.cc.xithcluster;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class XithClusterConfiguration {

	private static final String DEFAULT_LISTENING_ADDRESS = "localhost";

	private static final Integer DEFAULT_RENDERERS_CONNECTION_PORT = 11111;

	private static final Integer DEFAULT_COMPOSER_CONNECTION_PORT = 33333;
	
	private static final Integer DEFAULT_SCREEN_WIDTH = 800;
	
	private static final Integer DEFAULT_SCREEN_HEIGHT = 600;
	
	private static final Float DEFAULT_TARGET_FPS = 80.0f;

	public static String listeningAddress;
	
	public static int renderersConnectionPort;
	
	public static int composerConnectionPort;
	
	public static int screenWidth;
	
	public static int screenHeight;
	
	public static float targetFPS;
	
	static {
		Properties properties;
		InputStream in;
		
		properties = new Properties();
		
		in = XithClusterConfiguration.class.getResourceAsStream("/xithcluster.properties");
		
		if (in == null) {
			try {
				in = new FileInputStream("xithcluster.properties");
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Error reading properties file", e);
			}
		}
		
		try {
			properties.load(XithClusterConfiguration.class.getResourceAsStream("/xithcluster.properties"));
			listeningAddress = properties.getProperty("listening.address", DEFAULT_LISTENING_ADDRESS);
			renderersConnectionPort = Integer.parseInt(properties.getProperty("renderers.connection.port", DEFAULT_RENDERERS_CONNECTION_PORT.toString()));
			composerConnectionPort = Integer.parseInt(properties.getProperty("composer.connection.port", DEFAULT_COMPOSER_CONNECTION_PORT.toString()));
			screenWidth = Integer.parseInt(properties.getProperty("screen.width", DEFAULT_SCREEN_WIDTH.toString()));
			screenHeight = Integer.parseInt(properties.getProperty("screen.height", DEFAULT_SCREEN_HEIGHT.toString()));
			targetFPS = Float.parseFloat(properties.getProperty("target.fps", DEFAULT_TARGET_FPS.toString()));
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error loading configuration file");
		}
	}
	
}
