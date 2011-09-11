package br.edu.univercidade.cc.xithcluster;

import br.edu.univercidade.cc.xithcluster.communication.ComposerNetworkManager;

public class Composer {
	
	private final ComposerNetworkManager composerNetworkManager;

	public Composer() {
		composerNetworkManager = new ComposerNetworkManager(this);
	}
	
	public final static void main(String args[]) {
		new Composer();
	}
	
}
