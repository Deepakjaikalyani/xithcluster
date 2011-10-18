package br.edu.univercidade.cc.xithcluster;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.xml.DOMConfigurator;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.loop.opscheduler.Animator;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Group;
import org.xith3d.schedops.movement.AnimatableGroup;
import org.xith3d.schedops.movement.GroupRotator;
import org.xith3d.schedops.movement.TransformationDirectives;
import br.edu.univercidade.cc.xithcluster.communication.NetworkManager;
import br.edu.univercidade.cc.xithcluster.configuration.CommandLineParsingException;
import br.edu.univercidade.cc.xithcluster.configuration.PropertiesFileLoadingException;

public class SampleApp {
	
	private static final String LOG4J_CONFIGURATION_FILE = "xithcluster-log4j.xml";
	
	private static SceneCreationCallback sceneCreationCallback = new SceneCreationCallback() {
		
		@Override
		public BranchGroup createSceneRoot(Animator animator) {
			BranchGroup root;
			Group group;
			AnimatableGroup animatableGroup;
			GroupRotator groupRotator;
			DirectionalLight directionalLight;
			
			root = new BranchGroup();
			
			group = new Group();
			group.setName("allShapesGrouper");
			root.addChild(group);
			
			groupRotator = new GroupRotator(new TransformationDirectives(new Vector3f(0.0f, 1.0f, 0.0f), 0.0f, 1.0f));
			animatableGroup = new AnimatableGroup(groupRotator);
			group.addChild(animatableGroup);
			
			animator.addAnimatableObject(animatableGroup);
			
			SceneUtils.addRectangle(group, "floor", 3.0f, 3.0f, new Tuple3f(0.0f, -1.0f, -1.0f), new Tuple3f(90.0f, 0.0f, 0.0f), SceneUtils.loadTexture2D("resources/textures/floor.png"));
			
			
			// Shapes
			
			SceneUtils.addCube(group, "cube1", 0.75f, new Tuple3f(0.0f, 0.3f, -0.5f), new Tuple3f(-35.0f, 20.0f, 0.0f), Colorf.RED);
			SceneUtils.addCube(animatableGroup, "cube2", 0.4f, new Tuple3f(-1.0f, 0.3f, -0.5f), new Tuple3f(25.0f, 0.0f, 0.0f), SceneUtils.loadTexture2D("resources/textures/crate.png"));
			
			SceneUtils.addSphere(group, "sphere1", 0.2f, new Tuple3f(0.7f, 0.2f, 0.3f), Colorf.GREEN);
			SceneUtils.addSphere(group, "sphere2", 0.1f, new Tuple3f(0.55f, -0.2f, 0.2f), Colorf.BLUE);
			SceneUtils.addSphere(group, "sphere3", 0.75f, new Tuple3f(-0.75f, 0.0f, -2.0f), Colorf.BROWN);
			
			// Lights
			
			directionalLight = new DirectionalLight(true, new Colorf(0.5f, 0.5f, 0.5f), Vector3f.NEGATIVE_Z_AXIS);
			root.addChild(directionalLight);
			
			return root;
		}
		
	};
	
	/*
	 * ================ 
	 * 		MAIN 
	 * ================
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		XithClusterConfiguration xithClusterConfiguration;
		DistributedRenderLoop distributedRenderLoop;
		Xith3DEnvironment xith3dEnvironment;
		UpdateManager updateManager;
		NetworkManager networkManager;
		
		initializeLog4j();
		
		xithClusterConfiguration = new XithClusterConfiguration();
		
		try {
			xithClusterConfiguration.load(args);
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
		
		xith3dEnvironment = new Xith3DEnvironment(new Tuple3f(0.0f, 0.0f, 3.0f), new Tuple3f(0.0f, 0.0f, 0.0f), new Tuple3f(0.0f, 1.0f, 0.0f), distributedRenderLoop);
		
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
	
	private static void printErrorMessage(String errorMessage, Exception e) {
		System.err.println(errorMessage);
		e.printStackTrace(System.err);
	}
	
	private static void printCommandLineHelp() {
		System.err.println("java -jar <your application>.jar <listeningAddress> <renderersConnectionPort> <composerConnectionPort> <targetScreenWidth> <targetScreenHeight> <targetFPS>");
	}
	
}
