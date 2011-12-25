package br.edu.univercidade.cc.xithcluster.utils;

public class SimpleAssertions {
	
	public static void assertNonNull(Object... args) {
		if (args == null) throw new AssertionError();
		
		for (Object arg : args)
			if (arg == null) throw new AssertionError();
	}
	
}
