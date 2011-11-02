package br.edu.univercidade.cc.xithcluster.composition;

public class ColorAndAlphaBuffer {
	
	private int size;
	
	private byte[] data;
	
	public static ColorAndAlphaBuffer wrap(byte[] colorAndAlphaBufferData) {
		if (colorAndAlphaBufferData == null || colorAndAlphaBufferData.length == 0) {
			// TODO:
			throw new IllegalArgumentException();
		}
		
		return new ColorAndAlphaBuffer(colorAndAlphaBufferData);
	}
	
	private ColorAndAlphaBuffer(byte[] data) {
		this.data = data;
		size = this.data.length;
	}
	
	public int getARGB(int pixelIndex) {
		int componentIndex = getFirstComponentIndex(pixelIndex);
		
		if (componentIndex > (size - 4)) {
			throw new ArrayIndexOutOfBoundsException("Trying to get pixel " + componentIndex + " but the buffer has only " + size + " pixels");
		}
		
		// TODO: Use alpha component!
		// int alpha = data[componentIndex];
		int red = data[componentIndex + 1];
		int green = data[componentIndex + 2];
		int blue = data[componentIndex + 3];
		
		return 0xff000000 | ((red & 0x000000ff) << 16) | ((green & 0x000000ff) << 8) | (blue & 0x000000ff);
	}
	
	private int getFirstComponentIndex(int pixelIndex) {
		return pixelIndex << 2;
	}

}
