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

public class Displayer {
	
	private static final int ARGB_PIXEL_PACKAGING = BufferedImage.TYPE_INT_ARGB;
	
	private static final int NUMBER_OF_BUFFERS = 2;
	
	private static final int DEFAULT_WIDTH = 800;
	
	private static final int DEFAULT_HEIGHT = 600;
	
	private JFrame frame;
	
	private Canvas canvas;
	
	private BufferedImage backBuffer;
	
	private BufferStrategy buffer;
	
	private AWTFPSCounter fpsCounter;
	
	private String windowTitle;
	
	private int screenWidth;
	
	private int screenHeight;
	
	public Displayer(String windowTitle) {
		if (windowTitle == null || windowTitle.isEmpty()) {
			throw new IllegalArgumentException();
		}
		
		this.windowTitle = windowTitle;
	}
	
	public void show() {
		createJFrame();
		
		setupBufferStrategy();
		
		setScreenSizeAndRecreateBackBuffer(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	private void createJFrame() {
		frame = new JFrame(windowTitle);
		frame.addWindowListener(new WindowListener() {

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
			
		});
		
		frame.setResizable(false);
		frame.setIgnoreRepaint(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setVisible(true);
	}
	
	protected void onWindowClosing(WindowEvent e) {
		// TODO:
		System.exit(-1);
	}

	public void setScreenSizeAndRecreateBackBuffer(int screenWidth, int screenHeight) {
		if (screenWidth <= 0 || screenHeight <= 0) {
			throw new IllegalArgumentException();
		}
		
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		
		frame.setSize(this.screenWidth, this.screenHeight);
		canvas.setSize(this.screenWidth, this.screenHeight);
		
		createDataBuffer();
	}
	
	private void setupBufferStrategy() {
		canvas = new Canvas();
		canvas.setIgnoreRepaint(true);
		
		frame.add(canvas);
		
		canvas.createBufferStrategy(NUMBER_OF_BUFFERS);
		buffer = canvas.getBufferStrategy();
	}
	
	private void createDataBuffer() {
		backBuffer = new BufferedImage(screenWidth, screenHeight, ARGB_PIXEL_PACKAGING);
	}
	
	public void setARGBImageData(int[] argbImageData) {
		backBuffer.setRGB(0, 0, screenWidth, screenHeight, argbImageData, 0, screenWidth);
	}
	
	public void blit() {
		Graphics graphics = null;
		
		try {
			graphics = buffer.getDrawGraphics();
			
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
		graphics.fillRect(0, 0, screenWidth, screenHeight);
	}
	
	private void drawBackBuffer(Graphics graphics) {
		graphics.drawImage(backBuffer, 0, 0, null);
	}
	
	private void swapBuffers() {
		if (!buffer.contentsLost()) {
			buffer.show();
		}
	}
	
	public void setFPSCounter(AWTFPSCounter fpsCounter) {
		if (fpsCounter == null) {
			throw new IllegalArgumentException();
		}
		
		this.fpsCounter = fpsCounter;
	}
	
}
