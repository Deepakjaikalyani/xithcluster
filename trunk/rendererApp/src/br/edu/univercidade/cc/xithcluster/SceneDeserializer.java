package br.edu.univercidade.cc.xithcluster;

import java.io.IOException;
import java.util.List;
import java.util.Observable;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.View;
import br.edu.univercidade.cc.xithcluster.serial.pack.GeometriesPackager;
import br.edu.univercidade.cc.xithcluster.serial.pack.LightSourcesPackager;
import br.edu.univercidade.cc.xithcluster.serial.pack.PointOfViewPackager;

public class SceneDeserializer extends Observable implements Runnable {
	
	public static class DeserializationResult {
		
		private View view;
		
		private List<Light> lightSources;
		
		private BranchGroup geometries;

		DeserializationResult(View view, List<Light> lightSources, BranchGroup geometries) {
			this.view = view;
			this.lightSources = lightSources;
			this.geometries = geometries;
		}
		
		public View getView() {
			return view;
		}
		
		public List<Light> getLightSources() {
			return lightSources;
		}

		public BranchGroup getGeometries() {
			return geometries;
		}
		
	}
	
	private PointOfViewPackager pointOfViewPackager = new PointOfViewPackager();
	
	private LightSourcesPackager lightSourcesPackager = new LightSourcesPackager();
	
	private GeometriesPackager geometriesPackager = new GeometriesPackager();
	
	private byte[] pointOfViewData;
	
	private byte[] lightSourcesData;
	
	private byte[] geometriesData;
	
	public void setPackagesData(byte[] pointOfViewData, byte[] lightSourcesData, byte[] geometriesData) {
		this.pointOfViewData = pointOfViewData;
		this.lightSourcesData = lightSourcesData;
		this.geometriesData = geometriesData;
	}
	
	@Override
	public void run() {
		View view;
		List<Light> lightSources;
		BranchGroup geometries;
		DeserializationResult result;
		
		try {
			view = pointOfViewPackager.deserialize(pointOfViewData);
			lightSources = lightSourcesPackager.deserialize(lightSourcesData);
			geometries = geometriesPackager.deserialize(geometriesData);
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error deserializing scene data", e);
		}
		
		result = new DeserializationResult(view, lightSources, geometries);
		
		// TODO:
		System.out.println("Scene deserialized successfully");
		
		setChanged();
		
		notifyObservers(result);
	}
}