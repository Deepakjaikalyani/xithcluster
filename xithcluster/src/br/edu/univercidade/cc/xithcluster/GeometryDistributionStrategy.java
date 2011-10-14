package br.edu.univercidade.cc.xithcluster;

import java.util.List;
import org.xith3d.scenegraph.BranchGroup;

public interface GeometryDistributionStrategy {
	
	List<BranchGroup> distribute(BranchGroup root, int numberOfRenderers);
	
}
