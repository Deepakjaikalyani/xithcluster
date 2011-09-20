package br.edu.univercidade.cc.xithcluster.util;

import java.util.ArrayList;
import java.util.List;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransformGroup;
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
		
		destination.removeAllChildren();
		children = new ArrayList<Node>();
		for (int i = 0; i < source.numChildren(); i++) {
			children.add(source.getChild(i));
		}
		
		source.removeAllChildren();
		for (Node child : children) {
			destination.addChild(child);
		}
		
		if (children.size() < 3) {
			Group stubGroup = new Group();
			Transform3D stubTransform = new Transform3D();
			TransformGroup stubTransformGroup = new TransformGroup(stubTransform);
			stubGroup.addChild(stubTransformGroup);
			destination.addChild(stubGroup);
		}
		
		destination.setIsOccluder(source.isOccluder());
		// TODO: bad hack!
        PrivateAccessor.setPrivateField(destination, "boundsDirty", true);
        destination.updateBounds(true);
        destination.setPickable(source.isPickable());
        destination.setPickable(source.isRenderable());
	}
	
}
