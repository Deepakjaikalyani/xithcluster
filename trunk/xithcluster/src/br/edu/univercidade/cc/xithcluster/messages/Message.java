package br.edu.univercidade.cc.xithcluster.messages;

import br.edu.univercidade.cc.xithcluster.utils.Timer;

public abstract class Message implements Comparable<Message> {
	
	private Long creationTime;
	
	protected Message() {
		creationTime = Timer.getCurrentTime();
	}
	
	@Override
	public int compareTo(Message o) {
		return creationTime.compareTo(o.creationTime);
	}
	
}
