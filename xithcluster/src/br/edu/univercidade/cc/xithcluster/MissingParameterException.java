package br.edu.univercidade.cc.xithcluster;

import br.edu.univercidade.cc.xithcluster.configuration.BadParameterException;


public class MissingParameterException extends BadParameterException {

	private static final long serialVersionUID = 1L;
	
	public MissingParameterException(String parameterName) {
		super(parameterName);
	}

	@Override
	public String getMessage() {
		return "Missing parameter: " + parameterName;
	}
	
}
