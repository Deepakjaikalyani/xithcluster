package br.edu.univercidade.cc.xithcluster;

import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Node;
import br.edu.univercidade.cc.xithcluster.util.PrivateAccessor;

public class NodePathReplicator extends PathBuilder {
	
	private BranchGroup root;
	
	private Node currentReplica;
	
	public void setRoot(BranchGroup arg0) {
		this.root = arg0;
	}
	
	@Override
	protected void beforeBuildPath(Node arg0) {
		currentReplica = null;
	}
	
	@Override
	protected void pathBuildStep(GroupNode parent, Node current) {
		Node parentReplica;
		
		parentReplica = replicate(parent);
		
		if (currentReplica == null) {
			currentReplica = replicate(current);
		}
		
		((GroupNode) parentReplica).addChild(currentReplica);
		
		currentReplica = parentReplica;
	}
	
	@Override
	protected void afterBuildPath(Node arg0) {
		if (currentReplica == null) {
			currentReplica = replicate(arg0);
		}
		
		root.addChild(currentReplica);
	}
	
	private static Node replicate(Node arg0) {
		Node replica;
		
		// FIXME: bad hack
		replica = (Node) PrivateAccessor.invokePrivateMethod(arg0, "newInstance");
		
		if (arg0 instanceof GroupNode) {
			replica.setName(arg0.getName() + "_" + System.nanoTime());
			replica.setIsOccluder(arg0.isOccluder());
			// FIXME: bad hack
			//PrivateAccessor.setPrivateField(replica, "boundsDirty", true);
			replica.updateBounds(true);
			replica.setPickable(arg0.isPickable());
			replica.setRenderable(replica.isRenderable());
		} else {
			replica.absorbDetails(arg0);
		}
		
		return replica;
	}
	
}
