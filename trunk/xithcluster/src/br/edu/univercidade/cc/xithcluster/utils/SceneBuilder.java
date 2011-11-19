package br.edu.univercidade.cc.xithcluster.utils;

import java.util.ArrayList;
import java.util.List;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.View;

public final class SceneBuilder {
	
	private SceneBuilder() {
	}
	
	public static void copyView(View dest, View src) {
		if (dest == null || src == null) {
			throw new IllegalArgumentException();
		}
		
		dest.setPosition(src.getPosition());
		dest.setCenterOfView(src.getCenterOfView());
		dest.setFacingDirection(src.getFacingDirection());
		dest.setFieldOfView(src.getFieldOfView());
		dest.setBackClipDistance(src.getBackClipDistance());
		dest.setFrontClipDistance(src.getFrontClipDistance());
	}
	
	public static void copyAndInvalidateSource(BranchGroup dest, BranchGroup src) {
		List<Node> children;
		
		if (src == null || dest == null) {
			throw new IllegalArgumentException();
		}
		
		int numChildren = src.numChildren();
		children = new ArrayList<Node>();
		for (int i = 0; i < numChildren; i++) {
			children.add(src.getChild(i));
		}
		
		src.removeAllChildren();
		
		for (Node child : children) {
			dest.addChild(child);
		}
	}
	
}
