package br.edu.univercidade.cc.xithcluster.composition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DepthBufferList implements Iterable<DepthBuffer> {
	
	private Map<Integer, DepthBuffer> depthBuffersMap = new HashMap<Integer, DepthBuffer>();
	
	public static DepthBufferList wrap(float[][] depthBuffersData) {
		if (depthBuffersData == null || depthBuffersData.length == 0) {
			// TODO:
			throw new IllegalArgumentException();
		}
		
		DepthBufferList depthBufferList = new DepthBufferList();
		
		int i = 0;
		for (float[] depthBufferData : depthBuffersData) {
			depthBufferList.depthBuffersMap.put(i++, DepthBuffer.wrap(depthBufferData));
		}
		
		return depthBufferList;
	}
	
	public int getDepthBufferIndexByLowerZValue(int index) {
		if (depthBuffersMap.isEmpty()) {
			throw new IllegalStateException("Depth buffer list not initialized");
		}
		
		float lowerZValue = Float.MAX_VALUE;
		int currentBufferIndex = 0, selectedBufferIndex = 0;
		for (DepthBuffer depthBuffer : depthBuffersMap.values()) {
			float zValue = depthBuffer.getZValue(index);
			if (zValue < lowerZValue) {
				selectedBufferIndex = currentBufferIndex;
				lowerZValue = zValue;
			}
			currentBufferIndex++;
		}
		
		return selectedBufferIndex;
	}
	
	@Override
	public Iterator<DepthBuffer> iterator() {
		return depthBuffersMap.values().iterator();
	}
	
	public static DepthBufferList emptyList() {
		return new DepthBufferList();
	}
	
	public void add(int index, DepthBuffer depthBuffer) {
		if (index < 0 || depthBuffer == null) {
			// TODO:
			throw new IllegalArgumentException();
		}
		
		depthBuffersMap.put(index, depthBuffer);
	}
	
	public void remove(int index) {
		if (index < 0 || index >= depthBuffersMap.size()) {
			// TODO:
			throw new ArrayIndexOutOfBoundsException();
		}
		
		depthBuffersMap.remove(index);
	}
	
}
