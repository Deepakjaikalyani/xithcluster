package br.edu.univercidade.cc.xithcluster.primitives;

import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;

public class Torus extends org.xith3d.scenegraph.primitives.Torus {
	
	private float radius;
	
	private float alpha;
	
	private Object radSlices;
	
	private int conSlices;
	
	private int features;
	
	private boolean colorAlpha;
	
	private int texCoordsSize;
	
	public Torus(String name, float radius, float alpha, int radSlices, int conSlices, int features, boolean colorAlpha, int texCoordsSize, Appearance appearance) {
		super(radius, alpha, radSlices, conSlices, features, colorAlpha, texCoordsSize);
		
		this.setName(name);
		this.radius = radius;
		this.alpha = alpha;
		this.radSlices = radSlices;
		this.conSlices = conSlices;
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
	
	public Object getRadSlices() {
		return radSlices;
	}
	
	public int getConSlices() {
		return conSlices;
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
		Torus torus;
		
		torus = (Torus) arg0;
		
		torus.setName(getName());
		torus.radius = radius;
		torus.alpha = alpha;
		torus.radSlices = radSlices;
		torus.conSlices = conSlices;
		torus.features = features;
		torus.colorAlpha = colorAlpha;
		torus.texCoordsSize = texCoordsSize;
		torus.setAppearance(getAppearance());
		
		torus.setBoundsAutoCompute(false);
		torus.setBounds(getBounds());
		torus.boundsDirty = true;
		torus.updateBounds(false);
		torus.setPickable(isPickable());
		torus.setRenderable(isRenderable());
	}
	
	@Override
	protected Shape3D newInstance() {
		Torus newTorus;
		boolean globalIgnoreBounds;
		
		globalIgnoreBounds = Node.globalIgnoreBounds;
		
		Node.globalIgnoreBounds = isIgnoreBounds();
		newTorus = new Torus(null, 1.0f, 1.0f, 5, 5, 11, false, 2, null);
		Node.globalIgnoreBounds = globalIgnoreBounds;
		
		return newTorus;
	}
}
