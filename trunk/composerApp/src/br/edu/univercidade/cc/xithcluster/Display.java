package br.edu.univercidade.cc.xithcluster;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

public class Display extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private static final Font defaultFont = new Font("Courier New", Font.PLAIN, 12);
	
	private Canvas canvas;
	
	private BufferedImage backBuffer;
	
	private BufferStrategy buffer;
	
	private long framesPerSecond;
	
	public void initialize() {
		decorate();
		setupBufferStrategy();
		createBackBuffer();
		setVisible(true);
		
		// TODO:
		setSize(640, 480);
	}
	
	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		canvas.setSize(width, height);
	}

	private void decorate() {
		setTitle(ComposerConfiguration.windowTitle);
		setIgnoreRepaint(true);
		setLocationRelativeTo(null);
	}
	
	private void setupBufferStrategy() {
		canvas = new Canvas();
		canvas.setIgnoreRepaint(true);
		
		add(canvas);
		
		canvas.createBufferStrategy(2);
		buffer = canvas.getBufferStrategy();
	}
	
	private void createBackBuffer() {
		backBuffer = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(640, 480);
	}
	
	public void updateFPSCounter(long framesPerSecond) {
		this.framesPerSecond = framesPerSecond;
	}
	
	public void setImageData(byte[] imageData) {
		int width;
		int x;
		int y;
		
		width = getWidth();
		// TODO: Is there a way to optimize it?
		for (int i = 0; i < imageData.length; i++) {
			x = i % width;
			y = i / width;
			backBuffer.setRGB(x, y, (int) imageData[i]);
		}
	}
	
	public void blit() {
		Graphics graphics = null;
		
		try {
			graphics = buffer.getDrawGraphics();
			
			if (ComposerConfiguration.displayFPSCounter) {
				graphics.setFont(defaultFont);
				graphics.setColor(Color.GREEN);
				graphics.drawString(String.format("FPS: %s", framesPerSecond), 20, 20);
			}
			
			graphics.drawImage(backBuffer, 0, 0, null);
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
