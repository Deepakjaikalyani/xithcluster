package br.edu.univercidade.cc.xithcluster;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.log4j.xml.DOMConfigurator;
import org.jagatoo.input.InputSystemException;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.base.Xith3DEnvironment;
import br.edu.univercidade.cc.xithcluster.communication.RendererNetworkManager;

public class RendererApp {

	private static final String LOG4J_CONFIGURATION_FILE = "rendererApp-log4j.xml";
	
	/*
	 * =============== 
	 * 		MAIN 
	 * ===============
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) throws InputSystemException {
		RendererConfigurationReader rendererConfigurationReader;
		RendererLoop rendererLoop;
		Xith3DEnvironment xith3dEnvironment;
		RendererNetworkManager networkManager;
		
		initializeLog4j();
		
		rendererConfigurationReader = new RendererConfigurationReader();
		
		if (args.length == 6) {
			try {
				rendererConfigurationReader.parseCommandLine(args);
			} catch (RuntimeException e) {
				printCommandLineHelpAndExit();
			}
		} else {
			try {
				rendererConfigurationReader.readPropertiesFile();
			} catch (FileNotFoundException e) {
				printErrorMessageAndExit("Properties file not found", e);
			} catch (IOException e) {
				printErrorMessageAndExit("I/O error reading properties file", e);
			} catch (RuntimeException e) {
				printErrorMessageAndExit("Invalid properties file", e);
			}
		}
		
		rendererLoop = new RendererLoop();
		
		xith3dEnvironment = new Xith3DEnvironment(new Tuple3f(0.0f, 0.0f, 3.0f), new Tuple3f(0.0f, 0.0f, 0.0f), new Tuple3f(0.0f, 1.0f, 0.0f), rendererLoop);
		
		networkManager = new RendererNetworkManager(rendererConfigurationReader.getMasterListeningAddress(), rendererConfigurationReader.getMasterListeningPort(), rendererConfigurationReader.getComposerListeningAddress(), rendererConfigurationReader.getComposerListeningPort(), rendererConfigurationReader.getCompositionOrder(), rendererConfigurationReader.getCompressionMethod());
		
		rendererLoop.setNetworkManager(networkManager);
		
		rendererLoop.begin();
	}
	
	private static void initializeLog4j() {
		if (new File(LOG4J_CONFIGURATION_FILE).exists()) {
			DOMConfigurator.configure(LOG4J_CONFIGURATION_FILE);
		} else {
			System.err.println("Log4j not initialized: \"rendererApp-log4j.xml\" could not be found");
		}
	}
	
	private static void printErrorMessageAndExit(String errorMessage, Exception e) {
		System.err.println(errorMessage);
		e.printStackTrace(System.err);
		System.exit(-1);
	}
	
	private static void printCommandLineHelpAndExit() {
		System.err.println("java -jar rendererApp.jar <masterListeningAddress> <masterListeningPort> <composerListeningAddress> <composerListeningPort> <compositionOrder> <compressionMethod>");
		System.exit(-1);
	}
	
}
