package br.edu.univercidade.cc.xithcluster;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import br.edu.univercidade.cc.xithcluster.hud.components.AWTFPSCounter;

public class Display {
	
	private final class WindowClosingListener implements WindowListener {
		
		@Override
		public void windowOpened(WindowEvent e) {
		}
		
		@Override
		public void windowClosing(WindowEvent e) {
			onWindowClosing(e);
		}
		
		@Override
		public void windowClosed(WindowEvent e) {
		}
		
		@Override
		public void windowIconified(WindowEvent e) {
		}
		
		@Override
		public void windowDeiconified(WindowEvent e) {
		}
		
		@Override
		public void windowActivated(WindowEvent e) {
		}
		
		@Override
		public void windowDeactivated(WindowEvent e) {
		}
	}
	
	private static final int NUMBER_OF_BUFFERS = 2;
	
	private static final int DEFAULT_WIDTH = 800;
	
	private static final int DEFAULT_HEIGHT = 600;
	
	private JFrame frame;
	
	private Canvas canvas;
	
	private BufferedImage backBuffer;
	
	private BufferStrategy bufferStrategy;
	
	private AWTFPSCounter fpsCounter;
	
	private String windowTitle;
	
	private int width;
	
	private int height;
	
	public Display(String windowTitle) {
		if (windowTitle == null || windowTitle.isEmpty()) {
			throw new IllegalArgumentException();
		}
		
		this.windowTitle = windowTitle;
	}
	
	public void show() {
		createFrameAndCanvas();
		
		setupBufferStrategy();
		
		setSizeAndRecreateBackBuffer(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	private void createFrameAndCanvas() {
		frame = new JFrame(windowTitle);
		frame.addWindowListener(new WindowClosingListener());
		
		frame.setResizable(false);
		frame.setIgnoreRepaint(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		canvas = new Canvas();
		canvas.setIgnoreRepaint(true);
		
		frame.add(canvas);
		
		frame.setVisible(true);
	}
	
	protected void onWindowClosing(WindowEvent e) {
		// TODO:
		System.exit(-1);
	}
	
	public void setSizeAndRecreateBackBuffer(int width, int height) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException();
		}
		
		this.width = width;
		this.height = height;
		
		frame.setSize(this.width, this.height);
		canvas.setSize(this.width, this.height);
		
		createBackBuffer();
	}
	
	private void setupBufferStrategy() {
		canvas.createBufferStrategy(NUMBER_OF_BUFFERS);
		bufferStrategy = canvas.getBufferStrategy();
	}
	
	private void createBackBuffer() {
		backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}
	
	public void setPixelBuffer(int[] argbImageData) {
		if (argbImageData == null || argbImageData.length == 0) {
			throw new IllegalArgumentException();
		}
		
		backBuffer.setRGB(0, 0, width, height, argbImageData, 0, width);
	}
	
	public void blit() {
		Graphics graphics = null;
		
		try {
			graphics = bufferStrategy.getDrawGraphics();
			
			clear(graphics);
			
			drawBackBuffer(graphics);
			
			if (fpsCounter != null) {
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
		graphics.fillRect(0, 0, width, height);
	}
	
	private void drawBackBuffer(Graphics graphics) {
		graphics.drawImage(backBuffer, 0, 0, null);
	}
	
	private void swapBuffers() {
		if (!bufferStrategy.contentsLost()) {
			bufferStrategy.show();
		}
	}
	
	public void setFPSCounter(AWTFPSCounter fpsCounter) {
		if (fpsCounter == null) {
			throw new IllegalArgumentException();
		}
		
		this.fpsCounter = fpsCounter;
	}
	
}
