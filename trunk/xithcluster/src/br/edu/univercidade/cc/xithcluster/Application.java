package br.edu.univercidade.cc.xithcluster;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.xml.DOMConfigurator;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.loop.opscheduler.Animator;
import org.xith3d.scenegraph.BranchGroup;
import br.edu.univercidade.cc.xithcluster.communication.NetworkManager;
import br.edu.univercidade.cc.xithcluster.configuration.CommandLineParsingException;
import br.edu.univercidade.cc.xithcluster.configuration.PropertiesFileLoadingException;

public abstract class Application {
	
	private static final String LOG4J_CONFIGURATION_FILE = "xithcluster-log4j.xml";
	
	private SceneCreationCallback sceneCreationCallback = new SceneCreationCallback() {
		
		@Override
		public BranchGroup createSceneRoot(Animator animator) {
			return Application.this.createSceneRoot(animator);
		}
		
	};
	
	protected abstract String getJARName();
	
	protected abstract BranchGroup createSceneRoot(Animator animator);
	
	public void init(String[] commandLineArguments) {
		XithClusterConfiguration xithClusterConfiguration;
		DistributedRenderLoop distributedRenderLoop;
		UpdateManager updateManager;
		NetworkManager networkManager;
		
		initializeLog4j();
		
		xithClusterConfiguration = new XithClusterConfiguration();
		
		try {
			xithClusterConfiguration.load(commandLineArguments);
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
		
		distributedRenderLoop = new DistributedRenderLoop(xithClusterConfiguration.getTargetFPS(), xithClusterConfiguration.getTargetScreenWidth(), xithClusterConfiguration.getTargetScreenHeight(), true, sceneCreationCallback);
		
		// The xith3d environment registers himself with the distributed render
		// loop
		new Xith3DEnvironment(new Tuple3f(0.0f, 0.0f, 3.0f), new Tuple3f(0.0f, 0.0f, 0.0f), new Tuple3f(0.0f, 1.0f, 0.0f), distributedRenderLoop);
		
		updateManager = new UpdateManager();
		
		networkManager = new NetworkManager(xithClusterConfiguration.getListeningAddress(), xithClusterConfiguration.getRenderersConnectionPort(), xithClusterConfiguration.getComposerConnectionPort(), new RoundRobinGeometryDistribution());
		
		networkManager.addUpdateManager(updateManager);
		
		distributedRenderLoop.setUpdateManager(updateManager);
		
		distributedRenderLoop.setNetworkManager(networkManager);
		
		distributedRenderLoop.begin();
	}
	
	private static void initializeLog4j() {
		if (new File(LOG4J_CONFIGURATION_FILE).exists()) {
			DOMConfigurator.configure(LOG4J_CONFIGURATION_FILE);
		} else {
			System.err.println("Log4j not initialized: \"xithcluster-log4j.xml\" could not be found");
		}
	}
	
	private void printErrorMessage(String errorMessage, Exception e) {
		System.err.println(errorMessage);
		e.printStackTrace(System.err);
	}
	
	private void printCommandLineHelp() {
		System.err.println("java -jar " + getJARName() + " <listeningAddress> <renderersConnectionPort> <composerConnectionPort> <targetScreenWidth> <targetScreenHeight> <targetFPS>");
	}

	
}
