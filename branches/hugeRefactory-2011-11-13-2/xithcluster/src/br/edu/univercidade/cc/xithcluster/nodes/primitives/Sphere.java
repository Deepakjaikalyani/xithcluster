package br.edu.univercidade.cc.xithcluster.nodes.primitives;

import org.xith3d.scenegraph.Appearance;
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
	
	public Sphere(String name, float centerX, float centerY, float centerZ, float radius, int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize, Appearance appearance) {
		super(centerX, centerY, centerZ, radius, slices, stacks, features, colorAlpha, texCoordsSize);

		this.setName(name);
		this.centerX = centerX;
		this.centerY = centerY;
		this.centerZ = centerZ;
		this.slices = slices;
		this.stacks = stacks;
		this.features = features;
		this.colorAlpha = colorAlpha;
		this.texCoordsSize = texCoordsSize;
		this.setAppearance(appearance);
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
	protected void copy(Shape3D arg0) {
		Sphere sphere;
		
		sphere = (Sphere) arg0;
		
		sphere.setRadius(getRadius());
		sphere.setName(getName());
		sphere.centerX = centerX;
		sphere.centerY = centerY;
		sphere.centerZ = centerZ;
		sphere.slices = slices;
		sphere.stacks = stacks;
		sphere.features = features;
		sphere.colorAlpha = colorAlpha;
		sphere.texCoordsSize = texCoordsSize;
        sphere.setAppearance(getAppearance());
        
        sphere.setBoundsAutoCompute(false);
        sphere.setBounds(getBounds());
        sphere.boundsDirty = true;
        sphere.updateBounds(false);
        sphere.setPickable(isPickable());
        sphere.setRenderable(isRenderable());
	}
	
	@Override
	protected Shape3D newInstance() {
		Sphere newSphere;
		boolean globalIgnoreBounds;
		
		globalIgnoreBounds = Node.globalIgnoreBounds;
		
		Node.globalIgnoreBounds = isIgnoreBounds();
		newSphere = new Sphere(null, 0.0f, 0.0f, 0.0f, 1.0f, 5, 5, 11, false, 2, null);
		Node.globalIgnoreBounds = globalIgnoreBounds;
		
		return newSphere;
	}
	
}
