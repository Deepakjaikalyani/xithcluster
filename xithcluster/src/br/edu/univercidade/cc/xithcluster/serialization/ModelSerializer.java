package br.edu.univercidade.cc.xithcluster.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.xith3d.loaders.models.Model;

public class ModelSerializer extends Serializer<Model> implements
		GroupNodeSerializer {

	@Override
	protected void doSerialization(Model model, DataOutputStream out)
			throws IOException {
		SerializationHelper.writeString(out, model.getName());
		out.writeInt(model.numChildren());
	}

	@Override
	protected Model doDeserialization(DataInputStream in) throws IOException {
		Model model;

		model = new Model();
		model.setName(SerializationHelper.readString(in));
		model.setUserData(NUMBER_OF_CHILDREN_USER_DATA, in.readInt());

		return model;
	}

}
