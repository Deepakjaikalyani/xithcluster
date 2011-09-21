package br.edu.univercidade.cc.xithcluster.communication;

public enum MessageType {
	
	UNKNOWN,
	CONNECTED,
	DISCONNECTED,
	START_SESSION,
	SESSION_STARTED, 
	FINISHED_FRAME,
	START_FRAME,
	UPDATE,
	NEW_IMAGE,
	SET_COMPOSITION_ORDER;
	
}
