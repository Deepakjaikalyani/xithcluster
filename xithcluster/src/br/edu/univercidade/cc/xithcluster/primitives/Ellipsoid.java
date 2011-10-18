package br.edu.univercidade.cc.xithcluster.primitives;

import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;

public class Ellipsoid extends org.xith3d.scenegraph.primitives.Ellipsoid {
	
	private float rx;
	
	private float ry;
	
	private float rz;
	
	private int slices;
	
	private int stacks;
	
	private int features;
	
	private boolean colorAlpha;
	
	private int texCoordsSize;
	
	public Ellipsoid(String name, float rx, float ry, float rz, int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize, Appearance appearance) {
		super(rx, ry, rz, slices, stacks, features, colorAlpha, texCoordsSize);
		
		this.setName(name);
		this.rx = rx;
		this.ry = ry;
		this.rz = rz;
		this.slices = slices;
		this.stacks = stacks;
		this.features = features;
		this.colorAlpha = colorAlpha;
		this.texCoordsSize = texCoordsSize;
		this.setAppearance(appearance);
	}
	
	public float getRx() {
		return rx;
	}
	
	public float getRy() {
		return ry;
	}
	
	public float getRz() {
		return rz;
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
		Ellipsoid ellipsoid;
		
		ellipsoid = (Ellipsoid) arg0;
		
		ellipsoid.setName(getName());
		ellipsoid.rx = rx;
		ellipsoid.ry = ry;
		ellipsoid.rz = rz;
		ellipsoid.slices = slices;
		ellipsoid.stacks = stacks;
		ellipsoid.features = features;
		ellipsoid.colorAlpha = colorAlpha;
		ellipsoid.texCoordsSize = texCoordsSize;
        ellipsoid.setAppearance(getAppearance());
        
        ellipsoid.setBoundsAutoCompute(false);
        ellipsoid.setBounds(getBounds());
        ellipsoid.boundsDirty = true;
        ellipsoid.updateBounds(false);
        ellipsoid.setPickable(isPickable());
        ellipsoid.setRenderable(isRenderable());
	}
	
	@Override
	protected Shape3D newInstance() {
		Ellipsoid newSphere;
		boolean globalIgnoreBounds;
		
		globalIgnoreBounds = Node.globalIgnoreBounds;
		
		Node.globalIgnoreBounds = isIgnoreBounds();
		newSphere = new Ellipsoid(null, 0.0f, 0.0f, 0.0f, 5, 5, 11, false, 2, null);
		Node.globalIgnoreBounds = globalIgnoreBounds;
		
		return newSphere;
	}
	
}
