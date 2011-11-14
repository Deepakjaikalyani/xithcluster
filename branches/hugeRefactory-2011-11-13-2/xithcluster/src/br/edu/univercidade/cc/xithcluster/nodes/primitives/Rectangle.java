package br.edu.univercidade.cc.xithcluster.nodes.primitives;

import org.openmali.vecmath2.Tuple2f;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Texture;

public class Rectangle extends org.xith3d.scenegraph.primitives.Rectangle {
	
	private float width;
	
	private float height;
	
	private ZeroPointLocation zpl;
	
	private Tuple3f offset;
	
	private Tuple2f texLowerLeft;
	
	private Tuple2f texUpperRight;
	
	public Rectangle(String name, float width, float height, ZeroPointLocation zpl, Texture texture, Tuple3f offset, Tuple2f texLowerLeft, Tuple2f texUpperRight) {
		super(width, height, zpl, offset, texture, texLowerLeft, texUpperRight);
		
		setName(name);
		this.width = width;
		this.height = height;
		this.zpl = zpl;
		getAppearance(true).setTexture(texture);
		this.offset = offset;
		this.texLowerLeft = texLowerLeft;
		this.texUpperRight = texUpperRight;
	}
	
	public float getOriginalWidth() {
		return width;
	}
	
	public float getOriginalHeight() {
		return height;
	}
	
	public ZeroPointLocation getZpl() {
		return zpl;
	}
	
	public Tuple3f getOffset() {
		return offset;
	}
	
	public Tuple2f getTexLowerLeft() {
		return texLowerLeft;
	}
	
	public Tuple2f getTexUpperRight() {
		return texUpperRight;
	}
	
	@Override
	protected void copy(Shape3D arg0) {
		Rectangle destination;
		
		destination = (Rectangle) arg0;
		
		setName(getName());
		destination.width = width;
		destination.height = height;
		destination.zpl = zpl;
		destination.offset = offset;
		destination.texLowerLeft = texLowerLeft;
		destination.texUpperRight = texUpperRight;
		destination.setAppearance(getAppearance());
		
		destination.setBoundsAutoCompute(false);
		destination.setBounds(getBounds());
		destination.boundsDirty = true;
		destination.updateBounds(false);
		destination.setPickable(isPickable());
		destination.setRenderable(isRenderable());
	}
	
	@Override
	protected Shape3D newInstance() {
		Rectangle newRectangle;
		boolean globalIgnoreBounds = Node.globalIgnoreBounds;
		
		Node.globalIgnoreBounds = isIgnoreBounds();
		newRectangle = new Rectangle(null, 1.0f, 1.0f, ZeroPointLocation.CENTER_CENTER, null, new Tuple3f(0.0f, 0.0f, 0.0f), null, null);
		Node.globalIgnoreBounds = globalIgnoreBounds;
		
		return newRectangle;
	}
}
