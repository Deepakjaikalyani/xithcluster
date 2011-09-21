package br.edu.univercidade.cc.xithcluster.communication;

import java.util.ArrayDeque;
import java.util.Deque;

public final class MessageQueue {
	
	private static final int MAX_NUM_MESSAGES = 1024;

	private static final MessageQueue instance = new MessageQueue();
	
	private Deque<Message> messageBuffer = new ArrayDeque<Message>(MAX_NUM_MESSAGES);

	public static MessageQueue getInstance() {
		return instance;
	}
	
	public synchronized void postMessage(Message message) {
		messageBuffer.offerLast(message);
	}
	
	public synchronized Deque<Message> retrieveMessages() {
		Deque<Message> messages;
		
		messages = new ArrayDeque<Message>(messageBuffer);
		messageBuffer.clear();
		
		return messages;
	}
	
}
