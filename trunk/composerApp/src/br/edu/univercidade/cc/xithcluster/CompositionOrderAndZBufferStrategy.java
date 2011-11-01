package br.edu.univercidade.cc.xithcluster;

import java.util.List;

public final class CompositionOrderAndZBufferStrategy implements CompositionStrategy {
	
	private int width = -1;
	
	private int height = -1;
	
	private int pixelBufferSize;
	
	private int lastComponentIndex;
	
	private int rowOffset;
	
	private int[] pixelBuffer;
	
	@Override
	public int[] compose(int width, int height, List<byte[]> colorAndAlphaBuffers, List<float[]> depthBuffers) {
		int numberOfImages;
		int lastComponentIndexRead;
		int lastZValueRead;
		int componentIndex;
		int zIndex;
		int i;
		int red;
		int green;
		int blue;
		//int alpha;
		int selectedImageIndex;
		float z;
		float lowerZ;
		
		if (colorAndAlphaBuffers == null || depthBuffers == null) {
			throw new IllegalArgumentException();
		}
		
		if (colorAndAlphaBuffers.size() != depthBuffers.size()) {
			throw new IllegalArgumentException();
		}
		
		if (this.width != width || this.height != height) {
			pixelBufferSize = width * height;
			lastComponentIndex = pixelBufferSize << 2;
			rowOffset = width << 2;
			
			createPixelBuffer();
		}
		
		numberOfImages = colorAndAlphaBuffers.size();
		lastComponentIndexRead = lastComponentIndex;
		lastZValueRead = pixelBufferSize;
		i = 0;
		for (int row = 0; row < height; row++) {
			lastComponentIndexRead -= rowOffset;
			lastZValueRead -= width;
			componentIndex = lastComponentIndexRead;
			zIndex = lastZValueRead;
			for (int column = 0; column < width; column++) {
				lowerZ = Integer.MAX_VALUE;
				selectedImageIndex = -1;
				for (int imageIndex = 0; imageIndex < numberOfImages; imageIndex++) {
					z = depthBuffers.get(imageIndex)[zIndex];
					if (z < lowerZ) {
						selectedImageIndex = imageIndex;
						lowerZ = z;
					}
				}
				zIndex++;
				
				red = colorAndAlphaBuffers.get(selectedImageIndex)[componentIndex++];
				green = colorAndAlphaBuffers.get(selectedImageIndex)[componentIndex++];
				blue = colorAndAlphaBuffers.get(selectedImageIndex)[componentIndex++];
				// TODO: Use alpha component!
				//alpha = colorAndAlphaBuffer[selectedImageIndex][pixelIndex++];
				componentIndex++;
				
				pixelBuffer[i++] = 0xff000000 | ((red & 0x000000ff) << 16) | ((green & 0x000000ff) << 8) | (blue & 0x000000ff);
			}
		}
		
		return pixelBuffer;
	}

	private void createPixelBuffer() {
		pixelBuffer = new int[pixelBufferSize];
	}
	
}
