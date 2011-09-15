package br.edu.univercidade.cc.xithcluster;

public final class LinearCompositionStrategy implements CompositionStrategy {
	
	@Override
	public int[] compose(int width, int height, int numImages, byte[][] colorAndAlphaBuffer, byte[][] depthBuffer) {
		int[] argbBuffer;
		byte z;
		int k;
		int i;
		int r;
		int p;
		
		argbBuffer = new int[width * height];
		i = 0;
		// TODO: Solve this mirroring other way!
		for (int y = height - 1; y > 0; y--) {
			r = y * width;
			for (int x = 0; x < width; x++) {
				p = r + x;
				z = Byte.MIN_VALUE;
				k = -1;
				for (int j = 0; j < numImages; j++) {
					if (z < depthBuffer[j][p]) {
						k = j;
						z = depthBuffer[j][p];
					}
				}
				
				argbBuffer[p] = (255 << colorAndAlphaBuffer[k][i + 3]) | (colorAndAlphaBuffer[k][i] << 16) | (colorAndAlphaBuffer[k][i + 1]) << 8 | (colorAndAlphaBuffer[k][i + 2]);
				i += 4;
			}
		}
		
		return argbBuffer;
	}
	
}
