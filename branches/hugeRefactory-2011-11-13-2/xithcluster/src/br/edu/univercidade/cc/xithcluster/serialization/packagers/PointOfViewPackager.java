package br.edu.univercidade.cc.xithcluster.serialization.packagers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.xith3d.scenegraph.View;
import br.edu.univercidade.cc.xithcluster.serialization.SerializationHelper;
import br.edu.univercidade.cc.xithcluster.serialization.Serializer;

public class PointOfViewPackager extends Serializer<View> {
	
	@Override
	protected void doSerialization(View pointOfView, DataOutputStream out) throws IOException {
		SerializationHelper.writeTransform3D(out, pointOfView.getTransform());
	}
	
	@Override
	protected View doDeserialization(DataInputStream in) throws IOException {
		View pointOfView;
		
		pointOfView = new View();
		pointOfView.setTransform(SerializationHelper.readTransform3D(in));
		
		return pointOfView;
	}
}
