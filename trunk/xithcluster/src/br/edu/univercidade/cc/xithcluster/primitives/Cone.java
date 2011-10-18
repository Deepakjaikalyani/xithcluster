package br.edu.univercidade.cc.xithcluster.primitives;

import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;

public class Cone extends org.xith3d.scenegraph.primitives.Cone {
	
	private float radius;
	
	private float height;
	
	private int slices;
	
	private int features;
	
	private boolean colorAlpha;
	
	private int texCoordsSize;
	
	public Cone(String name, float radius, float height, int slices, int features, boolean colorAlpha, int texCoordsSize, Appearance appearance) {
		super(radius, height, slices, features, colorAlpha, texCoordsSize);
		
		this.setName(name);
		this.radius = radius;
		this.height = height;
		this.slices = slices;
		this.features = features;
		this.colorAlpha = colorAlpha;
		this.texCoordsSize = texCoordsSize;
		this.setAppearance(appearance);
	}
	
	public float getRadius() {
		return radius;
	}
	
	public float getHeight() {
		return height;
	}
	
	public int getSlices() {
		return slices;
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
		Cone cone;
		
		cone = (Cone) arg0;
		
		cone.setName(getName());
		cone.radius = radius;
		cone.height = height;
		cone.slices = slices;
		cone.features = features;
		cone.colorAlpha = colorAlpha;
		cone.texCoordsSize = texCoordsSize;
        cone.setAppearance(getAppearance());
        
        cone.setBoundsAutoCompute(false);
        cone.setBounds(getBounds());
        cone.boundsDirty = true;
        cone.updateBounds(false);
        cone.setPickable(isPickable());
        cone.setRenderable(isRenderable());
	}
	
	@Override
	protected Shape3D newInstance() {
		Cone newCone;
		boolean globalIgnoreBounds;
		
		globalIgnoreBounds = Node.globalIgnoreBounds;
		
		Node.globalIgnoreBounds = isIgnoreBounds();
		newCone = new Cone(null, 1.0f, 1.0f, 5, 11, false, 2, null);
		Node.globalIgnoreBounds = globalIgnoreBounds;
		
		return newCone;
	}
}
