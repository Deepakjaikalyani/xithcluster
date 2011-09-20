package br.edu.univercidade.cc.xithcluster.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Tuple2f;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.scenegraph.Texture;
import br.edu.univercidade.cc.xithcluster.primitives.Rectangle;


public class RectangleSerializer extends Serializer<Rectangle> {

	@Override
	protected void doSerialization(Rectangle rectangle, DataOutputStream out) throws IOException {
		out.writeFloat(rectangle.getWidth());
		out.writeFloat(rectangle.getHeight());
		SerializationHelper.writeTuple3f(out, rectangle.getOffset());
		SerializationHelper.writeTexture(out, rectangle.getTexture());
		SerializationHelper.writeTuple2f(out, rectangle.getTexLowerLeft());
		SerializationHelper.writeTuple2f(out, rectangle.getTexUpperRight());
		SerializationHelper.writeColorf(out, rectangle.getColor());
	}

	@Override
	protected Rectangle doDeserialization(DataInputStream in) throws IOException {
		float width;
		float height;
		Tuple3f offset;
		Texture texture;
		Tuple2f texLowerLeft;
		Tuple2f texUpperRight;
		Colorf color;
		
		width = in.readFloat();
		height = in.readFloat();
		offset = SerializationHelper.readTuple3f(in);
		texture = SerializationHelper.readTexture(in);
		texLowerLeft = SerializationHelper.readTuple2f(in);
		texUpperRight = SerializationHelper.readTuple2f(in);;
		color = SerializationHelper.readColorf(in);
		
		return new Rectangle(width, height, offset, texture, texLowerLeft, texUpperRight, color);
	}
	
}
