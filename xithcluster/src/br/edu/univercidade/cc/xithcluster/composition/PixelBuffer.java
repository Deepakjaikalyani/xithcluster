package br.edu.univercidade.cc.xithcluster.composition;

public class PixelBuffer {
	
	private int[] pixels;
	
	private int size;
	
	public PixelBuffer(int size) {
		this.size = size;
		pixels = new int[this.size];
	}

	public void put(int i, int pixel) {
		if (i >= size) {
			throw new ArrayIndexOutOfBoundsException("Trying to set pixel " + i + " but there's only " + size + " pixels in this buffer");
		}
		
		pixels[i] = pixel;
	}

	public int[] toIntArray() {
		return pixels;
	}
	
}
