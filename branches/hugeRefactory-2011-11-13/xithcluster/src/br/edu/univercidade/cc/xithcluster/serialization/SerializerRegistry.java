package br.edu.univercidade.cc.xithcluster.serialization;

import java.util.HashMap;
import java.util.Map;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.schedops.movement.AnimatableGroup;
import br.edu.univercidade.cc.xithcluster.nodes.lights.DirectionalLight;
import br.edu.univercidade.cc.xithcluster.nodes.lights.SpotLight;
import br.edu.univercidade.cc.xithcluster.nodes.primitives.Cube;
import br.edu.univercidade.cc.xithcluster.nodes.primitives.Cylinder;
import br.edu.univercidade.cc.xithcluster.nodes.primitives.Rectangle;
import br.edu.univercidade.cc.xithcluster.nodes.primitives.Ring;
import br.edu.univercidade.cc.xithcluster.nodes.primitives.Sphere;

public final class SerializerRegistry {
	
	private static Map<Class<?>, Class<? extends Serializer<?>>> serializersMap = new HashMap<Class<?>, Class<? extends Serializer<?>>>();
	
	private static Map<Class<?>, Serializer<?>> serializersCache = new HashMap<Class<?>, Serializer<?>>();
	
	// FIXME:
	static {
		// Primitives
		register(Cube.class, CubeSerializer.class);
		register(Sphere.class, SphereSerializer.class);
		register(Rectangle.class, RectangleSerializer.class);
		register(Ring.class, RingSerializer.class);
		register(Cylinder.class, CylinderSerializer.class);
		
		// Groups
		register(TransformGroup.class, TransformGroupSerializer.class);
		register(AnimatableGroup.class, AnimatableGroupSerializer.class);
		register(BranchGroup.class, BranchGroupSerializer.class);
		register(Group.class, GroupSerializer.class);
		
		// Lights
		register(DirectionalLight.class, LightSerializer.class);
		register(SpotLight.class, LightSerializer.class);
	}
	
	static void register(Class<?> nodeClass, Class<? extends Serializer<?>> registeredClass) {
		if (serializersMap.containsKey(nodeClass)) {
			// TODO:
			throw new RuntimeException("There's already a registered serializer for class '" + nodeClass.getName() + "'");
		}
		
		serializersMap.put(nodeClass, registeredClass);
	}
	
	@SuppressWarnings("rawtypes")
	public static Serializer getSerializer(Class<?> clazz) {
		Serializer<?> serializer = null;
		
		try {
			if ((serializer = serializersCache.get(clazz)) == null) {
				Class<? extends Serializer<?>> serializerClass = serializersMap.get(clazz);
				
				if (serializerClass == null) {
					// TODO:
					throw new RuntimeException("No serializer found for class '" + clazz.getName()  + "'");
				}
				
				serializer = serializerClass.newInstance();
				serializersCache.put(clazz, serializer);
			}
		} catch (InstantiationException e) {
			// TODO:
			throw new RuntimeException("Error in serializer's default constructor", e);
		} catch (IllegalAccessException e) {
			// TODO:
			throw new RuntimeException("Serializer class doesn't have a public default constructor", e);
		}
		
		return serializer;
	}
	
}
