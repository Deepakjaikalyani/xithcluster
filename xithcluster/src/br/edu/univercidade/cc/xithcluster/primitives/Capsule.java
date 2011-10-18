package br.edu.univercidade.cc.xithcluster.primitives;

import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;

public class Capsule extends org.xith3d.scenegraph.primitives.Capsule {
	
	private float radius;
	
	private float length;
	
	private int slices;
	
	private int stacks;
	
	private int features;
	
	private boolean colorAlpha;
	
	private int texCoordsSize;
	
	public Capsule(String name, float radius, float length, int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize, Appearance appearance) {
		super(radius, length, slices, stacks, features, colorAlpha, texCoordsSize);
		
		this.setName(name);
		this.radius = radius;
		this.length = length;
		this.slices = slices;
		this.stacks = stacks;
		this.features = features;
		this.colorAlpha = colorAlpha;
		this.texCoordsSize = texCoordsSize;
		this.setAppearance(appearance);
	}
	
	public float getLength() {
		return length;
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
		Capsule capsule;
		
		capsule = (Capsule) arg0;
		
		capsule.setName(getName());
		capsule.radius = radius;
		capsule.length = length;
		capsule.slices = slices;
		capsule.stacks = stacks;
		capsule.features = features;
		capsule.colorAlpha = colorAlpha;
		capsule.texCoordsSize = texCoordsSize;
		capsule.setAppearance(getAppearance());
		
		capsule.setBoundsAutoCompute(false);
		capsule.setBounds(getBounds());
		capsule.boundsDirty = true;
		capsule.updateBounds(false);
		capsule.setPickable(isPickable());
		capsule.setRenderable(isRenderable());
	}
	
	@Override
	protected Shape3D newInstance() {
		Capsule newCapsule;
		boolean globalIgnoreBounds;
		
		globalIgnoreBounds = Node.globalIgnoreBounds;
		
		Node.globalIgnoreBounds = isIgnoreBounds();
		newCapsule = new Capsule(null, 1.0f, 1.0f, 5, 5, 11, false, 2, null);
		Node.globalIgnoreBounds = globalIgnoreBounds;
		
		return newCapsule;
	}
	
}
