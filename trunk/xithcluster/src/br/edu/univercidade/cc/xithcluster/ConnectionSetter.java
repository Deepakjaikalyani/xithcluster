package br.edu.univercidade.cc.xithcluster;

import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.traversal.TraversalCallback;
import org.xsocket.connection.INonBlockingConnection;

class ConnectionSetter implements TraversalCallback {
	
	public static final String CONNECTION_USER_DATA = "renderer";

	private static final ConnectionSetter rendererSetter = new ConnectionSetter();
	
	private INonBlockingConnection renderer;
	
	@Override
	public boolean traversalOperation(Node arg0) {
		arg0.setUserData(CONNECTION_USER_DATA, renderer);
		return true;
	}
	
	@Override
	public boolean traversalCheckGroup(GroupNode arg0) {
		return true;
	}
	
	public static void setConnection(BranchGroup arg0, INonBlockingConnection arg1) {
		rendererSetter.renderer = arg1;
		arg0.traverse(rendererSetter);
	}
}