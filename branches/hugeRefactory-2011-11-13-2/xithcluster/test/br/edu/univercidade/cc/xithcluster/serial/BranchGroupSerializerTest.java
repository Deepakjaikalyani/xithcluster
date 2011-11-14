package br.edu.univercidade.cc.xithcluster.serial;

import org.xith3d.scenegraph.BranchGroup;

public class BranchGroupSerializerTest extends SerializerTester<BranchGroup> {
	
	@Override
	protected BranchGroup buildTarget() {
		return new BranchGroup();
	}
	
	@Override
	protected boolean compareResults(BranchGroup target, BranchGroup deserializedObject) {
		return target.numChildren() == deserializedObject.numChildren();
	}
	
}
