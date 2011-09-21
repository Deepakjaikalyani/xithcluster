package br.edu.univercidade.cc.xithcluster;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Display extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private static final Font defaultFont = new Font("Courier New", Font.PLAIN, 12);
	
	private static final int DEFAULT_WIDTH = 800;
	
	private static final int DEFAULT_HEIGHT = 600;
	
	private Canvas canvas;
	
	private int[] argbDataBuffer;
	
	private BufferedImage backBuffer;
	
	private BufferStrategy buffer;
	
	private double framesPerSecond;
	
	public void initializeAndShow() {
		decorate();
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
	
	private void decorate() {
		setTitle(ComposerConfiguration.windowTitle);
		super.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setResizable(false);
		setIgnoreRepaint(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}
	
	private void setupBufferStrategy() {
		canvas = new Canvas();
		canvas.setIgnoreRepaint(true);
		
		add(canvas);
		
		canvas.createBufferStrategy(2);
		buffer = canvas.getBufferStrategy();
	}
	
	private synchronized void createDataBuffer(int width, int height) {
		WritableRaster raster;
		DataBufferInt dataBuffer;
		int bufferSize;
		
		bufferSize = width * height;
		
		argbDataBuffer = new int[bufferSize];
		dataBuffer = new DataBufferInt(argbDataBuffer, bufferSize);
		raster = Raster.createPackedRaster(dataBuffer, width, height, width, new int[] { 0x00ff0000, 0x0000ff00, 0x000000ff, 0xff000000}, null);
		backBuffer = new BufferedImage(ColorModel.getRGBdefault(), raster, true, null);
	}
	
	public void updateFPSCounter(double framesPerSecond) {
		this.framesPerSecond = framesPerSecond;
	}
	
	public void setARGBImageData(int[] argbImageData) {
		System.arraycopy(argbImageData, 0, argbDataBuffer, 0, argbDataBuffer.length);
	}
	
	public synchronized void blit() {
		Graphics graphics = null;
		
		try {
			graphics = buffer.getDrawGraphics();
			
			graphics.setColor(Color.BLACK);
			graphics.fillRect(0, 0, getWidth(), getHeight());
			
			graphics.drawImage(backBuffer, 0, 0, null);
			
			if (ComposerConfiguration.displayFPSCounter) {
				graphics.setFont(defaultFont);
				graphics.setColor(Color.GREEN);
				graphics.drawString(String.format("FPS: %s", framesPerSecond), 20, 20);
			}
			
			if (!buffer.contentsLost()) {
				buffer.show();
			}
		} finally {
			if (graphics != null) {
				graphics.dispose();
			}
		}
	}
	
}
