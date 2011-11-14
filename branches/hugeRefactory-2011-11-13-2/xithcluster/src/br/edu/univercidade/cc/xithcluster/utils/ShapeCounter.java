package br.edu.univercidade.cc.xithcluster.utils;

import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.traversal.TraversalCallback;

public final class ShapeCounter implements TraversalCallback {
	
	private int counter;
	
	public boolean traversalOperation(Node node) {
		if (node instanceof Shape3D) {
			this.counter++;
		}
		return true;
	}
	
	public boolean traversalCheckGroup(GroupNode group) {
		return true;
	}
	
	public static int getShapeCount(GroupNode group) {
		ShapeCounter pc = new ShapeCounter();
		
		group.traverse(pc);
		
		return pc.counter;
	}
	
}
