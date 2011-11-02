package br.edu.univercidade.cc.xithcluster.utils;

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
	
	public static void assertPixelBufferRegion(int[] expectedPixelBuffer, int[] actualPixelBuffer, int x, int y, int width, int height) {
		
	}
	
}
