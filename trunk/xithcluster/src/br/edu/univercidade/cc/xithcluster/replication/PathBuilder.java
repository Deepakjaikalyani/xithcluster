package br.edu.univercidade.cc.xithcluster.replication;

import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Node;

public abstract class PathBuilder {
	
	public void build(Node startingPoint) {
		Node currentNode;
		GroupNode parent;
		
		beforeBuildPath(startingPoint);
		
		currentNode = startingPoint;
		while (currentNode != null) {
			parent = currentNode.getParent();
			
			if (parent == null || parent instanceof BranchGroup) {
				break;
			}
			
			pathBuildStep(parent, currentNode);
			
			currentNode = parent;
		}
		
		afterBuildPath(startingPoint);
	}
	
	protected abstract void beforeBuildPath(Node arg0);
	
	protected abstract void pathBuildStep(GroupNode parent, Node current);
	
	protected abstract void afterBuildPath(Node arg0);
	
}