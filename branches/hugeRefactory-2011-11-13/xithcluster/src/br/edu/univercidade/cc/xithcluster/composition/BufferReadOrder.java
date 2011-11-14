package br.edu.univercidade.cc.xithcluster.composition;

public abstract class BufferReadOrder {
	
	protected int bufferSize;
	
	protected int lastReadIndex = 0;
	
	protected BufferReadOrder(int width, int height) {
		bufferSize = width * height;
	}

	public boolean hasNext() {
		return lastReadIndex < bufferSize;
	}
	
	public abstract int nextIndex();
	
	public void reset() {
		lastReadIndex = 0;
	}
	
}
