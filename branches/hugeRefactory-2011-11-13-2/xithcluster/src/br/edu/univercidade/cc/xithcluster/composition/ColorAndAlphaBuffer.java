package br.edu.univercidade.cc.xithcluster.composition;


public class ColorAndAlphaBuffer {
	
	public enum Type {
		RGBA,
		ARGB
	}
	
	private byte[] data;
	
	private Type type;
	
	public static ColorAndAlphaBuffer wrap(byte[] colorAndAlphaBufferData, Type type) {
		if (colorAndAlphaBufferData == null || colorAndAlphaBufferData.length == 0) {
			// TODO:
			throw new IllegalArgumentException();
		}
		
		return new ColorAndAlphaBuffer(colorAndAlphaBufferData, type);
	}
	
	private ColorAndAlphaBuffer(byte[] data, Type type) {
		this.data = data;
		this.type = type;
	}
	
	public int getRGB(int pixelIndex) {
		int componentIndex = getFirstComponentIndex(pixelIndex);
		
		if (componentIndex < 0 || componentIndex > (data.length - 4)) {
			throw new ArrayIndexOutOfBoundsException("Trying to get pixel " + componentIndex + " but the buffer has only " + data.length + " pixels");
		}
		
		int red, green, blue; //, alpha;
		switch (type) {
		case ARGB:
			//alpha = data[componentIndex];
			red = data[componentIndex + 1];
			green = data[componentIndex + 2];
			blue = data[componentIndex + 3];
			break;
		case RGBA:
			red = data[componentIndex];
			green = data[componentIndex + 1];
			blue = data[componentIndex + 2];
			//alpha = data[componentIndex + 3];
			break;
		default:
			throw new AssertionError();
		}

		return 0xff000000 | ((red & 0x000000ff) << 16) | ((green & 0x000000ff) << 8) | (blue & 0x000000ff);
	}
	
	private int getFirstComponentIndex(int pixelIndex) {
		return pixelIndex << 2;
	}
	
	public boolean hasOnlyZeroes() {
		for (int pixel : data) {
			if (pixel != 0) return false;
		}
		
		return true;
	}
	
}
