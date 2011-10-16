package br.edu.univercidade.cc.xithcluster;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public abstract class ConfigurationReader {
	
	public abstract void parseCommandLine(String args[]) throws RuntimeException;
	
	public abstract void readPropertiesFile() throws FileNotFoundException, IOException;
	
	protected static String getParameterAndCheckIfNullOrEmptyString(Properties properties, String parameterName) {
		String parameterValue;
		
		parameterValue = properties.getProperty(parameterName);
		
		checkIfNullOrEmptyString(parameterName, parameterValue);
		
		return parameterValue;
	}

	protected static void checkIfNullOrEmptyString(String parameterName, String parameterValue) {
		if (parameterValue == null) {
			throw new RuntimeException("Missing parameter: " + parameterName);
		} else if (parameterValue.isEmpty()) {
			throw new RuntimeException("Empty parameter: " + parameterName);
		}
	}

	protected static Integer getParameterAndConvertToIntegerSafely(Properties properties, String parameterName) {
		return convertToIntegerSafely(parameterName, properties.getProperty(parameterName));
	}

	protected static Integer convertToIntegerSafely(String parameterName, String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new RuntimeException("Invalid integer parameter: " + parameterName);
		} catch (NullPointerException e) {
			throw new RuntimeException("Missing parameter: " + parameterName);
		}
	}

	protected static Float getParameterAndConvertToFloatSafely(Properties properties, String parameterName) {
		return convertToFloatSafely(parameterName, properties.getProperty(parameterName));
	}

	protected static Float convertToFloatSafely(String parameterName, String propertyValue) {
		try {
			return Float.parseFloat(propertyValue);
		} catch (NumberFormatException e) {
			throw new RuntimeException("Invalid floating point parameter: " + parameterName);
		} catch (NullPointerException e) {
			throw new RuntimeException("Missing parameter: " + parameterName);
		}
	}

	protected static <T extends Enum<?>> T getParameterAndConvertToEnumSafely(Properties properties, String parameterName, T[] enumerations) {
		return convertToEnumSafely(parameterName, properties.getProperty(parameterName), enumerations);
	}

	protected static <T extends Enum<?>> T convertToEnumSafely(String parameterName, String value, T[] enumerations) {
		if (enumerations == null || enumerations.length == 0) {
			throw new IllegalArgumentException();
		}
		
		if (value == null || value.isEmpty()) {
			throw new RuntimeException("Missing parameter: " + parameterName); 
		}
		
		for (T enumeration : enumerations) {
			if (enumeration.name().equals(value)) {
				return enumeration;
			}
		}
		
		throw new RuntimeException("Invalid '" + enumerations[0].getClass().getSimpleName() + "' parameter: " + parameterName);
	}
	
}