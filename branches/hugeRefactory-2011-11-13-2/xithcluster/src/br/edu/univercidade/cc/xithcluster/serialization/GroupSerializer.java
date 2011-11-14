package br.edu.univercidade.cc.xithcluster.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.xith3d.scenegraph.Group;

public class GroupSerializer extends Serializer<Group> implements GroupNodeSerializer {
	
	@Override
	protected void doSerialization(Group group, DataOutputStream out) throws IOException {
		SerializationHelper.writeString(out, group.getName());
		out.writeInt(group.numChildren());
	}
	
	@Override
	protected Group doDeserialization(DataInputStream in) throws IOException {
		Group group;

		group = new Group();
		group.setName(SerializationHelper.readString(in));
		group.setUserData(NUMBER_OF_CHILDREN_USER_DATA, in.readInt());
		
		return group;
	}
}
