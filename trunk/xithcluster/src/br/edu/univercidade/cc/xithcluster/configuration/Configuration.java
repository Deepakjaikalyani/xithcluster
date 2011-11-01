package br.edu.univercidade.cc.xithcluster.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class Configuration {
	
	public void load(String[] args) throws CommandLineParsingException, PropertiesFileLoadingException, IOException  {
		if (args == null) {
			throw new IllegalArgumentException();
		}
		
		if (args.length == getArgsLength()) {
			try {
				parseFromCommandLine(args);
			} catch (BadParameterException e) {
				throw new CommandLineParsingException(e);
			}
		} else {
			try {
				loadFromProperties(openPropertiesFile());
			} catch (BadParameterException e) {
				throw new PropertiesFileLoadingException(e);
			}
		}
	}
	
	protected abstract int getArgsLength();
	
	private Properties openPropertiesFile() throws IOException {
		Properties properties;
		InputStream in;
		String propertiesFileName;
		
		properties = new Properties();
		
		propertiesFileName = getPropertiesFileName();
		in = getClass().getResourceAsStream("/" + propertiesFileName);
		
		if (in == null) {
			in = new FileInputStream(propertiesFileName);
		}
		
		properties.load(in);
		
		return properties;
	}
	
	protected abstract String getPropertiesFileName();
	
	protected abstract void parseFromCommandLine(String[] args) throws BadParameterException;
	
	protected abstract void loadFromProperties(Properties properties) throws BadParameterException;
	
	protected static String getParameterAndCheckIfNullOrEmptyString(Properties properties, String parameterName) throws BadParameterException {
		String parameterValue;
		
		parameterValue = properties.getProperty(parameterName);
		
		checkIfNullOrEmptyString(parameterName, parameterValue);
		
		return parameterValue;
	}
	
	protected static void checkIfNullOrEmptyString(String parameterName, String parameterValue) throws BadParameterException {
		if (parameterValue == null) {
			throw new MissingParameterException(parameterName);
		} else if (parameterValue.isEmpty()) {
			throw new InvalidParameterException(parameterName);
		}
	}
	
	protected static Integer getParameterAndConvertToIntegerSafely(Properties properties, String parameterName) throws BadParameterException {
		return convertToIntegerSafely(parameterName, properties.getProperty(parameterName));
	}
	
	protected static Integer convertToIntegerSafely(String parameterName, String value) throws BadParameterException {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new InvalidParameterException(parameterName);
		} catch (NullPointerException e) {
			throw new MissingParameterException(parameterName);
		}
	}
	
	protected static Float getParameterAndConvertToFloatSafely(Properties properties, String parameterName) throws BadParameterException {
		return convertToFloatSafely(parameterName, properties.getProperty(parameterName));
	}
	
	protected static Float convertToFloatSafely(String parameterName, String propertyValue) throws BadParameterException {
		try {
			return Float.parseFloat(propertyValue);
		} catch (NumberFormatException e) {
			throw new InvalidParameterException(parameterName);
		} catch (NullPointerException e) {
			throw new MissingParameterException(parameterName);
		}
	}
	
	protected static boolean getParameterAndConvertToBooleanSafely(Properties properties, String parameterName) throws BadParameterException {
		return convertToBooleanSafely(parameterName, properties.getProperty(parameterName));
	}
	
	private static boolean convertToBooleanSafely(String parameterName, String value) throws BadParameterException {
		try {
			return Boolean.parseBoolean(value);
		} catch (NumberFormatException e) {
			throw new InvalidParameterException(parameterName);
		} catch (NullPointerException e) {
			throw new MissingParameterException(parameterName);
		}
	}
	
	protected static <T extends Enum<?>> T getParameterAndConvertToEnumSafely(Properties properties, String parameterName, T[] enumerations) throws BadParameterException {
		return convertToEnumSafely(parameterName, properties.getProperty(parameterName), enumerations);
	}
	
	protected static <T extends Enum<?>> T convertToEnumSafely(String parameterName, String value, T[] enumerations) throws BadParameterException {
		if (enumerations == null || enumerations.length == 0) {
			throw new IllegalArgumentException();
		}
		
		if (value == null || value.isEmpty()) {
			throw new MissingParameterException(parameterName);
		}
		
		for (T enumeration : enumerations) {
			if (enumeration.name().equals(value)) {
				return enumeration;
			}
		}
		
		throw new InvalidParameterException(parameterName);
	}
	
}