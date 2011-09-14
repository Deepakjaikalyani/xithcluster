package br.edu.univercidade.cc.xithcluster;


public interface CompositionStrategy {
	
	int[] compose(int width, int height, int numImages, byte[][] colorAndAlphaBuffer, byte[][] depthBuffer);
	
}
