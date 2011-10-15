package br.edu.univercidade.cc.xithcluster.comparators;

import java.util.Comparator;
import org.xith3d.schedops.movement.TransformationDirectives;
import br.edu.univercidade.cc.xithcluster.util.CompareUtils;

public class TransformationDirectivesComparator implements Comparator<TransformationDirectives> {
	
	@Override
	public int compare(TransformationDirectives o1, TransformationDirectives o2) {
		int c;
		
		if (o1 == null)
			return -1;
		
		if (o2 == null)
			return 1;
		
		if (o1.getUserAxis() == null) {
			c = CompareUtils.compareTo(o1.getInitValueX(), o2.getInitValueX());
			
			if (c != 0) return c;
			
			c = CompareUtils.compareTo(o1.getInitValueY(), o2.getInitValueY());
			
			if (c != 0) return c;
			
			c = CompareUtils.compareTo(o1.getInitValueZ(), o2.getInitValueZ());
			
			if (c != 0) return c;
			
			c = CompareUtils.compareTo(o1.getSpeedX(), o2.getSpeedX());
			
			if (c != 0) return c;
			
			c = CompareUtils.compareTo(o1.getSpeedY(), o2.getSpeedY());
			
			if (c != 0) return c;
			
			c = CompareUtils.compareTo(o1.getSpeedZ(), o2.getSpeedZ());
			
			if (c != 0) return c;
			
			if (o1.getAxisOrder() == null) {
				if (o1.getAxisOrder() == null) {
					return 0;
				}
				else {
					return -1;
				}
			}
			
			c = o1.getAxisOrder().compareTo(o2.getAxisOrder());
			
			return c;
		} else {
			if (o2.getUserAxis() == null) return 1;
			
			c = CompareUtils.compareTo(o1.getUserAxis().length(), o2.getUserAxis().length());
			
			if (c != 0) return c;
			
			c = CompareUtils.compareTo(o1.getInitValueUser(), o2.getInitValueUser());
			
			if (c != 0) return c;
			
			c = CompareUtils.compareTo(o1.getSpeedUser(), o2.getSpeedUser());
			
			return c;
		}
	}
	
}
