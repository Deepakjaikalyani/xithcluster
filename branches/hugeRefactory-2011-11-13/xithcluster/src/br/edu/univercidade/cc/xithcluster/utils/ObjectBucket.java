package br.edu.univercidade.cc.xithcluster.utils;

import java.util.HashMap;
import java.util.Map;

public class ObjectBucket<K, V> {
	
	private Registry<K, V> registry = new Registry<K, V>();
	
	private Map<K, ObjectPool<BucketItem<V>>> cache = new HashMap<K, ObjectPool<BucketItem<V>>>();
	
	public V retrieveFromBucket(K key) {
		if (key == null) {
			throw new IllegalArgumentException();
		}
		
		ObjectPool<BucketItem<V>> bucketItemPool = findBucketItemPool(key);
		
		BucketItem<V> bucketItem = getBucketItemNotInUse(bucketItemPool);
		
		if (bucketItem == null) {
			bucketItem = getNewBucketItemAndAddToPool(key, bucketItemPool);
		}
		
		bucketItem.setInUse();
		
		return bucketItem.getItem();
	}
	
	public void returnToBucket(V item) {
		if (item == null) {
			throw new IllegalArgumentException();
		}
		
		K key = registry.getKeyForObject(item);
		
		ObjectPool<BucketItem<V>> bucketItemPool = findBucketItemPool(key);
		
		if (bucketItemPool == null) {
			// TODO:
			throw new AssertionError("Bucket item pool should never be null");
		}
		
		BucketItem<V> bucketItem = findBucketItemForObjectFromPool(item, bucketItemPool);
		
		bucketItem.setNotInUse();
	}
	
	private BucketItem<V> findBucketItemForObjectFromPool(V item, ObjectPool<BucketItem<V>> bucketItemPool) {
		for (BucketItem<V> bucketItem : bucketItemPool) {
			if (bucketItem.contains(item)) {
				return bucketItem;
			}
		}
		
		// TODO:
		throw new RuntimeException("");
	}
	
	private BucketItem<V> getNewBucketItemAndAddToPool(K key, ObjectPool<BucketItem<V>> bucketItemPool) {
		if (key == null || bucketItemPool == null) {
			throw new IllegalArgumentException();
		}
		
		V object = registry.getObject(key);
		
		BucketItem<V> bucketItem = new BucketItem<V>(object);
		
		bucketItemPool.push(bucketItem);
		
		return bucketItem;
	}
	
	private BucketItem<V> getBucketItemNotInUse(ObjectPool<BucketItem<V>> bucketItemPool) {
		for (BucketItem<V> bucketItem : bucketItemPool) {
			if (!bucketItem.isInUse()) {
				return bucketItem;
			}
		}
		
		return null;
	}
	
	private ObjectPool<BucketItem<V>> findBucketItemPool(K key) {
		ObjectPool<BucketItem<V>> pool = cache.get(key);
		
		if (pool == null) {
			pool = new ObjectPool<BucketItem<V>>();
			cache.put(key, pool);
		}
		
		return pool;
	}

	public void register(K key, Class<?> clazz) {
		if (key == null || clazz == null) {
			throw new IllegalArgumentException();
		}
		
		registry.register(key, clazz);
	}
	
}
