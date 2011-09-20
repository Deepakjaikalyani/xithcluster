package br.edu.univercidade.cc.xithcluster.serialization.packagers;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.log4j.Logger;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.traversal.TraversalCallback;
import br.edu.univercidade.cc.xithcluster.serialization.SerializationHelper;
import br.edu.univercidade.cc.xithcluster.serialization.SerializerRegistry;
import br.edu.univercidade.cc.xithcluster.util.PrintUtils;

class SceneSerializationTraversal implements TraversalCallback {
	
	private Logger log = Logger.getLogger(SceneSerializationTraversal.class);
	
	private ByteArrayOutputStream buffer;
	
	private DataOutputStream out;
	
	public void clear() {
		buffer = new ByteArrayOutputStream();
		out = new DataOutputStream(buffer);
	}
	
	public void writeTo(OutputStream out) throws IOException {
		buffer.writeTo(out);
	}
	
	@SuppressWarnings("unchecked")
	private boolean serializeNode(Node node) {
		String toString;
		int bufferGrowth;
		
		try {
			SerializationHelper.writeClass(out, node.getClass());
			
			bufferGrowth = out.size();
			
			SerializationHelper.writeByteArray(out, SerializerRegistry.getSerializer(node.getClass()).serialize(node));
			
			toString = PrintUtils.print(node, getNodeLevel(node));
			
			bufferGrowth = out.size() - bufferGrowth;
			
			log.info(toString + " (" + bufferGrowth + " bytes)");
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error serializing node", e);
		}
		
		return true;
	}
	
	private int getNodeLevel(Node node) {
		int c;
		Node currentNode;
		
		c = 0;
		currentNode = node;
		while (currentNode.getParent() != null) {
			currentNode = currentNode.getParent();
			c++;
		}
		
		return c;
	}

	@Override
	public boolean traversalOperation(Node node) {
		return serializeNode(node);
	}
	
	@Override
	public boolean traversalCheckGroup(GroupNode groupNode) {
		return true;
	}
	
}