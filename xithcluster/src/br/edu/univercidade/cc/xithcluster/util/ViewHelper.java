package br.edu.univercidade.cc.xithcluster.util;

import org.xith3d.scenegraph.View;

public final class ViewHelper {
	
	private ViewHelper() {
	}
	
	public static void copy(View ori, View src) {
		src.setPosition(ori.getPosition());
		src.setCenterOfView(ori.getCenterOfView());
		src.setFacingDirection(ori.getFacingDirection());
		src.setFieldOfView(ori.getFieldOfView());
		src.setBackClipDistance(ori.getBackClipDistance());
		src.setFrontClipDistance(ori.getFrontClipDistance());
	}
	
}
