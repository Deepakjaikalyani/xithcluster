package br.edu.univercidade.cc.xithcluster.serial;

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
	
}
