package br.edu.univercidade.cc.xithcluster.composition;

public class DirectBufferReadOrder extends BufferReadOrder {
	
	public DirectBufferReadOrder(int width, int height) {
		super(width, height);
	}
	
	@Override
	public int nextIndex() {
		return lastReadIndex++;
	}
	
}
