package br.edu.univercidade.cc.xithcluster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jagatoo.input.InputSystem;
import org.jagatoo.input.InputSystemException;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.events.KeyPressedEvent;
import org.jagatoo.opengl.enums.TextureFormat;
import org.openmali.vecmath2.Colorf;
import org.xith3d.loaders.texture.TextureCreator;
import org.xith3d.loop.InputAdapterRenderLoop;
import org.xith3d.loop.opscheduler.Animatable;
import org.xith3d.render.BaseRenderPassConfig;
import org.xith3d.render.Canvas3D;
import org.xith3d.render.Canvas3DFactory;
import org.xith3d.render.DepthBufferRenderTarget;
import org.xith3d.render.TextureRenderTarget;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.View;
import org.xith3d.scenegraph.View.ProjectionPolicy;
import org.xith3d.utility.events.WindowClosingRenderLoopEnder;
import br.edu.univercidade.cc.xithcluster.communication.RendererNetworkManager;
import br.edu.univercidade.cc.xithcluster.util.BufferUtils;

public class RendererLoop extends InputAdapterRenderLoop implements Renderer {
	
	private static final float DEFAULT_TARGET_FPS = 80.0f;
	
	private static final Colorf BACKGROUND_COLOR = Colorf.BLACK;
	
	private static final TextureFormat RGBA_PIXEL_PACKAGING = TextureFormat.RGBA;
	
	private static final String APP_TITLE = "Renderer";
	
	private static final int DEFAULT_WIDTH = 800;
	
	private static final int DEFAULT_HEIGHT = 600;
	
	private RendererNetworkManager networkManager;
	
	private Texture2D colorAndAlphaTexture;
	
	private Canvas3D canvas;
	
	private int screenWidth = DEFAULT_WIDTH;
	
	private int screenHeight = DEFAULT_HEIGHT;
	
	private BranchGroup currentRoot;
	
	private DepthBufferRenderTarget depthBufferRenderTarget;
	
	public RendererLoop() {
		super(DEFAULT_TARGET_FPS);
	}
	
	@Override
	public void begin(RunMode runMode, TimingMode timingMode) {
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
		
		super.begin(runMode, timingMode);
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
		org.xith3d.render.Renderer renderer;
		TextureRenderTarget colorAndAlphaRenderTarget;
		BaseRenderPassConfig passConfig;
		
		colorAndAlphaTexture = TextureCreator.createTexture(RGBA_PIXEL_PACKAGING, screenWidth, screenHeight, BACKGROUND_COLOR);
		
		renderer = x3dEnvironment.getRenderer();
		
		passConfig = new BaseRenderPassConfig(ProjectionPolicy.PERSPECTIVE_PROJECTION);
		
		colorAndAlphaRenderTarget = new TextureRenderTarget(currentRoot, colorAndAlphaTexture, BACKGROUND_COLOR, true);
		renderer.addRenderTarget(colorAndAlphaRenderTarget, passConfig);
		
		depthBufferRenderTarget = new DepthBufferRenderTarget(currentRoot, screenWidth, screenHeight);
		renderer.addRenderTarget(depthBufferRenderTarget, passConfig);
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
		return BufferUtils.safeBufferRead(colorAndAlphaTexture.getTextureCanvas().getImage().getDataBuffer());
	}
	
	@Override
	public byte[] getDepthBuffer() {
		return depthBufferRenderTarget.getDepthBufferAsByteArray();
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
		
		copyRootAndInvalidateSource(currentRoot, newRoot);
		
		registerAllAnimatables(currentRoot);
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
	
	public void copyRootAndInvalidateSource(BranchGroup dest, BranchGroup src) {
		List<Node> children;
		
		if (src == null || dest == null) {
			throw new IllegalArgumentException();
		}
		
		currentRoot.removeAllChildren();
		
		int numChildren = src.numChildren();
		children = new ArrayList<Node>();
		for (int i = 0; i < numChildren; i++) {
			children.add(src.getChild(i));
		}
		
		src.removeAllChildren();
		
		for (Node child : children) {
			dest.addChild(child);
		}
	}
	
	public void registerAllAnimatables(GroupNode parent) {
		Node child;
		
		if (parent == null) {
			throw new IllegalArgumentException();
		}
		
		int numChildren = parent.numChildren();
		for (int i = 0; i < numChildren; i++) {
			child = parent.getChild(i);
			if (child instanceof Animatable) {
				x3dEnvironment.getOperationScheduler().addAnimatableObject((Animatable) child);
			} else if (child instanceof GroupNode) {
				registerAllAnimatables((GroupNode) child);
			}
		}
	}
	
}
