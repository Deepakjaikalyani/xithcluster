package br.edu.univercidade.cc.xithcluster.serial;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.openmali.vecmath2.Colorf;
import br.edu.univercidade.cc.xithcluster.primitives.Rectangle;

public class RectangleSerializer extends Serializer<Rectangle> {
	
	@Override
	protected void doSerialization(Rectangle rectangle, DataOutputStream out) throws IOException {
		SerializationHelper.writeString(out, rectangle.getName());
		/*out.writeFloat(rectangle.getWidth());
		out.writeFloat(rectangle.getHeight());
		SerializationHelper.writeTuple3f(out, rectangle.getOffset());
		SerializationHelper.writeTexture(out, rectangle.getTexture());
		SerializationHelper.writeTuple2f(out, rectangle.getTexLowerLeft());
		SerializationHelper.writeTuple2f(out, rectangle.getTexUpperRight());
		SerializationHelper.writeColorf(out, rectangle.getColor());*/
		SerializationHelper.writeGeometry(out, rectangle.getGeometry());
		SerializationHelper.writeAppearance(out, rectangle.getAppearance());
	}
	
	@Override
	protected Rectangle doDeserialization(DataInputStream in) throws IOException {
		String name;
		/*float width;
		float height;
		Tuple3f offset;
		Texture texture;
		Tuple2f texLowerLeft;
		Tuple2f texUpperRight;
		Colorf color;
		Appearance appearance;*/
		Rectangle rectangle;
		
		name = SerializationHelper.readString(in);
		/*width = in.readFloat();
		height = in.readFloat();
		offset = SerializationHelper.readTuple3f(in);
		texture = SerializationHelper.readTexture(in);
		texLowerLeft = SerializationHelper.readTuple2f(in);
		texUpperRight = SerializationHelper.readTuple2f(in);
		color = SerializationHelper.readColorf(in);
		appearance = SerializationHelper.readAppearance(in);
		
		rectangle = new Rectangle(width, height, offset, texture, texLowerLeft, texUpperRight, color, appearance);*/
		
		rectangle = new Rectangle(1.0f, 1.0f, Colorf.BLUE);
		rectangle.setGeometry(SerializationHelper.readGeometry(in));
		rectangle.setAppearance(SerializationHelper.readAppearance(in));
		rectangle.setName(name);
		
		return rectangle;
	}
}
