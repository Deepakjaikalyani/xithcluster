package br.edu.univercidade.cc.xithcluster.primitives;

import org.openmali.vecmath2.Colorf;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Material;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.primitives.GeomFactory;

public class Sphere extends org.xith3d.scenegraph.primitives.Sphere {
	
	private float centerX;
	
	private float centerY;
	
	private float centerZ;
	
	private int slices;
	
	private int stacks;
	
	private int features;
	
	private boolean colorAlpha;
	
	private int texCoordsSize;
	
	private Colorf color;
	
	private Material material;
	
	public Sphere() {
		this(0.0f, 0.0f, 0.0f, 1.0f, 5, 5, 11, false, 2);
	}
	
	public Sphere(float centerX, float centerY, float centerZ, float radius, int slices, int stacks, Appearance app) {
		this(centerX, centerY, centerZ, radius, slices, stacks, 0x3 | GeomFactory.getFeaturesFromAppearance(app), false, GeomFactory.getTexCoordsSize(app));
	}
	
	public Sphere(float centerX, float centerY, float centerZ, float radius, int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize) {
		super(centerX, centerY, centerZ, radius, slices, stacks, features, colorAlpha, texCoordsSize);
		
		this.centerX = centerX;
		this.centerY = centerY;
		this.centerZ = centerZ;
		this.slices = slices;
		this.stacks = stacks;
		this.features = features;
		this.colorAlpha = colorAlpha;
		this.texCoordsSize = texCoordsSize;
	}
	
	public Sphere(float centerX, float centerY, float centerZ, float radius, int slices, int stacks, Colorf color) {
		this(centerX, centerY, centerZ, radius, slices, stacks, 3, false, 2);
		
		setColor(color);
	}
	
	public void setColor(Colorf color) {
		getAppearance(true).getColoringAttributes(true).setColor(color);
		
		if (color.hasAlpha()) {
			getAppearance(true).getTransparencyAttributes(true).setTransparency(color.getAlpha());
		}
		
		this.color = color;
	}
	
	public Sphere(float radius, int slices, int stacks, Material material) {
		this(0.0f, 0.0f, 0.0f, radius, slices, stacks, 3, false, 2);
		
		setMaterial(material);
	}
	
	public void setMaterial(Material material) {
		getAppearance(true).setMaterial(material);
		
		this.material = material;
	}
	
	public Sphere(float centerX, float centerY, float centerZ, float radius, int slices, int stacks, String texture) {
		this(centerX, centerY, centerZ, radius, slices, stacks, TextureLoader.getInstance().getTextureOrNull(texture, Texture.MipmapMode.MULTI_LEVEL_MIPMAP));
	}
	
	public Sphere(float centerX, float centerY, float centerZ, float radius, int slices, int stacks, Texture texture) {
		this(centerX, centerY, centerZ, radius, slices, stacks, 11, false, 2);
		
		getAppearance(true).setTexture(texture);
	}
	
	public Sphere(float radius, int slices, int stacks, Appearance app) {
		this(0.0f, 0.0f, 0.0f, radius, slices, stacks, app);
	}
	
	public Sphere(float radius, int slices, int stacks, Colorf color) {
		this(0.0F, 0.0F, 0.0F, radius, slices, stacks, color);
	}
	
	public Sphere(float radius, int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize) {
		this(0.0f, 0.0f, 0.0f, radius, slices, stacks, features, colorAlpha, texCoordsSize);
	}
	
	public Sphere(float radius, int slices, int stacks, String texture) {
		this(0.0f, 0.0f, 0.0f, radius, slices, stacks, texture);
	}
	
	public Sphere(float radius, int slices, int stacks, Texture texture) {
		this(0.0f, 0.0f, 0.0f, radius, slices, stacks, texture);
	}
	
	public Sphere(int slices, int stacks, Appearance app) {
		this(0.0F, 0.0F, 0.0F, 1.0F, slices, stacks, app);
	}
	
	public Sphere(int slices, int stacks, Colorf color) {
		this(0.0F, 0.0F, 0.0F, 1.0F, slices, stacks, color);
	}
	
	public Sphere(int slices, int stacks, int features, boolean colorAlpha, int texCoordsSize) {
		this(0.0F, 0.0F, 0.0F, 1.0F, slices, stacks, features, colorAlpha, texCoordsSize);
	}
	
	public Sphere(int slices, int stacks, String texture) {
		this(0.0F, 0.0F, 0.0F, 1.0F, slices, stacks, texture);
	}
	
	public Sphere(int slices, int stacks, Texture texture) {
		this(0.0F, 0.0F, 0.0F, 1.0F, slices, stacks, texture);
	}
	
	public float getCenterX() {
		return centerX;
	}
	
	public float getCenterY() {
		return centerY;
	}
	
	public float getCenterZ() {
		return centerZ;
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
	
	public Colorf getColor() {
		return color;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	@Override
	protected Shape3D newInstance() {
		Sphere newSphere;
		boolean globalIgnoreBounds;
		
		globalIgnoreBounds = Node.globalIgnoreBounds;
		
		Node.globalIgnoreBounds = isIgnoreBounds();
		newSphere = new Sphere(centerX, centerY, centerZ, getRadius(), slices, stacks, features, colorAlpha, texCoordsSize);
		if (color != null) {
			newSphere.setColor(color);
		} else if (material != null) {
			newSphere.setMaterial(material);
		}
		Node.globalIgnoreBounds = globalIgnoreBounds;
		
		return newSphere;
	}
	
}
