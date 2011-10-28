package br.edu.univercidade.cc.xithcluster;

import java.io.IOException;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import br.edu.univercidade.cc.xithcluster.Timer.TimeMeasurementUnit;
import br.edu.univercidade.cc.xithcluster.communication.ComposerNetworkManager;

public class ComposerLoop implements Runnable, Rasterizer {
	
	private static final double TARGET_FPS = 80.0;
	
	private static final int FPS_SAMPLES = 10;
	
	private Logger log = Logger.getLogger(ComposerLoop.class);
	
	private ComposerNetworkManager networkManager;
	
	private CompositionStrategy compositionStrategy;
	
	private Display display;
	
	private AWTFPSCounter fpsCounter;

	private int screenWidth;

	private int screenHeight;

	private byte[][] colorAndAlphaBuffers;

	private float[][] depthBuffers;

	private boolean displayFPSCounter;
	
	private boolean running = false;
	
	public ComposerLoop(boolean displayFPSCounter,
			CompositionStrategy compositionStrategy) {
		if (compositionStrategy == null) {
			throw new IllegalArgumentException();
		}
		
		this.displayFPSCounter = displayFPSCounter;
		this.compositionStrategy = compositionStrategy;
	}
	
	public void setNetworkManager(ComposerNetworkManager networkManager) {
		if (networkManager == null) {
			throw new IllegalArgumentException();
		}
		
		this.networkManager = networkManager;
		this.networkManager.setRasterizer(this);
	}
	
	public void setDisplayer(Display display) {
		if (display == null) {
			throw new IllegalArgumentException();
		}
		
		this.display = display;
	}

	@Override
	public void run() {
		if (!running) {
			if (networkManager == null) {
				// TODO:
				throw new RuntimeException("Network manager must be set");
			}
			
			if (display == null) {
				// TODO:
				throw new RuntimeException("Display must be set");
			}
			
			showDisplayer();
			
			startNetworkManager();
			
			createFPSCounterIfSpecified();
			
			startLoop();
		}
	}
	
	private void createFPSCounterIfSpecified() {
		if (displayFPSCounter) {
			fpsCounter = new AWTFPSCounter(FPS_SAMPLES);
			display.setFPSCounter(fpsCounter);
			networkManager.setFpsCounter(fpsCounter);
		}
	}

	private void startNetworkManager() {
		try {
			networkManager.start();
		} catch (UnknownHostException e) {
			// TODO:
			throw new RuntimeException("Error starting network manager", e);
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error starting network manager", e);
		}
	}

	private void showDisplayer() {
		display.show();
	}

	private void startLoop() {
		long frameTime;
		long elapsedTime;
		long startingTime;
		long lastElapsedTime;
		long endingTime;
		
		running = true;
		
		log.info("Composer started successfully.");
		
		Timer.setTimeMeasurementUnit(TimeMeasurementUnit.MILLISECONDS);
		frameTime = (long) Math.floor(Timer.getTimeDivisor() / TARGET_FPS);
		
		lastElapsedTime = 0L;
		while (running) {
			startingTime = Timer.getCurrentTime();
			
			loopIteration(startingTime, lastElapsedTime);
			
			endingTime = Timer.getCurrentTime();
			
			elapsedTime = endingTime - startingTime;
			
			if (elapsedTime < frameTime) {
				try {
					Thread.sleep(frameTime - elapsedTime);
				} catch (InterruptedException e) {
				}
			}
			
			lastElapsedTime = elapsedTime;
		}
	}

	private void loopIteration(long startingTime, long elapsedTime) {
		int[] argbImageData;
		
		if (hasNewImage()) {
			argbImageData = compositionStrategy.compose(screenWidth, screenHeight, colorAndAlphaBuffers, depthBuffers);
			
			display.setARGBImageData(argbImageData);
			
			clearBuffers();
		}
		
		display.blit();
		
		networkManager.update(startingTime, elapsedTime);
	}

	private void clearBuffers() {
		colorAndAlphaBuffers = null;
		depthBuffers = null;
	}

	private boolean hasNewImage() {
		return colorAndAlphaBuffers != null && depthBuffers != null;
	}

	@Override
	public void setScreenSize(int screenWidth, int screenHeight) {
		if (display == null) {
			throw new IllegalStateException();
		}
		
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		
		display.setSizeAndRecreateBackBuffer(screenWidth, screenHeight);
	}
	
	@Override
	public void setColorAlphaAndDepthBuffers(byte[][] colorAndAlphaBuffers, float[][] depthBuffers) {
		this.colorAndAlphaBuffers = colorAndAlphaBuffers;
		this.depthBuffers = depthBuffers;
	}
	
}
