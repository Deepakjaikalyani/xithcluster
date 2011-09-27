package br.edu.univercidade.cc.xithcluster;


public interface CompositionStrategy {
	
	int[] compose(int width, int height, byte[][] colorAndAlphaBuffer, float[][] depthBuffer);
	
}
