package br.edu.univercidade.cc.xithcluster.utils;

import java.util.Comparator;

public final class ArraysExtention {
	
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
	
}
