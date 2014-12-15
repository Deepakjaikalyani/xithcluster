package br.edu.univercidade.cc.xithcluster.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.xith3d.scenegraph.TransformGroup;

public class TransformGroupSerializer extends Serializer<TransformGroup> implements GroupNodeSerializer {
	
	@Override
	protected void doSerialization(TransformGroup transformGroup, DataOutputStream out) throws IOException {
		SerializationHelper.writeString(out, transformGroup.getName());
		out.writeInt(transformGroup.numChildren());
		SerializationHelper.writeTransform3D(out, transformGroup.getTransform());
	}
	
	@Override
	protected TransformGroup doDeserialization(DataInputStream in) throws IOException {
		TransformGroup transformGroup;

		transformGroup = new TransformGroup();
		
		transformGroup.setName(SerializationHelper.readString(in));
		transformGroup.setUserData(NUMBER_OF_CHILDREN_USER_DATA, in.readInt());
		transformGroup.setTransform(SerializationHelper.readTransform3D(in));
		
		return transformGroup;
	}
}
