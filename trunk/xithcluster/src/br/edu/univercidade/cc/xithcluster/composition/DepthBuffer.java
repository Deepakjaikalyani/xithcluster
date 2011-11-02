package br.edu.univercidade.cc.xithcluster.composition;

public class DepthBuffer {
	
	private float[] data;
	
	private int size;
	
	public static DepthBuffer wrap(float[] depthBufferData) {
		if (depthBufferData == null || depthBufferData.length == 0) {
			// TODO:
			throw new IllegalArgumentException();
		}
		
		return new DepthBuffer(depthBufferData);
	}
	
	public DepthBuffer(float[] data) {
		this.data = data;
		size = this.data.length;
	}
	
	public float getZValue(int index) {
		if (index >= size) {
			throw new ArrayIndexOutOfBoundsException("Trying to get z value " + index + " but the buffer has only " + size + " z values");
		}
		
		return data[index];
	}
	
	public boolean hasOnlyOnes() {
		for (float zValue : data) {
			if (zValue != 1.0f) return false;
		}
		
		return true;
	}
	
}
