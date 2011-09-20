package br.edu.univercidade.cc.xithcluster.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.openmali.vecmath2.Tuple2f;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.scenegraph.Appearance;
import br.edu.univercidade.cc.xithcluster.primitives.Rectangle;

public class RectangleSerializer extends Serializer<Rectangle> {

	@Override
	protected void doSerialization(Rectangle rectangle, DataOutputStream out) throws IOException {
		out.writeFloat(rectangle.getWidth());
		out.writeFloat(rectangle.getHeight());
		SerializationHelper.writeTuple3f(out, rectangle.getOffset());
		SerializationHelper.writeTuple2f(out, rectangle.getTexLowerLeft());
		SerializationHelper.writeTuple2f(out, rectangle.getTexUpperRight());
		SerializationHelper.writeAppearance(out, rectangle.getAppearance());
	}

	@Override
	protected Rectangle doDeserialization(DataInputStream in) throws IOException {
		float width;
		float height;
		Tuple3f offset;
		Tuple2f texLowerLeft;
		Tuple2f texUpperRight;
		Appearance appearance;
		Rectangle newRectangle;
		
		width = in.readFloat();
		height = in.readFloat();
		offset = SerializationHelper.readTuple3f(in);
		texLowerLeft = SerializationHelper.readTuple2f(in);
		texUpperRight = SerializationHelper.readTuple2f(in);
		appearance = SerializationHelper.readAppearance(in);
		
		newRectangle = new Rectangle(width, height, offset, texLowerLeft, texUpperRight);
		newRectangle.setAppearance(appearance);
		
		return newRectangle;
	}
	
}
