package br.edu.univercidade.cc.xithcluster.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.xith3d.scenegraph.Appearance;
import br.edu.univercidade.cc.xithcluster.nodes.primitives.Cube;

public class CubeSerializer extends Serializer<Cube> {
	
	@Override
	protected void doSerialization(Cube cube, DataOutputStream out) throws IOException {
		SerializationHelper.writeString(out, cube.getName());
		out.writeFloat(cube.getSize());
		out.writeInt(cube.getFeatures());
		out.writeBoolean(cube.isColorAlpha());
		out.writeInt(cube.getTexCoordsSize());
		SerializationHelper.writeAppearance(out, cube.getAppearance());
	}
	
	@Override
	protected Cube doDeserialization(DataInputStream in) throws IOException {
		String name;
		float size;
		int features;
		boolean colorAlpha;
		int texCoordsSize;
		Appearance appearance;
		
		name = SerializationHelper.readString(in);
		size = in.readFloat();
		features = in.readInt();
		colorAlpha = in.readBoolean();
		texCoordsSize = in.readInt();
		appearance = SerializationHelper.readAppearance(in);
		
		Cube newCube = new Cube(name, size, features, colorAlpha, texCoordsSize, appearance);
		
		return newCube;
	}
	
}
