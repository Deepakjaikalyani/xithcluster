package br.edu.univercidade.cc.xithcluster.test;

import java.util.Random;
import java.util.TimerTask;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.scenegraph.primitives.Cube;

public class AddRandomShapeTask extends TimerTask {
	
	private int screenWidth;
	
	private int screenHeight;
	
	private SceneObserver sceneObserver;
	
	private Random random;
	
	public AddRandomShapeTask(int screenWidth, int screenHeight, SceneObserver sceneObserver) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.sceneObserver = sceneObserver;
		
		random = new Random();
	}
	
	private Tuple3f generateRandomPosition() {
		return new Tuple3f(random.nextFloat() * (float) screenWidth, random.nextFloat() * (float) screenHeight, 0);
	}
	
	@Override
	public void run() {
		BranchGroup root;
		Group group;
		TransformGroup transformGroup;
		Transform3D transform;
		Cube cube;
		
		root = new BranchGroup();
		group = new Group();
		
		transform = new Transform3D(generateRandomPosition());
		transform.rotXYZ(random.nextFloat() * 45f, random.nextFloat() * 45f, random.nextFloat() * 45f);
		transformGroup = new TransformGroup(transform);
		
		cube = new Cube(0.5f);
		transformGroup.addChild(cube);
		
		group.addChild(transformGroup);
		
		root.addChild(group);
		
		sceneObserver.sceneChange(root);
	}
	
}
