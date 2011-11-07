package br.edu.univercidade.cc.xithcluster.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.xith3d.scenegraph.Appearance;
import br.edu.univercidade.cc.xithcluster.nodes.primitives.Ring;

public class RingSerializer extends Serializer<Ring> {

	@Override
	protected void doSerialization(Ring ring, DataOutputStream out) throws IOException {
		SerializationHelper.writeString(out, ring.getName());
		out.writeFloat(ring.getRadius());
		out.writeFloat(ring.getAlpha());
		out.writeInt(ring.getSlices());
		out.writeInt(ring.getFeatures());
		out.writeBoolean(ring.isColorAlpha());
		out.writeInt(ring.getTexCoordsSize());
		SerializationHelper.writeAppearance(out, ring.getAppearance());
	}

	@Override
	protected Ring doDeserialization(DataInputStream in) throws IOException {
		String name;
		float radius;
		float alpha;
		int slices;
		int features;
		boolean colorAlpha;
		int textCoordsSize;
		Appearance appearance;
		Ring newRing;

		name = SerializationHelper.readString(in);
		radius = in.readFloat();
		alpha = in.readFloat();
		slices = in.readInt();
		features = in.readInt();
		colorAlpha = in.readBoolean();
		textCoordsSize = in.readInt();
		appearance = SerializationHelper.readAppearance(in);
		
		newRing = new Ring(name, radius, alpha, slices, features, colorAlpha, textCoordsSize, appearance);
		
		return newRing;
	}
	
}
