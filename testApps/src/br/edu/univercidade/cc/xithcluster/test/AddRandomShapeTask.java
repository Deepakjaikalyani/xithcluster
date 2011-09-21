package br.edu.univercidade.cc.xithcluster.test;

import java.util.Random;
import java.util.TimerTask;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.Transform;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.scenegraph.primitives.Cube;
import br.edu.univercidade.cc.xithcluster.test.utils.TestUtils;

public class AddRandomShapeTask extends TimerTask {
	
	private static final float MAX_ANGLE = 45.0f;

	private SceneObserver sceneObserver;
	
	private Random random;
	
	public AddRandomShapeTask(SceneObserver sceneObserver) {
		this.sceneObserver = sceneObserver;
		
		random = new Random();
	}
	
	private Tuple3f generateRandomPosition() {
		return new Tuple3f(random.nextFloat() * 1.0f, random.nextFloat() * 1.0f, -0.5f);
	}
	
	@Override
	public void run() {
		BranchGroup root;
		Group group;
		TransformGroup transformGroup;
		Transform transform;
		Cube cube;
		
		root = new BranchGroup();
		group = new Group();
		transform = new Transform();
		
		for (int i = 0; i < 5; i++) {
			cube = new Cube(0.3f);
			cube.getAppearance(true).setColor(TestUtils.randomColor());
			
			transform.clear();
			transform.addTranslation(generateRandomPosition());
			transform.addRotation(random.nextFloat() * MAX_ANGLE, random.nextFloat() * MAX_ANGLE, random.nextFloat() * MAX_ANGLE);
			transformGroup = new TransformGroup(transform.getTransform());
			transformGroup.addChild(cube);
			
			group.addChild(transformGroup);
		}
		
		root.addChild(group);
		
		sceneObserver.sceneChange(root);
	}
	
}
