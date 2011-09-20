package br.edu.univercidade.cc.xithcluster.serial;

import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.StaticTransform;
import br.edu.univercidade.cc.xithcluster.primitives.Rectangle;

public class RectangleSerializerTest extends SerializerTester<Rectangle> {
	
	@Override
	protected Rectangle buildTarget() {
		Rectangle rect0 = new Rectangle(1.0f, 1.0f, null, null, null);
		rect0.getAppearance(true).setColor(Colorf.RED);
		StaticTransform.translate(rect0, -4.0f, 2.8f, 0.0f);
		
		return rect0;
	}
	
	@Override
	protected boolean compareResults(Rectangle target, Rectangle deserializedObject) {
		return target.getSize().equals(deserializedObject.getSize()) && target.getColor().equals(deserializedObject.getColor());
	}
	
}
