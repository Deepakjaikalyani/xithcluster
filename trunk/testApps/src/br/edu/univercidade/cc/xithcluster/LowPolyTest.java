package br.edu.univercidade.cc.xithcluster;

import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.StaticTransform;
import br.edu.univercidade.cc.xithcluster.primitives.Rectangle;

public class LowPolyTest extends PolyTest {
	
	protected BranchGroup createTestScene() {
		BranchGroup root = new BranchGroup();
		Group group1 = new Group();
		Rectangle rectangle;
		
		group1.setName("group1");
		
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				rectangle = new Rectangle(0.5f, 0.5f, null, null, null);
				rectangle.getAppearance(true).setColor(randomColor());
				StaticTransform.translate(rectangle, (j * 0.5f) - 2.5f, (i * 0.5f) - 2.5f, 0);
				group1.addChild(rectangle);
			}
		}
		
		root.addChild(group1);
		
		return root;
	}
	
	public static void main(String[] args) {
		new LowPolyTest().begin();
	}
	
}
