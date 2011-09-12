package br.edu.univercidade.cc.xithcluster;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import br.edu.univercidade.cc.xithcluster.communication.ComposerNetworkManager;

public class Composer implements Runnable, WindowListener {
	
	private static final String LOG4J_CONFIGURATION_FILE = "log4j.xml";
	
	private final Logger log = Logger.getLogger(Composer.class);

	private final ComposerNetworkManager networkManager;
	
	private CompositionStrategy compositionStrategy;
	
	private Display display;
	
	public Composer() {
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
				log.error("Error creating composition strategy", e);
				
				return null;
			}
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	
	public void setScreenSize(int screenWidth, int screenHeight) {
		// TODO: Check!
		display.setSize(screenWidth, screenHeight);
	}

	@Override
	public void run() {
		long elapsedTime;
	    long currentTime;
	    long lastTime; 
	    long framesPerSecond;
	    long elapsedFrames;
		
		initializeLog4j();
		
		compositionStrategy = createCompositionStrategy(ComposerConfiguration.compositionStrategyClassName);
		
		try {
			networkManager.initialize();
		} catch (UnknownHostException e) {
			// TODO:
			throw new RuntimeException("Error starting network manager", e);
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error starting network manager", e);
		}
		
		display = new Display();
		display.addWindowListener(Composer.this);
		display.initialize();
		
		framesPerSecond = 0L;
		elapsedFrames = 0L;
		elapsedTime = 0L;
	    currentTime = System.currentTimeMillis();
		while (true) {
			lastTime = currentTime;
			currentTime = System.currentTimeMillis();
			elapsedTime += currentTime - lastTime;
			if (elapsedTime > 1000L) {
			  elapsedTime -= 1000L;
			  framesPerSecond = elapsedFrames;
			  elapsedFrames = 0L;
			} 
			elapsedFrames++;
			
			loopIteration(framesPerSecond);
			
			Thread.yield();
		}
	}

	private void loopIteration(long framesPerSecond) {
		if (display == null)
			return;
		
		display.updateFPSCounter(framesPerSecond);
	
		if (networkManager.hasAllSubImages()) {
			// Buffer swapping
			display.setImageData(compositionStrategy.compose(networkManager.getColorAndAlphaBuffer(), networkManager.getDepthBuffer()));
		}
		
		display.blit();
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
		display.dispose();
		
		// TODO: disconnect all
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
