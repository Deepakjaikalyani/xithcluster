package br.edu.univercidade.cc.xithcluster.test;

import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.loop.Updatable;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.render.BaseRenderPassConfig;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.View.ProjectionPolicy;


public class SceneReAddingUpdatable implements Updatable {
	
	private Xith3DEnvironment environment;
	
	private BranchGroup newRoot;
	
	public SceneReAddingUpdatable(Xith3DEnvironment environment) {
		this.environment = environment;
	}
	
	public synchronized void setNewRoot(BranchGroup newRoot) {
		this.newRoot = newRoot;
	}

	@Override
	public synchronized void update(long gameTime, long frameTime, TimingMode timingMode) {
		if (newRoot != null) {
			environment.removeAllBranchGraphs();
			environment.addBranchGraph(newRoot, new BaseRenderPassConfig(ProjectionPolicy.PERSPECTIVE_PROJECTION));
			newRoot = null;
			System.out.println("Scene changed!");
		}
	}
	
}
