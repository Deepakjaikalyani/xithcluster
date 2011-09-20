package br.edu.univercidade.cc.xithcluster.primitives;

import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Tuple2f;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Texture;

public class Rectangle extends org.xith3d.scenegraph.primitives.Rectangle {
	
	private Tuple3f offset;
	
	private Tuple2f texLowerLeft;
	
	private Tuple2f texUpperRight;
	
	public Rectangle() {
		super(1.0f, 1.0f);
	}
	
	public Rectangle(float width, float height, Tuple3f offset, Tuple2f texLowerLeft, Tuple2f texUpperRight) {
		super(width, height, offset, (Texture) null, texLowerLeft, texUpperRight, (Colorf) null);
		
		this.offset = offset;
		this.texLowerLeft = texLowerLeft;
		this.texUpperRight = texUpperRight;
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
        destination.setName(getName());
	}
	
	@Override
	protected Shape3D newInstance() {
		boolean globalIgnoreBounds = Node.globalIgnoreBounds;
		
		Node.globalIgnoreBounds = isIgnoreBounds();
		Rectangle newRectangle = new Rectangle();
		Node.globalIgnoreBounds = globalIgnoreBounds;
		
		return newRectangle;
	}
}
