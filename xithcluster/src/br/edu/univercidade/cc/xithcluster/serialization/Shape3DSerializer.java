package br.edu.univercidade.cc.xithcluster.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Shape3D;

public class Shape3DSerializer extends Serializer<Shape3D> {

	@Override
	protected void doSerialization(Shape3D shape, DataOutputStream out)
			throws IOException {
		SerializationHelper.writeString(out, shape.getName());
		SerializationHelper.writeAppearance(out, shape.getAppearance(true));
		SerializationHelper.writeGeometry(out, shape.getGeometry());
	}

	@Override
	protected Shape3D doDeserialization(DataInputStream in) throws IOException {
		String name = SerializationHelper.readString(in);
		Appearance appearance = SerializationHelper.readAppearance(in);
		Geometry geometry = SerializationHelper.readGeometry(in);
		Shape3D shape = new Shape3D(geometry, appearance);
		shape.setName(name);
		return shape;
	}

}
