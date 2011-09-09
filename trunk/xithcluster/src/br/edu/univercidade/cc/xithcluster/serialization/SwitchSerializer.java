package br.edu.univercidade.cc.xithcluster.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.xith3d.scenegraph.Switch;

public class SwitchSerializer extends Serializer<Switch> implements GroupNodeSerializer {
	
	@Override
	protected void doSerialization(Switch switchGroup, DataOutputStream out) throws IOException {
		SerializationHelper.writeString(out, switchGroup.getName());
		out.writeInt(switchGroup.numChildren());
		SerializationHelper.writeBitSet(out, switchGroup.getChildMask());
		out.writeInt(switchGroup.getWhichChild());
	}
	
	@Override
	protected Switch doDeserialization(DataInputStream in) throws IOException {
		Switch switchGroup;

		switchGroup = new Switch();
		switchGroup.setName(SerializationHelper.readString(in));
		switchGroup.setUserData(NUMBER_OF_CHILDREN_USER_DATA, in.readInt());
		switchGroup.setChildMask(SerializationHelper.readBitSet(in));
		switchGroup.setWhichChild(in.readInt());
		
		return switchGroup;
	}
	
}
