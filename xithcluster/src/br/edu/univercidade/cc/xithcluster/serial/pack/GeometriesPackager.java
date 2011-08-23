package br.edu.univercidade.cc.xithcluster.serial.pack;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.traversal.TraversalCallback;
import br.edu.univercidade.cc.xithcluster.serial.GroupNodeSerializer;
import br.edu.univercidade.cc.xithcluster.serial.SerializationHelper;
import br.edu.univercidade.cc.xithcluster.serial.Serializer;
import br.edu.univercidade.cc.xithcluster.serial.SerializerRegistry;

public class GeometriesPackager extends Serializer<BranchGroup> {
	
	private static class SerializationTraversal implements TraversalCallback {
		
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
			int bufferGrowth;
			
			try {
				bufferGrowth = out.size();
				
				SerializationHelper.writeClass(out, node.getClass());
				SerializationHelper.writeByteArray(out, SerializerRegistry.getSerializer(node.getClass()).serialize(node));
				
				// DEBUG:
				printNode(node, getNodeLevel(node));
				
				bufferGrowth = out.size() - bufferGrowth;
				
				// DEBUG:
				System.out.println(" (" + bufferGrowth + " bytes)");
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
	
	private SerializationTraversal serializationTraversal = new SerializationTraversal();
	
	@Override
	protected void doSerialization(BranchGroup root, DataOutputStream out) throws IOException {
		serializationTraversal.clear();
		
		root.traverse(serializationTraversal);
		
		serializationTraversal.writeTo(out);
	}
	
	@Override
	protected BranchGroup doDeserialization(DataInputStream in) throws IOException {
		Node firstUnpackedNode;
		
		firstUnpackedNode = deserializeNode(in);
		
		if (firstUnpackedNode != null) {
			if (firstUnpackedNode instanceof BranchGroup) {
				return (BranchGroup) firstUnpackedNode;
			} else {
				// TODO:
				throw new RuntimeException("The first unpacked node should be an instance of " + BranchGroup.class.getName());
			}
		} else {
			// TODO:
			throw new RuntimeException("The first unpacked node should not be null");
		}
	}
	
	private Node deserializeNode(DataInputStream in) throws IOException {
		return deserializeNode(in, 0);
	}
	
	private Node deserializeNode(DataInputStream in, int level) throws IOException {
		Node node;
		Integer numChildren;
		
		node = (Node) SerializerRegistry.getSerializer(SerializationHelper.readClass(in, Node.class)).deserialize(SerializationHelper.readByteArray(in));
		
		// DEBUG:
		printNode(node, level);
		System.out.println("(? bytes)");
		
		if (node instanceof GroupNode) {
			numChildren = (Integer) node.getUserData(GroupNodeSerializer.NUMBER_OF_CHILDREN_USER_DATA);
			
			for (int i = 0; i < numChildren; i++) {
				((GroupNode) node).addChild(deserializeNode(in, level + 1));
			}
		}
		
		return node;
	}

	static void printNode(Node node, int level) {
		String indent;
		
		indent = (level > 0) ? " |" : "";
		for (int j = 0; j < level; j++) {
			indent += "__";
		}
		
		System.out.print(indent + "  " + node.getClass().getSimpleName() + ((node.getName() != null && !node.getName().isEmpty()) ? "[\"" + node.getName() + "\"]" : ""));
	}
}
