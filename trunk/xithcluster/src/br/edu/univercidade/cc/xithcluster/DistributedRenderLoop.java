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
import org.xith3d.ui.hud.HUD;
import org.xith3d.utility.events.WindowClosingRenderLoopEnder;
import br.edu.univercidade.cc.xithcluster.communication.MasterNetworkManager;

public class DistributedRenderLoop extends InputAdapterRenderLoop implements SceneHolder {

	private static final String LOG4J_CONFIGURATION_FILE = "xithcluster-log4j.xml";
	
	private static final int FPS_SAMPLES = 100;
	
	private UpdateManager updateManager;
	
	private MasterNetworkManager networkManager;
	
	private GeometryDistributionStrategy geometryDistributionStrategy;
	
	private Canvas3D canvas;
	
	public DistributedRenderLoop(GeometryDistributionStrategy geometryDistributionStrategy) {
		super(XithClusterConfiguration.targetFPS);
		
		this.geometryDistributionStrategy = geometryDistributionStrategy;
	}
	
	@Override
	public void begin(RunMode runMode, TimingMode timingMode) {
		HUD hud;
		HUDFPSCounter fpsCounter;
		
		initializeLog4j();
		
		if (geometryDistributionStrategy == null) {
			// TODO:
			throw new RuntimeException("You must set a distribution strategy");
		}
		
		canvas = Canvas3DFactory.createWindowed(XithClusterConfiguration.screenWidth, XithClusterConfiguration.screenHeight, XithClusterConfiguration.windowTitle);
		canvas.setBackgroundColor(Colorf.BLACK);
		canvas.addWindowClosingListener(new WindowClosingRenderLoopEnder(this));
		getXith3DEnvironment().addCanvas(canvas);
		
		try {
			InputSystem.getInstance().registerNewKeyboardAndMouse(canvas.getPeer());
		} catch (InputSystemException e) {
			// TODO:
			throw new RuntimeException("Error registering new keyboard and mouse", e);
		}
		
		updateManager = new UpdateManager();
		getXith3DEnvironment().addScenegraphModificationListener(updateManager);
		
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
			
			getXith3DEnvironment().addHUD(hud);
		}
		
		super.begin(runMode, timingMode);
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
		return new SceneInfo(getXith3DEnvironment().getBranchGroup(), getXith3DEnvironment().getView());
	}
	
}
