package br.edu.univercidade.cc.xithcluster.serialization;

import java.io.IOException;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransformGroup;

public class TransformGroupSerializerTest extends SerializerTester<TransformGroup> {
	
	@Override
	protected TransformGroup buildTarget() {
		return new TransformGroup(new Transform3D(0.0f, 3.0f, -5.0f));
	}
	
	@Override
	protected boolean compareResults(TransformGroup target, TransformGroup deserializedObject) {
		return target.numChildren() == deserializedObject.numChildren() && target.getTransform().equals(deserializedObject.getTransform());
	}
	
	public void testShouldSerializeAndDeserializeTransformGroup() throws IOException {
		this.serializeAndDeserializeAndCompareResult();
	}
	
}
