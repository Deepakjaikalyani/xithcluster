package br.edu.univercidade.cc.xithcluster.composition;

public class PixelBuffer {
	
	private int width;
	
	private int height;
	
	private int[] pixels;
	
	public PixelBuffer(int width, int height) {
		this.width = width;
		this.height = height;
		int size = this.width * this.height;
		pixels = new int[size];
	}
	
	public PixelBuffer(int width, int height, int[] pixels) {
		if (pixels == null || pixels.length == 0) {
			throw new IllegalArgumentException();
		}
		
		this.width = width;
		this.height = height;
		
		int size = this.width * this.height;
		if (pixels.length != size) {
			throw new IllegalArgumentException();
		}
		
		this.pixels = pixels;
	}
	
	public void put(int i, int pixel) {
		if (i >= pixels.length) {
			throw new ArrayIndexOutOfBoundsException("Trying to set pixel " + i + " but there's only " + pixels.length + " pixels in this buffer");
		}
		
		pixels[i] = pixel;
	}

	public int get(int i) {
		return pixels[i];
	}
	
	public int[] getPixelRegion(int x, int y, int width, int height) {
		checkForOutOfBoundsAccess(x, y, width, height);
		
		int[] pixelRegion = new int[width * height];
		int i = 0;
		for (int x1 = 0; x1 < width; x1++) {
			for (int y1 = 0; y1 < height; y1++) {
				pixelRegion[i++] = getPixel(x + x1, y + y1);
			}
		}
		
		return pixelRegion;
	}

	private void checkForOutOfBoundsAccess(int x, int y, int width, int height) {
		if (x < 0 || x + width > this.width) {
			throw new ArrayIndexOutOfBoundsException("Trying to access an out of bounds region");
		}
		
		if (y < 0 || y + height > this.height) {
			throw new ArrayIndexOutOfBoundsException("Trying to access an out of bounds region");
		}
	}
	
	private int getPixel(int x, int y) {
		return pixels[(y * width) + x];
	}
	
	public int[] toIntArray() {
		return pixels;
	}
	
}
