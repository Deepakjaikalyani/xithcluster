package br.edu.univercidade.cc.xithcluster;

import java.util.ArrayList;
import java.util.List;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.traversal.TraversalCallback;

public class LightSourceFinder implements TraversalCallback {
	
	private static LightSourceFinder lightSourceFinder = new LightSourceFinder();
	
	private List<Light> lightSources = new ArrayList<Light>();
	
	private LightSourceFinder() {
	}
	
	@Override
	public boolean traversalCheckGroup(GroupNode groupNode) {
		return true;
	}
	
	@Override
	public boolean traversalOperation(Node node) {
		if (node instanceof Light) {
			lightSources.add((Light) node);
		}
		
		return true;
	}
	
	public static List<Light> getLightSources(BranchGroup arg0) {
		arg0.traverse(lightSourceFinder);
		return lightSourceFinder.lightSources;
	}
	
}
