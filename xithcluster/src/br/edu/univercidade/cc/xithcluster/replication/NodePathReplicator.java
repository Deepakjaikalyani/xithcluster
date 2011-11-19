package br.edu.univercidade.cc.xithcluster.replication;

import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Node;
import br.edu.univercidade.cc.xithcluster.utils.PrivateAccessor;

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
		
		if (arg0 instanceof GroupNode) {
			replica = replicateGroupNode((GroupNode) arg0);
		} else {
			replica = replicateNode(arg0);
		}
		
		return replica;
	}

	private static Node replicateNode(Node arg0) {
		Node replica;
		
		replica = arg0.sharedCopy();
		replica.setName(arg0.getName() + "_" + System.nanoTime());
		
		return replica;
	}

	private static Node replicateGroupNode(GroupNode arg0) {
		Node replica;
		
		replica = (Node) PrivateAccessor.invokePrivateMethod(arg0, "newInstance");
		replica.setIsOccluder(arg0.isOccluder());
		PrivateAccessor.setPrivateField(replica, "boundsDirty", true);
		replica.setPickable(arg0.isPickable());
		replica.setRenderable(replica.isRenderable());
		replica.setName(arg0.getName() + "_" + System.nanoTime());
		
		return replica;
	}
	
}
