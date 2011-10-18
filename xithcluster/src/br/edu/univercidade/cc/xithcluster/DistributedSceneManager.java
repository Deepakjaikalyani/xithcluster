package br.edu.univercidade.cc.xithcluster;

import java.awt.Dimension;

public interface DistributedSceneManager {
	
	Dimension getTargetScreenDimension();
	
	float getTargetFPS();
	
	SceneInfo getSceneInfo();
	
}