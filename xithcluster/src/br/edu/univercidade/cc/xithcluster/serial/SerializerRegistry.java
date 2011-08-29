package br.edu.univercidade.cc.xithcluster.serial;

import java.util.HashMap;
import java.util.Map;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.Switch;
import org.xith3d.scenegraph.TransformGroup;
import br.edu.univercidade.cc.xithcluster.primitives.Cube;
import br.edu.univercidade.cc.xithcluster.primitives.Rectangle;
import br.edu.univercidade.cc.xithcluster.primitives.Sphere;

public final class SerializerRegistry {
	
	private static Map<Class<?>, Class<? extends Serializer<?>>> classRegistry = new HashMap<Class<?>, Class<? extends Serializer<?>>>();
	
	private static Map<Class<?>, Serializer<?>> cache = new HashMap<Class<?>, Serializer<?>>();
	
	// FIXME:
	static {
		register(Cube.class, Shape3DSerializer.class);
		register(Rectangle.class, Shape3DSerializer.class);
		register(Sphere.class, Shape3DSerializer.class);
		register(Switch.class, SwitchSerializer.class);
		register(TransformGroup.class, TransformGroupSerializer.class);
		register(BranchGroup.class, BranchGroupSerializer.class);
		register(Group.class, GroupSerializer.class);
	}
	
	static void register(Class<?> nodeClass, Class<? extends Serializer<?>> registeredClass) {
		if (classRegistry.containsKey(nodeClass)) {
			// TODO:
			throw new RuntimeException("It's not possible to register two serializers for the same class: " + nodeClass.getName());
		}
		
		classRegistry.put(nodeClass, registeredClass);
	}
	
	@SuppressWarnings("rawtypes")
	public static Serializer getSerializer(Class<?> clazz) {
		Serializer<?> serializer = null;
		
		try {
			if ((serializer = cache.get(clazz)) == null) {
				serializer = classRegistry.get(clazz).newInstance();
				cache.put(clazz, serializer);
			}
		} catch (InstantiationException e) {
			// TODO:
			throw new RuntimeException("Error in serializer's default constructor");
		} catch (IllegalAccessException e) {
			// TODO:
			throw new RuntimeException("Serializer class doesn't have a public default constructor");
		}
		
		return serializer;
	}
}
