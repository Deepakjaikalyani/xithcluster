package br.edu.univercidade.cc.xithcluster.test;

import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.events.KeyPressedEvent;
import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.scenegraph.BranchGroup;
import br.edu.univercidade.cc.xithcluster.DistributedRenderLoop;
import br.edu.univercidade.cc.xithcluster.RoundRobinGeometryDistribution;

public abstract class PolyTest extends DistributedRenderLoop {
	
	public PolyTest() {
		super(new RoundRobinGeometryDistribution());
		
		new Xith3DEnvironment(0f, 0f, 5f, 0f, 0f, 0f, 0f, 1f, 0f, this);
		
		getXith3DEnvironment().addPerspectiveBranch(createTestScene());
	}
	
	@Override
	public void onKeyPressed(KeyPressedEvent e, Key key) {
		switch (key.getKeyID()) {
		case ESCAPE:
			this.end();
			break;
		}
	}

	protected abstract BranchGroup createTestScene();
	
}