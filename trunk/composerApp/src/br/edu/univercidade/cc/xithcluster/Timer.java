package br.edu.univercidade.cc.xithcluster;

public final class Timer {
	
	public enum TimeMeasurementUnit {
		MILLISECONDS(1000.0), NANOSECONDS(1000000000.0);
		
		double timeDivisor;
		
		TimeMeasurementUnit(double timeDivisor) {
			this.timeDivisor = timeDivisor;
		}
		
	}
	
	private static TimeMeasurementUnit timeMeasurementUnit = TimeMeasurementUnit.NANOSECONDS;
	
	public static void setTimeMeasurementUnit(TimeMeasurementUnit timeMeasurementUnit) {
		Timer.timeMeasurementUnit = timeMeasurementUnit;
	}
	
	public static long getCurrentTime() {
		if (timeMeasurementUnit == TimeMeasurementUnit.NANOSECONDS) {
			return System.nanoTime();
		} else {
			return System.currentTimeMillis();
		}
	}
	
	public static double getTimeDivisor() {
		return timeMeasurementUnit.timeDivisor;
	}
	
}
