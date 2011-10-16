package br.edu.univercidade.cc.xithcluster;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RendererConfigurationReader extends ConfigurationReader {
	
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
	public void parseCommandLine(String[] args) throws RuntimeException {
		if (args.length != 6) {
			throw new IllegalArgumentException();
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
	public void readPropertiesFile() throws FileNotFoundException, IOException {
		Properties properties;
		InputStream in;
		
		properties = new Properties();
		
		in = RendererConfigurationReader.class.getResourceAsStream("/rendererApp.properties");
		
		if (in == null) {
			try {
				in = new FileInputStream("rendererApp.properties");
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Error reading properties file", e);
			}
		}
		
		properties.load(in);
		
		masterListeningAddress = getParameterAndCheckIfNullOrEmptyString(properties, "master.listening.address");
		masterListeningPort = getParameterAndConvertToIntegerSafely(properties, "master.listening.port");
		composerListeningAddress = getParameterAndCheckIfNullOrEmptyString(properties, "composer.listening.address");
		composerListeningPort = getParameterAndConvertToIntegerSafely(properties, "composer.listening.port");
		compositionOrder = getParameterAndConvertToIntegerSafely(properties, "composition.order");
		compressionMethod = getParameterAndConvertToEnumSafely(properties, "compression.method", CompressionMethod.values());
	}
	
}
