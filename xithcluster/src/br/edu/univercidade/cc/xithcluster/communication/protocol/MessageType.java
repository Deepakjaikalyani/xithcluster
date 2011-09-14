package br.edu.univercidade.cc.xithcluster.communication.protocol;

public enum MessageType {
	
	UNKNOWN,
	START_SESSION,
	GET_FRAMES_TO_SKIP,
	START_FRAME,
	UPDATE,
	NEW_IMAGE,
	FINISHED_FRAME,
	SESSION_STARTED, 
	SET_COMPOSITION_ORDER;
	
}
