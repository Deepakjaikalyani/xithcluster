package br.edu.univercidade.cc.xithcluster;

import java.io.IOException;
import java.util.Observable;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.View;
import br.edu.univercidade.cc.xithcluster.serialization.packagers.PointOfViewPackager;
import br.edu.univercidade.cc.xithcluster.serialization.packagers.ScenePackager;

public class SceneDeserializer extends Observable implements Runnable {
	
	private PointOfViewPackager pointOfViewPackager = new PointOfViewPackager();
	
	private ScenePackager scenePackager = new ScenePackager();
	
	private byte[] pointOfViewData;
	
	private byte[] sceneData;
	
	public SceneDeserializer(byte[] pointOfViewData, byte[] sceneData) {
		this.pointOfViewData = pointOfViewData;
		this.sceneData = sceneData;
	}

	@Override
	public void run() {
		View view;
		BranchGroup scene;
		DeserializationResult result;
		
		try {
			view = pointOfViewPackager.deserialize(pointOfViewData);
			scene = scenePackager.deserialize(sceneData);
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error deserializing scene data", e);
		}
		
		result = new DeserializationResult(view, scene);
		
		setChanged();
		
		notifyObservers(result);
	}
}