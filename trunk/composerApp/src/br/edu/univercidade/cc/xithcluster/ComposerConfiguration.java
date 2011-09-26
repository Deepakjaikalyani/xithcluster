package br.edu.univercidade.cc.xithcluster;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ComposerConfiguration {

	private static final String DEFAULT_WINDOW_TITLE = "XithCluster Application";
	
	private static final String DEFAULT_MASTER_LISTENING_ADDRESS = "localhost";

	private static final Integer DEFAULT_MASTER_LISTENING_PORT = 22222;
	
	private static final String DEFAULT_RENDERERS_CONNECTION_ADDRESS = "localhost";

	private static final Integer DEFAULT_RENDERERS_CONNECTION_PORT = 33333;

	private static final Boolean DEFAULT_DISPLAY_FPS_COUNTER = true;
	
	private static final String DEFAULT_COMPOSITION_STRATEGY_CLASSNAME = SimpleCompositionStrategy.class.getName();
	
	public static final String windowTitle;	

	public static final String masterListeningAddress;
	
	public static final int masterListeningPort;

	public static final String renderersConnectionAddress;

	public static final int renderersConnectionPort;
	
	public static final boolean displayFPSCounter;

	public static final String compositionStrategyClassName;
	
	static {
		Properties properties;
		InputStream in;
		
		properties = new Properties();
		
		in = ComposerConfiguration.class.getResourceAsStream("/composerApp.properties");
		
		if (in != null) {
			try {
				properties.load(in);
				windowTitle = properties.getProperty("window.title", DEFAULT_WINDOW_TITLE);
				masterListeningAddress = properties.getProperty("master.listening.address", DEFAULT_MASTER_LISTENING_ADDRESS);
				masterListeningPort = Integer.parseInt(properties.getProperty("master.listening.port", DEFAULT_MASTER_LISTENING_PORT.toString()));
				renderersConnectionAddress = properties.getProperty("renderers.connection.address", DEFAULT_RENDERERS_CONNECTION_ADDRESS);
				renderersConnectionPort = Integer.parseInt(properties.getProperty("renderers.connection.port", DEFAULT_RENDERERS_CONNECTION_PORT.toString()));
				displayFPSCounter = Boolean.parseBoolean(properties.getProperty("display.fps.counter", DEFAULT_DISPLAY_FPS_COUNTER.toString()));
				compositionStrategyClassName = properties.getProperty("composition.strategy.classname", DEFAULT_COMPOSITION_STRATEGY_CLASSNAME);
			} catch (IOException e) {
				// TODO:
				throw new RuntimeException("Error loading configuration file");
			}
		} else {
			windowTitle = DEFAULT_WINDOW_TITLE;
			masterListeningAddress = DEFAULT_MASTER_LISTENING_ADDRESS;
			masterListeningPort = DEFAULT_MASTER_LISTENING_PORT;
			renderersConnectionAddress = DEFAULT_RENDERERS_CONNECTION_ADDRESS;
			renderersConnectionPort = DEFAULT_RENDERERS_CONNECTION_PORT;
			displayFPSCounter = DEFAULT_DISPLAY_FPS_COUNTER;
			compositionStrategyClassName = DEFAULT_COMPOSITION_STRATEGY_CLASSNAME;
		}
	}
	
}
