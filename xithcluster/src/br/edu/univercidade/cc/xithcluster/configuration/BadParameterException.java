package br.edu.univercidade.cc.xithcluster.configuration;


public abstract class BadParameterException extends Exception {

	private static final long serialVersionUID = 1L;

	protected String parameterName;
	
	public BadParameterException(String parameterName) {
		if (parameterName == null) {
			throw new IllegalArgumentException();
		}
		
		this.parameterName = parameterName;
	}
	
}
