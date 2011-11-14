package br.edu.univercidade.cc.xithcluster.nodes.primitives;

import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;

public class Ring extends org.xith3d.scenegraph.primitives.Ring {
	
	private float radius;
	
	private float alpha;
	
	private int slices;
	
	private int features;
	
	private boolean colorAlpha;
	
	private int texCoordsSize;
	
	public Ring(String name, float radius, float alpha, int slices, int features, boolean colorAlpha, int texCoordsSize, Appearance appearance) {
		super(radius, alpha, slices, features, colorAlpha, texCoordsSize);
		
		this.setName(name);
		this.radius = radius;
		this.alpha = alpha;
		this.slices = slices;
		this.features = features;
		this.colorAlpha = colorAlpha;
		this.texCoordsSize = texCoordsSize;
		this.setAppearance(appearance);
	}
	
	public float getRadius() {
		return radius;
	}
	
	public float getAlpha() {
		return alpha;
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
		Ring ring;
		
		ring = (Ring) arg0;
		
		ring.setName(getName());
		ring.radius = radius;
		ring.alpha = alpha;
		ring.slices = slices;
		ring.features = features;
		ring.colorAlpha = colorAlpha;
		ring.texCoordsSize = texCoordsSize;
        ring.setAppearance(getAppearance());
        
        ring.setBoundsAutoCompute(false);
        ring.setBounds(getBounds());
        ring.boundsDirty = true;
        ring.updateBounds(false);
        ring.setPickable(isPickable());
        ring.setRenderable(isRenderable());
	}
	
	@Override
	protected Shape3D newInstance() {
		Ring newRing;
		boolean globalIgnoreBounds;
		
		globalIgnoreBounds = Node.globalIgnoreBounds;
		
		Node.globalIgnoreBounds = isIgnoreBounds();
		newRing = new Ring(null, 1.0f, 1.0f, 5, 11, false, 2, null);
		Node.globalIgnoreBounds = globalIgnoreBounds;
		
		return newRing;
	}
	
}
