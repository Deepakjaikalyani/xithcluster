package br.edu.univercidade.cc.xithcluster.test;

import java.util.Timer;
import org.jagatoo.input.InputSystem;
import org.jagatoo.input.InputSystemException;
import org.openmali.vecmath2.Colorf;
import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.loop.RenderLoop;
import org.xith3d.render.Canvas3D;
import org.xith3d.render.Canvas3DFactory;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.utility.events.WindowClosingRenderLoopEnder;
import br.edu.univercidade.cc.xithcluster.util.SceneBuilder;

public class AddingShapesAtRuntimeTest extends RenderLoop implements SceneObserver {
	
	private static final int SCREEN_WIDTH = 800;
	
	private static final int SCREEN_HEIGHT = 600;
	
	private static final String APP_TITLE = "Adding Shapes At Runtime Test";
	
	private static final Long ADD_RANDOM_SHAPE_TASK_INTERVAL = 1500L;
	
	private BranchGroup root;
	
	private Canvas3D canvas;
	
	private Timer timer;
	
	public AddingShapesAtRuntimeTest() {
		new Xith3DEnvironment(this);
		
		root = new BranchGroup();
		getXith3DEnvironment().addPerspectiveBranch(root);
		
		canvas = Canvas3DFactory.createWindowed(SCREEN_WIDTH, SCREEN_HEIGHT, APP_TITLE);
		canvas.setBackgroundColor(Colorf.BLACK);
		canvas.addWindowClosingListener(new WindowClosingRenderLoopEnder(this));
		
		getXith3DEnvironment().addCanvas(canvas);
		
		try {
			InputSystem.getInstance().registerNewKeyboardAndMouse(canvas.getPeer());
		} catch (InputSystemException e) {
			// TODO:
			throw new RuntimeException("Error registering new keyboard and mouse", e);
		}
		
		timer = new Timer();
		timer.schedule(new AddRandomShapeTask(SCREEN_WIDTH, SCREEN_HEIGHT, this), ADD_RANDOM_SHAPE_TASK_INTERVAL);
	}
	
	@Override
	public void sceneChange(BranchGroup arg0) {
		SceneBuilder.copyAndInvalidateSource(root, arg0);
	}
	
	/*
	 * ===================================== 
	 * 					MAIN
	 * =====================================
	 */
	public static void main(String[] args) {
		new AddingShapesAtRuntimeTest().begin();
	}
	
}
