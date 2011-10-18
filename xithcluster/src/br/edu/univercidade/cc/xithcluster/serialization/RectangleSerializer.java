package br.edu.univercidade.cc.xithcluster.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.openmali.vecmath2.Tuple2f;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.primitives.Rectangle.ZeroPointLocation;
import br.edu.univercidade.cc.xithcluster.primitives.Rectangle;

public class RectangleSerializer extends Serializer<Rectangle> {

	@Override
	protected void doSerialization(Rectangle rectangle, DataOutputStream out) throws IOException {
		SerializationHelper.writeString(out, rectangle.getName());
		out.writeFloat(rectangle.getOriginalWidth());
		out.writeFloat(rectangle.getOriginalHeight());
		SerializationHelper.writeEnum(out, rectangle.getZpl());
		SerializationHelper.writeTexture(out, rectangle.getAppearance().getTexture());
		SerializationHelper.writeTuple3f(out, rectangle.getOffset());
		SerializationHelper.writeTuple2f(out, rectangle.getTexLowerLeft());
		SerializationHelper.writeTuple2f(out, rectangle.getTexUpperRight());
	}

	@Override
	protected Rectangle doDeserialization(DataInputStream in) throws IOException {
		String name;
		float width;
		float height;
		ZeroPointLocation zeroPointLocation;
		Texture texture;
		Tuple3f offset;
		Tuple2f texLowerLeft;
		Tuple2f texUpperRight;
		Rectangle newRectangle;

		name = SerializationHelper.readString(in);
		width = in.readFloat();
		height = in.readFloat();
		zeroPointLocation = SerializationHelper.readEnum(in, ZeroPointLocation.values());
		texture = SerializationHelper.readTexture(in);
		offset = SerializationHelper.readTuple3f(in);
		texLowerLeft = SerializationHelper.readTuple2f(in);
		texUpperRight = SerializationHelper.readTuple2f(in);
		
		newRectangle = new Rectangle(name, width, height, zeroPointLocation, texture, offset, texLowerLeft, texUpperRight);
		
		return newRectangle;
	}
	
}
