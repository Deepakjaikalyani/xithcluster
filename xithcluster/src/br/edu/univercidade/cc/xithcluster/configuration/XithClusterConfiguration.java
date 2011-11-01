package br.edu.univercidade.cc.xithcluster.configuration;

import java.util.Properties;

public final class XithClusterConfiguration extends Configuration {
	
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
	protected void parseFromCommandLine(String args[]) throws BadParameterException {
		if (args == null || args.length != getArgsLength()) {
			throw new AssertionError();
		}
		
		listeningAddress = args[0];
		
		checkIfNullOrEmptyString("listeningAddress", listeningAddress);
		
		renderersConnectionPort = convertToIntegerSafely("renderersConnectionPort", args[1]);
		composerConnectionPort = convertToIntegerSafely("composerConnectionPort", args[2]);
		targetScreenWidth = convertToIntegerSafely("targetScreenWidth", args[3]);
		targetScreenHeight = convertToIntegerSafely("targetScreenHeight", args[4]);
		targetFPS = convertToFloatSafely("targetFPS", args[5]);
	}
	
	@Override
	protected void loadFromProperties(Properties properties) throws BadParameterException {
		if (properties == null) {
			throw new AssertionError();
		}
		
		listeningAddress = getParameterAndCheckIfNullOrEmptyString(properties, "listening.address");
		renderersConnectionPort = getParameterAndConvertToIntegerSafely(properties, "renderers.connection.port");
		composerConnectionPort = getParameterAndConvertToIntegerSafely(properties, "composer.connection.port");
		targetScreenWidth = getParameterAndConvertToIntegerSafely(properties, "screen.width");
		targetScreenHeight = getParameterAndConvertToIntegerSafely(properties, "screen.height");
		targetFPS = getParameterAndConvertToFloatSafely(properties, "target.fps");
	}

	@Override
	protected int getArgsLength() {
		return 6;
	}

	@Override
	protected String getPropertiesFileName() {
		return "xithcluster.properties";
	}
	
}
