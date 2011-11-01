package br.edu.univercidade.cc.xithcluster.configuration;

import java.util.Properties;
import br.edu.univercidade.cc.xithcluster.CompressionMethod;
import br.edu.univercidade.cc.xithcluster.configuration.BadParameterException;
import br.edu.univercidade.cc.xithcluster.configuration.Configuration;

public class RendererConfiguration extends Configuration {
	
	private String masterListeningAddress;
	
	private int masterListeningPort;
	
	private String composerListeningAddress;
	
	private int composerListeningPort;
	
	private int compositionOrder;
	
	private CompressionMethod compressionMethod;
	
	public String getMasterListeningAddress() {
		return masterListeningAddress;
	}
	
	public int getMasterListeningPort() {
		return masterListeningPort;
	}
	
	public String getComposerListeningAddress() {
		return composerListeningAddress;
	}
	
	public int getComposerListeningPort() {
		return composerListeningPort;
	}
	
	public int getCompositionOrder() {
		return compositionOrder;
	}
	
	public CompressionMethod getCompressionMethod() {
		return compressionMethod;
	}
	
	@Override
	protected void parseFromCommandLine(String[] args) throws BadParameterException {
		if (args == null || args.length != getArgsLength()) {
			throw new AssertionError();
		}
		
		masterListeningAddress = args[0];
		checkIfNullOrEmptyString("masterListeningAddress", masterListeningAddress);
		
		masterListeningPort = convertToIntegerSafely("masterListeningPort", args[1]);
		
		composerListeningAddress = args[2];
		checkIfNullOrEmptyString("composerListeningAddress", composerListeningAddress);
		
		composerListeningPort = convertToIntegerSafely("composerListeningPort", args[3]);
		compositionOrder = convertToIntegerSafely("composerListeningPort", args[4]);
		compressionMethod = convertToEnumSafely("compressionMethod", args[5], CompressionMethod.values());
	}
	
	@Override
	protected void loadFromProperties(Properties properties) throws BadParameterException {
		if (properties == null) {
			throw new AssertionError();
		}
		
		masterListeningAddress = getParameterAndCheckIfNullOrEmptyString(properties, "master.listening.address");
		masterListeningPort = getParameterAndConvertToIntegerSafely(properties, "master.listening.port");
		composerListeningAddress = getParameterAndCheckIfNullOrEmptyString(properties, "composer.listening.address");
		composerListeningPort = getParameterAndConvertToIntegerSafely(properties, "composer.listening.port");
		compositionOrder = getParameterAndConvertToIntegerSafely(properties, "composition.order");
		compressionMethod = getParameterAndConvertToEnumSafely(properties, "compression.method", CompressionMethod.values());
	}

	@Override
	protected int getArgsLength() {
		return 6;
	}

	@Override
	protected String getPropertiesFileName() {
		return "rendererApp.properties";
	}
	
}
