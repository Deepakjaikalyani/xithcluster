package br.edu.univercidade.cc.xithcluster;


public interface CompositionStrategy {
	
	byte[] compose(byte[][] colorAndAlphaBuffer, byte[][] depthBuffer);
	
}
