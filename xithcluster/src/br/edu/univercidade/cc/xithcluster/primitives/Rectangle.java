package br.edu.univercidade.cc.xithcluster.primitives;

import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Tuple2f;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.Texture2D;

public class Rectangle extends org.xith3d.scenegraph.primitives.Rectangle {
	
	private Tuple3f offset;
	
	private Tuple2f texLowerLeft;
	
	private Tuple2f texUpperRight;
	
	private Texture texture;
	
	private Colorf color;
	
	public Rectangle() {
		super(1.0f, 1.0f);
	}
	
	public Rectangle(float width, float height, Tuple3f offset, Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight, Colorf color) {
		super(width, height, offset, texture, texLowerLeft, texUpperRight, color);
		
		this.offset = offset;
		this.texture = texture;
		this.texLowerLeft = texLowerLeft;
		this.texUpperRight = texUpperRight;
		this.color = color;
	}
	
	public Rectangle(float width, float height) {
		this(width, height, null, null, null, null, null);
	}
	
	public Rectangle(float width, float height, Texture2D texture) {
		this(width, height, null, texture, null, null, null);
	}
	
	public Rectangle(float width, float height, Colorf color) {
		this(width, height, null, null, null, null, color);
	}
	
	public Tuple3f getOffset() {
		return offset;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public Tuple2f getTexLowerLeft() {
		return texLowerLeft;
	}
	
	public Tuple2f getTexUpperRight() {
		return texUpperRight;
	}
	
	public Colorf getColor() {
		return color;
	}
	
	@Override
	protected Shape3D newInstance() {
		Rectangle newRectangle;
		boolean globalIgnoreBounds;
		
		globalIgnoreBounds = Node.globalIgnoreBounds;
		
		Node.globalIgnoreBounds = isIgnoreBounds();
		newRectangle = new Rectangle(getWidth(), getHeight(), offset, texture, texLowerLeft, texUpperRight, color);
		Node.globalIgnoreBounds = globalIgnoreBounds;
		
		return newRectangle;
	}
}