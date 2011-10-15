package br.edu.univercidade.cc.xithcluster;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import org.apache.log4j.xml.DOMConfigurator;
import br.edu.univercidade.cc.xithcluster.communication.ComposerNetworkManager;

public class Composer implements Runnable, WindowListener {
	
	private static final String LOG4J_CONFIGURATION_FILE = "log4j.xml";
	
	private static final int FPS_SAMPLES = 100;
	
	private final ComposerNetworkManager networkManager;
	
	private final long secondsPerFrame;
	
	private CompositionStrategy compositionStrategy;
	
	private Displayer displayer;
	
	private AWTFPSCounter fpsCounter;

	private int screenWidth;

	private int screenHeight;

	private byte[][] colorAndAlphaBuffers;

	private float[][] depthBuffers;

	public Composer() {
		// TODO:
		secondsPerFrame = 1000L / 60L;
		
		networkManager = new ComposerNetworkManager(this);
	}

	private void initializeLog4j() {
		if (new File(LOG4J_CONFIGURATION_FILE).exists()) {
			DOMConfigurator.configure(LOG4J_CONFIGURATION_FILE);
		} else {
			System.err.println("Log4j not initialized: \"log4j.xml\" could not be found");
		}
	}
	
	@SuppressWarnings("unchecked")
	private CompositionStrategy createCompositionStrategy(String compositionStrategyClassName) {
		Class<? extends CompositionStrategy> compositionStrategyClass;
		
		try {
			compositionStrategyClass = (Class<? extends CompositionStrategy>) Class.forName(compositionStrategyClassName);
			
			try {
				return compositionStrategyClass.newInstance();
			} catch (Exception e) {
				// TODO:
				throw new RuntimeException("Error creating composition strategy", e);
			}
		} catch (ClassNotFoundException e) {
			// TODO:
			throw new RuntimeException("Error creating composition strategy", e);
		}
	}
	
	public void setScreenSize(int screenWidth, int screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		
		displayer.setSize(new Dimension(screenWidth, screenHeight));
	}

	@Override
	public void run() {
		long elapsedTime;
	    long currentTime;
	    long lastTime; 
	    double fps;
		
		initializeLog4j();
		
		compositionStrategy = createCompositionStrategy(ComposerConfiguration.compositionStrategyClassName);
		
		displayer = new Displayer();
		displayer.addWindowListener(Composer.this);
		displayer.initializeAndShow();
		
		if (ComposerConfiguration.displayFPSCounter) {
			fpsCounter = new AWTFPSCounter(FPS_SAMPLES);
			displayer.setFPSCounter(fpsCounter);
		}
		
		try {
			networkManager.initialize();
		} catch (UnknownHostException e) {
			// TODO:
			throw new RuntimeException("Error starting network manager", e);
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error starting network manager", e);
		}
		
		fps = 0L;
		elapsedTime = 0L;
	    currentTime = System.currentTimeMillis();
		while (true) {
			lastTime = currentTime;
			currentTime = System.currentTimeMillis();
			elapsedTime = currentTime - lastTime;
			if (elapsedTime != 0) {
				fps = 1000L / elapsedTime;
			}
			
			loopIteration(fps);
			
			if (elapsedTime < secondsPerFrame) {
				try {
					Thread.sleep(secondsPerFrame - elapsedTime);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private void loopIteration(double fps) {
		int[] argbImageData;
		
		if (displayer == null) {
			return;
		}

		if (ComposerConfiguration.displayFPSCounter) {
			fpsCounter.update(fps);
		}
	
		if (colorAndAlphaBuffers != null && depthBuffers != null) {
			argbImageData = compositionStrategy.compose(screenWidth, screenHeight, colorAndAlphaBuffers, depthBuffers);
			
			displayer.setARGBImageData(argbImageData);
			
			colorAndAlphaBuffers = null;
			depthBuffers = null;
		}
		
		displayer.blit();
		
		networkManager.update();
	}
	
	public void setColorAlphaAndDepthBuffers(byte[][] colorAndAlphaBuffers, float[][] depthBuffers) {
		this.colorAndAlphaBuffers = colorAndAlphaBuffers;
		this.depthBuffers = depthBuffers;
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		displayer.setVisible(false);
		
		// TODO: Do soft-disconnect
		
		System.exit(-1);
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
	
	/*
	 * =============== 
	 * 		MAIN 
	 * ===============
	 */
	public final static void main(String args[]) {
		new Thread(new Composer()).start();
	}

}
