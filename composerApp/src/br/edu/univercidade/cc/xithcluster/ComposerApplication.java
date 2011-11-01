package br.edu.univercidade.cc.xithcluster;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.xml.DOMConfigurator;
import br.edu.univercidade.cc.xithcluster.communication.ComposerNetworkManager;
import br.edu.univercidade.cc.xithcluster.configuration.CommandLineParsingException;
import br.edu.univercidade.cc.xithcluster.configuration.ComposerConfiguration;
import br.edu.univercidade.cc.xithcluster.configuration.PropertiesFileLoadingException;


public class ComposerApplication {
	
	private static final String LOG4J_CONFIGURATION_FILE = "composerApp-log4j.xml";
	
	/*
	 * =============== 
	 * 		MAIN 
	 * ===============
	 */
	public final static void main(String args[]) {
		ComposerConfiguration composerConfiguration;
		ComposerLoop composerLoop;
		Thread composerLoopThread;
		ComposerNetworkManager networkManager;
		Display display;

		initializeLog4j();
		
		composerConfiguration = new ComposerConfiguration();
		
		try {
			composerConfiguration.load(args);
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
		
		composerLoop = new ComposerLoop(composerConfiguration.isDisplayFPSCounter(), 
				composerConfiguration.getCompositionStrategy());
		
		networkManager = new ComposerNetworkManager(composerConfiguration.getMasterListeningAddress(),
				composerConfiguration.getMasterListeningPort(),
				composerConfiguration.getRenderersConnectionAddress(),
				composerConfiguration.getRenderersConnectionPort());
		composerLoop.setNetworkManager(networkManager);
		
		display = new Display(composerConfiguration.getWindowTitle());
		composerLoop.setDisplayer(display);
		
		composerLoopThread = new Thread(composerLoop);
		composerLoopThread.start();
	}
	
	private static void initializeLog4j() {
		if (new File(LOG4J_CONFIGURATION_FILE).exists()) {
			DOMConfigurator.configure(LOG4J_CONFIGURATION_FILE);
		} else {
			System.err.println("Log4j not initialized: \"log4j.xml\" could not be found");
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
