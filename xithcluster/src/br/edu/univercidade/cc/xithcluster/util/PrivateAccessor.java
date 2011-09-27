package br.edu.univercidade.cc.xithcluster.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PrivateAccessor {
	
	public static Object getPrivateField(Object src, String fieldName) {
		Class<?> clazz;
		Field field;
		
		if (src == null || fieldName == null)
			throw new IllegalArgumentException();
		
		clazz = src.getClass();
		field = null;
		while (clazz != Object.class) {
			field = findField(clazz, fieldName);
			
			if (field != null) {
				break;
			} else {
				clazz = clazz.getSuperclass();
			}
		}
		
		if (field != null) {
			field.setAccessible(true);
			
			try {
				return field.get(src);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
		return null;
	}
	
	private static Field findField(Class<?> clazz, String fieldName) {
		Field fields[] = clazz.getDeclaredFields();
		
		for (Field field : fields) {
			if (fieldName.equals(field.getName())) {
				return field;
			}
		}
		
		return null;
	}
	
	public static boolean setPrivateField(Object src, String fieldName, Object value) {
		Class<?> clazz;
		Field field;
		
		if (src == null || fieldName == null)
			throw new IllegalArgumentException();
		
		clazz = src.getClass();
		field = null;
		while (clazz != Object.class) {
			field = findField(clazz, fieldName);
			
			if (field != null) {
				break;
			} else {
				clazz = clazz.getSuperclass();
			}
		}
		
		if (field != null) {
			field.setAccessible(true);
			
			try {
				field.set(src, value);
				
				return true;
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
		return false;
	}
	
	public static Object invokePrivateMethod(Object o, String methodName, Object... params) {
		if (o == null || methodName == null || params == null) {
			throw new IllegalArgumentException();
		}
		
		final Method methods[] = o.getClass().getDeclaredMethods();
		for (int i = 0; i < methods.length; ++i) {
			if (methodName.equals(methods[i].getName())) {
				try {
					methods[i].setAccessible(true);
					return methods[i].invoke(o, params);
				} catch (IllegalAccessException ex) {
					throw new RuntimeException("IllegalAccessException accessing " + methodName);
				} catch (InvocationTargetException ite) {
					throw new RuntimeException("InvocationTargetException accessing " + methodName);
				}
			}
		}
		
		return null;
	}
}