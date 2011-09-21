package br.edu.univercidade.cc.xithcluster.util;

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
		if (source == null || destination == null) {
			throw new IllegalArgumentException();
		}
		
		for (int i = 0; i < source.numChildren(); i++) {
			Node child = source.getChild(i);
			child.detach();
			destination.addChild(child);
		}
	}
	
}
