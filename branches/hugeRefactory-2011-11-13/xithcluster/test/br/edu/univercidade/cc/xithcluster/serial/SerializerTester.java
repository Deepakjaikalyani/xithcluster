package br.edu.univercidade.cc.xithcluster.serial;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import org.junit.Test;
import org.xith3d.scenegraph.Node;
import br.edu.univercidade.cc.xithcluster.serialization.Serializer;
import br.edu.univercidade.cc.xithcluster.serialization.SerializerRegistry;

public abstract class SerializerTester<T extends Node> {
	
	protected abstract T buildTarget();
	
	protected abstract boolean compareResults(T target, T deserializedObject);
	
	@SuppressWarnings("rawtypes")
	private Serializer getNodeSerializerImpl() {
		return SerializerRegistry.getSerializer((Class<?>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
	}
	
	@SuppressWarnings({
	"unchecked", "rawtypes"
	})
	@Test
	public void testSerialization() throws IOException {
		T target;
		Serializer serializer;
		
		target = buildTarget();
		
		serializer = getNodeSerializerImpl();
		
		compareResults(target, (T) serializer.deserialize(serializer.serialize(target)));
	}
	
}
