package br.edu.univercidade.cc.xithcluster.messages;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class MessageQueue {
	
	private static final int INITIAL_CAPACITY = 1024;

	private Lock primaryBufferLock = new ReentrantLock();
	
	private final Queue<Message> primaryBuffer = new PriorityQueue<Message>(INITIAL_CAPACITY);
	
	private final Queue<Message> secondaryBuffer = new PriorityQueue<Message>(INITIAL_CAPACITY);

	
	public void postMessage(Message message) {
		if (primaryBufferLock.tryLock()) {
			primaryBuffer.add(message);
			primaryBufferLock.unlock();
		} else {
			synchronized (secondaryBuffer) {
				secondaryBuffer.add(message);
			}
		}
	}
	
	public Queue<Message> startReadingMessages() {
		primaryBufferLock.lock();
		
		// copying secondary buffer
		synchronized (secondaryBuffer) {
			if (!secondaryBuffer.isEmpty()) {
				primaryBuffer.addAll(secondaryBuffer);
				secondaryBuffer.clear();
			}
		}
		
		return primaryBuffer;
	}
	
	public void stopReadingMessages() {
		primaryBufferLock.unlock();
	}
	
}
