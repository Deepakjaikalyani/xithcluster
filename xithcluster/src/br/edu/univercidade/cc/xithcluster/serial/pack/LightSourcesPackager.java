package br.edu.univercidade.cc.xithcluster.serial.pack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.AmbientLight;
import org.xith3d.scenegraph.DirectionalLight;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.PointLight;
import org.xith3d.scenegraph.SpotLight;
import br.edu.univercidade.cc.xithcluster.serial.SerializationHelper;
import br.edu.univercidade.cc.xithcluster.serial.Serializer;

public class LightSourcesPackager extends Serializer<List<Light>> {
	
	private static final int LC_SPOT = 0;
	
	private static final int LC_POINT = 1;
	
	private static final int LC_AMBIENT = 2;
	
	private static final int LC_DIRECTIONAL = 3;
	
	@Override
	protected void doSerialization(List<Light> lights, DataOutputStream out) throws IOException {
		out.writeInt(lights.size());
		
		for (Light lightSource : lights) {
			SerializationHelper.writeColorf(out, lightSource.getColor());
			out.writeBoolean(lightSource.isEnabled());
			
			if (lightSource instanceof SpotLight) {
				out.writeInt(LC_SPOT);
				packSpotLight(out, (SpotLight) lightSource);
			} else if (lightSource instanceof PointLight) {
				out.writeInt(LC_POINT);
				packPointLight(out, (PointLight) lightSource);
			} else if (lightSource instanceof AmbientLight) {
				out.writeInt(LC_AMBIENT);
			} else if (lightSource instanceof DirectionalLight) {
				out.writeInt(LC_DIRECTIONAL);
				packDirectionalLight(out, (DirectionalLight) lightSource);
			}
		}
	}
	
	private void packSpotLight(DataOutputStream out, SpotLight spotLight) throws IOException {
		packPointLight(out, (PointLight) spotLight);
		SerializationHelper.writeVector3f(out, spotLight.getDirection());
		out.writeFloat(spotLight.getSpreadAngle());
		out.writeFloat(spotLight.getSpreadAngleDeg());
		out.writeFloat(spotLight.getConcentration());
	}
	
	private void packPointLight(DataOutputStream out, PointLight pointLight) throws IOException {
		SerializationHelper.writePoint3f(out, pointLight.getLocation());
		SerializationHelper.writeTuple3f(out, pointLight.getAttenuation());
	}
	
	private void packDirectionalLight(DataOutputStream out, DirectionalLight directionalLight) throws IOException {
		SerializationHelper.writeVector3f(out, directionalLight.getDirection());
	}
	
	@Override
	protected List<Light> doDeserialization(DataInputStream in) throws IOException {
		int i;
		Colorf color;
		boolean enabled;
		int lightCode;
		Light lightSource;
		List<Light> lights;
		
		i = in.readInt();
		lights = new ArrayList<Light>();
		while (i-- > 0) {
			color = SerializationHelper.readColorf(in);
			enabled = in.readBoolean();
			
			lightCode = in.readInt();
			
			switch (lightCode) {
			case LC_SPOT:
				lightSource = new SpotLight(enabled, color, SerializationHelper.readTuple3f(in), SerializationHelper.readTuple3f(in), SerializationHelper.readTuple3f(in), in.readFloat(), in.readFloat());
				break;
			case LC_POINT:
				lightSource = new PointLight(enabled, color, SerializationHelper.readTuple3f(in), in.readFloat());
				break;
			case LC_AMBIENT:
				lightSource = new AmbientLight(enabled, color);
				break;
			case LC_DIRECTIONAL:
				lightSource = new DirectionalLight(enabled, color, SerializationHelper.readVector3f(in));
				break;
			default:
				// TODO:
				throw new RuntimeException();
			}
			
			lights.add(lightSource);
		}
		
		return lights;
	}
}
