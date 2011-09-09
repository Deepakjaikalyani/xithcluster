package br.edu.univercidade.cc.xithcluster.communication.protocol;

public enum RecordType {
	
	START_SESSION,
	GET_FRAMES_TO_SKIP,
	START_FRAME,
	UPDATE,
	NEW_IMAGE,
	FRAME_FINISHED,
	SESSION_STARTED, 
	UNKNOWN;
	
}
