package br.edu.univercidade.cc.xithcluster.composition;

public final class ImageOrderAndZBufferStrategy implements CompositionStrategy {
	
	@Override
	public void compose(CompositionContext context) {
		BufferReadOrder bufferReadOrder = context.getPixelReadOrder();
		PixelBuffer pixelBuffer = context.getPixelBuffer();
		while (bufferReadOrder.hasNext()) {
			int pixelIndex = bufferReadOrder.nextIndex();
			
			int depthBufferIndex = context.getDepthBufferIndexByLowerZComponent(pixelIndex);
			ColorAndAlphaBuffer colorAndAlphaBuffer = context.getColorAndAlphaBufferByIndex(depthBufferIndex);
			
			int pixel = colorAndAlphaBuffer.getRGB(pixelIndex);
			
			pixelBuffer.put(pixelIndex, pixel);
		}
	}
	
}
