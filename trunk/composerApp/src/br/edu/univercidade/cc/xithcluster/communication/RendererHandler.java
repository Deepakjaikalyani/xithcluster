package br.edu.univercidade.cc.xithcluster.communication;

public class RendererHandler implements Comparable<RendererHandler> {
	
	private Long creationTime;
	
	private Integer order;
	
	private byte[] colorAndAlphaBuffer;
	
	private float[] depthBuffer;
	
	public RendererHandler(int order) {
		creationTime = System.currentTimeMillis();
		this.order = order;
	}
	
	public byte[] getColorAndAlphaBuffer() {
		return colorAndAlphaBuffer;
	}
	
	public void setColorAndAlphaBuffer(byte[] colorAndAlphaBuffer) {
		this.colorAndAlphaBuffer = colorAndAlphaBuffer;
	}
	
	public float[] getDepthBuffer() {
		return depthBuffer;
	}
	
	public void setDepthBuffer(float[] depthBuffer) {
		this.depthBuffer = depthBuffer;
	}

	@Override
	public int compareTo(RendererHandler o) {
		if (order == o.order) {
			return creationTime.compareTo(o.creationTime);
		}
		
		return order.compareTo(o.order);
	}
	
}
