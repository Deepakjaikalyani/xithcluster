package br.edu.univercidade.cc.xithcluster;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class AWTFPSCounter extends FPSCounter {
	
	private static final Font DEFAULT_FONT = new Font("Courier New", Font.PLAIN, 12);
	
	private static final Color DEFAULT_COLOR = Color.GREEN;
	
	protected AWTFPSCounter(int numSamples) {
		super(numSamples);
	}
	
	public void print(Graphics graphics, int x, int y) {
		graphics.setFont(DEFAULT_FONT);
		graphics.setColor(DEFAULT_COLOR);
		
		graphics.drawString(String.format(PATTERN, getAverageFps()), x, y);
	}
	
}
