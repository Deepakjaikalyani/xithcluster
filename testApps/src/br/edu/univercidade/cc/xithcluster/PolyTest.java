package br.edu.univercidade.cc.xithcluster;

import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.events.KeyPressedEvent;
import org.openmali.vecmath2.Colorf;
import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.scenegraph.BranchGroup;

public abstract class PolyTest extends DistributedRenderLoop {
	
	private static final Colorf[] COLORS = { Colorf.WHITE, Colorf.BLUE, Colorf.BROWN, Colorf.CYAN, Colorf.GRAY, Colorf.GREEN, Colorf.LIGHT_BROWN, Colorf.LIGHT_GRAY, Colorf.MAGENTA, Colorf.ORANGE, Colorf.PINK, Colorf.RED, Colorf.YELLOW };

	protected static Colorf randomColor() {
		return COLORS[(int) Math.floor(Math.random() * COLORS.length)];
	}

	public PolyTest(Xith3DEnvironment x3dEnv, float maxFPS) {
		super(x3dEnv, maxFPS);
	}
	
	public PolyTest(Xith3DEnvironment x3dEnv) {
		super(x3dEnv);
	}
	
	@Override
	public void onKeyPressed(KeyPressedEvent e, Key key) {
		switch (key.getKeyID()) {
		case ESCAPE:
			this.end();
			break;
		}
	}

	public PolyTest() {
		super(XithClusterConfiguration.targetFPS);
		
		new Xith3DEnvironment(0f, 0f, 5f, 0f, 0f, 0f, 0f, 1f, 0f, this);
		
		getXith3DEnvironment().addPerspectiveBranch(createTestScene());
		
		setDistributionStrategy(new SimpleDistribution());
	}
	
	protected abstract BranchGroup createTestScene();
	
}