package br.edu.univercidade.cc.xithcluster.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.xith3d.scenegraph.BranchGroup;

public class BranchGroupSerializer extends Serializer<BranchGroup> implements GroupNodeSerializer {
	
	@Override
	protected void doSerialization(BranchGroup branchGroup, DataOutputStream out) throws IOException {
		SerializationHelper.writeString(out, branchGroup.getName());
		out.writeInt(branchGroup.numChildren());
	}
	
	@Override
	protected BranchGroup doDeserialization(DataInputStream in) throws IOException {
		BranchGroup branchGroup;

		branchGroup = new BranchGroup();
		branchGroup.setName(SerializationHelper.readString(in));
		branchGroup.setUserData(NUMBER_OF_CHILDREN_USER_DATA, in.readInt());
		
		return branchGroup;
	}
}
