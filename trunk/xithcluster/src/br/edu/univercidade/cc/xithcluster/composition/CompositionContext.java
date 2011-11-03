package br.edu.univercidade.cc.xithcluster.composition;

public class CompositionContext {
	
	private static CompositionContext instance = null;
	
	private static Class<? extends BufferReadOrder> defaultBufferReadOrderClass = DirectBufferReadOrder.class;
	
	private int width;
	
	private int height;
	
	private PixelBuffer pixelBuffer;
	
	private ColorAndAlphaBufferList colorAndAlphaBuffers;
	
	private DepthBufferList depthBuffers;
	
	private BufferReadOrder bufferReadOrder;
	
	public static CompositionContext getInstance(int width, int height) {
		if (instance == null || !instance.hasWidthAndHeight(width, height)) {
			instance = new CompositionContext(width, height);
		}
		
		instance.clear();
		
		return instance;
	}
	
	public static void setDefaultBufferReadOrderClass(Class<? extends BufferReadOrder> defaultBufferReadOrderClass) {
		CompositionContext.defaultBufferReadOrderClass = defaultBufferReadOrderClass;
	}
	
	private CompositionContext(int width, int height) {
		this.width = width;
		this.height = height;
		
		pixelBuffer = new PixelBuffer(this.width, this.height);
		bufferReadOrder = createBufferReadOrder();
	}
	
	private void clear() {
		colorAndAlphaBuffers = null;
		depthBuffers = null;
		
		bufferReadOrder.reset();
	}
	
	private BufferReadOrder createBufferReadOrder() {
		if (defaultBufferReadOrderClass == null) {
			throw new IllegalStateException("There's no default buffer read order class set");
		}
		
		try {
			return defaultBufferReadOrderClass.getConstructor(int.class, int.class).newInstance(width, height);
		} catch (Exception e) {
			// TODO:
			throw new RuntimeException("Error creating buffer read order", e);
		}
	}
	
	public PixelBuffer getPixelBuffer() {
		return pixelBuffer;
	}
	
	public int getDepthBufferIndexByLowerZComponent(int index) {
		return depthBuffers.getDepthBufferIndexByLowerZValue(index);
	}
	
	public ColorAndAlphaBuffer getColorAndAlphaBufferByIndex(int index) {
		return colorAndAlphaBuffers.getColorAndAlphaBufferByIndex(index);
	}
	
	public BufferReadOrder getPixelReadOrder() {
		return bufferReadOrder;
	}
	
	private boolean hasWidthAndHeight(int width, int height) {
		return this.width == width && this.height == height;
	}
	
	public void setColorAndAlphaBuffers(ColorAndAlphaBufferList colorAndAlphaBuffers) {
		this.colorAndAlphaBuffers = colorAndAlphaBuffers;
	}
	
	public void setDepthBuffers(DepthBufferList depthBuffers) {
		this.depthBuffers = depthBuffers;
	}
	
}
