package br.edu.univercidade.cc.xithcluster;

import java.io.File;
import java.io.IOException;
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
import org.xith3d.render.BaseRenderPassConfig;
import org.xith3d.render.Canvas3D;
import org.xith3d.render.Canvas3DFactory;
import org.xith3d.render.DepthBufferRenderTarget;
import org.xith3d.render.TextureRenderTarget;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.View;
import org.xith3d.scenegraph.View.ProjectionPolicy;
import org.xith3d.utility.events.WindowClosingRenderLoopEnder;
import br.edu.univercidade.cc.xithcluster.communication.RendererNetworkManager;
import br.edu.univercidade.cc.xithcluster.util.BufferUtils;
import br.edu.univercidade.cc.xithcluster.util.SceneBuilder;

public class Renderer extends InputAdapterRenderLoop {
	
	private static final float FRAMES_PER_SECOND = 60.0f;
	
	private static final String LOG4J_CONFIGURATION_FILE = "log4j.xml";
	
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

	public Renderer(float maxFPS) {
		super(maxFPS);
	}
	
	@Override
	public void begin(RunMode runMode, TimingMode timingMode) {
		initializeLog4j();
		
		new Xith3DEnvironment(this);
		
		currentRoot = new BranchGroup();
		getXith3DEnvironment().addPerspectiveBranch(currentRoot);
		
		buildRenderTargets();
		
		canvas = Canvas3DFactory.createWindowed(screenWidth, screenHeight, APP_TITLE);
		canvas.setBackgroundColor(Colorf.BLACK);
		canvas.addWindowClosingListener(new WindowClosingRenderLoopEnder(this));
		getXith3DEnvironment().addCanvas(canvas);
		
		try {
			InputSystem.getInstance().registerNewKeyboardAndMouse(canvas.getPeer());
		} catch (InputSystemException e) {
			// TODO:
			throw new RuntimeException("Error registering new keyboard and mouse", e);
		}
		
		networkManager = new RendererNetworkManager(this);
		try {
			networkManager.initialize();
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error starting network manager", e);
		}
		
		getXith3DEnvironment().getOperationScheduler().addUpdatable(networkManager);
		
		super.begin(runMode, timingMode);
	}

	private void initializeLog4j() {
		if (new File(LOG4J_CONFIGURATION_FILE).exists()) {
			DOMConfigurator.configure(LOG4J_CONFIGURATION_FILE);
		} else {
			System.err.println("Log4j not initialized: \"xithcluster-log4j.xml\" could not be found");
		}
	}
	
	private void buildRenderTargets() {
		org.xith3d.render.Renderer renderer;
		TextureRenderTarget colorAndAlphaRenderTarget;
		BaseRenderPassConfig passConfig;
		
		colorAndAlphaTexture = TextureCreator.createTexture(TextureFormat.RGBA, screenWidth, screenHeight, Colorf.BLACK);
		
		renderer = getXith3DEnvironment().getRenderer();
		
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
		getXith3DEnvironment().getCanvas().setTitle(APP_TITLE + "[id=" + id + "]");
	}
	
	public void updateScene(View view, BranchGroup newRoot) {
		SceneBuilder.copy(getXith3DEnvironment().getView(), view);
		
		currentRoot.removeAllChildren();
		SceneBuilder.copyAndInvalidateSource(currentRoot, newRoot);
	}
	
	/*
	 * =============== 
	 * 		MAIN 
	 * ===============
	 */
	public static void main(String[] args) throws InputSystemException {
		new Renderer(FRAMES_PER_SECOND).begin();
	}

}
