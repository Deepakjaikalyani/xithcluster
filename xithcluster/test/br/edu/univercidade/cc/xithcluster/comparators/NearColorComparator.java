package br.edu.univercidade.cc.xithcluster.comparators;

import java.util.Comparator;

public class NearColorComparator implements Comparator<Integer> {
	
	private int tolerance;
	
	public NearColorComparator(int tolerance) {
		this.tolerance = tolerance;
	}
	
	@Override
	public int compare(Integer o1, Integer o2) {
		if (o1 == null || o2 == null) {
			throw new IllegalArgumentException();
		}
		
		Integer inferiorLimit = o1 - tolerance;
		Integer superiorLimit = o1 + tolerance;
		
		if (o2 < inferiorLimit)
			return 1;
		else if (o2 > superiorLimit)
			return -1;
		else
			return 0;
	}
	
}
