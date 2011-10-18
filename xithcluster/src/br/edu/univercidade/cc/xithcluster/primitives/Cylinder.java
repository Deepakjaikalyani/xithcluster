package br.edu.univercidade.cc.xithcluster.primitives;

import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;

public class Cylinder extends org.xith3d.scenegraph.primitives.Cylinder {
	
	private float radius;
	
	private float height;
	
	private float taper;
	
	private boolean closed;
	
	private int slices;
	
	private int features;
	
	private boolean colorAlpha;
	
	private int texCoordsSize;
	
	public Cylinder(String name, float radius, float height, float taper, boolean closed, int slices, int features, boolean colorAlpha, int texCoordsSize, Appearance appearance) {
		super(radius, height, taper, closed, slices, features, colorAlpha, texCoordsSize);
		
		this.setName(name);
		this.radius = radius;
		this.height = height;
		this.taper = taper;
		this.closed = closed;
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
	
	public float getTaper() {
		return taper;
	}
	
	public boolean isClosed() {
		return closed;
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
		Cylinder cylinder;
		
		cylinder = (Cylinder) arg0;
		
		cylinder.setName(getName());
		cylinder.radius = radius;
		cylinder.height = height;
		cylinder.taper = taper;
		cylinder.closed = closed;
		cylinder.slices = slices;
		cylinder.features = features;
		cylinder.colorAlpha = colorAlpha;
		cylinder.texCoordsSize = texCoordsSize;
		cylinder.setAppearance(getAppearance());
		
		cylinder.setBoundsAutoCompute(false);
		cylinder.setBounds(getBounds());
		cylinder.boundsDirty = true;
		cylinder.updateBounds(false);
		cylinder.setPickable(isPickable());
		cylinder.setRenderable(isRenderable());
	}
	
	@Override
	protected Shape3D newInstance() {
		Cylinder newCylinder;
		boolean globalIgnoreBounds;
		
		globalIgnoreBounds = Node.globalIgnoreBounds;
		
		Node.globalIgnoreBounds = isIgnoreBounds();
		newCylinder = new Cylinder(null, 1.0f, 1.0f, 1.0f, true, 5, 11, false, 2, null);
		Node.globalIgnoreBounds = globalIgnoreBounds;
		
		return newCylinder;
	}
	
}
