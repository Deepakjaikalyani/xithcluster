package br.edu.univercidade.cc.xithcluster;

import java.io.IOException;
import java.util.Observable;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.View;
import br.edu.univercidade.cc.xithcluster.serialization.packagers.PointOfViewPackager;
import br.edu.univercidade.cc.xithcluster.serialization.packagers.ScenePackager;

public class SceneDeserializer extends Observable implements Runnable {
	
	public static class DeserializationResult {
		
		private View pointOfView;
		
		private BranchGroup scene;

		DeserializationResult(View pointOfView, BranchGroup scene) {
			this.pointOfView = pointOfView;
			this.scene = scene;
		}
		
		public View getPointOfView() {
			return pointOfView;
		}
		
		public BranchGroup getScene() {
			return scene;
		}
		
	}
	
	private PointOfViewPackager pointOfViewPackager = new PointOfViewPackager();
	
	private ScenePackager scenePackager = new ScenePackager();
	
	private byte[] pointOfViewData;
	
	private byte[] sceneData;
	
	public void setSceneData(byte[] pointOfViewData, byte[] sceneData) {
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