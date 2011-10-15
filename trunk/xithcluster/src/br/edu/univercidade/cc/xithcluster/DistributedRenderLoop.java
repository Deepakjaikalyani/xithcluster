package br.edu.univercidade.cc.xithcluster;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import javax.xml.parsers.FactoryConfigurationError;
import org.apache.log4j.xml.DOMConfigurator;
import org.jagatoo.input.InputSystem;
import org.jagatoo.input.InputSystemException;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.events.KeyPressedEvent;
import org.openmali.vecmath2.Colorf;
import org.xith3d.loop.InputAdapterRenderLoop;
import org.xith3d.render.Canvas3D;
import org.xith3d.render.Canvas3DFactory;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.ui.hud.HUD;
import org.xith3d.utility.events.WindowClosingRenderLoopEnder;
import br.edu.univercidade.cc.xithcluster.communication.MasterNetworkManager;

public abstract class DistributedRenderLoop extends InputAdapterRenderLoop implements SceneHolder {
	
	private static final String LOG4J_CONFIGURATION_FILE = "xithcluster-log4j.xml";
	
	private static final int FPS_SAMPLES = 10;
	
	private UpdateManager updateManager;
	
	private MasterNetworkManager networkManager;
	
	private GeometryDistributionStrategy geometryDistributionStrategy;
	
	private Canvas3D canvas;
	
	public DistributedRenderLoop(GeometryDistributionStrategy geometryDistributionStrategy) {
		super(XithClusterConfiguration.targetFPS);
		
		if (geometryDistributionStrategy == null) {
			// TODO:
			throw new RuntimeException("You must set a distribution strategy");
		}
		
		this.geometryDistributionStrategy = geometryDistributionStrategy;
	}
	
	@Override
	public void begin(RunMode runMode, TimingMode timingMode) {
		if (x3dEnvironment == null) {
			// TODO:
			throw new RuntimeException("Xith3d environment must be initialized");
		}
		
		if (!isRunning()) {
			initializeLog4j();
			
			createCanvas();
			
			createUpdateManager();
			
			createNetworkManager();
			
			registerDistributedScene();
		}
		
		super.begin(runMode, timingMode);
	}

	private void registerDistributedScene() {
		x3dEnvironment.addPerspectiveBranch(createSceneRoot());
	}
	
	protected abstract BranchGroup createSceneRoot();
	
	private void createNetworkManager() {
		HUD hud;
		HUDFPSCounter fpsCounter;
		
		networkManager = new MasterNetworkManager(this, updateManager, geometryDistributionStrategy);
		try {
			networkManager.initialize();
		} catch (UnknownHostException e) {
			// TODO:
			throw new RuntimeException("Error starting network manager", e);
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error starting network manager", e);
		}
		
		setOperationScheduler(networkManager);
		setUpdater(networkManager);
		
		if (XithClusterConfiguration.displayFPSCounter) {
			hud = new HUD(canvas, XithClusterConfiguration.screenHeight);
			
			fpsCounter = new HUDFPSCounter(FPS_SAMPLES);
			fpsCounter.registerTo(hud);
			
			networkManager.setFpsCounter(fpsCounter);
			
			x3dEnvironment.addHUD(hud);
		}
	}
	
	private void createUpdateManager() {
		try {
			InputSystem.getInstance().registerNewKeyboardAndMouse(canvas.getPeer());
		} catch (InputSystemException e) {
			// TODO:
			throw new RuntimeException("Error registering new keyboard and mouse", e);
		}
		
		updateManager = new UpdateManager();
		x3dEnvironment.addScenegraphModificationListener(updateManager);
	}
	
	private void createCanvas() {
		canvas = Canvas3DFactory.createWindowed(XithClusterConfiguration.screenWidth, XithClusterConfiguration.screenHeight, XithClusterConfiguration.windowTitle);
		canvas.setBackgroundColor(Colorf.BLACK);
		canvas.addWindowClosingListener(new WindowClosingRenderLoopEnder(this));
		
		x3dEnvironment.addCanvas(canvas);
	}
	
	private void initializeLog4j() throws FactoryConfigurationError {
		if (new File(LOG4J_CONFIGURATION_FILE).exists()) {
			DOMConfigurator.configure(LOG4J_CONFIGURATION_FILE);
		} else {
			System.err.println("Log4j not initialized: \"xithcluster-log4j.xml\" could not be found");
		}
	}
	
	@Override
	public void onKeyPressed(KeyPressedEvent e, Key key) {
		switch (key.getKeyID()) {
		case ESCAPE:
			this.end();
			break;
		}
	}
	
	@Override
	public SceneInfo getSceneInfo() {
		BranchGroup branchGroup;

		if (XithClusterConfiguration.displayFPSCounter) {
			branchGroup = getFirstBranchGroupThatDoesntBelongToHUD();
		} else {
			branchGroup = x3dEnvironment.getBranchGroup();
		}
		
		if (branchGroup == null) {
			// TODO:
			throw new RuntimeException("There's no suitable branch group");
		}
		
		return new SceneInfo(branchGroup, x3dEnvironment.getView());
	}

	private BranchGroup getFirstBranchGroupThatDoesntBelongToHUD() {
		BranchGroup hudBranchGroup;
		BranchGroup currentBranchGroup;
		
		hudBranchGroup = x3dEnvironment.getHUD().getSGGroup();
		for (int i = 0; i < x3dEnvironment.getNumberOfBranchGroups(); i++) {
			currentBranchGroup = x3dEnvironment.getBranchGroup(i);
			
			if (!currentBranchGroup.equals(hudBranchGroup)) {
				return currentBranchGroup;
			}
		}
		
		return null;
	}
	
}
