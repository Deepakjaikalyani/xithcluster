package br.edu.univercidade.cc.xithcluster.distribution;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.apache.log4j.Logger;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.traversal.TraversalCallback;
import br.edu.univercidade.cc.xithcluster.replication.NodePathReplicator;

public class RoundRobinDistribution implements DistributionStrategy {
	
	private Logger log = Logger.getLogger(RoundRobinDistribution.class);
	
	private List<Light> lights = new ArrayList<Light>();
	
	private Stack<Shape3D> shapes = new Stack<Shape3D>();
	
	private List<BranchGroup> branchGroups = new ArrayList<BranchGroup>();
	
	private NodePathReplicator nodePathReplicator = new NodePathReplicator();
	
	@Override
	public List<BranchGroup> distribute(BranchGroup root, int numberOfRenderers) {
		BranchGroup branchGroup;
		int c;
		
		lights.clear();
		shapes.clear();
		
		root.traverse(new TraversalCallback() {
			
			@Override
			public boolean traversalOperation(Node node) {
				if (node instanceof Shape3D) {
					shapes.push((Shape3D) node);
				} else if (node instanceof Light) {
					lights.add((Light) node);
				}
				
				return true;
			}
			
			@Override
			public boolean traversalCheckGroup(GroupNode paramGroupNode) {
				return true;
			}
			
		});
		
		log.info("Number of lights: " + lights.size());
		log.info("Number of shapes: " + shapes.size());
		log.info("Number of renderers: " + numberOfRenderers);
		
		branchGroups.clear();
		for (int i = 0; i < numberOfRenderers; i++) {
			branchGroup = new BranchGroup();
			branchGroups.add(branchGroup);
			
			c = 0;
			nodePathReplicator.setRoot(branchGroup);
			
			for (Light light : lights) {
				nodePathReplicator.build(light);
			}
		}
		
		c = 0;
		// Round-robin algorithm
		while (!shapes.isEmpty()) {
			nodePathReplicator.setRoot(branchGroups.get((c++) % branchGroups.size()));
			nodePathReplicator.build(shapes.pop());
		}
		
		return branchGroups;
	}
	
}
