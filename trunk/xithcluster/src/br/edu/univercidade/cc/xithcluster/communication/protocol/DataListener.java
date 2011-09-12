package br.edu.univercidade.cc.xithcluster.communication.protocol;

public interface DataListener {
	
	void onComplete(ChainedSafeDataHandler contentHandler, Object... data);
	
}
