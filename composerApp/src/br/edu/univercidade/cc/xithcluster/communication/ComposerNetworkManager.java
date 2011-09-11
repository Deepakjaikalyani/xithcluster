package br.edu.univercidade.cc.xithcluster.communication;

import org.apache.log4j.Logger;
import br.edu.univercidade.cc.xithcluster.Composer;
import br.edu.univercidade.cc.xithcluster.communication.protocol.ComposerProtocolHandler;

public final class ComposerNetworkManager {
	
	private final Logger log = Logger.getLogger(ComposerNetworkManager.class);
	
	private final Composer composer;

	private final ComposerProtocolHandler composerProtocolHandler;
	
	public ComposerNetworkManager(Composer composer) {
		this.composer = composer;
		this.composerProtocolHandler = new ComposerProtocolHandler(this);
	}
	
}
