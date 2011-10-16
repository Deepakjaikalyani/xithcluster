package br.edu.univercidade.cc.xithcluster;

import java.awt.Dimension;

public interface SceneRenderer {
	
	Dimension getTargetScreenDimension();
	
	float getTargetFPS();
	
	SceneInfo getDistributableSceneInfo();
	
}