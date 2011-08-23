package br.edu.univercidade.cc.xithcluster.primitives;

import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Texture;

public class Cube extends org.xith3d.scenegraph.primitives.Cube {
	
	private float size = 1.0f;
	
	public Cube() {
		this(1.0f);
	}
	
	public Cube(Appearance app) {
		super(app);
	}
	
	public Cube(Colorf color) {
		super(color);
	}
	
	public Cube(float size, Appearance app) {
		super(size, app);
		
		this.size = size;
	}
	
	public Cube(float size, Colorf color) {
		super(size, color);
		
		this.size = size;
	}
	
	public Cube(float size, int features, boolean colorAlpha, int texCoordsSize) {
		super(size, features, colorAlpha, texCoordsSize);
		
		this.size = size;
	}
	
	public Cube(float size, String texture) {
		super(size, texture);
		
		this.size = size;
	}
	
	public Cube(float size, Texture texture) {
		super(size, texture);
		
		this.size = size;
	}
	
	public Cube(float size) {
		super(size);
		
		this.size = size;
	}
	
	public Cube(String texture) {
		super(texture);
	}
	
	public Cube(Texture texture) {
		super(texture);
	}
	
	public float getSize() {
		return size;
	}
	
	protected Shape3D newInstance() {
		boolean gib = Node.globalIgnoreBounds;
		Node.globalIgnoreBounds = isIgnoreBounds();
		Shape3D newShape = new Cube(size);
		Node.globalIgnoreBounds = gib;
		return newShape;
	}
	
}
