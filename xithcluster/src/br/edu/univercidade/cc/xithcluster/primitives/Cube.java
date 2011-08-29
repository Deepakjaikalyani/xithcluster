package br.edu.univercidade.cc.xithcluster.primitives;

import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;

public class Cube extends org.xith3d.scenegraph.primitives.Cube {
	
	private float size = 1.0f;
	
	public Cube() {
		this(1.0f);
	}
	
	public Cube(float size) {
		super(size);
		
		this.size = size;
	}
	
	public float getSize() {
		return size;
	}
	
	@Override
	protected Shape3D newInstance() {
		boolean gib = Node.globalIgnoreBounds;
		Node.globalIgnoreBounds = isIgnoreBounds();
		Shape3D newShape = new Cube(size);
		Node.globalIgnoreBounds = gib;
		return newShape;
	}
	
}
