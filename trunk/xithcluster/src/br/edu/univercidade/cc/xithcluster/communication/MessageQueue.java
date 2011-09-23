package br.edu.univercidade.cc.xithcluster.communication;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class MessageQueue {
	
	private static final int INITIAL_CAPACITY = 1024;

	private static Lock primaryBufferLock = new ReentrantLock();
	
	private static Queue<Message> primaryBuffer = new PriorityQueue<Message>(INITIAL_CAPACITY);
	
	private static Queue<Message> secondaryBuffer = new PriorityBlockingQueue<Message>(INITIAL_CAPACITY);

	
	public static void postMessage(Message message) {
		if (primaryBufferLock.tryLock()) {
			primaryBuffer.add(message);
			primaryBufferLock.unlock();
		} else {
			secondaryBuffer.add(message);
		}
	}
	
	public static Queue<Message> startReadingMessages() {
		primaryBufferLock.lock();
		
		// copy secondary buffer
		if (!secondaryBuffer.isEmpty()) {
			primaryBuffer.addAll(secondaryBuffer);
		}
		
		return primaryBuffer;
	}
	
	public static void stopReadingMessages() {
		primaryBufferLock.unlock();
	}
	
}
