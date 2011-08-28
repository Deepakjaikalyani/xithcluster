package br.edu.univercidade.cc.xithcluster;

import java.util.List;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.View;

public interface SceneManager {
	
	void setRoot(BranchGroup arg0);
	
	BranchGroup getRoot();
	
	void setPointOfView(Tuple3f eyePosition, Tuple3f viewFocus, Tuple3f vecUp);
	
	View getPointOfView();
	
	void addLightSources(List<Light> arg0);
	
	Object getSceneLock();

	void updateModifications();

	List<Light> getLightSources();

	void setId(int id);

}
