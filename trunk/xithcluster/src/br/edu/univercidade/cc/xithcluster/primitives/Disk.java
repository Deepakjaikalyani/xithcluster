package br.edu.univercidade.cc.xithcluster.primitives;

import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;

public class Disk extends org.xith3d.scenegraph.primitives.Disk {
	
	private float radius;
	
	private int slices;
	
	private int features;
	
	private boolean colorAlpha;
	
	private int texCoordsSize;
	
	public Disk(String name, float radius, int slices, int features, boolean colorAlpha, int texCoordsSize, Appearance appearance) {
		super(radius, slices, features, colorAlpha, texCoordsSize);
		
		this.setName(name);
		this.radius = radius;
		this.slices = slices;
		this.features = features;
		this.colorAlpha = colorAlpha;
		this.texCoordsSize = texCoordsSize;
		this.setAppearance(appearance);
	}
	
	public float getRadius() {
		return radius;
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
		Disk disk;
		
		disk = (Disk) arg0;
		
		disk.setName(getName());
		disk.radius = radius;
		disk.slices = slices;
		disk.features = features;
		disk.colorAlpha = colorAlpha;
		disk.texCoordsSize = texCoordsSize;
		disk.setAppearance(getAppearance());
		
		disk.setBoundsAutoCompute(false);
		disk.setBounds(getBounds());
		disk.boundsDirty = true;
		disk.updateBounds(false);
		disk.setPickable(isPickable());
		disk.setRenderable(isRenderable());
	}
	
	@Override
	protected Shape3D newInstance() {
		Disk newDisk;
		boolean globalIgnoreBounds;
		
		globalIgnoreBounds = Node.globalIgnoreBounds;
		
		Node.globalIgnoreBounds = isIgnoreBounds();
		newDisk = new Disk(null, 1.0f, 5, 11, false, 2, null);
		Node.globalIgnoreBounds = globalIgnoreBounds;
		
		return newDisk;
	}
	
}
