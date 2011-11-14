package br.edu.univercidade.cc.xithcluster.utils;

import java.util.HashMap;
import java.util.Map;

public class Registry<K, V> {
	
	private Map<K, Class<?>> registry = new HashMap<K, Class<?>>();
	
	public void register(K key, Class<?> clazz) {
		if (key == null || clazz == null) {
			throw new IllegalArgumentException();
		}
		
		if (registry.containsKey(key)) {
			// TODO:
			throw new RuntimeException("");
		}
		
		registry.put(key, clazz);
	}
	
	@SuppressWarnings("unchecked")
	public V getObject(K key) {
		if (key == null) {
			throw new IllegalArgumentException();
		}
		
		Class<?> clazz = (Class<?>) registry.get(key);
		
		if (clazz == null) {
			// TODO:
			throw new RuntimeException("");
		}
		
		try {
			return (V) clazz.newInstance();
		} catch (InstantiationException e) {
			// TODO:
			throw new RuntimeException("");
		} catch (IllegalAccessException e) {
			// TODO:
			throw new RuntimeException("");
		} catch (ClassCastException e) {
			// TODO:
			throw new RuntimeException("");
		}
	}
	
	public K getKeyForObject(V object) {
		if (object == null) {
			throw new IllegalArgumentException();
		}
		
		Class<?> clazz = object.getClass();
		
		for (Map.Entry<K, Class<?>> entry : registry.entrySet()) {
			if (entry.getValue().equals(clazz)) {
				return entry.getKey();
			}
		}
		
		// TODO:
		throw new RuntimeException("");
	}
}
