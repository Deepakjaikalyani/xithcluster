package br.edu.univercidade.cc.xithcluster.configuration;

public class CommandLineParsingException extends ConfigurationLoadingException {

	private static final long serialVersionUID = 1L;
	
	public CommandLineParsingException(BadParameterException e) {
		super(e);
	}
	
}
