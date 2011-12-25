package br.edu.univercidade.cc.xithcluster.serialization;

import java.io.IOException;
import org.xith3d.scenegraph.Group;

public class GroupSerializerTest extends SerializerTester<Group> {
	
	@Override
	protected Group buildTarget() {
		return new Group();
	}
	
	@Override
	protected boolean compareResults(Group target, Group deserializedObject) {
		return target.numChildren() == deserializedObject.numChildren();
	}
	
	public void testShouldSerializeAndDeserializeGroup() throws IOException {
		this.serializeAndDeserializeAndCompareResult();
	}
	
}
