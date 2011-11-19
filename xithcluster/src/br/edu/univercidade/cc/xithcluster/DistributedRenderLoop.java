package br.edu.univercidade.cc.xithcluster;

import java.awt.Dimension;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
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
import br.edu.univercidade.cc.xithcluster.callbacks.ProcessInputCallback;
import br.edu.univercidade.cc.xithcluster.callbacks.SceneCreationCallback;
import br.edu.univercidade.cc.xithcluster.hud.components.HUDFPSCounter;
import br.edu.univercidade.cc.xithcluster.update.UpdateManager;

public class DistributedRenderLoop extends InputAdapterRenderLoop implements SceneManager {
	
	private static final int MIN_FPS_SAMPLES = 10;
	
	private UpdateManager updateManager;
	
	private NetworkManager networkManager;
	
	private Canvas3D debuggingCanvas;
	
	private Dimension targetScreenDimension;
	
	private boolean enableDebuggingScreen;
	
	private SceneCreationCallback sceneCreationCallback;
	
	private List<ProcessInputCallback> processInputCallbacks = new ArrayList<ProcessInputCallback>();
	
	public DistributedRenderLoop(float targetFPS, int targetScreenWidth, int targetScreenHeight, boolean enableDebuggingScreen, SceneCreationCallback sceneCreationCallback) {
		super(targetFPS);
		
		if (sceneCreationCallback == null) {
			throw new IllegalArgumentException("The scene creation callback cannot be null");
		}
		
		this.targetScreenDimension = new Dimension(targetScreenWidth, targetScreenHeight);
		this.enableDebuggingScreen = enableDebuggingScreen;
		this.sceneCreationCallback = sceneCreationCallback;
	}
	
	public void setUpdateManager(UpdateManager updateManager) {
		if (isRunning()) {
			throw new IllegalStateException("Cannot set the update manager while the application is running");
		}
		
		if (updateManager == null) {
			throw new IllegalArgumentException("The update manager cannot be null");
		}
		
		this.updateManager = updateManager;
	}
	
	public void setNetworkManager(NetworkManager networkManager) {
		if (isRunning()) {
			throw new IllegalStateException("Cannot set the network manager while the application is running");
		}
		
		if (networkManager == null) {
			throw new IllegalArgumentException("The network manager cannot be null");
		}
		
		this.networkManager = networkManager;
		this.networkManager.setSceneRenderer(this);
		
		setOperationScheduler(this.networkManager);
		setUpdater(this.networkManager);
		
	}
	
	public void addProcessInputCallback(ProcessInputCallback processInputCallback) {
		if (isRunning()) {
			throw new IllegalStateException("Cannot add a process input callback while the application is running");
		}
		
		if (processInputCallback == null) {
			throw new IllegalArgumentException("You can't add a null process input callback");
		}
		
		processInputCallbacks.add(processInputCallback);
	}
	
	@Override
	public void begin(RunMode runMode, TimingMode timingMode) {
		if (runMode == null || timingMode == null) {
			throw new IllegalArgumentException();
		}
		
		// TODO:
		if (runMode != RunMode.RUN_IN_SAME_THREAD || timingMode != TimingMode.MILLISECONDS) {
			System.err.println("Unsupported run and/or timing mode. Using 'RunMode.RUN_IN_SAME_THREAD' and 'TimingMode.MILLISECONDS'.");
		}
		
		if (!isRunning()) {
			if (updateManager == null) {
				throw new IllegalStateException("You cannot start the application without an update manager");
			}
			
			if (networkManager == null) {
				throw new IllegalStateException("You cannot start the application without a network manager");
			}
			
			if (x3dEnvironment == null) {
				throw new IllegalStateException("The Xith3D environment must be initialized");
			}
			
			registerUpdateManager();
			
			createDebuggingCanvasIfSpecified();
			
			createSceneAndAddToSceneGraph();
			
			startNetworkManager();
		}
		
		super.begin(RunMode.RUN_IN_SAME_THREAD, TimingMode.MILLISECONDS);
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
		if (!enableDebuggingScreen)
			return;
		
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
	protected void renderNextFrame(long gameTime, long frameTime, TimingMode timingMode) {
		// TODO: Check if really optimizes something...
		if (debuggingCanvas != null) {
			super.renderNextFrame(gameTime, frameTime, timingMode);
		}
	}

	@Override
	public void onKeyPressed(KeyPressedEvent e, Key key) {
		processInput(key);
		
		invokeProcessInputCallbacks(e, key);
	}
	
	private void processInput(Key key) {
		switch (key.getKeyID()) {
		case ESCAPE:
			this.end();
			break;
		}
	}
	
	private void invokeProcessInputCallbacks(KeyPressedEvent e, Key key) {
		for (ProcessInputCallback processInputCallback : processInputCallbacks) {
			processInputCallback.keyPressed(e, key);
		}
	}
	
	@Override
	public SceneInfo getSceneInfo() {
		BranchGroup branchGroup;
		
		branchGroup = getFirstBranchGroupThatDoesntBelongToTheHUD();
		
		if (branchGroup == null) {
			throw new IllegalStateException("There's no suitable branch group in the scene");
		}
		
		return new SceneInfo(branchGroup, x3dEnvironment.getView());
	}
	
	private BranchGroup getFirstBranchGroupThatDoesntBelongToTheHUD() {
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
	public Dimension getScreenSize() {
		return targetScreenDimension;
	}
	
}
