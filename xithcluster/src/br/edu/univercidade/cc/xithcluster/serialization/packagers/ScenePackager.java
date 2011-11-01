package br.edu.univercidade.cc.xithcluster.serialization.packagers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Node;
import br.edu.univercidade.cc.xithcluster.serialization.GroupNodeSerializer;
import br.edu.univercidade.cc.xithcluster.serialization.SerializationHelper;
import br.edu.univercidade.cc.xithcluster.serialization.Serializer;
import br.edu.univercidade.cc.xithcluster.serialization.SerializerRegistry;
import br.edu.univercidade.cc.xithcluster.utils.PrintUtils;

public class ScenePackager extends Serializer<BranchGroup> {
	
	private Logger log = Logger.getLogger(ScenePackager.class);
	
	private SceneSerializationTraversal sceneSerializationTraversal = new SceneSerializationTraversal();
	
	@Override
	protected void doSerialization(BranchGroup root, DataOutputStream out) throws IOException {
		sceneSerializationTraversal.clear();
		
		root.traverse(sceneSerializationTraversal);
		
		sceneSerializationTraversal.writeTo(out);
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
			return null;
		}
	}
	
	private Node deserializeNode(DataInputStream in) throws IOException {
		return deserializeNode(in, 0);
	}
	
	private Node deserializeNode(DataInputStream in, int level) throws IOException {
		Node node;
		Integer numChildren;
		Class<? extends Node> nodeClass;
		byte[] nodeData;
		
		nodeClass = SerializationHelper.readClass(in, Node.class);
		nodeData = SerializationHelper.readByteArray(in);
		
		node = (Node) SerializerRegistry.getSerializer(nodeClass).deserialize(nodeData);
		
		log.info(PrintUtils.print(node, level)); // + " (" + nodeData.length + " bytes)");
		
		if (node instanceof GroupNode) {
			numChildren = (Integer) node.getUserData(GroupNodeSerializer.NUMBER_OF_CHILDREN_USER_DATA);
			
			for (int i = 0; i < numChildren; i++) {
				((GroupNode) node).addChild(deserializeNode(in, level + 1));
			}
			
			node.setUserData(GroupNodeSerializer.NUMBER_OF_CHILDREN_USER_DATA, null);
		}
		
		return node;
	}
	
}

