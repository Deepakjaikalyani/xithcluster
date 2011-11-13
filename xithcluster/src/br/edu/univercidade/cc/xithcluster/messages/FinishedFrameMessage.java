package br.edu.univercidade.cc.xithcluster.messages;

public class FinishedFrameMessage extends Message {
	
	private long frameIndex;
	
	public FinishedFrameMessage(long frameIndex) {
		super();
		this.frameIndex = frameIndex;
	}
	
	public long getFrameIndex() {
		return frameIndex;
	}
	
}
