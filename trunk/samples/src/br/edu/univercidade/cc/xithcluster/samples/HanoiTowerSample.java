package br.edu.univercidade.cc.xithcluster.samples;

import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.loop.opscheduler.Animator;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Group;
import org.xith3d.schedops.movement.AnimatableGroup;
import org.xith3d.schedops.movement.GroupRotator;
import org.xith3d.schedops.movement.TransformationDirectives;
import br.edu.univercidade.cc.xithcluster.SampleApplication;
import br.edu.univercidade.cc.xithcluster.utils.SceneUtils;

public class HanoiTowerSample extends SampleApplication {
	
	@Override
	protected String getJARName() {
		return "sampleApp.jar";
	}
	
	@Override
	protected BranchGroup createSceneRoot(Animator animator) {
		BranchGroup root;
		Group group;
		AnimatableGroup animatableGroup;
		GroupRotator groupRotator;
		
		root = new BranchGroup();
		
		group = new Group();
		group.setName("allShapesGrouper");
		root.addChild(group);
		
		groupRotator = new GroupRotator(new TransformationDirectives(new Vector3f(0.0f, 1.0f, 0.0f), 0.0f, 1.0f));
		animatableGroup = new AnimatableGroup(groupRotator);
		group.addChild(animatableGroup);
		
		animator.addAnimatableObject(animatableGroup);
		
		SceneUtils.addRectangle(group, "floor", 3.0f, 3.0f, new Tuple3f(0.0f, -1.0f, -1.0f), new Tuple3f(90.0f, 0.0f, 0.0f), SceneUtils.loadTexture2D("resources/textures/floor.png"));
		
		
		// Shapes
		
		SceneUtils.addCube(group, "cube1", 0.75f, new Tuple3f(0.0f, 0.3f, -0.5f), new Tuple3f(-35.0f, 20.0f, 0.0f), Colorf.RED);
		SceneUtils.addCube(animatableGroup, "cube2", 0.4f, new Tuple3f(-1.0f, 0.3f, -0.5f), new Tuple3f(25.0f, 0.0f, 0.0f), SceneUtils.loadTexture2D("resources/textures/crate.png"));
		
		SceneUtils.addSphere(group, "sphere1", 0.2f, 20, 20, new Tuple3f(0.7f, 0.2f, 0.3f), Colorf.GREEN);
		SceneUtils.addSphere(group, "sphere2", 0.1f, 20, 20, new Tuple3f(0.55f, -0.2f, 0.2f), Colorf.BLUE);
		SceneUtils.addSphere(group, "sphere3", 0.75f, 20, 20, new Tuple3f(-0.75f, 0.0f, -2.0f), Colorf.BROWN);
		
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
