package br.edu.univercidade.cc.xithcluster;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Display extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private static final Font defaultFont = new Font("Courier New", Font.PLAIN, 12);

	private static final int DEFAULT_WIDTH = 800;

	private static final int DEFAULT_HEIGHT = 600;
	
	private Canvas canvas;
	
	private BufferedImage backBuffer;
	
	private BufferStrategy buffer;
	
	private double framesPerSecond;
	
	public void initialize() {
		decorate();
		setVisible(true);
		setupBufferStrategy();
		createBackBuffer(DEFAULT_WIDTH, DEFAULT_HEIGHT);
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
		canvas.setSize(dimension);
		
		// FIXME:
		createBackBuffer((int) dimension.getWidth(), (int) dimension.getHeight());
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
	
	private void createBackBuffer(int width, int height) {
		backBuffer = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, height);
	}
	
	public void updateFPSCounter(double framesPerSecond) {
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
