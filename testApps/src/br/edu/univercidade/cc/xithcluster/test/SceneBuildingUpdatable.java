package br.edu.univercidade.cc.xithcluster.test;

import org.xith3d.loop.Updatable;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.scenegraph.BranchGroup;
import br.edu.univercidade.cc.xithcluster.util.SceneBuilder;


public class SceneBuildingUpdatable implements Updatable {

	private BranchGroup root;
	
	private BranchGroup newRoot = null;
	
	public SceneBuildingUpdatable(BranchGroup root) {
		this.root = root;
	}

	@Override
	public synchronized void update(long gameTime, long frameTime, TimingMode timingMode) {
		if (newRoot != null) {
			SceneBuilder.copyAndInvalidateSource(root, newRoot);
			newRoot = null;
			System.out.println("Scene changed!");
		}
	}
	
	public synchronized void setNewRoot(BranchGroup newRoot) {
		this.newRoot = newRoot;
	}
	
}
