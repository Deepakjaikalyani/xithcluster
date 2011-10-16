package br.edu.univercidade.cc.xithcluster;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class XithClusterConfigurationReader extends ConfigurationReader {
	
	private String listeningAddress;
	
	private int renderersConnectionPort;
	
	private int composerConnectionPort;
	
	private int targetScreenWidth;
	
	private int targetScreenHeight;
	
	private float targetFPS;
	
	public String getListeningAddress() {
		return listeningAddress;
	}
	
	public int getRenderersConnectionPort() {
		return renderersConnectionPort;
	}
	
	public int getComposerConnectionPort() {
		return composerConnectionPort;
	}
	
	public int getTargetScreenWidth() {
		return targetScreenWidth;
	}
	
	public int getTargetScreenHeight() {
		return targetScreenHeight;
	}
	
	public float getTargetFPS() {
		return targetFPS;
	}
	
	@Override
	public void parseCommandLine(String args[]) throws RuntimeException {
		if (args.length != 6) {
			throw new IllegalArgumentException();
		}
		
		listeningAddress = args[0];
		
		checkIfNullOrEmptyString("listeningAddress", listeningAddress);
		
		renderersConnectionPort = convertToIntegerSafely("renderersConnectionPort", args[1]);
		composerConnectionPort = convertToIntegerSafely("composerConnectionPort", args[2]);
		targetScreenWidth = convertToIntegerSafely("targetScreenWidth", args[3]);
		targetScreenHeight = convertToIntegerSafely("targetScreenHeight", args[4]);
		targetFPS = convertToIntegerSafely("targetFPS", args[5]);
	}
	
	@Override
	public void readPropertiesFile() throws FileNotFoundException, IOException {
		Properties properties;
		InputStream in;
		
		properties = new Properties();
		
		in = XithClusterConfigurationReader.class.getResourceAsStream("/xithcluster.properties");
		
		if (in == null) {
			in = new FileInputStream("xithcluster.properties");
		}
		
		properties.load(in);
		
		listeningAddress = getParameterAndCheckIfNullOrEmptyString(properties, "listening.address");
		renderersConnectionPort = getParameterAndConvertToIntegerSafely(properties, "renderers.connection.port");
		composerConnectionPort = getParameterAndConvertToIntegerSafely(properties, "composer.connection.port");
		targetScreenWidth = getParameterAndConvertToIntegerSafely(properties, "screen.width");
		targetScreenHeight = getParameterAndConvertToIntegerSafely(properties, "screen.height");
		targetFPS = getParameterAndConvertToFloatSafely(properties, "target.fps");
	}
	
}
