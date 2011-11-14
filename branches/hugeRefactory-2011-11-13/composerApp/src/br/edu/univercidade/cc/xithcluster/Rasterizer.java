package br.edu.univercidade.cc.xithcluster;

import br.edu.univercidade.cc.xithcluster.composition.ColorAndAlphaBufferList;
import br.edu.univercidade.cc.xithcluster.composition.DepthBufferList;

public interface Rasterizer {

	void setNewImageData(ColorAndAlphaBufferList colorAndAlphaBuffers, DepthBufferList depthBuffers);

	void setScreenSize(int screenWidth, int screenHeight);

}
