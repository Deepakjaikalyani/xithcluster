package br.edu.univercidade.cc.xithcluster.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.AmbientLight;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.PointLight;
import org.xith3d.scenegraph.SpotLight;
import br.edu.univercidade.cc.xithcluster.nodes.lights.DirectionalLight;

public class LightSerializer extends Serializer<Light> {
	
	private static final int LC_SPOT = 0;
	
	private static final int LC_POINT = 1;
	
	private static final int LC_AMBIENT = 2;
	
	private static final int LC_DIRECTIONAL = 3;
	
	@Override
	protected void doSerialization(Light light, DataOutputStream out) throws IOException {
		SerializationHelper.writeColorf(out, light.getColor());
		out.writeBoolean(light.isEnabled());
		
		if (light instanceof SpotLight) {
			out.writeInt(LC_SPOT);
			packSpotLight(out, (SpotLight) light);
		} else if (light instanceof PointLight) {
			out.writeInt(LC_POINT);
			packPointLight(out, (PointLight) light);
		} else if (light instanceof AmbientLight) {
			out.writeInt(LC_AMBIENT);
		} else if (light instanceof DirectionalLight) {
			out.writeInt(LC_DIRECTIONAL);
			packDirectionalLight(out, (DirectionalLight) light);
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
	protected Light doDeserialization(DataInputStream in) throws IOException {
		Colorf color;
		boolean enabled;
		int lightCode;
		Light light;
		
		color = SerializationHelper.readColorf(in);
		enabled = in.readBoolean();
		
		lightCode = in.readInt();
		
		switch (lightCode) {
		case LC_SPOT:
			light = new SpotLight(enabled, color, SerializationHelper.readTuple3f(in), SerializationHelper.readTuple3f(in), SerializationHelper.readTuple3f(in), in.readFloat(), in.readFloat());
			break;
		case LC_POINT:
			light = new PointLight(enabled, color, SerializationHelper.readTuple3f(in), in.readFloat());
			break;
		case LC_AMBIENT:
			light = new AmbientLight(enabled, color);
			break;
		case LC_DIRECTIONAL:
			light = new DirectionalLight(enabled, color, SerializationHelper.readVector3f(in));
			break;
		default:
			// TODO:
			throw new RuntimeException("Unknown light code: " + lightCode);
		}
			
		return light;
	}
}
