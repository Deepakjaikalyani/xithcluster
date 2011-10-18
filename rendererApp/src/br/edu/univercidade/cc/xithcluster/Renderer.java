package br.edu.univercidade.cc.xithcluster;

import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.View;

public interface Renderer {

	void updateScene(View view, BranchGroup newRoot);
	
	void updateOnScreenInformation(int rendererId, int screenWidth, int screenHeight);
	
	byte[] getColorAndAlphaBuffer();
	
	byte[] getDepthBuffer();
	
}
