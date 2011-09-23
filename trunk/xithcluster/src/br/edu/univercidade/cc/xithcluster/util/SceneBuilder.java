package br.edu.univercidade.cc.xithcluster.util;

import java.util.ArrayList;
import java.util.List;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.View;

public final class SceneBuilder {

	private SceneBuilder() {
	}
	
	public static void copy(View destination, View source) {
		destination.setPosition(source.getPosition());
		destination.setCenterOfView(source.getCenterOfView());
		destination.setFacingDirection(source.getFacingDirection());
		destination.setFieldOfView(source.getFieldOfView());
		destination.setBackClipDistance(source.getBackClipDistance());
		destination.setFrontClipDistance(source.getFrontClipDistance());
	}

	public static void copyAndInvalidateSource(BranchGroup destination, BranchGroup source) {
		List<Node> children;
		
		if (source == null || destination == null) {
			throw new IllegalArgumentException();
		}
		
		int numChildren = source.numChildren();
		children = new ArrayList<Node>();
		for (int i = 0; i < numChildren; i++) {
			children.add(source.getChild(i));
		}
		
		source.removeAllChildren();
		
		for (Node child : children) {
			destination.addChild(child);
		}
	}
	
}
