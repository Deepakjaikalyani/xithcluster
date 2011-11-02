package br.edu.univercidade.cc.xithcluster.composition;

import java.util.ArrayList;
import java.util.List;

public class ColorAndAlphaBufferList {
	
	private List<ColorAndAlphaBuffer> colorAndAlphaBuffers = new ArrayList<ColorAndAlphaBuffer>();
	
	public static ColorAndAlphaBufferList wrap(byte[][] colorAndAlphaBuffersData) {
		if (colorAndAlphaBuffersData == null || colorAndAlphaBuffersData.length == 0) {
			// TODO:
			throw new IllegalArgumentException();
		}
		
		ColorAndAlphaBufferList colorAndAlphaBufferList = new ColorAndAlphaBufferList();
		
		for (byte[] colorAndAlphaBufferData : colorAndAlphaBuffersData) {
			ColorAndAlphaBuffer colorAndAlphaBuffer = ColorAndAlphaBuffer.wrap(colorAndAlphaBufferData);
			colorAndAlphaBufferList.addColorAndAlphaBuffer(colorAndAlphaBuffer);
		}
		
		return colorAndAlphaBufferList;
	}
	
	private ColorAndAlphaBufferList() {
	}
	
	public void addColorAndAlphaBuffer(ColorAndAlphaBuffer colorAndAlphaBuffer) {
		colorAndAlphaBuffers.add(colorAndAlphaBuffer);
	}
	
	public ColorAndAlphaBuffer getColorAndAlphaBufferByIndex(int index) {
		if (colorAndAlphaBuffers.isEmpty()) {
			throw new IllegalStateException("Color and alpha buffer list not initialized");
		}
		
		if (index >= colorAndAlphaBuffers.size()) {
			throw new ArrayIndexOutOfBoundsException("Trying to get color and alpha buffer " + index + " but the list has only " + colorAndAlphaBuffers.size() + " buffers");
		}
		
		return colorAndAlphaBuffers.get(index);
	}
	
	public static ColorAndAlphaBufferList emptyList() {
		return new ColorAndAlphaBufferList();
	}
	
	public void add(int index, ColorAndAlphaBuffer colorAndAlphaBuffer) {
		if (colorAndAlphaBuffer == null) {
			// TODO:
			throw new IllegalArgumentException();
		}
		
		allocateSpaceIfNecessary(index);
		
		colorAndAlphaBuffers.set(index, colorAndAlphaBuffer);
	}
	
	private void allocateSpaceIfNecessary(int index) {
		while (index > colorAndAlphaBuffers.size() - 1) {
			colorAndAlphaBuffers.add(null);
		}
	}
	
	public void remove(int index) {
		if (index < 0 || index >= colorAndAlphaBuffers.size()) {
			// TODO:
			throw new ArrayIndexOutOfBoundsException();
		}
		
		colorAndAlphaBuffers.remove(index);
	}
	
}
