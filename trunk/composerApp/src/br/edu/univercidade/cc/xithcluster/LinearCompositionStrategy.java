package br.edu.univercidade.cc.xithcluster;

public final class LinearCompositionStrategy implements CompositionStrategy {
	
	@Override
	public int[] compose(int width, int height, int numImages, byte[][] colorAndAlphaBuffer, byte[][] depthBuffer) {
		int[] imageData;
		byte z;
		int k;
		int l;
		
		imageData = new int[width * height];
		for (int i = 0; i < imageData.length; i++) {
			z = Byte.MIN_VALUE;
			k = -1;
			
			for (int j = 0; j < numImages; j++) {
				if (z < depthBuffer[j][i]) {
					k = j;
					z = depthBuffer[j][i];
				}
			}
			
			l = i * 4;
			
			imageData[i] = toInt((byte) 0, colorAndAlphaBuffer[k][l + 1], colorAndAlphaBuffer[k][l + 2], colorAndAlphaBuffer[k][l + 3]);
		}
		
		return imageData;
	}
	
	public static int toInt(byte... bytes) {
		int result = 0;
		
		for (int i = 0; i < 4; i++) {
			result = (result << 8) - Byte.MIN_VALUE + (int) bytes[i];
		}
		
		return result;
	}
	
}
