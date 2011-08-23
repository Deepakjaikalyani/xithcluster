package br.edu.univercidade.cc.xithcluster;

import java.util.ArrayList;
import java.util.List;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.traversal.TraversalCallback;
import org.xith3d.scenegraph.traversal.impl.PolygonCounter;

public class PolyCountDistribution implements DistributionStrategy, TraversalCallback {
	
	private int polygonsPerRenderer;
	
	// private int lowerIndex;
	
	private int[] polyCounters;
	
	private int currentIndex;
	
	private List<BranchGroup> distribution = new ArrayList<BranchGroup>();
	
	@Override
	public List<BranchGroup> distribute(BranchGroup root, int numberOfRenderers) {
		if (ShapeCounter.getShapeCount(root) < numberOfRenderers) {
			// TODO:
			throw new RuntimeException("Less shapes than renderers");
		}
		
		polygonsPerRenderer = (int) Math.ceil((double) PolygonCounter.getPolygonCount(root) / (double) numberOfRenderers);
		
		distribution.clear();
		
		for (int i = 0; i < numberOfRenderers; i++) {
			distribution.add(new BranchGroup());
		}
		
		// lowerIndex = -1;
		polyCounters = new int[numberOfRenderers];
		currentIndex = 0;
		
		root.traverse(this);
		
		return distribution;
	}
	
	@Override
	public boolean traversalOperation(Node node) {
		Shape3D shape;
		int polyCount;
		
		if (node instanceof Shape3D) {
			shape = (Shape3D) node;
			polyCount = shape.getGeometry().getVertexCount() / 3;
			
			if (polyCount > polygonsPerRenderer) {
				addToDistribution(getHasLowerPolysIndex(), shape, polyCount);
			}
			
			for (; currentIndex < polyCounters.length; currentIndex++) {
				if (polyCounters[currentIndex] + polyCount <= polygonsPerRenderer) {
					addToDistribution(currentIndex, shape, polyCount);
					return false;
				}
			}
		}
		
		return true;
	}
	
	private int getHasLowerPolysIndex() {
		int index = -1;
		int polyCount;
		
		polyCount = 0;
		for (int i = 0; i < polyCounters.length - 1; i++) {
			if (polyCounters[i] > polyCount) {
				index = i;
				polyCount = polyCounters[i];
			}
		}
		
		return index;
	}
	
	private void addToDistribution(int i, Shape3D shape, int polyCount) {
		// TODO:
	}
	
	@Override
	public boolean traversalCheckGroup(GroupNode groupNode) {
		return true;
	}
	
	public List<BranchGroup> getDistribution() {
		return null;
	}
	
}
