package br.edu.univercidade.cc.xithcluster;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.xml.DOMConfigurator;
import org.jagatoo.input.InputSystem;
import org.jagatoo.input.InputSystemException;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.events.KeyPressedEvent;
import org.jagatoo.opengl.enums.TextureFormat;
import org.openmali.vecmath2.Colorf;
import org.xith3d.base.Xith3DEnvironment;
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

public class Renderer extends InputAdapterRenderLoop {
	
	private static final String LOG4J_CONFIGURATION_FILE = "log4j.xml";
	
	private static final float DEFAULT_TARGET_FPS = 80.0f;
	
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

	public Renderer() {
		super(DEFAULT_TARGET_FPS);
		
		initialize();
	}
	
	/**
	 * The following startup sequence must be strictly obeyed:
	 * 
	 * 1). Initialize logging system (log4j).
	 * 
	 * 2). Create the Xith3D scene graph.
	 * 
	 * 3). Create an empty scene.
	 * 
	 * 4). Create the render targets for capturing the color and depth buffers.
	 * 
	 * 5). Create the canvas (screen).
	 * 
	 * 6). Register input system.
	 * 
	 * 7). Create network manager.
	 */
	private void initialize() {
		initializeLog4j();
		
		createXith3DSceneGraph();
		
		createEmptyScene();
		
		createRenderTargets();
		
		createCanvas();
		
		registerInputSystem();
		
		createNetworkManager();
	}

	private void createXith3DSceneGraph() {
		new Xith3DEnvironment(this);
	}

	private void createNetworkManager() {
		networkManager = new RendererNetworkManager(this);
		try {
			networkManager.initialize();
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error starting network manager", e);
		}
		
		setOperationScheduler(networkManager);
		setUpdater(networkManager);
	}

	private void registerInputSystem() {
		try {
			InputSystem.getInstance().registerNewKeyboardAndMouse(canvas.getPeer());
		} catch (InputSystemException e) {
			// TODO:
			throw new RuntimeException("Error registering new keyboard and mouse", e);
		}
	}

	private void createCanvas() {
		canvas = Canvas3DFactory.createWindowed(screenWidth, screenHeight, APP_TITLE);
		canvas.setBackgroundColor(Colorf.BLACK);
		canvas.addWindowClosingListener(new WindowClosingRenderLoopEnder(this));
		
		x3dEnvironment.addCanvas(canvas);
	}

	private void createEmptyScene() {
		currentRoot = new BranchGroup();
		x3dEnvironment.addPerspectiveBranch(currentRoot);
	}

	private void initializeLog4j() {
		if (new File(LOG4J_CONFIGURATION_FILE).exists()) {
			DOMConfigurator.configure(LOG4J_CONFIGURATION_FILE);
		} else {
			System.err.println("Log4j not initialized: \"xithcluster-log4j.xml\" could not be found");
		}
	}
	
	private void createRenderTargets() {
		org.xith3d.render.Renderer renderer;
		TextureRenderTarget colorAndAlphaRenderTarget;
		BaseRenderPassConfig passConfig;
		
		colorAndAlphaTexture = TextureCreator.createTexture(TextureFormat.RGBA, screenWidth, screenHeight, Colorf.BLACK);
		
		renderer = x3dEnvironment.getRenderer();
		
		passConfig = new BaseRenderPassConfig(ProjectionPolicy.PERSPECTIVE_PROJECTION);
		
		colorAndAlphaRenderTarget = new TextureRenderTarget(currentRoot, colorAndAlphaTexture, Colorf.BLACK, true);
		renderer.addRenderTarget(colorAndAlphaRenderTarget, passConfig);
		
		depthBufferRenderTarget = new DepthBufferRenderTarget(currentRoot, screenWidth, screenHeight);
		renderer.addRenderTarget(depthBufferRenderTarget, passConfig);
	}
	
	public void setScreenSize(int screenWidth, int screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		
		canvas.setSize(screenWidth, screenHeight);
	}
	
	public byte[] getColorAndAlphaBuffer() {
		return BufferUtils.safeBufferRead(colorAndAlphaTexture.getTextureCanvas().getImage().getDataBuffer());
	}
	
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
	
	public void setId(int id) {
		x3dEnvironment.getCanvas().setTitle(APP_TITLE + "[id=" + id + "]");
	}
	
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
	
	/*
	 * =============== 
	 * 		MAIN 
	 * ===============
	 */
	public static void main(String[] args) throws InputSystemException {
		new Renderer().begin();
	}

}
