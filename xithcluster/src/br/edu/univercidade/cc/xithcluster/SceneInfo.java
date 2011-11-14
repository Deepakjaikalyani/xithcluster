package br.edu.univercidade.cc.xithcluster;

import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.View;

public class SceneInfo {
	
	private BranchGroup root;
	
	private View pointOfView;
	
	public SceneInfo(BranchGroup root, View pointOfView) {
		this.root = root;
		this.pointOfView = pointOfView;
	}
	
	public BranchGroup getRoot() {
		return root;
	}
	
	public View getPointOfView() {
		return pointOfView;
	}
	
}
