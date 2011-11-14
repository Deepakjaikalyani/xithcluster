package br.edu.univercidade.cc.xithcluster.nodes.lights;

import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.utils.CopyListener;

public class SpotLight extends org.xith3d.scenegraph.SpotLight {
	
	private boolean enabled;
	
	private Colorf color;
	
	private Tuple3f location;
	
	private Tuple3f direction;
	
	private Tuple3f attenuation;
	
	private float spreadAngle;
	
	private float concentration;
	
	public SpotLight(boolean enabled, Colorf color, Tuple3f location, Tuple3f direction, Tuple3f attenuation, float spreadAngle, float concentration) {
		super(enabled, color, location, direction, attenuation, spreadAngle, concentration);
		
		this.enabled = enabled;
		this.color = color;
		this.location = location;
		this.direction = direction;
		this.attenuation = attenuation;
		this.spreadAngle = spreadAngle;
		this.concentration = concentration;
	}

	@Override
	public Node sharedCopy(CopyListener listener) {
		SpotLight destination;
		boolean globalIgnoreBounds;
		
		globalIgnoreBounds = Node.globalIgnoreBounds;
		
		Node.globalIgnoreBounds = isIgnoreBounds();
		destination = new SpotLight(enabled, color, location, direction, attenuation, spreadAngle, concentration);
		Node.globalIgnoreBounds = globalIgnoreBounds;
		
		destination.boundsDirty = true;
		
		if (listener != null) {
			listener.onNodeCopied(this, destination, true);
		}
		
		return destination;
	}
	
}
