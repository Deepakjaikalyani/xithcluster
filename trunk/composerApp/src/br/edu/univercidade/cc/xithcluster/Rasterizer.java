package br.edu.univercidade.cc.xithcluster;

public interface Rasterizer {

	void setColorAlphaAndDepthBuffers(byte[][] colorAndAlphaBuffers, float[][] depthBuffers);

	void setScreenSize(int screenWidth, int screenHeight);

}
