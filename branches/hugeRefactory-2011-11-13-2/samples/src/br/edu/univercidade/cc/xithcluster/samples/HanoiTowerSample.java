package br.edu.univercidade.cc.xithcluster.samples;

import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.loop.opscheduler.Animator;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.Transform;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.schedops.movement.AnimatableGroup;
import org.xith3d.schedops.movement.GroupTranslator;
import org.xith3d.schedops.movement.TransformationDirectives;
import br.edu.univercidade.cc.xithcluster.SampleApplication;
import br.edu.univercidade.cc.xithcluster.utils.SceneUtils;

public class HanoiTowerSample extends SampleApplication {
	
	private static final int STOP_CYLINDER3_FROM_GOING_UP = 2;

	private int counter = 0;
	
	private AnimatableGroup cylinder3GoingUp;
	
	@Override
	protected String getJARName() {
		return "sampleApp.jar";
	}
	
	@Override
	protected void oneSecondTick() {
		switch (counter++) {
		case STOP_CYLINDER3_FROM_GOING_UP:
			cylinder3GoingUp.stopAnimation();
			break;
		}
	}

	@Override
	protected BranchGroup createSceneRoot(Animator animator) {
		BranchGroup root;
		Group group;
		GroupTranslator groupTranslator;
		
		root = new BranchGroup();

		Transform transform;
		transform = new Transform();
		transform.setTranslation(new Tuple3f(0.0f, -1.0f, -2.0f));
		group = new TransformGroup(transform.getTransform());
		group.setName("allGeometries");
		root.addChild(group);
		
		groupTranslator = new GroupTranslator(new TransformationDirectives(new Vector3f(0.0f, 1.0f, 0.0f), 0.0f, 1.0f));
		cylinder3GoingUp = new AnimatableGroup(groupTranslator);
		group.addChild(cylinder3GoingUp);
		
		animator.addAnimatableObject(cylinder3GoingUp);
		
		SceneUtils.addCylinder(group, "pole1", 0.1f, 2.0f, 1.0f, false, 30, new Tuple3f(0.0f, 1.0f, 0.0f), new Tuple3f(0.0f, 0.0f, 0.0f), Colorf.BROWN);
		SceneUtils.addCylinder(group, "pole2", 0.1f, 2.0f, 1.0f, false, 30, new Tuple3f(-2.0f, 1.0f, 0.0f), new Tuple3f(0.0f, 0.0f, 0.0f), Colorf.BROWN);
		SceneUtils.addCylinder(group, "pole3", 0.1f, 2.0f, 1.0f, false, 30, new Tuple3f(2.0f, 1.0f, 0.0f), new Tuple3f(0.0f, 0.0f, 0.0f), Colorf.BROWN);
		
		SceneUtils.addCylinder(group, "cylinder1", 1.0f, 0.3f, 1.0f, false, 50, new Tuple3f(0.0f, 0.0f, 0.0f), new Tuple3f(0.0f, 0.0f, 0.0f), Colorf.GREEN);
		SceneUtils.addCylinder(group, "cylinder2", 0.66f, 0.3f, 1.0f, false, 50, new Tuple3f(0.0f, 0.3f, 0.0f), new Tuple3f(0.0f, 0.0f, 0.0f), Colorf.GREEN);
		SceneUtils.addCylinder(cylinder3GoingUp, "cylinder3", 0.33f, 0.3f, 1.0f, false, 50, new Tuple3f(0.0f, 0.6f, 0.0f), new Tuple3f(0.0f, 0.0f, 0.0f), Colorf.GREEN);
		
		// Lights
		
		SceneUtils.addDirectionalLight(root, "light1", new Colorf(0.5f, 0.5f, 0.5f), Vector3f.NEGATIVE_Z_AXIS);
		
		return root;
	}
	
	/*
	 * ================ 
	 * 		MAIN 
	 * ================
	 */
	public static void main(String[] args) {
		HanoiTowerSample hanoiTowerSample = new HanoiTowerSample();
		hanoiTowerSample.init(args);
	}

}
