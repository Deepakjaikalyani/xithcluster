package br.edu.univercidade.cc.xithcluster.messages;

import br.edu.univercidade.cc.xithcluster.Component;
import br.edu.univercidade.cc.xithcluster.utils.Timer;

public abstract class Message implements Comparable<Message> {
	
	protected Long creationTime;
	
	protected Message() {
		creationTime = Timer.getCurrentTime();
	}
	
	public abstract void sendTo(Component component);
	
	@Override
	public int compareTo(Message o) {
		return creationTime.compareTo(o.creationTime);
	}
	
}
