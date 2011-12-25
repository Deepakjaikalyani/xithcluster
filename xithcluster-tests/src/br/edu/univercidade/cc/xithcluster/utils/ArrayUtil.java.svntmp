package br.edu.univercidade.cc.xithcluster.utils;

public final class ArrayUtil {
	
	public static float[] normalize(int[] array, float newMinimun, float newMaximum) {
		if (array == null || array.length == 0) {
			throw new IllegalArgumentException();
		}
		
		int realMinimum = Integer.MAX_VALUE, realMaximum = Integer.MIN_VALUE;
		for (int i = 0; i < array.length; i++) {
			if (array[i] < realMinimum) {
				realMinimum = array[i];
			} else if (array[i] > realMaximum) {
				realMaximum = array[i];
			}
		}
		
		int average = (realMinimum + realMaximum) / 2;
		float[] normalizedArray = new float[array.length];
		for (int i = 0; i < array.length; i++) {
			if (array[i] >= average) {
				normalizedArray[i] = newMaximum;
			} else {
				normalizedArray[i] = newMinimun;
			}
		}
		
		return normalizedArray;
	}
	
}
