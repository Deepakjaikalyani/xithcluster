package br.edu.univercidade.cc.xithcluster.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PrivateAccessor {
	
	public static Object getPrivateField(Object src, String fieldName) {
		Field field;
		
		if (src == null || fieldName == null) {
			throw new IllegalArgumentException();
		}
		
		field = findFieldOnClassHierarchy(src, fieldName);
		
		if (field != null) {
			field.setAccessible(true);
			
			try {
				return field.get(src);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(buildFieldAccessErrorMessage(src, fieldName, field), e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(buildFieldAccessErrorMessage(src, fieldName, field), e);
			}
		}
		
		return null;
	}
	
	private static Field findFieldOnClassHierarchy(Object src, String fieldName) {
		Class<?> currentClass;
		Field field;
		
		currentClass = src.getClass();
		field = null;
		do {
			try {
				field = currentClass.getDeclaredField(fieldName);
				break;
			} catch (SecurityException e) {
				// TODO:
				throw new RuntimeException(e);
			} catch (NoSuchFieldException e) {
				currentClass = currentClass.getSuperclass();
			}
		} while (currentClass != Object.class);
		
		return field;
	}
	
	public static void setPrivateField(Object src, String fieldName, Object value) {
		Field field;
		
		if (src == null || fieldName == null) {
			throw new IllegalArgumentException();
		}
		
		field = findFieldOnClassHierarchy(src, fieldName);
		
		if (field != null) {
			field.setAccessible(true);
			
			try {
				field.set(src, value);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(buildFieldAccessErrorMessage(src, fieldName, field), e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(buildFieldAccessErrorMessage(src, fieldName, field), e);
			}
		}
	}
	
	private static String buildFieldAccessErrorMessage(Object src, String fieldName, Field field) {
		String errorMessage;
		
		errorMessage = "Error accessing field '" + fieldName + "' on class '" + src.getClass().getName() + "'"; 
		
		if (src.getClass() != field.getDeclaringClass()) {
			errorMessage += " (declaring class: '" + field.getDeclaringClass().getName() + "')";
		}
		
		return errorMessage;
	}
	
	public static Object invokePrivateMethod(Object src, String methodName, Object... params) {
		if (src == null || methodName == null || params == null) {
			throw new IllegalArgumentException();
		}
		
		Method method = findMethodOnHierarchy(src, methodName, params);
		
		if (method == null) {
			// TODO:
			throw new RuntimeException("Method '" + methodName + "' not found on class '" + src.getClass().getName() + "' or in any of its superclasses");
		}
			
		try {
			method.setAccessible(true);
			
			return method.invoke(src, params);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(buildMethodAccessErrorMessage(src, methodName, method), e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(buildMethodAccessErrorMessage(src, methodName, method), e);
		}
	}

	private static String buildMethodAccessErrorMessage(Object src, String methodName, Method method) {
		String errorMessage;
		
		errorMessage = "Error accessing method '" + methodName + "' on class '" + src.getClass().getName() + "'"; 
		
		if (src.getClass() != method.getDeclaringClass()) {
			errorMessage += " (declaring class: '" + method.getDeclaringClass().getName() + "')";
		}
		
		return errorMessage;
	}
	
	private static Method findMethodOnHierarchy(Object src, String methodName, Object... params) {
		Class<?> currentClass;
		Method classMethod;
		
		currentClass = src.getClass();
		classMethod = null;
		do {
			try {
				if (params.length > 0) {
					classMethod = currentClass.getDeclaredMethod(methodName, getTypes(params));
				} else {
					classMethod = currentClass.getDeclaredMethod(methodName);
				}
				break;
			} catch (SecurityException e) {
				// TODO:
				throw new RuntimeException(e);
			} catch (NoSuchMethodException e) {
				currentClass = currentClass.getSuperclass();
			}
		} while (currentClass != Object.class);
		
		return classMethod;
	}

	private static Class<?>[] getTypes(Object[] params) {
		Class<?>[] types;

		if (params == null || params.length == 0) {
			throw new IllegalArgumentException();
		}
		
		types = new Class<?>[params.length];
		for (int i = 0; i < params.length; i++) {
			if (params[i] != null) {
				types[i] = params[i].getClass();
			} else {
				types[i] = null;
			}
		}
		
		return types;
	}
	
}