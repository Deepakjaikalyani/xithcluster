package br.edu.univercidade.cc.xithcluster.composition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DepthBufferList implements Iterable<DepthBuffer> {
	
	private List<DepthBuffer> depthBuffers = new ArrayList<DepthBuffer>();
	
	public static DepthBufferList wrap(float[][] depthBuffersData) {
		if (depthBuffersData == null || depthBuffersData.length == 0) {
			// TODO:
			throw new IllegalArgumentException();
		}
		
		DepthBufferList depthBufferList = new DepthBufferList();
		
		for (float[] depthBufferData : depthBuffersData) {
			depthBufferList.depthBuffers.add(DepthBuffer.wrap(depthBufferData));
		}
		
		return depthBufferList;
	}
	
	public int getDepthBufferIndexByLowerZValue(int index) {
		if (depthBuffers.isEmpty()) {
			throw new IllegalStateException("Depth buffer list not initialized");
		}
		
		float lowerZValue = Float.MAX_VALUE;
		int currentBufferIndex = 0, selectedBufferIndex = 0;
		for (DepthBuffer depthBuffer : depthBuffers) {
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
		return depthBuffers.iterator();
	}
	
	public static DepthBufferList emptyList() {
		return new DepthBufferList();
	}
	
	public void add(int index, DepthBuffer depthBuffer) {
		if (depthBuffer == null) {
			// TODO:
			throw new IllegalArgumentException();
		}
		
		allocateSpaceIfNecessary(index);
		
		depthBuffers.set(index, depthBuffer);
	}
	
	private void allocateSpaceIfNecessary(int index) {
		while (index > depthBuffers.size() - 1) {
			depthBuffers.add(null);
		}
	}
	
	public void remove(int index) {
		if (index < 0 || index >= depthBuffers.size()) {
			// TODO:
			throw new ArrayIndexOutOfBoundsException();
		}
		
		depthBuffers.remove(index);
	}
	
}
