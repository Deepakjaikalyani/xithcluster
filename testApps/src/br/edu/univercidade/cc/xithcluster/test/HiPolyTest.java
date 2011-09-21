package br.edu.univercidade.cc.xithcluster.test;

import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.TransformGroup;
import br.edu.univercidade.cc.xithcluster.primitives.Rectangle;
import br.edu.univercidade.cc.xithcluster.test.utils.TestUtils;

public class HiPolyTest extends PolyTest {
	
	protected BranchGroup createTestScene() {
		BranchGroup root;
		TransformGroup transformGroup;
		Group mainGroup;
		Rectangle rectangle;
		
		root = new BranchGroup();
		mainGroup = new Group();
		mainGroup.setName("mainGroup");
		
		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < 100; j++) {
				rectangle = new Rectangle(0.07f, 0.07f, null, null, null);
				rectangle.setName("rectangle[" + i + "][" + j + "]");
				rectangle.getAppearance(true).setColor(TestUtils.randomColor());
				transformGroup = new TransformGroup((j * 0.07f) - 3.5f, (i * 0.07f) - 3.5f, 0);
				transformGroup.addChild(rectangle);
				mainGroup.addChild(transformGroup);
			}
		}
		
		root.addChild(mainGroup);
		
		return root;
	}
	
	public static void main(String[] args) {
		new HiPolyTest().begin();
	}
	
}
