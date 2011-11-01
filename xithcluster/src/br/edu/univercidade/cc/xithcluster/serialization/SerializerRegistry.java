package br.edu.univercidade.cc.xithcluster.serialization;

import java.util.HashMap;
import java.util.Map;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.schedops.movement.AnimatableGroup;
import br.edu.univercidade.cc.xithcluster.DirectionalLight;
import br.edu.univercidade.cc.xithcluster.SpotLight;
import br.edu.univercidade.cc.xithcluster.primitives.Cube;
import br.edu.univercidade.cc.xithcluster.primitives.Rectangle;
import br.edu.univercidade.cc.xithcluster.primitives.Sphere;

public final class SerializerRegistry {
	
	private static Map<Class<?>, Class<? extends Serializer<?>>> classRegistry = new HashMap<Class<?>, Class<? extends Serializer<?>>>();
	
	private static Map<Class<?>, Serializer<?>> cache = new HashMap<Class<?>, Serializer<?>>();
	
	// FIXME:
	static {
		register(Cube.class, CubeSerializer.class);
		register(Sphere.class, SphereSerializer.class);
		register(Rectangle.class, RectangleSerializer.class);
		register(DirectionalLight.class, LightSerializer.class);
		register(SpotLight.class, LightSerializer.class);
		register(TransformGroup.class, TransformGroupSerializer.class);
		register(AnimatableGroup.class, AnimatableGroupSerializer.class);
		register(BranchGroup.class, BranchGroupSerializer.class);
		register(Group.class, GroupSerializer.class);
	}
	
	static void register(Class<?> nodeClass, Class<? extends Serializer<?>> registeredClass) {
		if (classRegistry.containsKey(nodeClass)) {
			// TODO:
			throw new RuntimeException("There's already a registered serializer for class '" + nodeClass.getName() + "'");
		}
		
		classRegistry.put(nodeClass, registeredClass);
	}
	
	@SuppressWarnings("rawtypes")
	public static Serializer getSerializer(Class<?> clazz) {
		Class<? extends Serializer<?>> serializerClass;
		Serializer<?> serializer = null;
		
		try {
			if ((serializer = cache.get(clazz)) == null) {
				serializerClass = classRegistry.get(clazz);
				
				if (serializerClass == null) {
					// TODO:
					throw new RuntimeException("No serializer found for class '" + clazz.getName()  + "'");
				}
				
				serializer = serializerClass.newInstance();
				cache.put(clazz, serializer);
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
