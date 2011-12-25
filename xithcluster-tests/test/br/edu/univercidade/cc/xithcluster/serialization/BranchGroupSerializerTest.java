package br.edu.univercidade.cc.xithcluster.serialization;

import java.io.IOException;
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
	
	public void testShouldSerializeAndDeserializeBranchGroup() throws IOException {
		this.serializeAndDeserializeAndCompareResult();
	}
	
}
