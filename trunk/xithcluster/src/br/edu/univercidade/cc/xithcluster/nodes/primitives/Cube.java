package br.edu.univercidade.cc.xithcluster.nodes.primitives;

import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;

public class Cube extends org.xith3d.scenegraph.primitives.Cube {
	
	private float size = 1.0f;
	
	private int features;
	
	private boolean colorAlpha;
	
	private int texCoordsSize;
	
	public Cube(String name, float size, int features, boolean colorAlpha, int texCoordsSize, Appearance appearance) {
		super(size, features, colorAlpha, texCoordsSize);
		
		this.setName(name);
		this.size = size;
		this.features = features;
		this.colorAlpha = colorAlpha;
		this.texCoordsSize = texCoordsSize;
		this.setAppearance(appearance);
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
		Cube copy;
		
		copy = (Cube) arg0;
		
		copy.setName(getName());
		copy.size = size;
		copy.features = features;
		copy.colorAlpha = colorAlpha;
		copy.texCoordsSize = texCoordsSize;
        copy.setAppearance(getAppearance());
        
        copy.setBoundsAutoCompute(false);
        copy.setBounds(getBounds());
        copy.boundsDirty = true;
        copy.updateBounds(false);
        copy.setPickable(isPickable());
        copy.setRenderable(isRenderable());
	}

	@Override
	protected Shape3D newInstance() {
		boolean globalIgnoreBounds = Node.globalIgnoreBounds;
		
		Node.globalIgnoreBounds = isIgnoreBounds();
		Cube newCube = new Cube(null, 1.0f, Geometry.COORDINATES | Geometry.NORMALS, false, 2, null);
		Node.globalIgnoreBounds = globalIgnoreBounds;
		
		return newCube;
	}
	
}
