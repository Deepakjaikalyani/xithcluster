package br.edu.univercidade.cc.xithcluster.util;

import java.util.Comparator;

public final class AssertExtention {
	
	public static <T> void assertEquals(T expected, T actual, Comparator<T> comparator) {
		if (expected == null || actual == null || comparator == null) {
			throw new IllegalArgumentException();
		}
		
		if (comparator.compare(expected, actual) != 0) {
			throw new AssertionError("Expected: " + expected + "\nGot: " + actual);
		}
	}
	
}
