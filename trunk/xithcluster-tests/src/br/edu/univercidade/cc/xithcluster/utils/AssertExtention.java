package br.edu.univercidade.cc.xithcluster.utils;

import java.util.Comparator;
import br.edu.univercidade.cc.xithcluster.composition.PixelBuffer;

public final class AssertExtention {
	
	public static void assertNonNull(Object... args) {
		if (args == null)
			throw new AssertionError();
		
		for (Object arg : args)
			if (arg == null)
				throw new AssertionError();
	}
	
	public static <T> void assertEquals(T expected, T actual, Comparator<T> comparator) {
		if (expected == null || actual == null || comparator == null) {
			throw new IllegalArgumentException();
		}
		
		if (comparator.compare(expected, actual) != 0) {
			throw new AssertionError("Expected: " + expected + "\nGot: " + actual);
		}
	}
	
	public static void assertPixelBufferRegion(PixelBuffer expectedPixelBuffer, PixelBuffer actualPixelBuffer, int x, int y, int width, int height) {
		assertPixelBufferRegion(expectedPixelBuffer, actualPixelBuffer, x, y, width, height, new Comparator<Integer>() {
			
			@Override
			public int compare(Integer o1, Integer o2) {
				if (o1 == null || o2 == null) {
					throw new IllegalArgumentException();
				}
				
				return o1.compareTo(o2);
			}
			
		});
	}
	
	public static void assertPixelBufferRegion(PixelBuffer expectedPixelBuffer, PixelBuffer actualPixelBuffer, int x, int y, int width, int height, Comparator<Integer> comparator) {
		if (expectedPixelBuffer == null || actualPixelBuffer == null) {
			throw new IllegalArgumentException();
		}
		
		int[] expectedPixelRegion = expectedPixelBuffer.getPixelRegion(x, y, width, height);
		int[] actualPixelRegion = actualPixelBuffer.getPixelRegion(x, y, width, height);
		
		if (!ArraysExtention.equals(expectedPixelRegion, actualPixelRegion, comparator))
			throw new AssertionError("Expected:\n" + printPixelRegion(width, height, expectedPixelRegion) + "\nGot:\n" + printPixelRegion(width, height, actualPixelRegion));
	}
	
	private static String printPixelRegion(int width, int height, int[] pixelRegion) {
		String result = "[";
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				result += pixelRegion[x] + ", ";
			}
			result += "\n";
		}
		result = result.substring(0, result.length() - 3) + "] - (" + width + "x" + height + ")";
		
		return result;
	}
	
}
