package br.edu.univercidade.cc.xithcluster;

public final class SimpleCompositionStrategy implements CompositionStrategy {
	
	@Override
	public int[] compose(int width, int height, int numImages, byte[][] colorAndAlphaBuffer, byte[][] depthBuffer) {
		int[] pixels;
		int lastPixelRead;
		int pixelIndex;
		int i;
		int rowOffset;
		int red;
		int green;
		int blue;
		int alpha;
		int selectedImage;
		int z;
		int greaterZ;
		
		pixels = new int[width * height];
		lastPixelRead = width * height * 4;
		rowOffset = width * 4;
		i = 0;
		for (int row = 0; row < height; row++) {
			lastPixelRead -= rowOffset;
			pixelIndex = lastPixelRead;
			for (int column = 0; column < width; column++) {
				greaterZ = Integer.MIN_VALUE;
				selectedImage = -1;
				for (int image = 0; image < numImages; image++) {
					z = depthBuffer[image][i];
					if (z > greaterZ) {
						selectedImage = image;
						greaterZ = z;
					}
				}
				
				red = colorAndAlphaBuffer[selectedImage][pixelIndex++];
				green = colorAndAlphaBuffer[selectedImage][pixelIndex++];
				blue = colorAndAlphaBuffer[selectedImage][pixelIndex++];
				alpha = colorAndAlphaBuffer[selectedImage][pixelIndex++];
				
				pixels[i++] = 0xff000000 | ((red & 0x000000ff) << 16) | ((green & 0x000000ff) << 8) | (blue & 0x000000ff);
			}
		}
		
		return pixels;
	}
	
}
