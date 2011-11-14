package br.edu.univercidade.cc.xithcluster.configuration;



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
