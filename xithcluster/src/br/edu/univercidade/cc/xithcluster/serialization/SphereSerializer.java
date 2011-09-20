package br.edu.univercidade.cc.xithcluster.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.Material;
import br.edu.univercidade.cc.xithcluster.primitives.Sphere;

public class SphereSerializer extends Serializer<Sphere> {
	
	@Override
	protected void doSerialization(Sphere sphere, DataOutputStream out) throws IOException {
		out.writeFloat(sphere.getCenterX());
		out.writeFloat(sphere.getCenterY());
		out.writeFloat(sphere.getCenterZ());
		out.writeFloat(sphere.getRadius());
		out.writeInt(sphere.getSlices());
		out.writeInt(sphere.getStacks());
		out.writeInt(sphere.getFeatures());
		out.writeBoolean(sphere.isColorAlpha());
		out.writeInt(sphere.getTexCoordsSize());
		SerializationHelper.writeColorf(out, sphere.getColor());
		SerializationHelper.writeMaterial(out, sphere.getMaterial());
		//SerializationHelper.writeTexture(out, sphere.getTexture());
	}
	
	@Override
	protected Sphere doDeserialization(DataInputStream in) throws IOException {
		float centerX;
		float centerY;
		float centerZ;
		float radius;
		int slices;
		int stacks;
		int features;
		boolean colorAlpha;
		int texCoordsSize;
		Colorf color;
		Material material;
		
		centerX = in.readFloat();
		centerY = in.readFloat();
		centerZ = in.readFloat();
		radius = in.readFloat();
		slices = in.readInt();
		stacks = in.readInt();
		features = in.readInt();
		colorAlpha = in.readBoolean();
		texCoordsSize = in.readInt();
		color = SerializationHelper.readColorf(in);
		material = SerializationHelper.readMaterial(in);
		
		Sphere newSphere = new Sphere(centerX, centerY, centerZ, radius, slices, stacks, features, colorAlpha, texCoordsSize);
		
		if (color != null) {
			newSphere.setColor(color);
		} else if (material != null) {
			newSphere.setMaterial(material);
		}
		
		return newSphere;
	}
	
}
