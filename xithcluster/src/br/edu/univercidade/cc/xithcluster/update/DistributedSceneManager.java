package br.edu.univercidade.cc.xithcluster.update;

import java.awt.Dimension;
import br.edu.univercidade.cc.xithcluster.SceneInfo;

public interface DistributedSceneManager {
	
	Dimension getTargetScreenDimension();
	
	float getTargetFPS();
	
	SceneInfo getSceneInfo();
	
}