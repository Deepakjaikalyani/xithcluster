package br.edu.univercidade.cc.xithcluster.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.xith3d.scenegraph.Appearance;
import br.edu.univercidade.cc.xithcluster.nodes.primitives.Cylinder;

public class CylinderSerializer extends Serializer<Cylinder> {
	
	@Override
	protected void doSerialization(Cylinder cylinder, DataOutputStream out) throws IOException {
		SerializationHelper.writeString(out, cylinder.getName());
		out.writeFloat(cylinder.getRadius());
		out.writeFloat(cylinder.getHeight());
		out.writeFloat(cylinder.getTaper());
		out.writeBoolean(cylinder.isClosed());
		out.writeInt(cylinder.getSlices());
		out.writeInt(cylinder.getFeatures());
		out.writeBoolean(cylinder.isColorAlpha());
		out.writeInt(cylinder.getTexCoordsSize());
		SerializationHelper.writeAppearance(out, cylinder.getAppearance());
	}
	
	@Override
	protected Cylinder doDeserialization(DataInputStream in) throws IOException {
		String name;
		float radius;
		float height;
		float taper;
		boolean closed;
		int slices;
		int features;
		boolean colorAlpha;
		int textCoordsSize;
		Appearance appearance;
		Cylinder newCylinder;
		
		name = SerializationHelper.readString(in);
		radius = in.readFloat();
		height = in.readFloat();
		taper = in.readFloat();
		closed = in.readBoolean();
		slices = in.readInt();
		features = in.readInt();
		colorAlpha = in.readBoolean();
		textCoordsSize = in.readInt();
		appearance = SerializationHelper.readAppearance(in);
		
		newCylinder = new Cylinder(name, radius, height, taper, closed, slices, features, colorAlpha, textCoordsSize, appearance);
		
		return newCylinder;
	}
	
}
