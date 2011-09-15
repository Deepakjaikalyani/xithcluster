package br.edu.univercidade.cc.xithcluster;

import java.io.File;
import java.io.IOException;
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
import org.xith3d.render.Canvas3D;
import org.xith3d.render.Canvas3DFactory;
import org.xith3d.render.RenderPass;
import org.xith3d.render.TextureRenderTarget;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.View;
import org.xith3d.utility.events.WindowClosingRenderLoopEnder;
import br.edu.univercidade.cc.xithcluster.communication.RendererNetworkManager;
import br.edu.univercidade.cc.xithcluster.util.BufferUtils;
import br.edu.univercidade.cc.xithcluster.util.ViewHelper;

public class Renderer extends InputAdapterRenderLoop {
	
	private static final float FRAMES_PER_SECOND = 60.0f;
	
	private static final String LOG4J_CONFIGURATION_FILE = "log4j.xml";
	
	private static final String APP_TITLE = "Renderer";
	
	private static final int DEFAULT_WIDTH = 800;
	
	private static final int DEFAULT_HEIGHT = 600;
	
	private BranchGroup root;
	
	private RendererNetworkManager networkManager;
	
	private Texture2D colorAndAlphaTexture;
	
	private Texture2D depthTexture;
	
	private final Object sceneLock = new Object();

	private Canvas3D canvas;

	private int screenWidth = DEFAULT_WIDTH;

	private int screenHeight = DEFAULT_HEIGHT;
	
	public Renderer(float maxFPS) {
		super(maxFPS);
	}
	
	@Override
	public void begin(RunMode runMode, TimingMode timingMode) {
		initializeLog4j();
		
		new Xith3DEnvironment(this);
		
		setRoot(new BranchGroup());
		
		canvas = Canvas3DFactory.createWindowed(screenWidth, screenHeight, APP_TITLE);
		canvas.setBackgroundColor(Colorf.BLACK);
		canvas.addWindowClosingListener(new WindowClosingRenderLoopEnder(this));
		getXith3DEnvironment().addCanvas(canvas);
		
		try {
			InputSystem.getInstance().registerNewKeyboardAndMouse(canvas.getPeer());
		} catch (InputSystemException e) {
			throw new RuntimeException("Error registering new keyboard and mouse", e);
		}
		
		networkManager = new RendererNetworkManager(this);
		try {
			networkManager.initialize();
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error starting network manager", e);
		}
		
		super.begin(runMode, timingMode);
	}

	private void initializeLog4j() {
		if (new File(LOG4J_CONFIGURATION_FILE).exists()) {
			DOMConfigurator.configure(LOG4J_CONFIGURATION_FILE);
		} else {
			System.err.println("Log4j not initialized: \"xithcluster-log4j.xml\" could not be found");
		}
	}
	
	private void setRoot(BranchGroup newRoot) {
		org.xith3d.render.Renderer renderer;
		RenderPass renderPass;
		
		if (root != null) {
			getXith3DEnvironment().removeBranchGraph(root);
		}
		
		root = newRoot;
		renderPass = getXith3DEnvironment().addPerspectiveBranch(root);
		
		colorAndAlphaTexture = TextureCreator.createTexture(TextureFormat.RGBA, screenWidth, screenHeight, Colorf.BLACK);
		depthTexture = TextureCreator.createTexture(TextureFormat.DEPTH, screenWidth, screenHeight);
		
		renderer = getXith3DEnvironment().getRenderer();
		
		renderer.addRenderTarget(new TextureRenderTarget(root, colorAndAlphaTexture, Colorf.BLACK, true), renderPass.getConfig());
		renderer.addRenderTarget(new TextureRenderTarget(root, depthTexture), renderPass.getConfig());
	}
	
	public void setScreenSize(int screenWidth, int screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		
		canvas.setSize(screenWidth, screenHeight);
	}
	
	@Override
	protected void loopIteration(long gameTime, long frameTime, TimingMode timingMode) {
		super.prepareNextFrame(gameTime, frameTime, timingMode);
		
		if (networkManager.startFrame()) {
			synchronized (sceneLock) {
				super.renderNextFrame(gameTime, frameTime, timingMode);
				
				networkManager.sendColorAlphaAndDepthBuffers(readBytesFromTexture(colorAndAlphaTexture), readBytesFromTexture(depthTexture));
			}
		}
	}
	
	private byte[] readBytesFromTexture(Texture2D texture) {
		return BufferUtils.safeBufferRead(texture.getTextureCanvas().getImage().getDataBuffer());
	}
	
	@Override
	public void onKeyPressed(KeyPressedEvent e, Key key) {
		switch (key.getKeyID()) {
		case ESCAPE:
			this.end();
			break;
		}
	}
	
	public Object getSceneLock() {
		return sceneLock;
	}
	
	public void setId(int id) {
		// FIXME:
		// getXith3DEnvironment().getCanvas().setTitle(APP_TITLE + "[id=" + id + "]");
	}
	
	public void updateScene(View view, List<Light> lightSources, BranchGroup newRoot) {
		for (Light lightSource : lightSources) {
			newRoot.addChild(lightSource);
		}
		
		// TODO: Check this!
		ViewHelper.copy(view, getXith3DEnvironment().getView());
		
		setRoot(newRoot);
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
