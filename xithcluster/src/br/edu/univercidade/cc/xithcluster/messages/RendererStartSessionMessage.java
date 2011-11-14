package br.edu.univercidade.cc.xithcluster.messages;

import br.edu.univercidade.cc.xithcluster.Component;
import br.edu.univercidade.cc.xithcluster.SceneData;

public class RendererStartSessionMessage extends Message {
	
	private SceneData sceneData;
	
	public RendererStartSessionMessage(SceneData sceneData) {
		this.sceneData = sceneData;
	}
	
	@Override
	public void sendTo(Component component) {
		// rendererConnection.write(MessageType.START_SESSION.ordinal());
		// rendererConnection.flush();
		//
		// rendererConnection.write(getRendererIndex(rendererConnection));
		// rendererConnection.write(sceneManager.getScreenSize().width);
		// rendererConnection.write(sceneManager.getScreenSize().height);
		// rendererConnection.write(sceneManager.getTargetFPS());
		// rendererConnection.write(pointOfViewData.length);
		// rendererConnection.write(pointOfViewData);
		// rendererConnection.write(sceneData.length);
		// rendererConnection.write(sceneData);
		// rendererConnection.flush();
	}
	
}
