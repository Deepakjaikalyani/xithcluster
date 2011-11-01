package br.edu.univercidade.cc.xithcluster.hud.components;


public abstract class FPSCounter {
	
	protected static final String PATTERN = "FPS: %.2f";

	private final int numSamples;
	
	private int samplesCounter = 0;
	
	private double averageFps = 0.0;
	
	private double accumulator = 0.0;
	
	protected FPSCounter(int numSamples) {
		if (numSamples <= 0) {
			// TODO:
			throw new IllegalArgumentException();
		}
		
		this.numSamples = numSamples;
	}
	
	private void calculateAverageFpsAndResetStats() {
		averageFps = accumulator / samplesCounter;
		accumulator = 0;
		samplesCounter = 0;
	}

	private boolean collectedAllSamples() {
		return (samplesCounter >= numSamples);
	}

	public void update(double fps) {
		accumulator += fps;
		samplesCounter++;
		
		if (collectedAllSamples()) {
			calculateAverageFpsAndResetStats();
		}
	}
	
	protected double getAverageFps() {
		return averageFps;
	}
	
}