package br.edu.univercidade.cc.xithcluster.utils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class ObjectPool<T> implements Iterable<T> {

	private Deque<T> pool = new ArrayDeque<T>();

	@Override
	public Iterator<T> iterator() {
		return pool.iterator();
	}
	
	public void push(T object) {
		pool.add(object);
	}
	
}
