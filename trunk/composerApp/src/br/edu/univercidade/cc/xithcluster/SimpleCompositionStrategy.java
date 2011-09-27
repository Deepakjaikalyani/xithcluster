package br.edu.univercidade.cc.xithcluster;

public final class SimpleCompositionStrategy implements CompositionStrategy {
	
	@Override
	public int[] compose(int width, int height, byte[][] colorAndAlphaBuffer, byte[][] depthBuffer) {
		int numberOfImages;
		int[] argbBuffer;
		int lastPixelRead;
		int pixelIndex;
		int i;
		int rowOffset;
		int red;
		int green;
		int blue;
		int alpha;
		int selectedImageIndex;
		int z;
		int greaterZ;
		
		if (colorAndAlphaBuffer.length != depthBuffer.length) {
			throw new IllegalArgumentException();
		}
		
		numberOfImages = colorAndAlphaBuffer.length;
		argbBuffer = new int[width * height];
		lastPixelRead = width * height * 4;
		rowOffset = width * 4;
		i = 0;
		for (int row = 0; row < height; row++) {
			lastPixelRead -= rowOffset;
			pixelIndex = lastPixelRead;
			for (int column = 0; column < width; column++) {
				greaterZ = Integer.MIN_VALUE;
				selectedImageIndex = -1;
				for (int imageIndex = 0; imageIndex < numberOfImages; imageIndex++) {
					z = depthBuffer[imageIndex][i];
					if (z > greaterZ) {
						selectedImageIndex = imageIndex;
						greaterZ = z;
					}
				}
				
				red = colorAndAlphaBuffer[selectedImageIndex][pixelIndex++];
				green = colorAndAlphaBuffer[selectedImageIndex][pixelIndex++];
				blue = colorAndAlphaBuffer[selectedImageIndex][pixelIndex++];
				alpha = colorAndAlphaBuffer[selectedImageIndex][pixelIndex++];
				
				argbBuffer[i++] = 0xff000000 | ((red & 0x000000ff) << 16) | ((green & 0x000000ff) << 8) | (blue & 0x000000ff);
			}
		}
		
		return argbBuffer;
	}
	
}
