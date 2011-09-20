package br.edu.univercidade.cc.xithcluster.util;

import org.xith3d.scenegraph.Node;

public final class PrintUtils {
	
	private PrintUtils() {
	}
	
	public static String print(Node node) {
		return print(node, 0);
	}
	
	public static String print(Node node, int level) {
		String indent;
		
		indent = (level > 0) ? " |" : "";
		for (int j = 0; j < level; j++) {
			indent += "__";
		}
		
		return indent + "  " + node.getClass().getSimpleName() + ((node.getName() != null && !node.getName().isEmpty()) ? "[\"" + node.getName() + "\"]" : "");
	}
	
}
