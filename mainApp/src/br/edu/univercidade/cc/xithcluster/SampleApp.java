package br.edu.univercidade.cc.xithcluster;

import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.events.KeyPressedEvent;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Group;

public class SampleApp extends DistributedRenderLoop {
	
	public SampleApp() {
		super(new RoundRobinGeometryDistribution());
		
		Tuple3f eyePosition = new Tuple3f(0.0f, 0.0f, 3.0f);
		Tuple3f viewFocus = new Tuple3f(0.0f, 0.0f, 0.0f);
		Tuple3f upVector = new Tuple3f(0.0f, 1.0f, 0.0f);
		
		Xith3DEnvironment xith3dEnvironment = new Xith3DEnvironment(eyePosition, viewFocus, upVector, this);
		
		xith3dEnvironment.addPerspectiveBranch(createScene());
	}
	
	private BranchGroup createScene() {
		BranchGroup root;
		Group group;
		DirectionalLight directionalLight;
		
		root = new BranchGroup();
		
		group = new Group();
		group.setName("allShapesGrouper");
		root.addChild(group);

		// Shapes
		
		SceneUtils.addCube(group, "cube1", 0.75f, new Tuple3f(0.0f, 0.3f, -0.5f), new Tuple3f(-35.0f, 20.0f, 0.0f), Colorf.RED);
		SceneUtils.addCube(group, "cube2", 0.4f, new Tuple3f(-1.0f, 0.3f, -0.5f), new Tuple3f(25.0f, 0.0f, 0.0f), SceneUtils.loadTexture2D("resources/textures/crate.png"));
		
		SceneUtils.addSphere(group, "sphere1", 0.2f, new Tuple3f(0.7f, 0.2f, 0.3f), Colorf.GREEN);
		SceneUtils.addSphere(group, "sphere2", 0.1f, new Tuple3f(0.55f, -0.2f, 0.2f), Colorf.BLUE);
		SceneUtils.addSphere(group, "sphere3", 0.75f, new Tuple3f(-0.75f, 0.0f, -2.0f), Colorf.BROWN);
		
		// Lights
		
		directionalLight = new DirectionalLight(true, new Colorf(0.5f, 0.5f, 0.5f), Vector3f.NEGATIVE_Z_AXIS);
		root.addChild(directionalLight);
		
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
