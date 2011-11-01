package br.edu.univercidade.cc.xithcluster.update;

import java.util.ArrayList;
import java.util.List;
import org.xith3d.render.Clipper;
import org.xith3d.render.ScissorRect;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.NodeComponent;
import org.xith3d.scenegraph.Switch;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.scenegraph.modifications.ScenegraphModificationsListener;
import br.edu.univercidade.cc.xithcluster.update.PendingUpdate.Type;

public class UpdateManager implements ScenegraphModificationsListener {
	
	private List<PendingUpdate> pendingUpdates = new ArrayList<PendingUpdate>();
	
	public boolean hasPendingUpdates() {
		return !pendingUpdates.isEmpty();
	}
	
	@Override
	public void onBranchGraphAdded(BranchGroup branchGroup) {
		PendingUpdate pendingUpdate;
		
		pendingUpdate = new PendingUpdate(Type.BRANCH_ADDED, branchGroup);
		
		pendingUpdates.add(pendingUpdate);
	}
	
	@Override
	public void onBranchGraphRemoved(BranchGroup branchGroup) {
		PendingUpdate pendingUpdate;
		
		pendingUpdate = new PendingUpdate(Type.BRANCH_REMOVED, branchGroup);
		
		pendingUpdates.add(pendingUpdate);
	}
	
	@Override
	public void onChildAddedToGroup(GroupNode groupNode, Node node) {
		PendingUpdate pendingUpdate;
		
		pendingUpdate = new PendingUpdate(Type.NODE_ADDED, node);
		pendingUpdate.setData("groupNode", groupNode);
		
		pendingUpdates.add(pendingUpdate);
	}
	
	@Override
	public void onNodePropertyChanged(Node node, String nodePropertyName) {
		PendingUpdate pendingUpdate;
		
		pendingUpdate = new PendingUpdate(Type.PROPERTY_CHANGED, node);
		pendingUpdate.setData("nodePropertyName", nodePropertyName);
		
		pendingUpdates.add(pendingUpdate);
	}
	
	@Override
	public void onChildRemovedFromGroup(GroupNode groupNode, Node node) {
		PendingUpdate pendingUpdate;
		
		pendingUpdate = new PendingUpdate(Type.NODE_REMOVED, node);
		pendingUpdate.setData("groupNode", groupNode);
		
		pendingUpdates.add(pendingUpdate);
	}
	
	@Override
	public void onSwitchWhichChildChanged(Switch switchGroup, int oldChildIndex, int newChildIndex) {
		PendingUpdate pendingUpdate;
		
		pendingUpdate = new PendingUpdate(Type.SWITCH_CHILD_CHANGED, switchGroup);
		pendingUpdate.setData("oldChildIndex", oldChildIndex);
		pendingUpdate.setData("newChildIndex", newChildIndex);
		
		pendingUpdates.add(pendingUpdate);
	}
	
	@Override
	public void onStateModifierContainmentChanged(GroupNode groupNode, boolean paramBoolean1, boolean paramBoolean2) {
		
	}
	
	@Override
	public void onScissorRectChanged(GroupNode groupNode, ScissorRect scissorRect1, ScissorRect scissorRect2) {
		
	}
	
	@Override
	public void onClipperChanged(GroupNode paramGroupNode, Clipper clipper1, Clipper clipper2) {
		
	}
	
	@Override
	public void onTransformChanged(TransformGroup transformGroup, Transform3D transform3D) {
		PendingUpdate pendingUpdate;
		
		pendingUpdate = new PendingUpdate(Type.TRANSFORM_CHANGED, transformGroup);
		pendingUpdate.setData("transform3D", transform3D);
		
		pendingUpdates.add(pendingUpdate);
	}
	
	@Override
	public void onNodeComponentChanged(NodeComponent nodeComponent) {
		PendingUpdate pendingUpdate;
		
		pendingUpdate = new PendingUpdate(Type.NODE_COMPONENT_CHANGED, nodeComponent);
		
		pendingUpdates.add(pendingUpdate);
	}
	
	public List<PendingUpdate> getPendingUpdates() {
		return pendingUpdates;
	}
	
}
