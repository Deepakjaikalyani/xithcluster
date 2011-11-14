package br.edu.univercidade.cc.xithcluster.configuration;

public class PropertiesFileLoadingException extends ConfigurationLoadingException {

	private static final long serialVersionUID = 1L;
	
	public PropertiesFileLoadingException(BadParameterException e) {
		super(e);
	}
	
}
