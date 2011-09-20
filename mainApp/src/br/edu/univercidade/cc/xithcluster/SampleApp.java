package br.edu.univercidade.cc.xithcluster;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.events.KeyPressedEvent;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.Material;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransformGroup;
import br.edu.univercidade.cc.xithcluster.primitives.Cube;
import br.edu.univercidade.cc.xithcluster.primitives.Rectangle;
import br.edu.univercidade.cc.xithcluster.primitives.Sphere;

public class SampleApp extends DistributedRenderLoop {
	
	public SampleApp() {
		super(120f);
		
		new Xith3DEnvironment(0f, 0f, 3f, 0f, 0f, 0f, 0f, 1f, 0f, this);
		
		getXith3DEnvironment().addPerspectiveBranch(createTestScene());
		
		setDistributionStrategy(new SimpleDistribution());
	}
	
	private BranchGroup createTestScene() {
		BranchGroup root;
		Group group1;
		Material material1;
		Material material2;
		Transform3D transform;
		TransformGroup transformGroup;
		Cube cube1;
		Rectangle rectangle1;
		Sphere sphere1;
		Light light1;
		Texture2D texture2D;
		
		root = new BranchGroup();
		
		group1 = new Group();
		group1.setName("group1");
		
		material1 = new Material();
        material1.setEmissiveColor(Colorf.RED);
        material1.setColorTarget(Material.NONE);
        material1.setLightingEnabled(true);
        
        material2 = new Material();
        material2.setEmissiveColor(Colorf.GREEN);
        material2.setColorTarget(Material.NONE);
        material2.setLightingEnabled(true);
		
		transform = new Transform3D(0.0f, 0.3f, 0.5f);
		transform.rotXYZ(35, 25, 0);
		transformGroup = new TransformGroup(transform);
		
		cube1 = new Cube(0.5f, material1);
		cube1.setName("cube1");
		
		transformGroup.addChild(cube1);
		group1.addChild(transformGroup);
		
		try {
			texture2D = TextureLoader.getInstance().loadTexture(new FileInputStream("resources/textures/crate.png"));
		} catch (FileNotFoundException e) {
			// TODO:
			throw new RuntimeException("Texture not found");
		}
		
		transform = new Transform3D(-1.0f, 0.5f, 0.0f);
		transformGroup = new TransformGroup(transform);
		
		rectangle1 = new Rectangle(0.5f, 0.5f, texture2D);
		rectangle1.setName("rectangle1");
		
		transformGroup.addChild(rectangle1);
		group1.addChild(transformGroup);
		
		transform = new Transform3D(0.7f, 0.2f, 0.3f);
		transformGroup = new TransformGroup(transform);
		
		sphere1 = new Sphere(0.2f, 20, 20, material2);
		sphere1.setName("sphere1");
		
		transformGroup.addChild(sphere1);
		group1.addChild(transformGroup);
		
		transform = new Transform3D(0.0f, 0.0f, 1.0f);
		transformGroup = new TransformGroup(transform);
		
		light1 = new DirectionalLight(true, new Colorf(0.5f, 0.5f, 0.5f), Vector3f.NEGATIVE_Z_AXIS);
		
		transformGroup.addChild(light1);
		root.addChild(transformGroup);
		
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
