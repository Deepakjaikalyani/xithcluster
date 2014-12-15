package br.edu.univercidade.cc.xithcluster.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.xith3d.scenegraph.Transform3D;
import org.xith3d.schedops.movement.AnimatableGroup;
import org.xith3d.schedops.movement.GroupAnimator;

public class AnimatableGroupSerializer extends Serializer<AnimatableGroup>
		implements GroupNodeSerializer {

	@Override
	protected void doSerialization(AnimatableGroup animatableGroup,
			DataOutputStream out) throws IOException {
		SerializationHelper.writeString(out, animatableGroup.getName());
		out.writeInt(animatableGroup.numChildren());
		SerializationHelper.writeTransform3D(out,
				animatableGroup.getTransform());
		SerializationHelper.writeGroupAnimator(out,
				animatableGroup.getGroupAnimator());
	}

	@Override
	protected AnimatableGroup doDeserialization(DataInputStream in)
			throws IOException {
		AnimatableGroup animatableGroup;
		GroupAnimator groupAnimator;
		String name;
		int numberOfChildren;
		Transform3D transform;

		name = SerializationHelper.readString(in);
		numberOfChildren = in.readInt();
		transform = SerializationHelper.readTransform3D(in);
		groupAnimator = SerializationHelper.readGroupAnimator(in);

		animatableGroup = new AnimatableGroup(groupAnimator);

		animatableGroup.setName(name);
		animatableGroup.setUserData(NUMBER_OF_CHILDREN_USER_DATA,
				numberOfChildren);
		animatableGroup.setTransform(transform);

		return animatableGroup;
	}
}
