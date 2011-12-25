package br.edu.univercidade.cc.xithcluster.serialization;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import org.xith3d.scenegraph.Node;

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
	protected void serializeAndDeserializeAndCompareResult() throws IOException {
		T target;
		Serializer serializer;
		
		target = buildTarget();
		
		serializer = getNodeSerializerImpl();
		
		compareResults(target, (T) serializer.deserialize(serializer.serialize(target)));
	}
	
}
