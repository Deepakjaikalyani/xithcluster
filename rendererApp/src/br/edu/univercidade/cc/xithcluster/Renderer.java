package br.edu.univercidade.cc.xithcluster;

import java.io.File;
import java.util.List;
import org.apache.log4j.xml.DOMConfigurator;
import org.jagatoo.input.InputSystem;
import org.jagatoo.input.InputSystemException;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.events.KeyPressedEvent;
import org.jagatoo.opengl.enums.TextureFormat;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.loaders.texture.TextureCreator;
import org.xith3d.loop.InputAdapterRenderLoop;
import org.xith3d.render.BaseRenderPassConfig;
import org.xith3d.render.Canvas3D;
import org.xith3d.render.Canvas3DFactory;
import org.xith3d.render.RenderPassConfig;
import org.xith3d.render.TextureRenderTarget;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.View;
import org.xith3d.utility.events.WindowClosingRenderLoopEnder;
import br.edu.univercidade.cc.xithcluster.communication.RendererNetworkManager;

public class Renderer extends InputAdapterRenderLoop implements SceneManager {
	
	private static final float FRAMES_PER_SECOND = 120f;

	private static final String LOG4J_CONFIGURATION_FILE = "log4j.xml";
	
	private static final String APP_TITLE = "Renderer";
	
	private static final int WIDTH = 800;
	
	private static final int HEIGHT = 600;
	
	private BranchGroup root;
	
	private RendererNetworkManager networkManager;
	
	private Texture2D colorAndAlphaTexture;
	
	private Texture2D depthTexture;
	
	private final Object sceneLock = new Object();
	
	public Renderer(float maxFPS) {
		super(maxFPS);
	}

	@Override
	public void begin(RunMode runMode, TimingMode timingMode) {
		Canvas3D canvas;
		Xith3DEnvironment xith3DEnvironment;
		
		initializeLog4j();
		
		xith3DEnvironment = new Xith3DEnvironment(this);
		xith3DEnvironment.addCanvas(canvas = Canvas3DFactory.createWindowed(WIDTH, HEIGHT, APP_TITLE));
		
		networkManager = new RendererNetworkManager(this);
		networkManager.initialize();
		
		setRoot(new BranchGroup());
		getXith3DEnvironment().addPerspectiveBranch(root);
		
		createColorAndAlphaTexture();
		createDepthTexture();
		 
		addTexturesAsRenderTargets(xith3DEnvironment);

		canvas.addWindowClosingListener(new WindowClosingRenderLoopEnder(this));
		
		try {
			InputSystem.getInstance().registerNewKeyboardAndMouse(canvas.getPeer());
		} catch (InputSystemException e) {
			throw new RuntimeException("Error registering new keyboard and mouse", e);
		}
		
		super.begin(runMode, timingMode);
	}

	private void addTexturesAsRenderTargets(Xith3DEnvironment xith3DEnvironment) {
		org.xith3d.render.Renderer renderer;
		RenderPassConfig renderPassConfig;
		
		renderer = xith3DEnvironment.getRenderer(); 
		renderPassConfig = new BaseRenderPassConfig();
		
		renderer.addRenderTarget(new TextureRenderTarget(root, colorAndAlphaTexture, Colorf.BLACK), renderPassConfig);
		renderer.addRenderTarget(new TextureRenderTarget(root, depthTexture, Colorf.BLACK), renderPassConfig);
	}

	private void createDepthTexture() {
		depthTexture = TextureCreator.createTexture(TextureFormat.DEPTH, WIDTH, HEIGHT); 
		depthTexture.enableAutoFreeLocalData();
	}

	private void createColorAndAlphaTexture() {
		colorAndAlphaTexture = TextureCreator.createTexture(TextureFormat.RGBA, WIDTH, HEIGHT, Colorf.BLACK); 
		colorAndAlphaTexture.enableAutoFreeLocalData();
	}
	
	private void initializeLog4j() {
		if (new File(LOG4J_CONFIGURATION_FILE).exists()) {
			DOMConfigurator.configure(LOG4J_CONFIGURATION_FILE);
		} else {
			System.err.println("Log4j not initialized: \"xithcluster-log4j.xml\" could not be found");
		}
	}

	@Override
	protected void loopIteration(long gameTime, long frameTime, TimingMode timingMode) {
		super.prepareNextFrame(gameTime, frameTime, timingMode);
		
		if (networkManager.startFrame()) {
			synchronized (sceneLock) {
				super.renderNextFrame(gameTime, frameTime, timingMode);
				
				networkManager.sendColorAlphaAndDepthBuffer(colorAndAlphaTexture, depthTexture);
			}
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
	public Object getSceneLock() {
		return sceneLock;
	}
	
	@Override
	public BranchGroup getRoot() {
		return null;
	}
	
	@Override
	public void setId(int id) {
		// FIXME:
		//getXith3DEnvironment().getCanvas().setTitle(APP_TITLE + "[id=" + id + "]");
	}
	
	@Override
	public void setRoot(BranchGroup arg0) {
		if (root != null) {
			getXith3DEnvironment().removeBranchGraph(root);
		}
		
		root = arg0;
	}

	@Override
	public View getPointOfView() {
		return null;
	}
	
	@Override
	public void setPointOfView(Tuple3f eyePosition, Tuple3f viewFocus, Tuple3f vecUp) {
		// TODO: Check this!
		getXith3DEnvironment().getView().lookAt(eyePosition, viewFocus, vecUp);
	}

	@Override
	public List<Light> getLightSources() {
		return null;
	}
	
	@Override
	public void addLightSources(List<Light> arg0) {
		for (Light lightSource : arg0) {
			root.addChild(lightSource);
		}
	}

	@Override
	public void updateScene() {
		getXith3DEnvironment().addPerspectiveBranch(root);
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
