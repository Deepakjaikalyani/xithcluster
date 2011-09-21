package br.edu.univercidade.cc.xithcluster.test.utils;

import org.openmali.vecmath2.Colorf;


public final class TestUtils {

	private static final Colorf[] COLORS = { Colorf.WHITE, Colorf.BLUE, Colorf.BROWN, Colorf.CYAN, Colorf.GRAY, Colorf.GREEN, Colorf.LIGHT_BROWN, Colorf.LIGHT_GRAY, Colorf.MAGENTA, Colorf.ORANGE, Colorf.PINK, Colorf.RED, Colorf.YELLOW };
	
	private TestUtils() {
	}

	public static Colorf randomColor() {
		return COLORS[(int) Math.floor(Math.random() * COLORS.length)];
	}
	
}
