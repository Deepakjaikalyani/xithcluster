package br.edu.univercidade.cc.xithcluster;

import java.awt.Dimension;

public interface SceneManager {
	
	Dimension getScreenSize();
	
	float getTargetFPS();
	
	SceneInfo getSceneInfo();
	
}