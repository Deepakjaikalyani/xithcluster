package br.edu.univercidade.cc.xithcluster;

import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.utils.CopyListener;

public class DirectionalLight extends org.xith3d.scenegraph.DirectionalLight {
	
	private boolean enabled;
	
	private Colorf color;
	
	private Vector3f direction;

	public DirectionalLight(boolean enabled, Colorf color, Vector3f direction) {
		super(enabled, color, direction);
		
		this.enabled = enabled;
		this.color = color;
		this.direction = direction;
	}

	@Override
	public Node sharedCopy(CopyListener listener) {
		DirectionalLight destination;
		boolean globalIgnoreBounds;
		
		globalIgnoreBounds = Node.globalIgnoreBounds;
		
		Node.globalIgnoreBounds = isIgnoreBounds();
		destination = new DirectionalLight(enabled, color, direction);
		Node.globalIgnoreBounds = globalIgnoreBounds;
		
		destination.boundsDirty = true;
		
		if (listener != null) {
			listener.onNodeCopied(this, destination, true);
		}
		
		return destination;
	}

}
