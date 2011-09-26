package br.edu.univercidade.cc.xithcluster;

public final class SimpleCompositionStrategy implements CompositionStrategy {
	
	@Override
	public int[] compose(int width, int height, int numImages, byte[][] colorAndAlphaBuffer, byte[][] depthBuffer) {
		/*int[] argbBuffer;
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
		
		return argbBuffer;*/
		
		int[] pixelInts = new int[width * height];
		
		int p = width * height * 4;
		int q; // Index into ByteBuffer
		int i = 0; // Index into target int[]
		int w4 = width * 4; // Number of bytes in each row
		for (int row = 0; row < height; row++) {
			p -= w4;
			q = p;
			for (int col = 0; col < width; col++) {
				int iR = colorAndAlphaBuffer[0][q++];
				int iG = colorAndAlphaBuffer[0][q++];
				int iB = colorAndAlphaBuffer[0][q++];
				int iA = colorAndAlphaBuffer[0][q++];
				pixelInts[i++] = ((iA & 0x000000FF) << 0xFF000000) | ((iR & 0x000000FF) << 16) | ((iG & 0x000000FF) << 8) | (iB & 0x000000FF);
			}
		}
		
		return pixelInts;
	}
	
}
