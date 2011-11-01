package br.edu.univercidade.cc.xithcluster;

import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.events.KeyPressedEvent;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.loop.opscheduler.Animator;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.Material;
import org.xith3d.schedops.movement.AnimatableGroup;
import org.xith3d.schedops.movement.GroupRotator;
import org.xith3d.schedops.movement.TransformationDirectives;
import br.edu.univercidade.cc.xithcluster.primitives.Sphere;

public class EarthAndMoonSample extends SampleApplication {
	
	public EarthAndMoonSample(Tuple3f eyePosition, Tuple3f viewFocus) {
		super(eyePosition, viewFocus);
	}

	@Override
	protected String getJARName() {
		return "solarSystemSample.jar";
	}
	
	@Override
	protected void keyPressed(KeyPressedEvent event, Key key) {
	}

	@Override
	protected BranchGroup createSceneRoot(Animator animator) {
		BranchGroup root;
		Group group;
		AnimatableGroup earthGroup, moonGroup;
		GroupRotator earthRotation, moonRotation;
		
		root = new BranchGroup();
		
		group = new Group();
		group.setName("allShapesGrouper");
		root.addChild(group);
		
		earthRotation = new GroupRotator(new TransformationDirectives(new Vector3f(0.0f, 1.0f, 0.0f), 0.0f, 0.1f));
		earthGroup = new AnimatableGroup(earthRotation);
		group.addChild(earthGroup);
		
		animator.addAnimatableObject(earthGroup);

		// The earth
		
		Sphere earth = SceneUtils.addSphere(earthGroup, 
											"earth", 
											4.0f,
											100,
											100,
											new Tuple3f(0.0f, 0.0f, 0.0f), 
											SceneUtils.loadTexture2D("resources/textures/earth.png"));
		
		earth.getAppearance().setMaterial(new Material(true, 1.0f));

		moonRotation = new GroupRotator(new TransformationDirectives(new Vector3f(0.0f, 1.0f, 0.0f), 0.0f, 0.1f));
		moonGroup = new AnimatableGroup(moonRotation);
		group.addChild(moonGroup);
		
		animator.addAnimatableObject(moonGroup);
		
		// The moon
		
		Sphere moon = SceneUtils.addSphere(moonGroup, 
											"moon", 
											0.5f,
											30,
											30,
											new Tuple3f(4.5f, 0.0f, 0.0f), 
											SceneUtils.loadTexture2D("resources/textures/moon.png"));
		
		moon.getAppearance().setMaterial(new Material(true, 1.0f));
		
		// Light of the sun
		
		SceneUtils.addSpotLight(root, 
				"lightOfTheSun", 
				new Colorf(0.5f, 0.5f, 0.5f), 
				new Tuple3f(-50.0f, 0.0f, 0.0f), 
				new Tuple3f(1.0f, 0.0f, 0.0f), 
				new Tuple3f(0f, 0f, 0f), 
				60.0f, 
				1.0f);
		
		return root;
	}
	
	/*
	 * ================ 
	 * 		MAIN 
	 * ================
	 */
	public static void main(String[] args) {
		EarthAndMoonSample earthAndMoonSample = new EarthAndMoonSample(new Tuple3f(0.0f, 0.0f, 10.0f), new Tuple3f(0.0f, 0.0f, 0.0f));
		earthAndMoonSample.init(args);
	}
	
}
