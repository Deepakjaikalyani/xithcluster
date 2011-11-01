package br.edu.univercidade.cc.xithcluster;

import java.awt.Dimension;

public interface SceneManager {
	
	Dimension getTargetScreenDimension();
	
	float getTargetFPS();
	
	SceneInfo getSceneInfo();
	
}