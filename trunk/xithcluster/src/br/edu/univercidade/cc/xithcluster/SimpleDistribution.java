package br.edu.univercidade.cc.xithcluster;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.traversal.TraversalCallback;

public class SimpleDistribution implements DistributionStrategy {
	
	private Stack<Shape3D> shapes = new Stack<Shape3D>();
	
	private List<BranchGroup> branchGroups = new ArrayList<BranchGroup>();
	
	private NodePathReplicator nodePathReplicator = new NodePathReplicator();
	
	@Override
	public List<BranchGroup> distribute(BranchGroup root, int numberOfRenderers) {
		int shapesPerRenderer;
		BranchGroup branchGroup;
		int c;
		
		shapes.clear();
		
		root.traverse(new TraversalCallback() {
			
			@Override
			public boolean traversalOperation(Node node) {
				if (node instanceof Shape3D) {
					shapes.push((Shape3D) node);
				}
				
				return true;
			}
			
			@Override
			public boolean traversalCheckGroup(GroupNode paramGroupNode) {
				return true;
			}
			
		});
		
		shapesPerRenderer = (int) Math.ceil((double) shapes.size() / (double) numberOfRenderers);
		
		// DEBUG:
		System.out.println("totalShapes=" + shapes.size());
		System.out.println("numberOfRenderers=" + numberOfRenderers);
		System.out.println("shapesPerRenderer=" + shapesPerRenderer);
		
		branchGroups.clear();
		for (int i = 0; i < numberOfRenderers; i++) {
			branchGroup = new BranchGroup();
			branchGroups.add(branchGroup);
			
			c = 0;
			nodePathReplicator.setRoot(branchGroup);
			while (!shapes.isEmpty() && c++ < shapesPerRenderer) {
				nodePathReplicator.build(shapes.pop());
			}
		}
		
		return branchGroups;
	}
	
}
