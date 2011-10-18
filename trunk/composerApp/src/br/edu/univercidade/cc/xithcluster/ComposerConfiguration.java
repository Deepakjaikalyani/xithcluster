package br.edu.univercidade.cc.xithcluster;

import java.util.Properties;
import br.edu.univercidade.cc.xithcluster.configuration.BadParameterException;
import br.edu.univercidade.cc.xithcluster.configuration.Configuration;

public class ComposerConfiguration extends Configuration {
	
	private String windowTitle;
	
	private String masterListeningAddress;
	
	private int masterListeningPort;
	
	private String renderersConnectionAddress;
	
	private int renderersConnectionPort;
	
	private boolean displayFPSCounter;
	
	private CompositionStrategy compositionStrategy;
	
	public String getWindowTitle() {
		return windowTitle;
	}
	
	public String getMasterListeningAddress() {
		return masterListeningAddress;
	}
	
	public int getMasterListeningPort() {
		return masterListeningPort;
	}
	
	public String getRenderersConnectionAddress() {
		return renderersConnectionAddress;
	}
	
	public int getRenderersConnectionPort() {
		return renderersConnectionPort;
	}
	
	public boolean isDisplayFPSCounter() {
		return displayFPSCounter;
	}
	
	public CompositionStrategy getCompositionStrategy() {
		return compositionStrategy;
	}
	
	@SuppressWarnings("unchecked")
	private CompositionStrategy createCompositionStrategy(String compositionStrategyClassName) {
		Class<? extends CompositionStrategy> compositionStrategyClass;
		
		if (compositionStrategyClassName == null) {
			throw new IllegalArgumentException();
		}
		
		try {
			compositionStrategyClass = (Class<? extends CompositionStrategy>) Class.forName(compositionStrategyClassName);
			
			try {
				return compositionStrategyClass.newInstance();
			} catch (Exception e) {
				// TODO:
				throw new RuntimeException("Error creating composition strategy", e);
			}
		} catch (ClassNotFoundException e) {
			// TODO:
			throw new RuntimeException("Error creating composition strategy", e);
		}
	}
	
	@Override
	protected void parseFromCommandLine(String[] args) throws BadParameterException {
		if (args == null || args.length != getArgsLength()) {
			throw new AssertionError();
		}
		
		// TODO:
	}
	
	@Override
	protected void loadFromProperties(Properties properties) throws BadParameterException {
		if (properties == null) {
			throw new AssertionError();
		}
		
		windowTitle = getParameterAndCheckIfNullOrEmptyString(properties, "window.title");
		masterListeningAddress = getParameterAndCheckIfNullOrEmptyString(properties, "master.listening.address");
		masterListeningPort = getParameterAndConvertToIntegerSafely(properties, "master.listening.port");
		renderersConnectionAddress = getParameterAndCheckIfNullOrEmptyString(properties, "renderers.connection.address");
		renderersConnectionPort = getParameterAndConvertToIntegerSafely(properties, "renderers.connection.port");
		displayFPSCounter = getParameterAndConvertToBooleanSafely(properties, "display.fps.counter");
		compositionStrategy = createCompositionStrategy(getParameterAndCheckIfNullOrEmptyString(properties, "composition.strategy.classname"));
	}

	@Override
	protected int getArgsLength() {
		return 7;
	}

	@Override
	protected String getPropertiesFileName() {
		return "composerApp.properties";
	}
	
}
