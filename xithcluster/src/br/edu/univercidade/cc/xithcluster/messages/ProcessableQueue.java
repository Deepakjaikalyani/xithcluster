package br.edu.univercidade.cc.xithcluster.messages;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class ProcessableQueue<T> implements Iterable<T> {
	
	private static final int INITIAL_CAPACITY = 1024;
	
	private Lock primaryBufferLock = new ReentrantLock();
	
	private final Queue<T> primaryBuffer = new PriorityQueue<T>(INITIAL_CAPACITY);
	
	private final Queue<T> secondaryBuffer = new PriorityQueue<T>(INITIAL_CAPACITY);
	
	public void add(T object) {
		if (!addToPrimaryBuffer(object)) {
			addToSecondaryBuffer(object);
		}
	}
	
	private boolean addToPrimaryBuffer(T object) {
		if (!primaryBufferLock.tryLock())
			return false;
		
		primaryBuffer.add(object);
		primaryBufferLock.unlock();
		
		return true;
	}
	
	private void addToSecondaryBuffer(T object) {
		synchronized (secondaryBuffer) {
			secondaryBuffer.add(object);
		}
	}
	
	public void startProcessingQueue() {
		primaryBufferLock.lock();
		
		copySecondaryToPrimaryBuffer();
	}
	
	private void copySecondaryToPrimaryBuffer() {
		synchronized (secondaryBuffer) {
			if (!secondaryBuffer.isEmpty()) {
				primaryBuffer.addAll(secondaryBuffer);
				secondaryBuffer.clear();
			}
		}
	}
	
	public void stopProcessingQueue() {
		primaryBufferLock.unlock();
	}
	
	@Override
	public Iterator<T> iterator() {
		return primaryBuffer.iterator();
	}
}
