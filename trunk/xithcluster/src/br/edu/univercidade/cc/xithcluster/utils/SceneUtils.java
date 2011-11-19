package br.edu.univercidade.cc.xithcluster.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Material;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Transform;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.scenegraph.primitives.GeomFactory;
import org.xith3d.scenegraph.primitives.Rectangle.ZeroPointLocation;
import br.edu.univercidade.cc.xithcluster.nodes.lights.DirectionalLight;
import br.edu.univercidade.cc.xithcluster.nodes.lights.SpotLight;
import br.edu.univercidade.cc.xithcluster.nodes.primitives.Cube;
import br.edu.univercidade.cc.xithcluster.nodes.primitives.Cylinder;
import br.edu.univercidade.cc.xithcluster.nodes.primitives.Rectangle;
import br.edu.univercidade.cc.xithcluster.nodes.primitives.Ring;
import br.edu.univercidade.cc.xithcluster.nodes.primitives.Sphere;

public final class SceneUtils {
	
	private SceneUtils() {
	}
	
	// /////////////////////////////////
	// RECTANGLE
	// /////////////////////////////////
	
	public static Rectangle addRectangle(GroupNode parent, String name, float width, float height, Tuple3f translation, Tuple3f rotation, Texture2D texture) {
		TransformGroup translationTransform;
		Rectangle rectangle;
		
		rectangle = new Rectangle(name, width, height, ZeroPointLocation.CENTER_CENTER, texture, new Tuple3f(0.0f, 0.0f, 0.0f), null, null);
		
		translationTransform = createTransformBranchAndAddShape(translation, rotation, rectangle);
		
		parent.addChild(translationTransform);
		
		return rectangle;
	}
	
	// /////////////////////////////////
	// CYLINDER
	// /////////////////////////////////
	
	public static Cylinder addCylinder(GroupNode parent, String name, float radius, float height, float taper, boolean closed, int slices, Tuple3f translation, Tuple3f rotation, Colorf emissiveColor) {
		Appearance appearance = createAppearanceWithEmissiveColor(emissiveColor);
		
		return addCylinder(parent, name, radius, height, taper, closed, slices, translation, rotation, appearance);
	}
	
	private static Cylinder addCylinder(GroupNode parent, String name, float radius, float height, float taper, boolean closed, int slices, Tuple3f translation, Tuple3f rotation, Appearance appearance) {
		TransformGroup translationTransform;
		Cylinder cylinder;
		
		cylinder = new Cylinder(name, radius, height, taper, closed, slices, getFeaturesFromAppearancePlusCoordinatesAndNormals(appearance), false, 2, appearance);
		
		translationTransform = createTransformBranchAndAddShape(translation, rotation, cylinder);
		
		parent.addChild(translationTransform);
		
		return cylinder;
	}
	
	// /////////////////////////////////
	// RING
	// /////////////////////////////////
	
	public static Ring addRing(GroupNode parent, String name, float radius, float alpha, int slices, Tuple3f translation, Tuple3f rotation, Colorf emissiveColor) {
		Appearance appearance = createAppearanceWithEmissiveColor(emissiveColor);
		
		return addRing(parent, name, radius, alpha, slices, translation, rotation, appearance);
	}
	
	private static Ring addRing(GroupNode parent, String name, float radius, float alpha, int slices, Tuple3f translation, Tuple3f rotation, Appearance appearance) {
		TransformGroup translationTransform;
		Ring ring;
		
		ring = new Ring(name, radius, alpha, slices, getFeaturesFromAppearancePlusCoordinatesAndNormals(appearance), false, 2, appearance);
		
		translationTransform = createTransformBranchAndAddShape(translation, rotation, ring);
		
		parent.addChild(translationTransform);
		
		return ring;
	}
	
	// /////////////////////////////////
	// SPHERE
	// /////////////////////////////////
	
	public static Sphere addSphere(GroupNode parent, String name, float radius, int slices, int stacks, Tuple3f translation, Colorf emissiveColor) {
		Appearance appearance = createAppearanceWithEmissiveColor(emissiveColor);
		
		return addSphere(parent, name, radius, slices, stacks, translation, appearance);
	}
	
	public static Sphere addSphere(GroupNode parent, String name, float radius, int slices, int stacks, Tuple3f translation, Texture2D texture) {
		Appearance appearance;
		
		appearance = new Appearance();
		appearance.setTexture(texture);
		
		return addSphere(parent, name, radius, slices, stacks, translation, appearance);
	}
	
