package br.edu.univercidade.cc.xithcluster.utils;

import java.util.Comparator;

public final class ArraysUtils {
	
	public static boolean equals(int[] array1, int[] array2, Comparator<Integer> comparator) {
		if (array1 == null || array2 == null || array1.length != array2.length) {
			throw new IllegalArgumentException();
		}
		
		for (int i = 0; i < array1.length; i++) {
			if (comparator.compare(array1[i], array2[i]) != 0) {
				return false;
			}
		}
		
		return true;
	}
	
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
