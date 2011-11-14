package br.edu.univercidade.cc.xithcluster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.jagatoo.input.InputSystem;
import org.jagatoo.input.InputSystemException;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.events.KeyPressedEvent;
import org.lwjgl.opengl.GL11;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.loop.InputAdapterRenderLoop;
import org.xith3d.loop.opscheduler.Animatable;
import org.xith3d.render.BaseRenderPassConfig;
import org.xith3d.render.Canvas3D;
import org.xith3d.render.Canvas3DFactory;
import org.xith3d.render.DirectByteBufferRenderTarget;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.View;
import org.xith3d.scenegraph.View.ProjectionPolicy;
import org.xith3d.utility.events.WindowClosingRenderLoopEnder;
import br.edu.univercidade.cc.xithcluster.utils.SceneUtils;

public class RendererLoop extends InputAdapterRenderLoop implements Renderer {
	
	private static final float DEFAULT_TARGET_FPS = 80.0f;
	
	private static final Colorf BACKGROUND_COLOR = Colorf.BLACK;
	
	private static final String APP_TITLE = "Renderer";
	
	private static final int DEFAULT_WIDTH = 640;
	
	private static final int DEFAULT_HEIGHT = 480;
	
	private Logger log = Logger.getLogger(RendererLoop.class);
	
	private RendererNetworkManager networkManager;
	
	private Canvas3D canvas;
	
	private int screenWidth = DEFAULT_WIDTH;
	
	private int screenHeight = DEFAULT_HEIGHT;
	
	private BranchGroup currentRoot;
	
	private DirectByteBufferRenderTarget depthBufferRenderTarget;
	
	private DirectByteBufferRenderTarget colorAndAlphaRenderTarget;
	
	private List<Animatable> animatables = new ArrayList<Animatable>();
	
	public RendererLoop() {
		super(DEFAULT_TARGET_FPS);
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
			if (networkManager == null) {
				// TODO:
				throw new RuntimeException("Network manager must be set");
			}
			
			if (x3dEnvironment == null) {
				// TODO:
				throw new RuntimeException("Xith3d environment must be initialized");
			}
			
			createEmptyScene();
			
			createRenderTargets();
			
			createCanvas();
			
			startNetworkManager();
		}
		
		log.info("Renderer started successfully.");
		
