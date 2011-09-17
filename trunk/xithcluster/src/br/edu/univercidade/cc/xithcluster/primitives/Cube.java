package br.edu.univercidade.cc.xithcluster.primitives;

import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Texture;

public class Cube extends org.xith3d.scenegraph.primitives.Cube {
	
	private float size = 1.0f;
	
	private int features;
	
	private boolean colorAlpha;
	
	private int texCoordsSize;
	
	private Colorf color;
	
	private Texture texture;
	
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
		this(size, Geometry.COORDINATES | Geometry.NORMALS | Geometry.TEXTURE_COORDINATES, false, 2);
	}
	
	public Cube(float size, Colorf color) {
		super(size, Geometry.COORDINATES | Geometry.NORMALS, false, 2);
		
		setColor(color);
		
		this.color = color;
	}
	
	public void setColor(Colorf color) {
		getAppearance(true).getColoringAttributes(true).setColor(color);
		if (color.hasAlpha()) {
			getAppearance(true).getTransparencyAttributes(true).setTransparency(color.getAlpha());
		}
	}
	
	public Cube(float size, Texture texture) {
		this(size, Geometry.COORDINATES | Geometry.NORMALS | Geometry.TEXTURE_COORDINATES, false, 2);
		
		setTexture(texture);
		
		this.texture = texture;
	}
	
	public void setTexture(Texture texture) {
		this.getAppearance(true).setTexture(texture);
	}
	
	public float getSize() {
		return size;
	}
	
	public int getFeatures() {
		return features;
	}
	
	public void setFeatures(int features) {
		this.features = features;
	}
	
	public boolean isColorAlpha() {
		return colorAlpha;
	}
	
	public void setColorAlpha(boolean colorAlpha) {
		this.colorAlpha = colorAlpha;
	}
	
	public int getTexCoordsSize() {
		return texCoordsSize;
	}
	
	public void setTexCoordsSize(int texCoordsSize) {
		this.texCoordsSize = texCoordsSize;
	}
	
	public Colorf getColor() {
		return color;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public void setSize(float size) {
		this.size = size;
	}
	
	@Override
	protected Shape3D newInstance() {
		boolean gib = Node.globalIgnoreBounds;
		
		Node.globalIgnoreBounds = isIgnoreBounds();
		
		Cube newCube = new Cube(size, features, colorAlpha, texCoordsSize);
		
		if (color != null) {
			newCube.setColor(color);
		} else if (texture != null) {
			newCube.setTexture(texture);
		}
		
		Node.globalIgnoreBounds = gib;
		
		return newCube;
	}
	
}
