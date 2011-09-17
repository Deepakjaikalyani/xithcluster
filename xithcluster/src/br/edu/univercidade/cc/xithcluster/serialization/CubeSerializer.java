package br.edu.univercidade.cc.xithcluster.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.Texture;
import br.edu.univercidade.cc.xithcluster.primitives.Cube;

public class CubeSerializer extends Serializer<Cube> {
	
	@Override
	protected void doSerialization(Cube cube, DataOutputStream out) throws IOException {
		out.writeFloat(cube.getSize());
		out.writeInt(cube.getFeatures());
		out.writeBoolean(cube.isColorAlpha());
		out.writeInt(cube.getTexCoordsSize());
		SerializationHelper.writeColorf(out, cube.getColor());
		SerializationHelper.writeTexture(out, cube.getTexture());
	}
	
	@Override
	protected Cube doDeserialization(DataInputStream in) throws IOException {
		float size;
		int features;
		boolean colorAlpha;
		int texCoordsSize;
		Colorf color;
		Texture texture;
		
		size = in.readFloat();
		features = in.readInt();
		colorAlpha = in.readBoolean();
		texCoordsSize = in.readInt();
		color = SerializationHelper.readColorf(in);
		texture = SerializationHelper.readTexture(in);
		
		Cube newCube = new Cube(size, features, colorAlpha, texCoordsSize);
		
		if (color != null) {
			newCube.setColor(color);
		} else if (texture != null) {
			newCube.setTexture(texture);
		}
		
		return newCube;
	}
	
}
