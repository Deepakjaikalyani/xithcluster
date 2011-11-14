package br.edu.univercidade.cc.xithcluster.messages;

import br.edu.univercidade.cc.xithcluster.Component;
import br.edu.univercidade.cc.xithcluster.update.UpdateData;

public class UpdateMessage extends Message {
	
	private UpdateData updateData;
	
	public UpdateMessage(UpdateData updateData) {
		this.updateData = updateData;
	}
	
	@Override
	public void sendTo(Component component) {
		// rendererConnection.write(MessageType.UPDATE.ordinal());
		// rendererConnection.flush();
		// rendererConnection.write(updateData);
		// rendererConnection.flush();
	}
	
}
