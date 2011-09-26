package br.edu.univercidade.cc.xithcluster;

import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.View;

public class DeserializationResult {
	
	private View pointOfView;
	
	private BranchGroup scene;

	DeserializationResult(View pointOfView, BranchGroup scene) {
		this.pointOfView = pointOfView;
		this.scene = scene;
	}
	
	public View getPointOfView() {
		return pointOfView;
	}
	
	public BranchGroup getScene() {
		return scene;
	}
	
}