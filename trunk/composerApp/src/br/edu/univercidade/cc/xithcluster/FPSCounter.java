package br.edu.univercidade.cc.xithcluster;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class FPSCounter {
	
	private static final String PATTERN = "FPS: %.2f";

	private static final Font DEFAULT_FONT = new Font("Courier New", Font.PLAIN, 12);
	
	private static final Color DEFAULT_COLOR = Color.GREEN;
	
	private final int numSamples;
	
	private int samplesCounter = 0;
	
	private double average = 0.0;
	
	private double accumulator = 0.0;
	
	protected FPSCounter(int numSamples) {
		if (numSamples <= 0) {
			// TODO:
			throw new IllegalArgumentException();
		}
		
		this.numSamples = numSamples;
	}
	
	public void print(Graphics graphics, int x, int y) {
		graphics.setFont(DEFAULT_FONT);
		graphics.setColor(DEFAULT_COLOR);
		
		graphics.drawString(String.format(PATTERN, average), x, y);
	}
	
	private void calculateAverageAndResetStats() {
		average = accumulator / samplesCounter;
		accumulator = 0;
		samplesCounter = 0;
	}

	private boolean collectedAllSamples() {
		return (samplesCounter >= numSamples);
	}

	public void update(double fpsSample) {
		accumulator += fpsSample;
		samplesCounter++;
		
		if (collectedAllSamples()) {
			calculateAverageAndResetStats();
		}
	}
	
}