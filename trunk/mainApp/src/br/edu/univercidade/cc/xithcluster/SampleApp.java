package br.edu.univercidade.cc.xithcluster;

import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.events.KeyPressedEvent;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.Material;
import org.xith3d.scenegraph.Transform;
import org.xith3d.scenegraph.TransformGroup;
import br.edu.univercidade.cc.xithcluster.primitives.Cube;
import br.edu.univercidade.cc.xithcluster.primitives.Sphere;

public class SampleApp extends DistributedRenderLoop {
	
	public SampleApp() {
		super(new RoundRobinGeometryDistribution());
		
		new Xith3DEnvironment(0f, 0f, 3f, 0f, 0f, 0f, 0f, 1f, 0f, this);
		
		getXith3DEnvironment().addPerspectiveBranch(createTestScene());
	}
	
	private BranchGroup createTestScene() {
		BranchGroup root;
		Group group1;
		Material material;
		Transform transform;
		TransformGroup transformGroup1;
		TransformGroup transformGroup2;
		Cube cube;
		Sphere sphere;
		Light light1;
		
		root = new BranchGroup();
		
		group1 = new Group();
		group1.setName("group1");
		
		// CUBE 1
		
		material = new Material();
        material.setEmissiveColor(Colorf.RED);
        material.setColorTarget(Material.NONE);
        material.setLightingEnabled(true);
        
		transform = new Transform();
		transform.setTranslation(0.0f, 0.3f, -0.5f);
		transformGroup1 = new TransformGroup(transform.getTransform());
		
		group1.addChild(transformGroup1);
		
		transform = new Transform();
		transform.setRotation(-35, 20, 0);
		transformGroup2 = new TransformGroup(transform.getTransform());
		
		transformGroup1.addChild(transformGroup2);
		
		cube = new Cube(0.75f);
		cube.setName("cube1");
		cube.getAppearance(true).setMaterial(material);
		
		transformGroup2.addChild(cube);
		
		// CUBE 2
		
		transform = new Transform();
		transform.setTranslation(-1.0f, 0.3f, -0.5f);
		transformGroup1 = new TransformGroup(transform.getTransform());
		
		group1.addChild(transformGroup1);
		
		transform = new Transform();
		transform.setRotationX(25);
		transformGroup2 = new TransformGroup(transform.getTransform());
		
		transformGroup1.addChild(transformGroup2);
		
		material = new Material();
        material.setEmissiveColor(Colorf.ORANGE);
        material.setColorTarget(Material.NONE);
        material.setLightingEnabled(true);
		
		cube = new Cube(0.4f);
		cube.setName("cube2");
		cube.getAppearance(true).setMaterial(material);
		
		transformGroup2.addChild(cube);
		
		// SPHERE 1
		
		transform = new Transform();
		transform.setTranslation(0.7f, 0.2f, 0.3f);
		transformGroup1 = new TransformGroup(transform.getTransform());
		
		group1.addChild(transformGroup1);
		
		material = new Material();
        material.setEmissiveColor(Colorf.GREEN);
        material.setColorTarget(Material.NONE);
        material.setLightingEnabled(true);
		
		sphere = new Sphere(0.2f, 20, 20, Geometry.COORDINATES | Geometry.NORMALS, false, 2);
		sphere.setName("sphere1");
		sphere.getAppearance(true).setMaterial(material);
		
		transformGroup1.addChild(sphere);
		
		// SPHERE 2
		
		transform = new Transform();
		transform.setTranslation(0.55f, -0.2f, 0.2f);
		transformGroup1 = new TransformGroup(transform.getTransform());
		
		group1.addChild(transformGroup1);
		
		material = new Material();
        material.setEmissiveColor(Colorf.BLUE);
        material.setColorTarget(Material.NONE);
        material.setLightingEnabled(true);
        
		sphere = new Sphere(0.1f, 20, 20, Geometry.COORDINATES | Geometry.NORMALS, false, 2);
		sphere.setName("sphere2");
		sphere.getAppearance(true).setMaterial(material);
		
		transformGroup1.addChild(sphere);
		
		// SPHERE 3
		
		transform = new Transform();
		transform.setTranslation(-0.75f, 0, -2.0f);
		transformGroup1 = new TransformGroup(transform.getTransform());
		
		group1.addChild(transformGroup1);
		
		material = new Material();
        material.setEmissiveColor(Colorf.BROWN);
        material.setColorTarget(Material.NONE);
        material.setLightingEnabled(true);
        
		sphere = new Sphere(0.75f, 20, 20, Geometry.COORDINATES | Geometry.NORMALS, false, 2);
		sphere.setName("sphere2");
		sphere.getAppearance(true).setMaterial(material);
		
		transformGroup1.addChild(sphere);
		
		// LIGHT 1
		
		light1 = new DirectionalLight(true, new Colorf(0.5f, 0.5f, 0.5f), Vector3f.NEGATIVE_Z_AXIS);
		root.addChild(light1);
		
		root.addChild(group1);
		
		return root;
	}
	
	@Override
	public void onKeyPressed(KeyPressedEvent e, Key key) {
		switch (key.getKeyID()) {
		case ESCAPE:
			this.end();
			break;
		}
	}
	
	public static void main(String[] args) {
		new SampleApp().begin();
	}
}
