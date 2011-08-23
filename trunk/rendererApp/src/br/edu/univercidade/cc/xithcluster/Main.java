package br.edu.univercidade.cc.xithcluster;

import java.util.List;
import org.jagatoo.input.InputSystem;
import org.jagatoo.input.InputSystemException;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.events.KeyPressedEvent;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.loop.InputAdapterRenderLoop;
import org.xith3d.render.Canvas3D;
import org.xith3d.render.Canvas3DFactory;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.View;
import org.xith3d.utility.events.WindowClosingRenderLoopEnder;
import br.edu.univercidade.cc.xithcluster.comm.RendererNetworkManager;

public class Main extends InputAdapterRenderLoop implements SceneManager {
	
	private static final String APP_TITLE = "Renderer";
	
	private static final int WIDTH = 800;
	
	private static final int HEIGHT = 600;
	
	private BranchGroup root;
	
	private RendererNetworkManager networkManager;
	
	/*private Texture2D colorBuffer;
	
	private Texture2D depthBuffer;*/
	
	private final Object sceneLock = new Object();
	
	public Main() throws InputSystemException {
		super(120f);
		
		Canvas3D canvas;
		Xith3DEnvironment xith3DEnvironment;
		/*Renderer renderer;
		RenderPassConfig renderPassConfig;*/
		
		xith3DEnvironment = new Xith3DEnvironment(this);
		xith3DEnvironment.addCanvas(canvas = Canvas3DFactory.createWindowed(WIDTH, HEIGHT, APP_TITLE));
		
		networkManager = new RendererNetworkManager(this);
		networkManager.initialize();
		
		setRoot(new BranchGroup());
		updateModifications();
		
		/*colorBuffer = TextureCreator.createTexture(TextureFormat.RGB, WIDTH, HEIGHT, Colorf.BLACK); 
		colorBuffer.enableAutoFreeLocalData();
		depthBuffer = TextureCreator.createTexture(TextureFormat.DEPTH, WIDTH, HEIGHT); 
		depthBuffer.enableAutoFreeLocalData();
		 
		renderer = xith3DEnvironment.getRenderer(); 
		renderPassConfig = new BaseRenderPassConfig();
		
		renderer.addRenderTarget(new TextureRenderTarget(root, colorBuffer, Colorf.BLACK), renderPassConfig);
		renderer.addRenderTarget(new TextureRenderTarget(root, depthBuffer, Colorf.BLACK), renderPassConfig);*/

		canvas.addWindowClosingListener(new WindowClosingRenderLoopEnder(this));
		
		InputSystem.getInstance().registerNewKeyboardAndMouse(canvas.getPeer());
	}
	
	@Override
	protected void loopIteration(long gameTime, long frameTime, TimingMode timingMode) {
		super.prepareNextFrame(gameTime, frameTime, timingMode);
		
		if (networkManager.renderNewFrame()) {
			synchronized (sceneLock) {
				super.renderNextFrame(gameTime, frameTime, timingMode);
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
	public void updateModifications() {
		getXith3DEnvironment().addPerspectiveBranch(root);
	}
	
	/*
	 * =============== 
	 * 		MAIN 
	 * ===============
	 */

	public static void main(String[] args) throws InputSystemException {
		new Main().begin();
	}

}
