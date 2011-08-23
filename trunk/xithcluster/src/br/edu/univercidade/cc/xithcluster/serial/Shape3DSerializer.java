package br.edu.univercidade.cc.xithcluster.serial;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.xith3d.scenegraph.Shape3D;

import br.edu.univercidade.cc.xithcluster.util.PrivateAccessor;

public class Shape3DSerializer extends Serializer<Shape3D> {
	
	@Override
	protected void doSerialization(Shape3D shape, DataOutputStream out) throws IOException {
		SerializationHelper.writeClass(out, shape.getClass());
		
		SerializationHelper.writeString(out, shape.getName());
		
		SerializationHelper.writeGeometry(out, shape.getGeometry());
		SerializationHelper.writeAppearance(out, shape.getAppearance());
		
		SerializationHelper.writeBounds(out, shape.getBounds());
		
		out.writeBoolean(shape.isRenderable());
		out.writeBoolean(shape.isPickable());
	}
	
	@Override
	protected Shape3D doDeserialization(DataInputStream in) throws IOException {
		Class<? extends Shape3D> shapeClass;
		Shape3D shape;
		
		try {
			shapeClass = SerializationHelper.readClass(in, Shape3D.class);
			
			shape = shapeClass.newInstance();
			
			shape.setName(SerializationHelper.readString(in));
			shape.setGeometry(SerializationHelper.readGeometry(in));
			shape.setAppearance(SerializationHelper.readAppearance(in));
			
			shape.setBoundsAutoCompute(false);
			shape.setBounds(SerializationHelper.readBounds(in));
			
			// FIXME:
			PrivateAccessor.setPrivateField(shape, "boundsDirty", true);
			
			shape.updateBounds(false);
			
			return shape;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		return null;
	}

}