	public static Sphere addSphere(GroupNode parent, String name, float radius, int slices, int stacks, Tuple3f translation, Appearance appearance) {
		TransformGroup transformGroup;
		Sphere sphere;
		
		sphere = new Sphere(name, 0.0f, 0.0f, 0.0f, radius, 20, 20, getFeaturesFromAppearancePlusCoordinatesAndNormals(appearance), false, 2, appearance);
		
		transformGroup = createTranslationNodeAndAddShape(translation, sphere);
		
		parent.addChild(transformGroup);
		
		return sphere;
	}
	
	// /////////////////////////////////
	// CUBE
	// /////////////////////////////////
	
	public static Cube addCube(GroupNode parent, String name, float side, Tuple3f translation, Tuple3f rotation, Colorf emissiveColor) {
		Appearance appearance = createAppearanceWithEmissiveColor(emissiveColor);
		
		return addCube(parent, name, side, translation, rotation, appearance);
	}
	
	public static Cube addCube(GroupNode parent, String name, float side, Tuple3f translation, Tuple3f rotation, Texture2D texture) {
		Appearance appearance;
		
		appearance = new Appearance();
		appearance.setTexture(texture);
		
		return addCube(parent, name, side, translation, rotation, appearance);
	}
	
	public static Cube addCube(GroupNode parent, String name, float side, Tuple3f translation, Tuple3f rotation, Appearance appearance) {
		TransformGroup transformBranch;
		Cube cube;
		
		cube = new Cube(name, side, getFeaturesFromAppearancePlusCoordinatesAndNormals(appearance), false, 2, appearance);
		
		transformBranch = createTransformBranchAndAddShape(translation, rotation, cube);
		
		parent.addChild(transformBranch);
		
		return cube;
	}
	
	// /////////////////////////////////
	// DIRECTIONAL LIGHT
	// /////////////////////////////////
	
	public static DirectionalLight addDirectionalLight(GroupNode parent, String name, Colorf color, Vector3f direction) {
		DirectionalLight directionalLight = new DirectionalLight(true, color, direction);
		
		directionalLight.setName(name);
		parent.addChild(directionalLight);
		
		return directionalLight;
	}
	
	// /////////////////////////////////
	// SPOT LIGHT
	// /////////////////////////////////
	
	public static SpotLight addSpotLight(GroupNode parent, String name, Colorf color, Tuple3f location, Tuple3f direction, Tuple3f attenuation, float spreadAngle, float concentration) {
		SpotLight spotLight = new SpotLight(true, color, location, direction, attenuation, spreadAngle, concentration);
		
		spotLight.setName(name);
		parent.addChild(spotLight);
		
		return spotLight;
	}
	
	// /////////////////////////////////
	// TEXTURE LOADING
	// /////////////////////////////////
	
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
	
	// /////////////////////////////////
	// UTILS
	// /////////////////////////////////
	
	private static TransformGroup createTransformBranchAndAddShape(Tuple3f translation, Tuple3f rotation, Shape3D shape) {
		Transform transform;
		TransformGroup translationTransform;
		TransformGroup rotationTransform;
		
		transform = new Transform();
		transform.setTranslation(translation);
		translationTransform = new TransformGroup(transform.getTransform());
		
		transform = new Transform();
		transform.setRotation(rotation);
		rotationTransform = new TransformGroup(transform.getTransform());
		
		translationTransform.addChild(rotationTransform);
		
		rotationTransform.addChild(shape);
		
		return translationTransform;
	}
	
	private static TransformGroup createTranslationNodeAndAddShape(Tuple3f translation, Shape3D shape) {
		Transform transform;
		TransformGroup transformGroup;
		transform = new Transform();
		
		transform.setTranslation(translation);
		transformGroup = new TransformGroup(transform.getTransform());
		transformGroup.addChild(shape);
		
		return transformGroup;
	}
	
	private static Appearance createAppearanceWithEmissiveColor(Colorf emissiveColor) {
		Appearance appearance;
		Material material;
		
		material = new Material();
		material.setEmissiveColor(emissiveColor);
		material.setColorTarget(Material.NONE);
		material.setLightingEnabled(true);
		
		appearance = new Appearance();
		appearance.setMaterial(material);
		return appearance;
	}
	
	private static int getFeaturesFromAppearancePlusCoordinatesAndNormals(Appearance appearance) {
		return Geometry.COORDINATES | Geometry.NORMALS | GeomFactory.getFeaturesFromAppearance(appearance);
	}
	
}
