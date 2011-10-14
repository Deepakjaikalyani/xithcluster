package br.edu.univercidade.cc.xithcluster;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Material;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Transform;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.scenegraph.primitives.GeomFactory;
import br.edu.univercidade.cc.xithcluster.primitives.Cube;
import br.edu.univercidade.cc.xithcluster.primitives.Sphere;


public final class SceneUtils {
	
	private SceneUtils() {
	}
	
	public static void addSphere(Group group, String name, float radius, Tuple3f translation, Colorf emissiveColor) {
		Appearance appearance;
		Material material;
		
		material = new Material();
		material.setEmissiveColor(emissiveColor);
		material.setColorTarget(Material.NONE);
		material.setLightingEnabled(true);
		
		appearance = new Appearance();
		appearance.setMaterial(material);
		
		addSphere(group, name, radius, translation, appearance);
	}
	
	public static void addSphere(Group group, String name, float radius, Tuple3f translation, Texture2D texture) {
		Appearance appearance;
		
		appearance = new Appearance();
		appearance.setTexture(texture);
		
		addSphere(group, name, radius, translation, appearance);
	}
	
	public static void addSphere(Group group, String name, float radius, Tuple3f translation, Appearance appearance) {
		Transform transform;
		TransformGroup transformGroup;
		Sphere sphere;
		
		transform = new Transform();
		transform.setTranslation(translation);
		transformGroup = new TransformGroup(transform.getTransform());
		
		group.addChild(transformGroup);
		
		sphere = new Sphere(name, 0.0f, 0.0f, 0.0f, radius, 20, 20, Geometry.COORDINATES | Geometry.NORMALS | GeomFactory.getFeaturesFromAppearance(appearance), false, 2, appearance);
		
		transformGroup.addChild(sphere);
	}

	public static void addCube(GroupNode parent, String name, float side, Tuple3f translation, Tuple3f rotation, Colorf emissiveColor) {
		Appearance appearance;
		Material material;
		
		material = new Material();
		material.setEmissiveColor(emissiveColor);
		material.setColorTarget(Material.NONE);
		material.setLightingEnabled(true);
		
		appearance = new Appearance();
		appearance.setMaterial(material);
		
		addCube(parent, name, side, translation, rotation, appearance);
	}
	
	public static void addCube(GroupNode parent, String name, float side, Tuple3f translation, Tuple3f rotation, Texture2D texture) {
		Appearance appearance;
		
		appearance = new Appearance();
		appearance.setTexture(texture);
		
		addCube(parent, name, side, translation, rotation, appearance);
	}
	
	public static void addCube(GroupNode parent, String name, float side, Tuple3f translation, Tuple3f rotation, Appearance appearance) {
		Transform transform;
		TransformGroup translationTransform;
		TransformGroup rotationTransform;
		Cube cube;
		
		transform = new Transform();
		transform.setTranslation(translation);
		translationTransform = new TransformGroup(transform.getTransform());
		
		transform = new Transform();
		transform.setRotation(rotation);
		rotationTransform = new TransformGroup(transform.getTransform());
		
		translationTransform.addChild(rotationTransform);
		
		cube = new Cube(name, side, Geometry.COORDINATES | Geometry.NORMALS | GeomFactory.getFeaturesFromAppearance(appearance), false, 2, appearance);
		
		rotationTransform.addChild(cube);
		
		parent.addChild(translationTransform);
	}
	
	public static Texture2D loadTexture2D(String fileName) {
		Texture2D texture2D;
		
		try {
			texture2D = TextureLoader.getInstance().loadTexture(new FileInputStream(fileName));
		} catch (FileNotFoundException e) {
			// TODO:
			throw new RuntimeException("Texture not found");
		}
		
		return texture2D;
	}
	
}
