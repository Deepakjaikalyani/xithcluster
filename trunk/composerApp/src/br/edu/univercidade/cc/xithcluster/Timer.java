package br.edu.univercidade.cc.xithcluster;


public class Timer {
	
	private static final double NANO_SECONDS_DIVISOR = 1000000000.0;
	
	public static long getCurrentTime() {
		return System.nanoTime();
	}
	
	public static double getTimeDivisor() {
		return NANO_SECONDS_DIVISOR;
	}
	
}
