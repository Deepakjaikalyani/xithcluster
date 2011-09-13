package br.edu.univercidade.cc.xithcluster;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class RendererConfiguration {

	private static final String DEFAULT_MASTER_LISTENING_ADDRESS = "localhost";

	private static final Integer DEFAULT_MASTER_LISTENING_PORT = 11111;
	
	private static final Integer DEFAULT_COMPOSITION_ORDER = 0;
	
	private static final CompressionMethod DEFAULT_COMPRESSION_METHOD = CompressionMethod.NONE;

	public static String masterListeningAddress;
	
	public static int masterListeningPort;

	public static int compositionOrder;
	
	public static CompressionMethod compressionMethod;
	
	static {
		Properties properties;
		InputStream in;
		
		properties = new Properties();
		
		in = RendererConfiguration.class.getResourceAsStream("/rendererApp.properties");
		
		if (in != null) {
			try {
				properties.load(in);
				masterListeningAddress = properties.getProperty("master.listening.address", DEFAULT_MASTER_LISTENING_ADDRESS);
				masterListeningPort = Integer.parseInt(properties.getProperty("master.listening.port", DEFAULT_MASTER_LISTENING_PORT.toString()));
				compositionOrder = Integer.parseInt(properties.getProperty("composition.order", DEFAULT_COMPOSITION_ORDER.toString()));
				compressionMethod = CompressionMethod.valueOf(properties.getProperty("compression.method", DEFAULT_COMPRESSION_METHOD.toString()));
			} catch (IOException e) {
				// TODO:
				throw new RuntimeException("Error loading configuration file");
			}
		} else {
			masterListeningAddress = DEFAULT_MASTER_LISTENING_ADDRESS;
			masterListeningPort = DEFAULT_MASTER_LISTENING_PORT;
			compositionOrder = DEFAULT_COMPOSITION_ORDER;
			compressionMethod = DEFAULT_COMPRESSION_METHOD;
		}
	}
	
}
