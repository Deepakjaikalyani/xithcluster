package br.edu.univercidade.cc.xithcluster.primitives;

import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;

public class Sphere extends org.xith3d.scenegraph.primitives.Sphere {
	
	private float centerX;
	
	private float centerY;
	
	private float centerZ;
	
	private int slices;
	
	private int stacks;
	
	private int features;
	
	private boolean colorAlpha;
	
	private int texCoordsSize;
	
	public Sphere() {
		this(0.0f, 0.0f, 0.0f, 1.0f, 5, 5, 11, false, 2);
	}
	
	public Sphere(float centerX, float centerY, float centerZ, float radius, int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize) {
		super(centerX, centerY, centerZ, radius, slices, stacks, features, colorAlpha, texCoordsSize);
		
		this.centerX = centerX;
		this.centerY = centerY;
		this.centerZ = centerZ;
		this.slices = slices;
		this.stacks = stacks;
		this.features = features;
		this.colorAlpha = colorAlpha;
		this.texCoordsSize = texCoordsSize;
	}
	
	public Sphere(float radius, int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize) {
		this(0.0f, 0.0f, 0.0f, radius, slices, stacks, features, colorAlpha, texCoordsSize);
	}
	
	public Sphere(int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize) {
		this(0.0F, 0.0F, 0.0F, 1.0F, slices, stacks, features, colorAlpha, texCoordsSize);
	}
	
	public float getCenterX() {
		return centerX;
	}
	
	public float getCenterY() {
		return centerY;
	}
	
	public float getCenterZ() {
		return centerZ;
	}
	
	public int getSlices() {
		return slices;
	}
	
	public int getStacks() {
		return stacks;
	}
	
	public int getFeatures() {
		return features;
	}
	
	public boolean isColorAlpha() {
		return colorAlpha;
	}
	
	public int getTexCoordsSize() {
		return texCoordsSize;
	}
	
	@Override
	protected Shape3D newInstance() {
		Sphere newSphere;
		boolean globalIgnoreBounds;
		
		globalIgnoreBounds = Node.globalIgnoreBounds;
		
		Node.globalIgnoreBounds = isIgnoreBounds();
		newSphere = new Sphere(centerX, centerY, centerZ, getRadius(), slices, stacks, features, colorAlpha, texCoordsSize);
		newSphere.setAppearance(getAppearance().cloneNodeComponent(true));
		Node.globalIgnoreBounds = globalIgnoreBounds;
		
		return newSphere;
	}
	
}
