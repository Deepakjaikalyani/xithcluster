package br.edu.univercidade.cc.xithcluster.composition;

import java.util.HashMap;
import java.util.Map;
import br.edu.univercidade.cc.xithcluster.composition.ColorAndAlphaBuffer.Type;

public class ColorAndAlphaBufferList {
	
	private Map<Integer, ColorAndAlphaBuffer> colorAndAlphaBuffersMap = new HashMap<Integer, ColorAndAlphaBuffer>();
	
	public static ColorAndAlphaBufferList wrap(byte[][] colorAndAlphaBuffersData, Type type) {
		if (colorAndAlphaBuffersData == null || colorAndAlphaBuffersData.length == 0) {
			// TODO:
			throw new IllegalArgumentException();
		}
		
		ColorAndAlphaBufferList colorAndAlphaBufferList = new ColorAndAlphaBufferList();
		
		int i = 0;
		for (byte[] colorAndAlphaBufferData : colorAndAlphaBuffersData) {
			ColorAndAlphaBuffer colorAndAlphaBuffer = ColorAndAlphaBuffer.wrap(colorAndAlphaBufferData, type);
			colorAndAlphaBufferList.add(i++, colorAndAlphaBuffer);
		}
		
		return colorAndAlphaBufferList;
	}
	
	private ColorAndAlphaBufferList() {
	}
	
	public ColorAndAlphaBuffer getColorAndAlphaBufferByIndex(int index) {
		if (colorAndAlphaBuffersMap.isEmpty()) {
			throw new IllegalStateException("Color and alpha buffer list not initialized");
		}
		
		if (index >= colorAndAlphaBuffersMap.size()) {
			throw new ArrayIndexOutOfBoundsException("Trying to get color and alpha buffer " + index + " but the list has only " + colorAndAlphaBuffersMap.size() + " buffers");
		}
		
		return colorAndAlphaBuffersMap.get(index);
	}
	
	public static ColorAndAlphaBufferList emptyList() {
		return new ColorAndAlphaBufferList();
	}
	
	public void add(int index, ColorAndAlphaBuffer colorAndAlphaBuffer) {
		if (index < 0 || colorAndAlphaBuffer == null) {
			// TODO:
			throw new IllegalArgumentException();
		}
		
		colorAndAlphaBuffersMap.put(index, colorAndAlphaBuffer);
	}
	
	public void remove(int index) {
		if (index < 0 || index >= colorAndAlphaBuffersMap.size()) {
			// TODO:
			throw new ArrayIndexOutOfBoundsException();
		}
		
		colorAndAlphaBuffersMap.remove(index);
	}
	
}
