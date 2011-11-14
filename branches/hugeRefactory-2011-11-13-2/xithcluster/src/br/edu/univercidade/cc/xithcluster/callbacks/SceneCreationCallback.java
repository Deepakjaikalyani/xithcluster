package br.edu.univercidade.cc.xithcluster.callbacks;

import org.xith3d.loop.opscheduler.Animator;
import org.xith3d.scenegraph.BranchGroup;

public interface SceneCreationCallback {
	
	BranchGroup createSceneRoot(Animator animator);
	
}
