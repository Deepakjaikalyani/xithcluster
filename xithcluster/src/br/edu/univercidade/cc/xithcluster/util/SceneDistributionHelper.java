package br.edu.univercidade.cc.xithcluster.util;

import org.xith3d.scenegraph.Node;

public final class SceneDistributionHelper {
	
	private static final Object HUD_CONTENT_PANEL_NAME = "HUD ContentPane";

	private SceneDistributionHelper() {
	}
	
	public static boolean isHUDComponent(Node node) {
		if (node == null) { 
			throw new IllegalArgumentException();
		}
		
		if (isHUDContentPanel(node)) {
			return true;
		}
		
		while (node.getParent() != null) {
			node = node.getParent();
			if (isHUDComponent(node)) {
				return true;
			}
		}
		
		return false;
	}

	public static boolean isHUDContentPanel(Node node) {
		if (node == null) { 
			throw new IllegalArgumentException();
		}
		
		return HUD_CONTENT_PANEL_NAME.equals(node.getName());
	}
	
}
