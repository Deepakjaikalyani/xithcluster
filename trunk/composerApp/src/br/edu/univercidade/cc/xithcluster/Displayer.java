package br.edu.univercidade.cc.xithcluster;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Displayer extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private static final int ARGB_PIXEL_PACKAGING = BufferedImage.TYPE_INT_ARGB;
	
	private static final int NUMBER_OF_BUFFERS = 2;
	
	private static final int DEFAULT_WIDTH = 800;
	
	private static final int DEFAULT_HEIGHT = 600;
	
	private Canvas canvas;
	
	private BufferedImage backBuffer;
	
	private BufferStrategy buffer;
	
	private FPSCounter fpsCounter = new FPSCounter(100);
	
	public void initializeAndShow() {
		setTitle(ComposerConfiguration.windowTitle);
		
		super.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		setResizable(false);
		setIgnoreRepaint(true);
		setLocationRelativeTo(null);
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		setVisible(true);
		setupBufferStrategy();
		createDataBuffer(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	@Override
	public void setSize(Dimension d) {
		super.setSize(d);
		
		adjustSize(d);
	}
	
	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		
		adjustSize(new Dimension(width, height));
	}
	
	@Override
	public void setPreferredSize(Dimension dimension) {
		super.setPreferredSize(dimension);
		
		adjustSize(dimension);
	}
	
	private void adjustSize(Dimension dimension) {
		int width;
		int height;
		
		canvas.setSize(dimension);
		
		width = (int) dimension.getWidth();
		height = (int) dimension.getHeight();
		
		createDataBuffer(width, height);
	}
	
	private void setupBufferStrategy() {
		canvas = new Canvas();
		canvas.setIgnoreRepaint(true);
		
		add(canvas);
		
		canvas.createBufferStrategy(NUMBER_OF_BUFFERS);
		buffer = canvas.getBufferStrategy();
	}
	
	private void createDataBuffer(int width, int height) {
		backBuffer = new BufferedImage(width, height, ARGB_PIXEL_PACKAGING);
	}
	
	public void updateFPSCounter(double framesPerSecond) {
		if (ComposerConfiguration.displayFPSCounter) {
			fpsCounter.update(framesPerSecond);
		}
	}
	
	public void setARGBImageData(int[] argbImageData) {
		backBuffer.setRGB(0, 0, getWidth(), getHeight(), argbImageData, 0, getWidth());
	}
	
	public void blit() {
		Graphics graphics = null;
		
		try {
			graphics = buffer.getDrawGraphics();
			
			clear(graphics);
			
			drawBackBuffer(graphics);
			
			if (ComposerConfiguration.displayFPSCounter) {
				fpsCounter.print(graphics, 20, 20);
			}
			
			swapBuffers();
		} finally {
			if (graphics != null) {
				graphics.dispose();
			}
		}
	}
	
	private void clear(Graphics graphics) {
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, getWidth(), getHeight());
	}
	
	private void drawBackBuffer(Graphics graphics) {
		graphics.drawImage(backBuffer, 0, 0, null);
	}
	
	private void swapBuffers() {
		if (!buffer.contentsLost()) {
			buffer.show();
		}
	}
	
}
