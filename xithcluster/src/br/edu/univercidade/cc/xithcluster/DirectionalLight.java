package br.edu.univercidade.cc.xithcluster;

import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.Node;

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

	public Light newInstance() {
		return new DirectionalLight(enabled, color, direction);
	}

	@Override
	public void absorbDetails(Node node) {
	}
	
}