		super.begin(RunMode.RUN_IN_SAME_THREAD, TimingMode.MILLISECONDS);
	}
	
	public void setNetworkManager(RendererNetworkManager networkManager) {
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
	
	private void startNetworkManager() {
		try {
			networkManager.start();
		} catch (IOException e) {
			printErrorMessageAndExit("Error starting network manager", e);
		}
	}
	
	private void printErrorMessageAndExit(String errorMessage, Exception e) {
		System.err.println(errorMessage);
		e.printStackTrace(System.err);
		System.exit(-1);
	}
	
	private void createCanvas() {
		canvas = Canvas3DFactory.createWindowed(screenWidth, screenHeight, APP_TITLE);
		canvas.setBackgroundColor(BACKGROUND_COLOR);
		canvas.addWindowClosingListener(new WindowClosingRenderLoopEnder(this));
		
		x3dEnvironment.addCanvas(canvas);
		
		registerDebuggingCanvasAsMouseAndKeyboardListener();
	}
	
	private void registerDebuggingCanvasAsMouseAndKeyboardListener() {
		try {
			InputSystem.getInstance().registerNewKeyboardAndMouse(canvas.getPeer());
		} catch (InputSystemException e) {
			printErrorMessageAndExit("Error registering new keyboard and mouse", e);
		}
	}
	
	private void createEmptyScene() {
		currentRoot = new BranchGroup();
		x3dEnvironment.addPerspectiveBranch(currentRoot);
	}
	
	private void createRenderTargets() {
		BaseRenderPassConfig passConfig;
		
		passConfig = new BaseRenderPassConfig(ProjectionPolicy.PERSPECTIVE_PROJECTION);
		
		colorAndAlphaRenderTarget = new DirectByteBufferRenderTarget(currentRoot, screenWidth, screenHeight, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 4);
		x3dEnvironment.getRenderer().addRenderTarget(colorAndAlphaRenderTarget, passConfig);
		
		depthBufferRenderTarget = new DirectByteBufferRenderTarget(currentRoot, screenWidth, screenHeight, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, 4);
		x3dEnvironment.getRenderer().addRenderTarget(depthBufferRenderTarget, passConfig);
	}
	
	@Override
	public void updateOnScreenInformation(int rendererId, int screenWidth, int screenHeight) {
		x3dEnvironment.getCanvas().setTitle(APP_TITLE + "[id=" + rendererId + "]");
		
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		
		canvas.setSize(screenWidth, screenHeight);
	}
	
	@Override
	public byte[] getColorAndAlphaBuffer() {
		return colorAndAlphaRenderTarget.getDirectByteBufferAsArray();
	}
	
	@Override
	public byte[] getDepthBuffer() {
		return depthBufferRenderTarget.getDirectByteBufferAsArray();
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
	public void updateScene(View view, BranchGroup newRoot) {
		copyView(x3dEnvironment.getView(), view);
		
		copyChildrenAndInvalidate(newRoot);
		
		unregisterAnimatables();
		
		registerAnimatableChildren(currentRoot);
	}
	
	private void unregisterAnimatables() {
		for (Animatable animatable : animatables) {
			x3dEnvironment.getOperationScheduler().removeAnimatableObject(animatable);
		}
	}
	
	public static void copyView(View dest, View src) {
		if (dest == null || src == null) {
			throw new IllegalArgumentException();
		}
		
		dest.setPosition(src.getPosition());
		dest.setCenterOfView(src.getCenterOfView());
		dest.setFacingDirection(src.getFacingDirection());
		dest.setFieldOfView(src.getFieldOfView());
		dest.setBackClipDistance(src.getBackClipDistance());
		dest.setFrontClipDistance(src.getFrontClipDistance());
	}
	
	public void copyChildrenAndInvalidate(BranchGroup newRoot) {
		List<Node> children;
		
		if (newRoot == null || currentRoot == null) {
			throw new IllegalArgumentException();
		}
		
		int numChildren = newRoot.numChildren();
		children = new ArrayList<Node>();
		for (int i = 0; i < numChildren; i++) {
			children.add(newRoot.getChild(i));
		}
		
		newRoot.removeAllChildren();
		currentRoot.removeAllChildren();
		
		for (Node child : children) {
			currentRoot.addChild(child);
		}
		
		applyLoneChildWorkaround(currentRoot);
	}
	
	/**
	 * TODO: Describe bug!
	 */
	private void applyLoneChildWorkaround(GroupNode group) {
		boolean hasOnlyOneChild;
		
		hasOnlyOneChild = group.numChildren() == 1;
		
		for (int i = 0; i < group.numChildren(); i++) {
			Node child = group.getChild(i);
			
			if (child instanceof GroupNode) {
				applyLoneChildWorkaround((GroupNode) child);
			}
		}
		
		if (hasOnlyOneChild) {
			addInvisibleChild(group);
		}
	}
	
	private void addInvisibleChild(GroupNode group) {
		SceneUtils.addSphere(group, "bug-fix-geom", 0.01f, 5, 5, new Tuple3f(0.0f, 0.0f, 0.0f), new Colorf(0.0f, 0.0f, 0.0f, 0.0f));
	}
	
	public void registerAnimatableChildren(GroupNode group) {
		Node child;
		
		if (group == null) {
			throw new IllegalArgumentException();
		}
		
		for (int i = 0; i < group.numChildren(); i++) {
			child = group.getChild(i);
			
			if (child instanceof Animatable) {
				registerAsAnimatable((Animatable) child);
			} else if (child instanceof GroupNode) {
				registerAnimatableChildren((GroupNode) child);
			}
		}
	}
	
	private void registerAsAnimatable(Animatable animatable) {
		x3dEnvironment.getOperationScheduler().addAnimatableObject(animatable);
		animatables.add(animatable);
	}
	
}
