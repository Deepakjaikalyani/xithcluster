package br.edu.univercidade.cc.xithcluster.serialization.packagers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xith3d.scenegraph.Node;
import br.edu.univercidade.cc.xithcluster.serialization.SerializationHelper;
import br.edu.univercidade.cc.xithcluster.serialization.Serializer;
import br.edu.univercidade.cc.xithcluster.serialization.SerializerRegistry;
import br.edu.univercidade.cc.xithcluster.update.PendingUpdate;
import br.edu.univercidade.cc.xithcluster.update.PendingUpdate.Type;

public class UpdatesPackager extends Serializer<List<PendingUpdate>> {
	
	@SuppressWarnings("unchecked")
	@Override
	protected void doSerialization(List<PendingUpdate> pendingUpdates, DataOutputStream out) throws IOException {
		Node node;
		Type pendingUpdateType;
		
		out.writeInt(pendingUpdates.size());
		for (PendingUpdate pendingUpdate : pendingUpdates) {
			pendingUpdateType = pendingUpdate.getType();
			out.writeInt(pendingUpdateType.ordinal());
			
			// TODO:
			switch (pendingUpdateType) {
			case NODE_ADDED:
			case NODE_REMOVED:
				node = (Node) pendingUpdate.getTarget();
				
				SerializationHelper.writeClass(out, node.getClass());
				SerializationHelper.writeByteArray(out, SerializerRegistry.getSerializer(node.getClass()).serialize(node));
				
				break;
			}
		}
	}
	
	@Override
	protected List<PendingUpdate> doDeserialization(DataInputStream in) throws IOException {
		List<PendingUpdate> pendingUpdates;
		Type pendingUpdateType;
		int numPendingUpdates;
		
		pendingUpdates = new ArrayList<PendingUpdate>();
		numPendingUpdates = in.readInt();
		while (numPendingUpdates-- > 0) {
			pendingUpdateType = Type.getByOrdinal(in.readInt());
			
			// TODO:
			switch (pendingUpdateType) {
			case NODE_ADDED:
			case NODE_REMOVED:
				pendingUpdates.add(new PendingUpdate(pendingUpdateType, SerializerRegistry.getSerializer(SerializationHelper.readClass(in, Node.class)).deserialize(SerializationHelper.readByteArray(in))));
				
				break;
			}
		}
		
		return pendingUpdates;
	}
}
