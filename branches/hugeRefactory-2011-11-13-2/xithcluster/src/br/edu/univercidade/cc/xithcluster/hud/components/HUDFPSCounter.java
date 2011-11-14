package br.edu.univercidade.cc.xithcluster.hud.components;

import org.openmali.vecmath2.Colorf;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.utils.HUDFont;
import org.xith3d.ui.hud.utils.HUDFont.FontStyle;
import org.xith3d.ui.hud.widgets.Label;

public class HUDFPSCounter extends FPSCounter {

	private static final float DEFAULT_WIDTH = 100.0f;
	
	private static final float DEFAULT_HEIGHT = 40.0f;
	
	private static final HUDFont DEFAULT_FONT = HUDFont.getFont("Courier New", FontStyle.PLAIN, 12);

	private static final Colorf DEFAULT_COLOR = Colorf.GREEN;
	
	private Label hudLabel;
	
	public HUDFPSCounter(int numSamples) {
		super(numSamples);
		
		hudLabel = new Label(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		hudLabel.setFont(DEFAULT_FONT);
		hudLabel.setFontColor(DEFAULT_COLOR);
	}
	
	@Override
	public void update(double fps) {
		super.update(fps);
		
		hudLabel.setText(String.format(PATTERN, getAverageFps()));
	}

	public void registerTo(HUD hud) {
		hud.getContentPane().addWidget(hudLabel, 20.0f, 20.0f);
	}
	
}
