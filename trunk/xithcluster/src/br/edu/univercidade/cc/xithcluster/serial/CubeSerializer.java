package br.edu.univercidade.cc.xithcluster.serial;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import br.edu.univercidade.cc.xithcluster.primitives.Cube;
import br.edu.univercidade.cc.xithcluster.util.PrivateAccessor;

public class CubeSerializer extends Serializer<Cube> {
	
	@Override
	protected void doSerialization(Cube cube, DataOutputStream out) throws IOException {
		SerializationHelper.writeString(out, cube.getName());
		
		SerializationHelper.writeGeometry(out, cube.getGeometry());
		SerializationHelper.writeAppearance(out, cube.getAppearance());
		
		SerializationHelper.writeBounds(out, cube.getBounds());
		
		out.writeBoolean(cube.isRenderable());
		out.writeBoolean(cube.isPickable());
	}
	
	@Override
	protected Cube doDeserialization(DataInputStream in) throws IOException {
		Cube cube;
		
		cube = new Cube();
		
		cube.setName(SerializationHelper.readString(in));
		cube.setGeometry(SerializationHelper.readGeometry(in));
		cube.setAppearance(SerializationHelper.readAppearance(in));
		
		cube.setBoundsAutoCompute(false);
        cube.setBounds(SerializationHelper.readBounds(in));
        
        // FIXME:
        PrivateAccessor.setPrivateField(cube, "boundsDirty", true);
        
        cube.updateBounds(false);
		
		return cube;
	}
	
}
