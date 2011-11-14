package br.edu.univercidade.cc.xithcluster.configuration;


public abstract class ConfigurationLoadingException extends Exception {

	private static final long serialVersionUID = 1L;
	
	protected BadParameterException badParameterException;

	public ConfigurationLoadingException(BadParameterException badParameterException) {
		this.badParameterException = badParameterException;
	}
	
	public BadParameterException getBadParameterException() {
		return badParameterException;
	}
	
}
