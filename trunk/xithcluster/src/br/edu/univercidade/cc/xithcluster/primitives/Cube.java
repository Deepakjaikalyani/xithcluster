package br.edu.univercidade.cc.xithcluster.primitives;

import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Material;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Texture;

public class Cube extends org.xith3d.scenegraph.primitives.Cube {
	
	private float size = 1.0f;
	
	private int features;
	
	private boolean colorAlpha;
	
	private int texCoordsSize;
	
	private Colorf color;
	
	private Material material;
	
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
	
	public Cube(float size, Material material) {
		this(size, Geometry.COORDINATES | Geometry.NORMALS, false, 2);
		
		setMaterial(material);
	}
	
	public Cube(float size, Colorf color) {
		this(size, Geometry.COORDINATES | Geometry.NORMALS, false, 2);
		
		setColor(color);
	}
	
	public Cube(float size, Texture texture) {
		this(size, Geometry.COORDINATES | Geometry.NORMALS | Geometry.TEXTURE_COORDINATES, false, 2);
		
		setTexture(texture);
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
	
	public Material getMaterial() {
		return material;
	}
	
	public void setMaterial(Material material) {
		this.getAppearance(true).setMaterial(material);
		
		this.material = material;
	}
	
	public int getTexCoordsSize() {
		return texCoordsSize;
	}
	
	public Colorf getColor() {
		return color;
	}
	
	public void setColor(Colorf color) {
		getAppearance(true).getColoringAttributes(true).setColor(color);
		
		if (color.hasAlpha()) {
			getAppearance(true).getTransparencyAttributes(true).setTransparency(color.getAlpha());
		}
		
		this.color = color;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public void setTexture(Texture texture) {
		this.getAppearance(true).setTexture(texture);
		
		this.texture = texture;
	}
	
	@Override
	protected Shape3D newInstance() {
		boolean gib = Node.globalIgnoreBounds;
		
		Node.globalIgnoreBounds = isIgnoreBounds();
		
		Cube newCube = new Cube(size, features, colorAlpha, texCoordsSize);
		
		if (color != null) {
			newCube.setColor(color);
		} else if (material != null) {
			newCube.setMaterial(material);
		} else if (texture != null) {
			newCube.setTexture(texture);
		}
		
		Node.globalIgnoreBounds = gib;
		
		return newCube;
	}
	
}
