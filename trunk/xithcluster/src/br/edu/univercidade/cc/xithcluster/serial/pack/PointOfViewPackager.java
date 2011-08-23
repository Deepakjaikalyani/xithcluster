package br.edu.univercidade.cc.xithcluster.serial.pack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.xith3d.scenegraph.View;
import br.edu.univercidade.cc.xithcluster.serial.SerializationHelper;
import br.edu.univercidade.cc.xithcluster.serial.Serializer;

public class PointOfViewPackager extends Serializer<View> {
	
	@Override
	protected void doSerialization(View pointOfView, DataOutputStream out) throws IOException {
		SerializationHelper.writeTransform(out, pointOfView.getTransform());
	}
	
	@Override
	protected View doDeserialization(DataInputStream in) throws IOException {
		View pointOfView;
		
		pointOfView = new View();
		pointOfView.setTransform(SerializationHelper.readTransform(in));
		
		return pointOfView;
	}
}
