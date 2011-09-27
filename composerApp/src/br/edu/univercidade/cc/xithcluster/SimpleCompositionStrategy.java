package br.edu.univercidade.cc.xithcluster;

public final class SimpleCompositionStrategy implements CompositionStrategy {
	
	@Override
	public int[] compose(int width, int height, byte[][] colorAndAlphaBuffer, float[][] depthBuffer) {
		int numberOfImages;
		int imageSize;
		int[] argbBuffer;
		int lastPixelRead;
		int lastZValueRead;
		int pixelIndex;
		int zIndex;
		int i;
		int rowOffset;
		int red;
		int green;
		int blue;
		//int alpha;
		int selectedImageIndex;
		float z;
		float lowerZ;
		
		if (colorAndAlphaBuffer.length != depthBuffer.length) {
			throw new IllegalArgumentException();
		}
		
		numberOfImages = colorAndAlphaBuffer.length;
		imageSize = width * height;
		argbBuffer = new int[imageSize];
		lastPixelRead = imageSize * 4;
		lastZValueRead = imageSize;
		rowOffset = width * 4;
		i = 0;
		for (int row = 0; row < height; row++) {
			lastPixelRead -= rowOffset;
			lastZValueRead -= width;
			pixelIndex = lastPixelRead;
			zIndex = lastZValueRead;
			for (int column = 0; column < width; column++) {
				lowerZ = Integer.MAX_VALUE;
				selectedImageIndex = -1;
				for (int imageIndex = 0; imageIndex < numberOfImages; imageIndex++) {
					z = depthBuffer[imageIndex][zIndex];
					if (z < lowerZ) {
						selectedImageIndex = imageIndex;
						lowerZ = z;
					}
				}
				zIndex++;
				
				red = colorAndAlphaBuffer[selectedImageIndex][pixelIndex++];
				green = colorAndAlphaBuffer[selectedImageIndex][pixelIndex++];
				blue = colorAndAlphaBuffer[selectedImageIndex][pixelIndex++];
				// TODO: Use alpha component!
				//alpha = colorAndAlphaBuffer[selectedImageIndex][pixelIndex++];
				pixelIndex++;
				
				argbBuffer[i++] = 0xff000000 | ((red & 0x000000ff) << 16) | ((green & 0x000000ff) << 8) | (blue & 0x000000ff);
			}
		}
		
		return argbBuffer;
	}
	
}
