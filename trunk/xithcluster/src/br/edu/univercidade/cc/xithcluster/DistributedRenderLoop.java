package br.edu.univercidade.cc.xithcluster;

import java.awt.Dimension;
import java.io.IOException;
import java.net.UnknownHostException;
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
import br.edu.univercidade.cc.xithcluster.communication.NetworkManager;

public class DistributedRenderLoop extends InputAdapterRenderLoop implements SceneRenderer {
	
	private static final int MIN_FPS_SAMPLES = 10;
	
	private UpdateManager updateManager;
	
	private NetworkManager networkManager;
	
	private Canvas3D debuggingCanvas;
	
	private Dimension targetScreenDimension;

	private boolean enableDebuggingScreen;
	
	private SceneCreationCallback sceneCreationCallback;

	public DistributedRenderLoop(float targetFPS,
			int targetScreenWidth,
			int targetScreenHeight,
			boolean enableDebuggingScreen,
			SceneCreationCallback sceneCreationCallback) {
		super(targetFPS);
		
		if (sceneCreationCallback == null) {
			// TODO:
			throw new IllegalArgumentException();
		}
		
		this.targetScreenDimension = new Dimension(targetScreenWidth, targetScreenHeight);
		
		this.enableDebuggingScreen  = enableDebuggingScreen;
		
		this.sceneCreationCallback = sceneCreationCallback;
	}
	
	public void setUpdateManager(UpdateManager updateManager) {
		if (updateManager == null) {
			throw new IllegalArgumentException();
		}
		
		this.updateManager = updateManager;
	}
	
	public void setNetworkManager(NetworkManager networkManager) {
		if (isRunning()) {
			throw new IllegalStateException("Cannot set network manager while application is running"); 
		}
		
		if (networkManager == null) {
			throw new IllegalArgumentException();
		}
		
		this.networkManager = networkManager;
		this.networkManager.setSceneRenderer(this);
		
		setOperationScheduler(this.networkManager);
		setUpdater(this.networkManager);
		
	}
	
	@Override
	public void begin(RunMode runMode, TimingMode timingMode) {
		if (!isRunning()) {
			if (updateManager == null) {
				// TODO:
				throw new RuntimeException("Update manager must be set");
			}
			
			if (networkManager == null) {
				// TODO:
				throw new RuntimeException("Network manager must be set");
			}
			
			if (x3dEnvironment == null) {
				// TODO:
				throw new RuntimeException("Xith3d environment must be initialized");
			}
			
			registerUpdateManager();
			
			createDebuggingCanvasIfSpecified();
			
			createSceneAndAddToSceneGraph();
			
			startNetworkManager();
		}
		
		super.begin(runMode, timingMode);
	}

	private void createSceneAndAddToSceneGraph() {
		BranchGroup root;
		
		root = sceneCreationCallback.createSceneRoot(getAnimator());
		
		if (root == null) {
			// TODO:
			throw new RuntimeException("Scene root cannot be null");
		}
		
		x3dEnvironment.addPerspectiveBranch(root);
	}
	
	private void startNetworkManager() {
		try {
			networkManager.start();
		} catch (UnknownHostException e) {
			printErrorMessageAndExit("Error starting network manager", e);
		} catch (IOException e) {
			printErrorMessageAndExit("Error starting network manager", e);
		}
	}

	private void printErrorMessageAndExit(String errorMessage, Exception e) {
		System.err.println(errorMessage);
		e.printStackTrace(System.err);
		System.exit(-1);
	}
	
	private void registerUpdateManager() {
		x3dEnvironment.addScenegraphModificationListener(updateManager);
	}

	private void createDebuggingCanvasIfSpecified() {
		if (!enableDebuggingScreen) return;
		
		debuggingCanvas = Canvas3DFactory.createWindowed(targetScreenDimension.width, targetScreenDimension.height, "XithCluster Debugging Screen");
		debuggingCanvas.setBackgroundColor(Colorf.BLACK);
		debuggingCanvas.addWindowClosingListener(new WindowClosingRenderLoopEnder(this));
		
		x3dEnvironment.addCanvas(debuggingCanvas);
		
		registerDebuggingCanvasAsMouseAndKeyboardListener();
		
		createHUDAndFPSCounter();
	}
	
	private void registerDebuggingCanvasAsMouseAndKeyboardListener() {
		try {
			InputSystem.getInstance().registerNewKeyboardAndMouse(debuggingCanvas.getPeer());
		} catch (InputSystemException e) {
			// TODO:
			throw new RuntimeException("Error registering debugging canvas as mouse and keyboard listener", e);
		}
	}
	
	private void createHUDAndFPSCounter() {
		HUD hud;
		HUDFPSCounter fpsCounter;
		
		hud = new HUD(debuggingCanvas, targetScreenDimension.height);
		
		fpsCounter = new HUDFPSCounter(MIN_FPS_SAMPLES);
		fpsCounter.registerTo(hud);
		
		networkManager.setFPSCounter(fpsCounter);
		
		x3dEnvironment.addHUD(hud);
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

		branchGroup = getFirstBranchGroupThatDoesntBelongToHUD();
		
		if (branchGroup == null) {
			// TODO:
			throw new RuntimeException("There's no suitable distributable branch group in the scene");
		}
		
		return new SceneInfo(branchGroup, x3dEnvironment.getView());
	}

	private BranchGroup getFirstBranchGroupThatDoesntBelongToHUD() {
		BranchGroup hudBranchGroup;
		BranchGroup currentBranchGroup;
		
		if (x3dEnvironment.getHUD() != null) {
			hudBranchGroup = x3dEnvironment.getHUD().getSGGroup();
			for (int i = 0; i < x3dEnvironment.getNumberOfBranchGroups(); i++) {
				currentBranchGroup = x3dEnvironment.getBranchGroup(i);
				
				if (!currentBranchGroup.equals(hudBranchGroup)) {
					return currentBranchGroup;
				}
			}
			
			return null;
		} else {
			return x3dEnvironment.getBranchGroup();
		}
	}

	@Override
	public float getTargetFPS() {
		return getMaxFPS();
	}
	
	@Override
	public Dimension getTargetScreenDimension() {
		return targetScreenDimension;
	}

}
