package br.edu.univercidade.cc.xithcluster.composition;

import java.util.List;

public interface CompositionStrategy {
	
	int[] compose(int width, int height, List<byte[]> colorAndAlphaBuffer, List<float[]> depthBuffer);
	
}
