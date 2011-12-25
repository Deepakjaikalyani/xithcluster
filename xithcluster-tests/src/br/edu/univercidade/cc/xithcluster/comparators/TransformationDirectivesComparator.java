package br.edu.univercidade.cc.xithcluster.comparators;

import static br.edu.univercidade.cc.xithcluster.utils.SimpleAssertions.assertNonNull;
import java.util.Comparator;
import org.eclipse.jdt.annotation.NonNull;
import org.xith3d.schedops.movement.TransformationDirectives;
import br.edu.univercidade.cc.xithcluster.utils.CompareUtils;

public class TransformationDirectivesComparator implements Comparator<TransformationDirectives> {
	
	@Override
	public int compare(TransformationDirectives transformationDirective1, TransformationDirectives transformationDirective2) {
		int comparationResult;
		
		if (transformationDirective1 == null)
			return -1;
		
		if (transformationDirective2 == null)
			return 1;
		
		if (transformationDirective1.getUserAxis() == null) {
			return compareUsingUserAxis(transformationDirective1, transformationDirective2);
		}
		
		if (transformationDirective2.getUserAxis() == null)
			return 1;
		
		comparationResult = CompareUtils.compareTo(transformationDirective1.getUserAxis().length(), transformationDirective2.getUserAxis().length());
		
		if (comparationResult != 0)
			return comparationResult;
		
		comparationResult = CompareUtils.compareTo(transformationDirective1.getInitValueUser(), transformationDirective2.getInitValueUser());
		
		if (comparationResult != 0)
			return comparationResult;
		
		comparationResult = CompareUtils.compareTo(transformationDirective1.getSpeedUser(), transformationDirective2.getSpeedUser());
		
		return comparationResult;
	}
	
	private int compareUsingUserAxis(@NonNull TransformationDirectives transformationDirective1, @NonNull TransformationDirectives transformationDirective2) {
		assertNonNull(transformationDirective1, transformationDirective2);
		
		int comparationResult = CompareUtils.compareTo(transformationDirective1.getInitValueX(), transformationDirective2.getInitValueX());
		
		if (comparationResult != 0)
			return comparationResult;
		
		comparationResult = CompareUtils.compareTo(transformationDirective1.getInitValueY(), transformationDirective2.getInitValueY());
		
		if (comparationResult != 0)
			return comparationResult;
		
		comparationResult = CompareUtils.compareTo(transformationDirective1.getInitValueZ(), transformationDirective2.getInitValueZ());
		
		if (comparationResult != 0)
			return comparationResult;
		
		comparationResult = CompareUtils.compareTo(transformationDirective1.getSpeedX(), transformationDirective2.getSpeedX());
		
		if (comparationResult != 0)
			return comparationResult;
		
		comparationResult = CompareUtils.compareTo(transformationDirective1.getSpeedY(), transformationDirective2.getSpeedY());
		
		if (comparationResult != 0)
			return comparationResult;
		
		comparationResult = CompareUtils.compareTo(transformationDirective1.getSpeedZ(), transformationDirective2.getSpeedZ());
		
		if (comparationResult != 0)
			return comparationResult;
		
		comparationResult = transformationDirective1.getAxisOrder().compareTo(transformationDirective2.getAxisOrder());
		
		return comparationResult;
	}
	
}
