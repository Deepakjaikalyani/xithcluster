package br.edu.univercidade.cc.xithcluster;

import java.util.List;

public interface Rasterizer {

	void setColorAlphaAndDepthBuffers(List<byte[]> colorAndAlphaBuffers, List<float[]> depthBuffers);

	void setScreenSize(int screenWidth, int screenHeight);

}
