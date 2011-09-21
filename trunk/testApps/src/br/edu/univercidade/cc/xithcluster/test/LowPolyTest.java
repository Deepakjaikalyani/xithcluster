package br.edu.univercidade.cc.xithcluster.test;

import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.TransformGroup;
import br.edu.univercidade.cc.xithcluster.primitives.Rectangle;
import br.edu.univercidade.cc.xithcluster.test.utils.TestUtils;

public class LowPolyTest extends PolyTest {
	
	protected BranchGroup createTestScene() {
		BranchGroup root;
		TransformGroup transformGroup;
		Group mainGroup;
		Rectangle rectangle;
		
		root = new BranchGroup();
		mainGroup = new Group();
		mainGroup.setName("mainGroup");
		
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				rectangle = new Rectangle(0.5f, 0.5f, null, null, null);
				rectangle.setName("rectangle[" + i + "][" + j + "]");
				rectangle.getAppearance(true).setColor(TestUtils.randomColor());
				transformGroup = new TransformGroup((j * 0.5f) - 2.5f, (i * 0.5f) - 2.5f, 0);
				transformGroup.addChild(rectangle);
				mainGroup.addChild(transformGroup);
			}
		}
		
		root.addChild(mainGroup);
		
		return root;
	}
	
	public static void main(String[] args) {
		new LowPolyTest().begin();
	}
	
}
