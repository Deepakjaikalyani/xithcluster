package br.edu.univercidade.cc.xithcluster.test;

import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.events.KeyPressedEvent;
import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.scenegraph.BranchGroup;
import br.edu.univercidade.cc.xithcluster.DistributedRenderLoop;
import br.edu.univercidade.cc.xithcluster.SimpleDistribution;
import br.edu.univercidade.cc.xithcluster.XithClusterConfiguration;

public abstract class PolyTest extends DistributedRenderLoop {
	
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