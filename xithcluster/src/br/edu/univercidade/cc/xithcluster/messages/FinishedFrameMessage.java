package br.edu.univercidade.cc.xithcluster.messages;

import br.edu.univercidade.cc.xithcluster.Component;

public class FinishedFrameMessage extends Message {
	
	private long frameIndex;
	
	public FinishedFrameMessage(long frameIndex) {
		super();
		this.frameIndex = frameIndex;
	}
	
	public long getFrameIndex() {
		return frameIndex;
	}

	@Override
	public void sendTo(Component component) {
		
	}
	
}
