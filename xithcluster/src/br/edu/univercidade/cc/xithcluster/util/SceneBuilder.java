package br.edu.univercidade.cc.xithcluster.util;

import java.util.ArrayList;
import java.util.List;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Node;


public final class SceneBuilder {

	private SceneBuilder() {
	}

	public static void copyToDestinationAndInvalidateSource(BranchGroup destination, BranchGroup source) {
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
		
		destination.setIsOccluder(source.isOccluder());
		// TODO: bad hack!
        PrivateAccessor.setPrivateField(destination, "boundsDirty", true);
        destination.updateBounds(true);
        destination.setPickable(source.isPickable());
        destination.setPickable(source.isRenderable());
	}
	
}
