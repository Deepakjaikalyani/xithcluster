package br.edu.univercidade.cc.xithcluster;

public final class SimpleCompositionStrategy implements CompositionStrategy {
	
	@Override
	public int[] compose(int width, int height, int numImages, byte[][] colorAndAlphaBuffer, byte[][] depthBuffer) {
		int[] argbBuffer;
		int i;
		int imageIndex;
		int pixelIndex;
		int offset;
		byte z;
		byte greaterZ;
		byte red, green, blue, alpha;
		
		argbBuffer = new int[width * height];
		i = 0;
		// mirroring
		for (int y = height - 1; y >= 0; y--) {
			offset = y * width;
			for (int x = 0; x < width; x++) {
				pixelIndex = offset + x;
				greaterZ = Byte.MIN_VALUE;
				imageIndex = -1;
				for (int j = 0; j < numImages; j++) {
					z = depthBuffer[j][pixelIndex];
					if (z > greaterZ) {
						imageIndex = j;
						greaterZ = z;
					}
				}
				
				red = colorAndAlphaBuffer[imageIndex][i];
				green = colorAndAlphaBuffer[imageIndex][i + 1];
				blue = colorAndAlphaBuffer[imageIndex][i + 2];
				alpha = colorAndAlphaBuffer[imageIndex][i + 3];
				
				argbBuffer[pixelIndex] = (255 << alpha) | (red << 16) | (green << 8) | blue;
				
				i += 4;
			}
		}
		
		return argbBuffer;
	}
	
}
