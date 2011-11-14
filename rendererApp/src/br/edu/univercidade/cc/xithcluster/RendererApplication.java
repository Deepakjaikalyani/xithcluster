package br.edu.univercidade.cc.xithcluster;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.xml.DOMConfigurator;
import org.jagatoo.input.InputSystemException;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.base.Xith3DEnvironment;
import br.edu.univercidade.cc.xithcluster.configuration.CommandLineParsingException;
import br.edu.univercidade.cc.xithcluster.configuration.PropertiesFileLoadingException;
import br.edu.univercidade.cc.xithcluster.configuration.RendererConfiguration;

public class RendererApplication {

	private static final String LOG4J_CONFIGURATION_FILE = "rendererApp-log4j.xml";
	
	/*
	 * =============== 
	 * 		MAIN 
	 * ===============
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) throws InputSystemException {
		RendererConfiguration rendererConfiguration;
		RendererLoop rendererLoop;
		Xith3DEnvironment xith3dEnvironment;
		RendererNetworkManager networkManager;
		
		initializeLog4j();
		
		rendererConfiguration = new RendererConfiguration();
		
		try {
			rendererConfiguration.load(args);
		} catch (CommandLineParsingException e) {
			printErrorMessage("Error parsing command line", e.getBadParameterException());
			printCommandLineHelp();
			System.exit(-1);
		} catch (PropertiesFileLoadingException e) {
			printErrorMessage("Error reading properties file", e.getBadParameterException());
			System.exit(-1);
		} catch (IOException e) {
			printErrorMessage("I/O error reading properties file", e);
			System.exit(-1);
		}
		
		rendererLoop = new RendererLoop();
		
		xith3dEnvironment = new Xith3DEnvironment(new Tuple3f(0.0f, 0.0f, 3.0f), new Tuple3f(0.0f, 0.0f, 0.0f), new Tuple3f(0.0f, 1.0f, 0.0f), rendererLoop);
		
		networkManager = new RendererNetworkManager(rendererConfiguration.getMasterListeningAddress(), rendererConfiguration.getMasterListeningPort(), rendererConfiguration.getComposerListeningAddress(), rendererConfiguration.getComposerListeningPort(), rendererConfiguration.getCompositionOrder(), rendererConfiguration.getCompressionMethod());
		
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
	
	private static void printErrorMessage(String errorMessage, Exception e) {
		System.err.println(errorMessage);
		e.printStackTrace(System.err);
	}
	
	private static void printCommandLineHelp() {
		System.err.println("java -jar <your application>.jar <listeningAddress> <renderersConnectionPort> <composerConnectionPort> <targetScreenWidth> <targetScreenHeight> <targetFPS>");
	}
	
}
