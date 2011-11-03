package br.edu.univercidade.cc.xithcluster;

import java.io.IOException;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import br.edu.univercidade.cc.xithcluster.communication.ComposerNetworkManager;
import br.edu.univercidade.cc.xithcluster.composition.BottomUpLeftToRightBufferReadOrder;
import br.edu.univercidade.cc.xithcluster.composition.ColorAndAlphaBufferList;
import br.edu.univercidade.cc.xithcluster.composition.CompositionContext;
import br.edu.univercidade.cc.xithcluster.composition.CompositionStrategy;
import br.edu.univercidade.cc.xithcluster.composition.DepthBufferList;
import br.edu.univercidade.cc.xithcluster.hud.components.AWTFPSCounter;
import br.edu.univercidade.cc.xithcluster.utils.Timer;
import br.edu.univercidade.cc.xithcluster.utils.Timer.TimeMeasurementUnit;

public class ComposerLoop implements Runnable, Rasterizer {
	
	private static final double TARGET_FPS = 200.0;
	
	private static final int FPS_SAMPLES_COLLECTED = 10;
	
	private Logger log = Logger.getLogger(ComposerLoop.class);
	
	private ComposerNetworkManager networkManager;
	
	private CompositionStrategy compositionStrategy;
	
	private Display display;
	
	private AWTFPSCounter fpsCounter;
	
	private int screenWidth;
	
	private int screenHeight;
	
	private ColorAndAlphaBufferList colorAndAlphaBuffers;
	
	private DepthBufferList depthBuffers;
	
	private boolean displayFPSCounter;
	
	private boolean running = false;
	
	private boolean hasNewImage;
	
	public ComposerLoop(boolean displayFPSCounter, CompositionStrategy compositionStrategy) {
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
				throw new IllegalStateException("You cannot start the application without a network manager");
			}
			
			if (display == null) {
				throw new IllegalStateException("You cannot start the application without a display");
			}
			
			showDisplayer();
			
			startNetworkManager();
			
			createFPSCounterIfSpecified();
			
			startLoop();
		}
	}
	
	private void createFPSCounterIfSpecified() {
		if (displayFPSCounter) {
			fpsCounter = new AWTFPSCounter(FPS_SAMPLES_COLLECTED);
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
		
		CompositionContext.setDefaultBufferReadOrderClass(BottomUpLeftToRightBufferReadOrder.class);
		
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
		if (hasNewImage()) {
			CompositionContext context = CompositionContext.getInstance(screenWidth, screenHeight);
			
			context.setColorAndAlphaBuffers(colorAndAlphaBuffers);
			context.setDepthBuffers(depthBuffers);
			
			compositionStrategy.compose(context);
			
			display.setPixelBuffer(context.getPixelBuffer().toIntArray());
		}
		
		display.blit();
		
		networkManager.update(startingTime, elapsedTime);
	}
	
	private boolean hasNewImage() {
		boolean nextImage = hasNewImage;
		hasNewImage = false;
		return nextImage;
	}
	
	@Override
	public void setScreenSize(int screenWidth, int screenHeight) {
		if (display == null) {
			throw new IllegalStateException("The display cannot be null");
		}
		
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		
		display.setSizeAndRecreateBackBuffer(screenWidth, screenHeight);
	}
	
	@Override
	public void setNewImageData(ColorAndAlphaBufferList colorAndAlphaBuffers, DepthBufferList depthBuffers) {
		if (colorAndAlphaBuffers == null || depthBuffers == null) {
			throw new IllegalArgumentException("Color, alpha and depth buffers cannot be null");
		}
		
		this.colorAndAlphaBuffers = colorAndAlphaBuffers;
		this.depthBuffers = depthBuffers;
		
		hasNewImage = true;
	}
	
}
