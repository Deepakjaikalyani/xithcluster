package br.edu.univercidade.cc.xithcluster.primitives;

import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;

public class Cube extends org.xith3d.scenegraph.primitives.Cube {
	
	private float size = 1.0f;
	
	private int features;
	
	private boolean colorAlpha;
	
	private int texCoordsSize;
	
	public Cube() {
		this(1.0f);
	}
	
	public Cube(float size, int features, boolean colorAlpha, int texCoordsSize) {
		super(size, features, colorAlpha, texCoordsSize);
		
		this.size = size;
		this.features = features;
		this.colorAlpha = colorAlpha;
		this.texCoordsSize = texCoordsSize;
	}
	
	public Cube(float size) {
		this(size, Geometry.COORDINATES | Geometry.NORMALS, false, 2);
	}
	
	public float getSize() {
		return size;
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
		Cube destination;
		
		destination = (Cube) arg0;
		
		destination.size = size;
		destination.features = features;
		destination.colorAlpha = colorAlpha;
		destination.texCoordsSize = texCoordsSize;
		
        destination.setAppearance(getAppearance());
        destination.setBoundsAutoCompute(false);
        destination.setBounds(getBounds());
        destination.boundsDirty = true;
        destination.updateBounds(false);
        destination.setPickable(isPickable());
        destination.setRenderable(isRenderable());
        destination.setName(getName());
	}

	@Override
	protected Shape3D newInstance() {
		boolean globalIgnoreBounds = Node.globalIgnoreBounds;
		
		Node.globalIgnoreBounds = isIgnoreBounds();
		Cube newCube = new Cube();
		Node.globalIgnoreBounds = globalIgnoreBounds;
		
		return newCube;
	}
	
}
