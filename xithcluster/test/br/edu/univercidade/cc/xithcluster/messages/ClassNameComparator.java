package br.edu.univercidade.cc.xithcluster.messages;

import java.util.Comparator;


public class ClassNameComparator implements Comparator<Object> {

	@Override
	public int compare(Object o1, Object o2) {
		if (o1 == null || o2 == null) return -1;
		
		return o1.getClass().getName().compareTo(o2.getClass().getName());
	}


}
