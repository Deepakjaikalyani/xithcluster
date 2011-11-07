package br.edu.univercidade.cc.xithcluster;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.xml.DOMConfigurator;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.events.KeyPressedEvent;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.Animator;
import org.xith3d.loop.opscheduler.Interval;
import org.xith3d.scenegraph.BranchGroup;
import br.edu.univercidade.cc.xithcluster.callbacks.ProcessInputCallback;
import br.edu.univercidade.cc.xithcluster.callbacks.SceneCreationCallback;
import br.edu.univercidade.cc.xithcluster.communication.NetworkManager;
import br.edu.univercidade.cc.xithcluster.configuration.CommandLineParsingException;
import br.edu.univercidade.cc.xithcluster.configuration.PropertiesFileLoadingException;
import br.edu.univercidade.cc.xithcluster.configuration.XithClusterConfiguration;
import br.edu.univercidade.cc.xithcluster.distribution.RoundRobinDistribution;
import br.edu.univercidade.cc.xithcluster.update.UpdateManager;

public abstract class SampleApplication {
	
	private static final Tuple3f DEFAULT_UP_DIRECTION = new Tuple3f(0.0f, 1.0f, 0.0f);
	
	private static final Tuple3f DEFAULT_VIEW_FOCUS = new Tuple3f(0.0f, 0.0f, 0.0f);
	
	private static final Tuple3f DEFAULT_EYE_POSITION = new Tuple3f(0.0f, 0.0f, 5.0f);
	
	private static final String LOG4J_CONFIGURATION_FILE = "xithcluster-log4j.xml";
	
	private SceneCreationCallback sceneCreationCallback = new SceneCreationCallback() {
		
		@Override
		public BranchGroup createSceneRoot(Animator animator) {
			return SampleApplication.this.createSceneRoot(animator);
		}
		
	};
	
	private ProcessInputCallback processInputCallback = new ProcessInputCallback() {
		
		@Override
		public void keyPressed(KeyPressedEvent e, Key key) {
			SampleApplication.this.keyPressed(e, key);
		}
		
	};
	
	protected Tuple3f eyePosition;
	
	protected Tuple3f viewFocus;
	
	protected Tuple3f upDirection;
	
	public SampleApplication() {
		super();
		eyePosition = DEFAULT_EYE_POSITION;
		viewFocus = DEFAULT_VIEW_FOCUS;
		upDirection = DEFAULT_UP_DIRECTION;
	}
	
	public SampleApplication(Tuple3f eyePosition) {
		super();
		this.eyePosition = eyePosition;
		this.viewFocus = DEFAULT_VIEW_FOCUS;
		upDirection = DEFAULT_UP_DIRECTION;
	}
	
	public SampleApplication(Tuple3f eyePosition, Tuple3f viewFocus) {
		super();
		this.eyePosition = eyePosition;
		this.viewFocus = viewFocus;
		upDirection = DEFAULT_UP_DIRECTION;
	}
	
	public SampleApplication(Tuple3f eyePosition, Tuple3f viewFocus, Tuple3f upDirection) {
		super();
		this.eyePosition = eyePosition;
		this.viewFocus = viewFocus;
		this.upDirection = upDirection;
	}
	
	protected abstract String getJARName();
	
	protected abstract BranchGroup createSceneRoot(Animator animator);
	
	protected void keyPressed(KeyPressedEvent event, Key key) {
		// Empty method
	}
	
	protected void oneSecondTick() {
		// Empty method
	}
	
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
		
		distributedRenderLoop = new DistributedRenderLoop(xithClusterConfiguration.getTargetFPS(), xithClusterConfiguration.getTargetScreenWidth(), xithClusterConfiguration.getTargetScreenHeight(), xithClusterConfiguration.isDebuggingScreenEnabled(), sceneCreationCallback);
		
		new Xith3DEnvironment(eyePosition, viewFocus, upDirection, distributedRenderLoop);
		
		updateManager = new UpdateManager();
		
		networkManager = new NetworkManager(xithClusterConfiguration.getListeningAddress(), xithClusterConfiguration.getRenderersConnectionPort(), xithClusterConfiguration.getComposerConnectionPort(), new RoundRobinDistribution());
		
		networkManager.addUpdateManager(updateManager);
		
		distributedRenderLoop.setUpdateManager(updateManager);
		
		distributedRenderLoop.setNetworkManager(networkManager);
		
		distributedRenderLoop.addProcessInputCallback(processInputCallback);
		
		distributedRenderLoop.getOperationScheduler().addInterval(new Interval(1000L) {
			
			@Override
			protected void onIntervalHit(long gameTime, long frameTime, TimingMode timingMode) {
				oneSecondTick();
			}
			
		});
		
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
		System.err.println("java -jar " + getJARName() + " <listeningAddress> <renderersConnectionPort> <composerConnectionPort> <enableDebuggingScreen> <targetScreenWidth> <targetScreenHeight> <targetFPS>");
	}
	
}
