package br.edu.univercidade.cc.xithcluster;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.events.KeyPressedEvent;
import org.openmali.vecmath2.Colorf;
import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.StaticTransform;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransformGroup;
import br.edu.univercidade.cc.xithcluster.primitives.Rectangle;

public class SampleApp extends DistributedRenderLoop {
	
	public SampleApp() {
		super(120f);
		
		new Xith3DEnvironment(0f, 0f, 5f, 0f, 0f, 0f, 0f, 1f, 0f, this);
		
		getXith3DEnvironment().addPerspectiveBranch(createTestScene());
		
		setDistributionStrategy(new SimpleDistribution());
	}
	
	private BranchGroup createTestScene() {
		BranchGroup root = new BranchGroup();
		Group group1 = new Group();
		Group group2 = new Group();
		
		group1.setName("group1");
		group2.setName("group2");
		
		Transform3D transform;
		
		transform = new Transform3D(0.3f, 0.3f, 0.5f);
		transform.rotY(15);
		
		TransformGroup transformGroup1 = new TransformGroup(transform);
		transformGroup1.setName("transformGroup1");
		
		Texture2D texture2D;
		try {
			texture2D = TextureLoader.getInstance().loadTexture(new FileInputStream("resources/textures/crate.png"));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Texture not found");
		}
		
		Rectangle rectangle1 = new Rectangle(0.5f, 0.5f, texture2D);
		rectangle1.setName("rectangle1");
		
		transformGroup1.addChild(rectangle1);
		
		Rectangle rectangle2 = new Rectangle(0.5f, 0.5f, Colorf.RED);
		rectangle2.setName("rectangle2");
		StaticTransform.translate(rectangle2, -1.0f, 0.5f, 0.0f);
		
		Rectangle rectangle3 = new Rectangle(0.5f, 0.5f, Colorf.GREEN);
		rectangle3.setName("rectangle3");
		StaticTransform.translate(rectangle3, -1.0f, 0.9f, 0.0f);
		
		Rectangle rectangle4 = new Rectangle(0.5f, 0.5f, Colorf.BLUE);
		rectangle4.setName("rectangle4");
		StaticTransform.translate(rectangle4, -0.5f, -0.5f, 0.0f);
		
		Rectangle rectangle5 = new Rectangle(0.5f, 0.5f, Colorf.YELLOW);
		rectangle5.setName("rectangle5");
		StaticTransform.translate(rectangle5, 0.5f, -0.5f, 0.0f);
		
		group2.addChild(transformGroup1);
		
		group2.addChild(rectangle2);
		group2.addChild(rectangle3);
		group2.addChild(rectangle4);
		group2.addChild(rectangle5);
		
		group1.addChild(group2);
		
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
