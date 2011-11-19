package br.edu.univercidade.cc.xithcluster.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.xith3d.scenegraph.Appearance;
import br.edu.univercidade.cc.xithcluster.nodes.primitives.Sphere;

public class SphereSerializer extends Serializer<Sphere> {
	
	@Override
	protected void doSerialization(Sphere sphere, DataOutputStream out) throws IOException {
		SerializationHelper.writeString(out, sphere.getName());
		out.writeFloat(sphere.getCenterX());
		out.writeFloat(sphere.getCenterY());
		out.writeFloat(sphere.getCenterZ());
		out.writeFloat(sphere.getRadius());
		out.writeInt(sphere.getSlices());
		out.writeInt(sphere.getStacks());
		out.writeInt(sphere.getFeatures());
		out.writeBoolean(sphere.isColorAlpha());
		out.writeInt(sphere.getTexCoordsSize());
		SerializationHelper.writeAppearance(out, sphere.getAppearance());
	}
	
	@Override
	protected Sphere doDeserialization(DataInputStream in) throws IOException {
		String name;
		float centerX;
		float centerY;
		float centerZ;
		float radius;
		int slices;
		int stacks;
		int features;
		boolean colorAlpha;
		int texCoordsSize;
		Appearance appearance;
		
		name = SerializationHelper.readString(in);
		centerX = in.readFloat();
		centerY = in.readFloat();
		centerZ = in.readFloat();
		radius = in.readFloat();
		slices = in.readInt();
		stacks = in.readInt();
		features = in.readInt();
		colorAlpha = in.readBoolean();
		texCoordsSize = in.readInt();
		appearance = SerializationHelper.readAppearance(in);
		
		Sphere newSphere = new Sphere(name, centerX, centerY, centerZ, radius, slices, stacks, features, colorAlpha, texCoordsSize, appearance);
		
		return newSphere;
	}
	
}
