package br.edu.univercidade.cc.xithcluster.serialization;

import java.io.ByteArrayInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.BitSet;
import javax.imageio.ImageIO;
import static junit.framework.Assert.*;
import static br.edu.univercidade.cc.xithcluster.utils.AssertExtention.*;
import org.jagatoo.opengl.enums.BlendFunction;
import org.jagatoo.opengl.enums.BlendMode;
import org.jagatoo.opengl.enums.ColorTarget;
import org.jagatoo.opengl.enums.CompareFunction;
import org.jagatoo.opengl.enums.DrawMode;
import org.jagatoo.opengl.enums.FaceCullMode;
import org.jagatoo.opengl.enums.LinePattern;
import org.jagatoo.opengl.enums.PerspectiveCorrectionMode;
import org.jagatoo.opengl.enums.ShadeModel;
import org.jagatoo.opengl.enums.StencilFace;
import org.jagatoo.opengl.enums.StencilOperation;
import org.jagatoo.opengl.enums.TestFunction;
import org.jagatoo.opengl.enums.TexCoordGenMode;
import org.jagatoo.opengl.enums.TextureBoundaryMode;
import org.jagatoo.opengl.enums.TextureCombineFunction;
import org.jagatoo.opengl.enums.TextureCombineMode;
import org.jagatoo.opengl.enums.TextureCombineSource;
import org.jagatoo.opengl.enums.TextureCompareMode;
import org.jagatoo.opengl.enums.TextureFilter;
import org.jagatoo.opengl.enums.TextureFormat;
import org.jagatoo.opengl.enums.TextureImageFormat;
import org.jagatoo.opengl.enums.TextureImageInternalFormat;
import org.jagatoo.opengl.enums.TextureMode;
import org.junit.Test;
import org.openmali.spatial.bounds.BoundingPolytope;
import org.openmali.spatial.bounds.BoundsType;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple2f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.openmali.vecmath2.Vector4f;
import org.xith3d.scenegraph.ColoringAttributes;
import org.xith3d.scenegraph.GeomNioFloatData;
import org.xith3d.scenegraph.GeomNioIntData;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Geometry.Optimization;
import org.xith3d.scenegraph.GeometryDataContainer;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.LineAttributes;
import org.xith3d.scenegraph.Material;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.PointAttributes;
import org.xith3d.scenegraph.PolygonAttributes;
import org.xith3d.scenegraph.RenderingAttributes;
import org.xith3d.scenegraph.StencilFuncSeparate;
import org.xith3d.scenegraph.StencilMaskSeparate;
import org.xith3d.scenegraph.StencilOpSeparate;
import org.xith3d.scenegraph.TexCoordGeneration;
import org.xith3d.scenegraph.TexCoordGeneration.CoordMode;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.TextureAttributes;
import org.xith3d.scenegraph.TextureImage;
import org.xith3d.scenegraph.TextureImage2D;
import org.xith3d.scenegraph.TextureUnit;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransparencyAttributes;
import org.xith3d.scenegraph.TriangleStripArray;
import org.xith3d.schedops.movement.GroupAnimator;
import org.xith3d.schedops.movement.GroupRotator;
import org.xith3d.schedops.movement.GroupTranslator;
import org.xith3d.schedops.movement.TransformationDirectives;
import org.xith3d.schedops.movement.TransformationDirectives.AxisOrder;
import br.edu.univercidade.cc.xithcluster.comparators.TransformationDirectivesComparator;
import br.edu.univercidade.cc.xithcluster.serialization.SerializationHelper;
import br.edu.univercidade.cc.xithcluster.utils.BufferUtils;
import br.edu.univercidade.cc.xithcluster.utils.PrivateAccessor;

public class SerializationHelperTest {
	
	private static final float[] GEOMETRY1_TEXTURE_COORDINATE = new float[] {
			3.0f, 5.0f, 7.0f
	};
	
	private static final int SIZE_OF_FLOAT = 32;
	
	private static final byte BYTE_DATA = (byte) 11;
	
	private static final int INT_DATA = 11;
	
	private static final long LONG_DATA = 19L;
	
	private static final float FLOAT_DATA = 11.0f;
	
	private static final int MEM_ALLOC_UNIT = 256;
	
	private static final BitSet BIT_SET;
	
	private static final Vector3f VECTOR_3F = new Vector3f(3.0f, 5.0f, 7.0f);
	
	private static final Vector4f VECTOR_4F = new Vector4f(3.0f, 5.0f, 7.0f, 11.0f);
	
	private static final Point3f POINT_3F = new Point3f(3.0f, 5.0f, 7.0f);
	
	private static final Tuple2f TUPLE_2F = new Tuple2f(3.0f, 5.0f);
	
	private static final Tuple3f TUPLE_3F = new Tuple3f(3.0f, 5.0f, 7.0f);
	
	private static final Colorf COLOR_F = new Colorf(3.0f, 5.0f, 7.0f, 0.0f);
	
	private static final Matrix4f MATRIX_4F = new Matrix4f(new float[] {
			0.003333902f, 0.28087205f, 0.9597394f, 13.0f, -0.7538963f, 0.6312473f, -0.1821185f, 17.0f, -0.6569849f, -0.7229368f, 0.21385293f, 19.0f, 0.0f, 0.0f, 0.0f, 1.0f
	});
	
	private static final Transform3D TRANSFORM_3D;
	
	private static final FloatBuffer FLOAT_BUFFER;
	
	private static final IntBuffer INT_BUFFER;
	
	private static final float[] FLOAT_ARRAY;
	
	private static final int[] INT_ARRAY;
	
	private static final long[] LONG_ARRAY;
	
	private static final byte[] BYTE_ARRAY;
	
	private static final GeomNioFloatData GEOM_NIO_FLOAT_DATA;
	
	private static final GeomNioIntData GEOM_NIO_INT_DATA;
	
	private static final Texture TEXTURE;
	
	private static final TextureImage TEXTURE_IMAGE;
	
	private static final TextureAttributes TEXTURE_ATTRIBUTES;
	
	private static final TexCoordGeneration TEX_COORD_GENERATION;
	
	private static final Geometry GEOMETRY1;
	
	private static final TransformationDirectives TRANSFORMATION_DIRECTIVES1 = new TransformationDirectives(1.0f, 2.0f, 3.0f, 5.0f, 7.0f, 9.0f, AxisOrder.ZYX);
	
	private static final TransformationDirectives TRANSFORMATION_DIRECTIVES2 = new TransformationDirectives(new Vector3f(1.0f, 2.0f, 3.0f), 5.0f, 9.0f);
	
	private static final GroupRotator GROUP_ROTATOR1 = new GroupRotator(TRANSFORMATION_DIRECTIVES1);
	
	private static final GroupTranslator GROUP_TRANSLATOR1 = new GroupTranslator(TRANSFORMATION_DIRECTIVES2);
	
	static {
		BIT_SET = new BitSet();
		BIT_SET.set(3);
		BIT_SET.set(5);
		BIT_SET.set(7);
		BIT_SET.set(11);
		BIT_SET.set(13);
		BIT_SET.set(17);
		BIT_SET.set(19);
		BIT_SET.set(21);
		
		TRANSFORM_3D = new Transform3D();
		TRANSFORM_3D.setRotation(new Tuple3f(5.0f, 7.0f, 11.0f));
		TRANSFORM_3D.setTranslation(13.0f, 17.0f, 19.0f);
		
		FLOAT_ARRAY = new float[MEM_ALLOC_UNIT];
		for (int i = 0; i < MEM_ALLOC_UNIT; i++) {
			FLOAT_ARRAY[i] = FLOAT_DATA;
		}
		
		INT_ARRAY = new int[MEM_ALLOC_UNIT];
		for (int i = 0; i < MEM_ALLOC_UNIT; i++) {
			INT_ARRAY[i] = INT_DATA;
		}
		
		LONG_ARRAY = new long[MEM_ALLOC_UNIT];
		for (int i = 0; i < MEM_ALLOC_UNIT; i++) {
			LONG_ARRAY[i] = LONG_DATA;
		}
		
		BYTE_ARRAY = new byte[MEM_ALLOC_UNIT];
		for (int i = 0; i < MEM_ALLOC_UNIT; i++) {
			BYTE_ARRAY[i] = BYTE_DATA;
		}
		
		FLOAT_BUFFER = FloatBuffer.allocate(MEM_ALLOC_UNIT);
		for (int i = 0; i < MEM_ALLOC_UNIT; i++) {
			FLOAT_BUFFER.put(i, FLOAT_DATA);
		}
		
		INT_BUFFER = IntBuffer.allocate(MEM_ALLOC_UNIT);
		for (int i = 0; i < MEM_ALLOC_UNIT; i++) {
			INT_BUFFER.put(i, INT_DATA);
		}
		
		GEOM_NIO_FLOAT_DATA = new GeomNioFloatData(MEM_ALLOC_UNIT, SIZE_OF_FLOAT, false);
		for (int i = 0; i < MEM_ALLOC_UNIT; i++) {
			GEOM_NIO_FLOAT_DATA.set(i, FLOAT_DATA);
		}
		
		GEOM_NIO_INT_DATA = new GeomNioIntData(MEM_ALLOC_UNIT, SIZE_OF_FLOAT, false);
		for (int i = 0; i < MEM_ALLOC_UNIT; i++) {
			GEOM_NIO_INT_DATA.set(i, INT_DATA);
		}
		
		TEXTURE_IMAGE = new TextureImage2D(TextureImageFormat.RGBA, 256, 256, 256, 265, true, TextureImageInternalFormat.RGBA);
		try {
			BufferedImage image = ImageIO.read(new FileInputStream("resources/crate.png"));
			((TextureImage2D) TEXTURE_IMAGE).setImageData(image);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		TEXTURE = new Texture2D(TextureFormat.RGBA);
		TEXTURE.setName("texture1");
		TEXTURE.setEnabled(true);
		TEXTURE.setBoundaryModeS(TextureBoundaryMode.CLAMP_TO_BORDER);
		TEXTURE.setBoundaryModeT(TextureBoundaryMode.CLAMP_TO_EDGE);
		TEXTURE.setBoundaryColor(COLOR_F);
		TEXTURE.setBoundaryWidth(3);
		TEXTURE.setFilter(TextureFilter.ANISOTROPIC_4);
		((Texture2D) TEXTURE).setImage(0, TEXTURE_IMAGE);
		
		TEXTURE_ATTRIBUTES = new TextureAttributes(TextureMode.MODULATE, TRANSFORM_3D, COLOR_F, PerspectiveCorrectionMode.NICEST);
		TEXTURE_ATTRIBUTES.setName("textureAttributes1");
		TEXTURE_ATTRIBUTES.setCombineRGBMode(TextureCombineMode.MODULATE);
		TEXTURE_ATTRIBUTES.setCombineAlphaMode(TextureCombineMode.INTERPOLATE);
		
		TEXTURE_ATTRIBUTES.setCombineRGBSource(0, TextureCombineSource.CONSTANT_COLOR);
		TEXTURE_ATTRIBUTES.setCombineRGBSource(1, TextureCombineSource.OBJECT_COLOR);
		TEXTURE_ATTRIBUTES.setCombineRGBSource(2, TextureCombineSource.PREVIOUS_TEXTURE_UNIT);
		
		TEXTURE_ATTRIBUTES.setCombineAlphaSource(0, TextureCombineSource.TEXTURE0);
		TEXTURE_ATTRIBUTES.setCombineAlphaSource(1, TextureCombineSource.TEXTURE1);
		TEXTURE_ATTRIBUTES.setCombineAlphaSource(2, TextureCombineSource.TEXTURE_COLOR);
		
		TEXTURE_ATTRIBUTES.setCombineRGBFunction(0, TextureCombineFunction.ONE_MINUS_SRC_ALPHA);
		TEXTURE_ATTRIBUTES.setCombineRGBFunction(1, TextureCombineFunction.ONE_MINUS_SRC_COLOR);
		TEXTURE_ATTRIBUTES.setCombineRGBFunction(2, TextureCombineFunction.SRC_ALPHA);
		
		TEXTURE_ATTRIBUTES.setCombineAlphaFunction(0, TextureCombineFunction.ONE_MINUS_SRC_COLOR);
		TEXTURE_ATTRIBUTES.setCombineAlphaFunction(1, TextureCombineFunction.SRC_ALPHA);
		TEXTURE_ATTRIBUTES.setCombineAlphaFunction(2, TextureCombineFunction.SRC_COLOR);
		
		TEXTURE_ATTRIBUTES.setCombineRGBScale(3);
		TEXTURE_ATTRIBUTES.setCombineAlphaScale(5);
		TEXTURE_ATTRIBUTES.setCompareMode(TextureCompareMode.COMPARE_R_TO_TEXTURE);
		TEXTURE_ATTRIBUTES.setCompareFunction(CompareFunction.LOWER_OR_EQUAL);
		
		TEX_COORD_GENERATION = new TexCoordGeneration(TexCoordGenMode.OBJECT_LINEAR, CoordMode.TEXTURE_COORDINATES_3, VECTOR_4F, VECTOR_4F, VECTOR_4F, VECTOR_4F);
		TEX_COORD_GENERATION.setName("texCoordGeneration1");
		TEX_COORD_GENERATION.setEnabled(true);
		
		// Creating test geometry
		GEOMETRY1 = new TriangleStripArray(300, 5000);
		
		GEOMETRY1.setName("geometry1");
		
		GeometryDataContainer geometryDataContainer = (GeometryDataContainer) PrivateAccessor.getPrivateField(GEOMETRY1, "dataContainer");
		geometryDataContainer.setValidVertexCount(5000);
		PrivateAccessor.setPrivateField(geometryDataContainer, "coordsOffset", 0);
		PrivateAccessor.setPrivateField(geometryDataContainer, "numIndices", 300);
		geometryDataContainer.setInitialIndex(0);
		PrivateAccessor.setPrivateField(geometryDataContainer, "indexData", GEOM_NIO_INT_DATA);
		PrivateAccessor.setPrivateField(geometryDataContainer, "coords", GEOM_NIO_FLOAT_DATA);
		geometryDataContainer.setNormalData(GEOM_NIO_FLOAT_DATA);
		geometryDataContainer.setColorData(GEOM_NIO_FLOAT_DATA);
		geometryDataContainer.setTextureCoordinate(0, 0, GEOMETRY1_TEXTURE_COORDINATE);
		geometryDataContainer.setTextureCoordinate(1, 0, GEOMETRY1_TEXTURE_COORDINATE);
		geometryDataContainer.setTextureCoordinate(2, 0, GEOMETRY1_TEXTURE_COORDINATE);
		PrivateAccessor.setPrivateField(geometryDataContainer, "vertexAttribs", new GeomNioFloatData[] {
				GEOM_NIO_FLOAT_DATA, GEOM_NIO_FLOAT_DATA, GEOM_NIO_FLOAT_DATA
		});
		geometryDataContainer.setStripCounts(INT_ARRAY);
		PrivateAccessor.setPrivateField(geometryDataContainer, "texCoordSetMap", INT_ARRAY);
		PrivateAccessor.setPrivateField(geometryDataContainer, "texCoordSetMap_public", INT_ARRAY);
		PrivateAccessor.setPrivateField(geometryDataContainer, "textureUnitSize", INT_ARRAY);
		PrivateAccessor.setPrivateField(geometryDataContainer, "colorsSize", 3);
		PrivateAccessor.setPrivateField(geometryDataContainer, "vertexFormat", 7);
		PrivateAccessor.setPrivateField(geometryDataContainer, "hasNormals", true);
		PrivateAccessor.setPrivateField(geometryDataContainer, "hasColors", true);
		PrivateAccessor.setPrivateField(geometryDataContainer, "normalsOffset", 11L);
		PrivateAccessor.setPrivateField(geometryDataContainer, "colorsOffset", 13L);
		PrivateAccessor.setPrivateField(geometryDataContainer, "texCoordsOffsets", LONG_ARRAY);
		PrivateAccessor.setPrivateField(geometryDataContainer, "vertexAttribsOffsets", LONG_ARRAY);
		GEOMETRY1.setOptimization(Optimization.USE_DISPLAY_LISTS);
		GEOMETRY1.setBoundsDirty();
	}
	
	private ByteArrayOutputStream out;
	
	public DataOutputStream getOutputStream() {
		return new DataOutputStream(out = new ByteArrayOutputStream());
	}
	
	public DataInputStream getInputStream() {
		return new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
	}
	
	@Test
	public void testShouldReadAndWriteEnum() throws IOException {
		// testing null check implementation
		SerializationHelper.writeEnum(getOutputStream(), null);
		assertNull(SerializationHelper.readEnum(getInputStream(), FaceCullMode.values()));
		
		SerializationHelper.writeEnum(getOutputStream(), FaceCullMode.BACK);
		assertEquals(FaceCullMode.BACK, SerializationHelper.readEnum(getInputStream(), FaceCullMode.values()));
	}
	
	@Test
	public void testShouldReadAndWriteVector3f() throws IOException {
		// testing null check implementation
		SerializationHelper.writeVector3f(getOutputStream(), null);
		assertNull(SerializationHelper.readVector3f(getInputStream()));
		
		SerializationHelper.writeVector3f(getOutputStream(), VECTOR_3F);
		assertEquals(VECTOR_3F, SerializationHelper.readVector3f(getInputStream()));
	}
	
	@Test
	public void testShouldReadAndWritePoint() throws IOException {
		// testing null check implementation
		SerializationHelper.writePoint3f(getOutputStream(), null);
		assertNull(SerializationHelper.readPoint3f(getInputStream()));
		
		SerializationHelper.writePoint3f(getOutputStream(), POINT_3F);
		assertEquals(POINT_3F, SerializationHelper.readPoint3f(getInputStream()));
	}
	
	@Test
	public void testShouldReadAndWriteTuple3f() throws IOException {
		// testing null check implementation
		SerializationHelper.writeTuple3f(getOutputStream(), null);
		assertNull(SerializationHelper.readTuple3f(getInputStream()));
		
		SerializationHelper.writeTuple3f(getOutputStream(), TUPLE_3F);
		assertEquals(TUPLE_3F, SerializationHelper.readTuple3f(getInputStream()));
	}
	
	@Test
	public void testShouldReadAndWriteColor() throws IOException {
		// testing null check implementation
		SerializationHelper.writeColorf(getOutputStream(), null);
		assertNull(SerializationHelper.readColorf(getInputStream()));
		
		SerializationHelper.writeColorf(getOutputStream(), COLOR_F);
		assertEquals(COLOR_F, SerializationHelper.readColorf(getInputStream()));
	}
	
	@Test
	public void testShouldReadAndWriteTransform3D() throws IOException {
		// testing null check implementation
		SerializationHelper.writeTransform3D(getOutputStream(), null);
		assertNull(SerializationHelper.readTransform3D(getInputStream()));
		
		SerializationHelper.writeTransform3D(getOutputStream(), TRANSFORM_3D);
		assertEquals(TRANSFORM_3D, SerializationHelper.readTransform3D(getInputStream()));
	}
	
	@Test
	public void testShouldReadAndWriteMatrix4f() throws IOException {
		// testing null check implementation
		SerializationHelper.writeMatrix4f(getOutputStream(), null);
		assertNull(SerializationHelper.readMatrix4f(getInputStream()));
		
		SerializationHelper.writeMatrix4f(getOutputStream(), MATRIX_4F);
		assertEquals(MATRIX_4F, SerializationHelper.readMatrix4f(getInputStream()));
	}
	
	@Test
	public void testShouldReadAndWriteString() throws IOException {
		// testing null check implementation
		SerializationHelper.writeString(getOutputStream(), null);
		assertNull(SerializationHelper.readString(getInputStream()));
		
		SerializationHelper.writeString(getOutputStream(), "something");
		assertEquals(SerializationHelper.readString(getInputStream()), "something");
	}
	
	@Test
	public void testShouldReadAndWriteVector4f() throws IOException {
		// testing null check implementation
		SerializationHelper.writeVector4f(getOutputStream(), null);
		assertNull(SerializationHelper.readVector4f(getInputStream()));
		
		SerializationHelper.writeVector4f(getOutputStream(), VECTOR_4F);
		assertEquals(VECTOR_4F, SerializationHelper.readVector4f(getInputStream()));
	}
	
	@Test
	public void testShouldReadAndWriteClass() throws IOException {
		// testing null check implementation
		SerializationHelper.writeClass(getOutputStream(), null);
		assertNull(SerializationHelper.readClass(getInputStream()));
		
		SerializationHelper.writeClass(getOutputStream(), Node.class);
		assertEquals(Node.class, SerializationHelper.readClass(getInputStream()));
	}
	
	@Test
	public void testShouldReadAndWriteClassByType() throws IOException {
		// testing null check implementation
		SerializationHelper.writeClass(getOutputStream(), null);
		assertNull(SerializationHelper.readClass(getInputStream(), Node.class));
		
		SerializationHelper.writeClass(getOutputStream(), GroupNode.class);
		assertEquals(GroupNode.class, SerializationHelper.readClass(getInputStream(), Node.class));
	}
	
	@Test
	public void testShouldReadAndWriteGeomNioFloatData() throws IOException {
		// testing null check implementation
		SerializationHelper.writeGeomNioFloatData(getOutputStream(), null);
		assertNull(SerializationHelper.readGeomNioFloatData(getInputStream()));
		
		SerializationHelper.writeGeomNioFloatData(getOutputStream(), GEOM_NIO_FLOAT_DATA);
		assertTrue(BufferUtils.equals(GEOM_NIO_FLOAT_DATA.getBuffer(), SerializationHelper.readGeomNioFloatData(getInputStream()).getBuffer()));
	}
	
	@Test
	public void testShouldReadAndWriteFloatArray() throws IOException {
		// testing null check implementation
		SerializationHelper.writeFloatArray(getOutputStream(), null);
		assertNull(SerializationHelper.readFloatArray(getInputStream()));
		
		SerializationHelper.writeFloatArray(getOutputStream(), FLOAT_ARRAY);
		assertTrue(Arrays.equals(SerializationHelper.readFloatArray(getInputStream()), FLOAT_ARRAY));
	}
	
	@Test
	public void testShouldReadAndWriteByteArray() throws IOException {
		// testing null check implementation
		SerializationHelper.writeByteArray(getOutputStream(), null);
		assertNull(SerializationHelper.readByteArray(getInputStream()));
		
		SerializationHelper.writeByteArray(getOutputStream(), BYTE_ARRAY);
		assertTrue(Arrays.equals(BYTE_ARRAY, SerializationHelper.readByteArray(getInputStream())));
	}
	
	@Test
	public void testShouldReadAndWriteGeomNioIntData() throws IOException {
		// testing null check implementation
		SerializationHelper.writeGeomNioIntData(getOutputStream(), null);
		assertNull(SerializationHelper.readGeomNioIntData(getInputStream()));
		
		SerializationHelper.writeGeomNioIntData(getOutputStream(), GEOM_NIO_INT_DATA);
		assertTrue(BufferUtils.equals(GEOM_NIO_INT_DATA.getBuffer(), SerializationHelper.readGeomNioIntData(getInputStream()).getBuffer()));
	}
	
	@Test
	public void testShouldReadAndWriteIntArray() throws IOException {
		// testing null check implementation
		SerializationHelper.writeIntArray(getOutputStream(), null);
		assertNull(SerializationHelper.readIntArray(getInputStream()));
		
		SerializationHelper.writeIntArray(getOutputStream(), INT_ARRAY);
		assertTrue(Arrays.equals(INT_ARRAY, SerializationHelper.readIntArray(getInputStream())));
	}
	
	@Test
	public void testShouldReadAndWriteMaterial() throws IOException {
		Material material;
		
		// testing null check implementation
		SerializationHelper.writeMaterial(getOutputStream(), null);
		assertNull(SerializationHelper.readMaterial(getInputStream()));
		
		material = new Material(COLOR_F, COLOR_F, COLOR_F, COLOR_F, 11.0f, ColorTarget.AMBIENT_AND_DIFFUSE, false, true);
		material.setName("material1");
		
		SerializationHelper.writeMaterial(getOutputStream(), material);
		material = SerializationHelper.readMaterial(getInputStream());
		
		assertEquals("material1", material.getName());
		assertEquals(COLOR_F, material.getAmbientColor());
		assertEquals(COLOR_F, material.getEmissiveColor());
		assertEquals(COLOR_F, material.getDiffuseColor());
		assertEquals(COLOR_F, material.getSpecularColor());
		assertEquals(11.0f, material.getShininess());
		assertEquals(ColorTarget.AMBIENT_AND_DIFFUSE, material.getColorTarget());
		assertFalse(material.getNormalizeNormals());
		assertTrue(material.isLightingEnabled());
	}
	
	@Test
	public void testShouldReadAndWriteTransparencyAttributes() throws IOException {
		TransparencyAttributes transparencyAttributes;
		
		// testing null check implementation
		SerializationHelper.writeTransparencyAttributes(getOutputStream(), null);
		assertNull(SerializationHelper.readTransparencyAttributes(getInputStream()));
		
		transparencyAttributes = new TransparencyAttributes(BlendMode.NICEST, 0.17f, BlendFunction.DST_ALPHA, BlendFunction.DST_COLOR, true, true);
		transparencyAttributes.setName("transparencyAttributes1");
		
		SerializationHelper.writeTransparencyAttributes(getOutputStream(), transparencyAttributes);
		transparencyAttributes = SerializationHelper.readTransparencyAttributes(getInputStream());
		
		assertEquals("transparencyAttributes1", transparencyAttributes.getName());
		assertEquals(BlendMode.NICEST, transparencyAttributes.getMode());
		assertEquals(0.17f, transparencyAttributes.getTransparency());
		assertEquals(BlendFunction.DST_ALPHA, transparencyAttributes.getSrcBlendFunction());
		assertEquals(BlendFunction.DST_COLOR, transparencyAttributes.getDstBlendFunction());
		assertTrue(transparencyAttributes.isSortEnabled());
		assertTrue(transparencyAttributes.isEnabled());
	}
	
	@Test
	public void testShouldReadAndWriteColoringAttributes() throws IOException {
		ColoringAttributes coloringAttributes;
		
		// testing null check implementation
		SerializationHelper.writeColoringAttributes(getOutputStream(), null);
		assertNull(SerializationHelper.readColoringAttributes(getInputStream()));
		
		coloringAttributes = new ColoringAttributes(1.0f, 1.0f, 1.0f, ShadeModel.NICEST);
		coloringAttributes.setName("coloringAttributes1");
		
		SerializationHelper.writeColoringAttributes(getOutputStream(), coloringAttributes);
		coloringAttributes = SerializationHelper.readColoringAttributes(getInputStream());
		
		assertEquals("coloringAttributes1", coloringAttributes.getName());
		assertEquals(new Colorf(1.0f, 1.0f, 1.0f), coloringAttributes.getColor());
		assertEquals(ShadeModel.NICEST, coloringAttributes.getShadeModel());
	}
	
	@Test
	public void testShouldReadAndWriteLineAttributes() throws IOException {
		LineAttributes lineAttributes;
		
		// testing null check implementation
		SerializationHelper.writeLineAttributes(getOutputStream(), null);
		assertNull(SerializationHelper.readLineAttributes(getInputStream()));
		
		lineAttributes = new LineAttributes(5.0f, LinePattern.DASHED_DOTTED, 3, 7, true);
		lineAttributes.setName("lineAttributes1");
		lineAttributes.setSortEnabled(true);
		
		SerializationHelper.writeLineAttributes(getOutputStream(), lineAttributes);
		lineAttributes = SerializationHelper.readLineAttributes(getInputStream());
		
		assertEquals("lineAttributes1", lineAttributes.getName());
		assertEquals(5.0f, lineAttributes.getLineWidth());
		assertEquals(LinePattern.DASHED_DOTTED, lineAttributes.getLinePattern());
		assertEquals(3, lineAttributes.getPatternScaleFactor());
		assertEquals(7, lineAttributes.getPatternMask());
		assertTrue(lineAttributes.isLineAntialiasingEnabled());
		assertTrue(lineAttributes.isSortEnabled());
	}
	
	@Test
	public void testShouldReadAndWritePointAttributes() throws IOException {
		PointAttributes pointAttributes;
		
		// testing null check implementation
		SerializationHelper.writePointAttributes(getOutputStream(), null);
		assertNull(SerializationHelper.readPointAttributes(getInputStream()));
		
		pointAttributes = new PointAttributes(3.0f, true);
		pointAttributes.setName("pointAttributes1");
		pointAttributes.setSortEnabled(true);
		
		SerializationHelper.writePointAttributes(getOutputStream(), pointAttributes);
		pointAttributes = SerializationHelper.readPointAttributes(getInputStream());
		
		assertEquals("pointAttributes1", pointAttributes.getName());
		assertEquals(3.0f, pointAttributes.getPointSize());
		assertTrue(pointAttributes.isPointAntialiasingEnabled());
		assertTrue(pointAttributes.isSortEnabled());
	}
	
	@Test
	public void testShouldReadAndWritePolygonAttributes() throws IOException {
		PolygonAttributes polygonAttributes;
		
		// testing null check implementation
		SerializationHelper.writePolygonAttributes(getOutputStream(), null);
		assertNull(SerializationHelper.readPolygonAttributes(getInputStream()));
		
		polygonAttributes = new PolygonAttributes(DrawMode.LINE, FaceCullMode.SWITCH, 3.0f, 7.0f, true, true, true);
		polygonAttributes.setName("polygonAttributes1");
		
		SerializationHelper.writePolygonAttributes(getOutputStream(), polygonAttributes);
		polygonAttributes = SerializationHelper.readPolygonAttributes(getInputStream());
		
		assertEquals("polygonAttributes1", polygonAttributes.getName());
		assertEquals(DrawMode.LINE, polygonAttributes.getDrawMode());
		assertEquals(FaceCullMode.SWITCH, polygonAttributes.getFaceCullMode());
		assertEquals(3.0f, polygonAttributes.getPolygonOffset());
		assertEquals(7.0f, polygonAttributes.getPolygonOffsetFactor());
		assertTrue(polygonAttributes.getBackFaceNormalFlip());
		assertTrue(polygonAttributes.isPolygonAntialiasingEnabled());
		assertTrue(polygonAttributes.isSortEnabled());
	}
	
	@Test
	public void testShouldReadAndWriteRenderingAttributes() throws IOException {
		RenderingAttributes renderingAttributes;
		
		// testing null check implementation
		SerializationHelper.writeRenderingAttributes(getOutputStream(), null);
		assertNull(SerializationHelper.readRenderingAttributes(getInputStream()));
		
		renderingAttributes = new RenderingAttributes(true, true, TestFunction.GREATER_OR_EQUAL, 3.0f, TestFunction.LESS_OR_EQUAL, true, true, StencilOperation.REPLACE, StencilOperation.INVERT, StencilOperation.ZERO, TestFunction.NEVER, 11, 13, true, true, true, true);
		renderingAttributes.setName("renderingAttributes1");
		
		SerializationHelper.writeRenderingAttributes(getOutputStream(), renderingAttributes);
		renderingAttributes = SerializationHelper.readRenderingAttributes(getInputStream());
		
		assertEquals("renderingAttributes1", renderingAttributes.getName());
		assertTrue(renderingAttributes.isDepthBufferEnabled());
		assertTrue(renderingAttributes.isDepthBufferWriteEnabled());
		assertEquals(TestFunction.GREATER_OR_EQUAL, renderingAttributes.getDepthTestFunction());
		assertEquals(3.0f, renderingAttributes.getAlphaTestValue());
		assertEquals(TestFunction.LESS_OR_EQUAL, renderingAttributes.getAlphaTestFunction());
		assertTrue(renderingAttributes.getIgnoreVertexColors());
		assertTrue(renderingAttributes.isStencilEnabled());
		assertEquals(StencilOperation.REPLACE, renderingAttributes.getStencilOpFail());
		assertEquals(StencilOperation.INVERT, renderingAttributes.getStencilOpZFail());
		assertEquals(StencilOperation.ZERO, renderingAttributes.getStencilOpZPass());
		assertEquals(TestFunction.NEVER, renderingAttributes.getStencilTestFunction());
		assertEquals(11, renderingAttributes.getStencilRef());
		assertEquals(13, renderingAttributes.getStencilMask());
		assertTrue(renderingAttributes.isRedWriteEnabled());
		assertTrue(renderingAttributes.isGreenWriteEnabled());
		assertTrue(renderingAttributes.isBlueWriteEnabled());
		assertTrue(renderingAttributes.isAlphaWriteEnabled());
	}
	
	@Test
	public void testShouldReadAndWriteStencilFunctionSeparate() throws IOException {
		StencilFuncSeparate stencilFuncSeparate;
		
		// testing null check implementation
		SerializationHelper.writeStencilFunctionSeparate(getOutputStream(), null);
		assertNull(SerializationHelper.readStencilFunctionSeparate(getInputStream()));
		
		stencilFuncSeparate = new StencilFuncSeparate(StencilFace.FRONT_AND_BACK, TestFunction.GREATER_OR_EQUAL);
		stencilFuncSeparate.setRef(3);
		stencilFuncSeparate.setMask(7);
		
		SerializationHelper.writeStencilFunctionSeparate(getOutputStream(), stencilFuncSeparate);
		stencilFuncSeparate = SerializationHelper.readStencilFunctionSeparate(getInputStream());
		
		assertEquals(StencilFace.FRONT_AND_BACK, stencilFuncSeparate.getFace());
		assertEquals(TestFunction.GREATER_OR_EQUAL, stencilFuncSeparate.getTestFunction());
		assertEquals(3, stencilFuncSeparate.getRef());
		assertEquals(7, stencilFuncSeparate.getMask());
	}
	
	@Test
	public void testShouldReadAndWriteStencilOperationSeparate() throws IOException {
		StencilOpSeparate stencilOpSeparate;
		
		// testing null check implementation
		SerializationHelper.writeStencilOperationSeparate(getOutputStream(), null);
		assertNull(SerializationHelper.readStencilOperationSeparate(getInputStream()));
		
		stencilOpSeparate = new StencilOpSeparate(StencilFace.FRONT_AND_BACK, StencilOperation.DECREMENT, StencilOperation.INCREMENT, StencilOperation.INVERT);
		
		SerializationHelper.writeStencilOperationSeparate(getOutputStream(), stencilOpSeparate);
		stencilOpSeparate = SerializationHelper.readStencilOperationSeparate(getInputStream());
		
		assertEquals(StencilFace.FRONT_AND_BACK, stencilOpSeparate.getFace());
		assertEquals(StencilOperation.DECREMENT, stencilOpSeparate.getSFail());
		assertEquals(StencilOperation.INCREMENT, stencilOpSeparate.getDPFail());
		assertEquals(StencilOperation.INVERT, stencilOpSeparate.getDPPass());
	}
	
	@Test
	public void testShouldReadAndWriteStencilMaskSeparate() throws IOException {
		StencilMaskSeparate stencilMaskSeparate;
		
		// testing null check implementation
		SerializationHelper.writeStencilMaskSeparate(getOutputStream(), null);
		assertNull(SerializationHelper.readStencilMaskSeparate(getInputStream()));
		
		stencilMaskSeparate = new StencilMaskSeparate(StencilFace.FRONT_AND_BACK, 7);
		
		SerializationHelper.writeStencilMaskSeparate(getOutputStream(), stencilMaskSeparate);
		stencilMaskSeparate = SerializationHelper.readStencilMaskSeparate(getInputStream());
		
		assertEquals(StencilFace.FRONT_AND_BACK, stencilMaskSeparate.getFace());
		assertEquals(7, stencilMaskSeparate.getMask());
	}
	
	@Test
	public void testShouldReadAndWriteTextureUnit() throws IOException {
		TextureUnit textureUnit;
		
		// testing null check implementation
		SerializationHelper.writeTextureUnit(getOutputStream(), null);
		assertNull(SerializationHelper.readTextureUnit(getInputStream()));
		
		textureUnit = new TextureUnit(TEXTURE, TEXTURE_ATTRIBUTES, TEX_COORD_GENERATION);
		textureUnit.setName("textureUnit1");
		
		SerializationHelper.writeTextureUnit(getOutputStream(), textureUnit);
		textureUnit = SerializationHelper.readTextureUnit(getInputStream());
		
		assertEquals("textureUnit1", textureUnit.getName());
		// TODO:
		// assertEquals(TEXTURE, textureUnit.getTexture());
		assertEquals(TEXTURE_ATTRIBUTES, textureUnit.getTextureAttributes());
		assertEquals(TEX_COORD_GENERATION, textureUnit.getTexCoordGeneration());
	}
	
	@Test
	public void testShouldReadAndWriteTexture() throws IOException {
		Texture texture;
		
		// testing null check implementation
		SerializationHelper.writeTexture(getOutputStream(), null);
		assertNull(SerializationHelper.readTexture(getInputStream()));
		
		SerializationHelper.writeTexture(getOutputStream(), TEXTURE);
		texture = SerializationHelper.readTexture(getInputStream());
		
		assertEquals(TEXTURE.getName(), texture.getName());
		assertEquals(TEXTURE.getBoundaryModeS(), texture.getBoundaryModeS());
		assertEquals(TEXTURE.getBoundaryModeT(), texture.getBoundaryModeT());
		assertEquals(TEXTURE.getBoundaryColor(), texture.getBoundaryColor());
		assertEquals(TEXTURE.getBoundaryWidth(), texture.getBoundaryWidth());
		assertEquals(TEXTURE.getFilter(), texture.getFilter());
	}
	
	@Test
	public void testShouldReadAndWriteTextureAttributes() throws IOException {
		TextureAttributes textureAttributes;
		
		// testing null check implementation
		SerializationHelper.writeTextureAttributes(getOutputStream(), null);
		assertNull(SerializationHelper.readTextureAttributes(getInputStream()));
		
		SerializationHelper.writeTextureAttributes(getOutputStream(), TEXTURE_ATTRIBUTES);
		textureAttributes = SerializationHelper.readTextureAttributes(getInputStream());
		
		assertEquals(TEXTURE_ATTRIBUTES.getName(), textureAttributes.getName());
		assertEquals(TEXTURE_ATTRIBUTES.getTextureMode(), textureAttributes.getTextureMode());
		assertEquals(TEXTURE_ATTRIBUTES.getTextureBlendColor(), textureAttributes.getTextureBlendColor());
		assertEquals(TEXTURE_ATTRIBUTES.getPerspectiveCorrectionMode(), textureAttributes.getPerspectiveCorrectionMode());
		assertEquals(TEXTURE_ATTRIBUTES.getTextureTransform(), textureAttributes.getTextureTransform());
		assertEquals(TEXTURE_ATTRIBUTES.getCombineRGBMode(), textureAttributes.getCombineRGBMode());
		assertEquals(TEXTURE_ATTRIBUTES.getCombineAlphaMode(), textureAttributes.getCombineAlphaMode());
		assertEquals(TEXTURE_ATTRIBUTES.getCombineRGBSource(0), textureAttributes.getCombineRGBSource(0));
		assertEquals(TEXTURE_ATTRIBUTES.getCombineRGBSource(1), textureAttributes.getCombineRGBSource(1));
		assertEquals(TEXTURE_ATTRIBUTES.getCombineRGBSource(2), textureAttributes.getCombineRGBSource(2));
		assertEquals(TEXTURE_ATTRIBUTES.getCombineAlphaSource(0), textureAttributes.getCombineAlphaSource(0));
		assertEquals(TEXTURE_ATTRIBUTES.getCombineAlphaSource(1), textureAttributes.getCombineAlphaSource(1));
		assertEquals(TEXTURE_ATTRIBUTES.getCombineAlphaSource(2), textureAttributes.getCombineAlphaSource(2));
		assertEquals(TEXTURE_ATTRIBUTES.getCombineRGBFunction(0), textureAttributes.getCombineRGBFunction(0));
		assertEquals(TEXTURE_ATTRIBUTES.getCombineRGBFunction(1), textureAttributes.getCombineRGBFunction(1));
		assertEquals(TEXTURE_ATTRIBUTES.getCombineRGBFunction(2), textureAttributes.getCombineRGBFunction(2));
		assertEquals(TEXTURE_ATTRIBUTES.getCombineAlphaFunction(0), textureAttributes.getCombineAlphaFunction(0));
		assertEquals(TEXTURE_ATTRIBUTES.getCombineAlphaFunction(1), textureAttributes.getCombineAlphaFunction(1));
		assertEquals(TEXTURE_ATTRIBUTES.getCombineAlphaFunction(2), textureAttributes.getCombineAlphaFunction(2));
		assertEquals(TEXTURE_ATTRIBUTES.getCombineRGBScale(), textureAttributes.getCombineRGBScale());
		assertEquals(TEXTURE_ATTRIBUTES.getCombineAlphaScale(), textureAttributes.getCombineAlphaScale());
		assertEquals(TEXTURE_ATTRIBUTES.getCompareMode(), textureAttributes.getCompareMode());
		assertEquals(TEXTURE_ATTRIBUTES.getCompareFunction(), textureAttributes.getCompareFunction());
	}
	
	@Test
	public void testShouldReadAndWriteTexCoordGeneration() throws IOException {
		TexCoordGeneration texCoordGeneration;
		
		// testing null check implementation
		SerializationHelper.writeTexCoordGeneration(getOutputStream(), null);
		assertNull(SerializationHelper.readTexCoordGeneration(getInputStream()));
		
		SerializationHelper.writeTexCoordGeneration(getOutputStream(), TEX_COORD_GENERATION);
		texCoordGeneration = SerializationHelper.readTexCoordGeneration(getInputStream());
		
		assertEquals(TEX_COORD_GENERATION.getName(), texCoordGeneration.getName());
		assertEquals(TEX_COORD_GENERATION.getGenMode(), texCoordGeneration.getGenMode());
		assertEquals(TEX_COORD_GENERATION.getFormat(), texCoordGeneration.getFormat());
		assertEquals(TEX_COORD_GENERATION.getPlaneS(), texCoordGeneration.getPlaneS());
		assertEquals(TEX_COORD_GENERATION.getPlaneT(), texCoordGeneration.getPlaneT());
		assertEquals(TEX_COORD_GENERATION.getPlaneR(), texCoordGeneration.getPlaneR());
		assertEquals(TEX_COORD_GENERATION.getPlaneQ(), texCoordGeneration.getPlaneQ());
		assertEquals(TEX_COORD_GENERATION.isEnabled(), texCoordGeneration.isEnabled());
	}
	
	@Test
	public void testShouldReadAndWriteBitSet() throws IOException {
		// testing null check implementation
		SerializationHelper.writeBitSet(getOutputStream(), null);
		assertNull(SerializationHelper.readBitSet(getInputStream()));
		
		SerializationHelper.writeBitSet(getOutputStream(), BIT_SET);
		assertEquals(BIT_SET, SerializationHelper.readBitSet(getInputStream()));
	}
	
	@Test
	public void testShouldReadAndWriteTuple2f() throws IOException {
		// testing null check implementation
		SerializationHelper.writeTuple2f(getOutputStream(), null);
		assertNull(SerializationHelper.readTuple2f(getInputStream()));
		
		SerializationHelper.writeTuple2f(getOutputStream(), TUPLE_2F);
		assertEquals(TUPLE_2F, SerializationHelper.readTuple2f(getInputStream()));
	}
	
	@Test
	public void testShouldReadAndWriteGeometry() throws IOException {
		Geometry geometry;
		
		// testing null check implementation
		SerializationHelper.writeGeometry(getOutputStream(), null);
		assertNull(SerializationHelper.readGeometry(getInputStream()));
		
		SerializationHelper.writeGeometry(getOutputStream(), GEOMETRY1);
		geometry = SerializationHelper.readGeometry(getInputStream());
		
		assertEquals(GEOMETRY1.getName(), geometry.getName());
		
		GeometryDataContainer geometryDataContainer1 = (GeometryDataContainer) PrivateAccessor.getPrivateField(geometry, "dataContainer");
		GeometryDataContainer geometryDataContainer2 = (GeometryDataContainer) PrivateAccessor.getPrivateField(GEOMETRY1, "dataContainer");
		
		assertEquals(geometryDataContainer1.getValidVertexCount(), geometryDataContainer2.getValidVertexCount());
		assertEquals(geometryDataContainer1.getCoordinatesOffset(), geometryDataContainer2.getCoordinatesOffset());
		// assertEquals(geometryDataContainer1.get, geometryDataContainer2.get);
		assertEquals(geometryDataContainer1.getInitialIndex(), geometryDataContainer2.getInitialIndex());
		
		assertEquals(geometryDataContainer1.hasIndex(), geometryDataContainer2.hasIndex());
		if (geometryDataContainer1.hasIndex()) {
			assertTrue(BufferUtils.equals(geometryDataContainer1.getIndexData().getBuffer(), geometryDataContainer2.getIndexData().getBuffer()));
		}
		
		assertEquals(geometryDataContainer1.isInterleaved(), geometryDataContainer2.isInterleaved());
		if (geometryDataContainer1.isInterleaved()) {
			assertTrue(BufferUtils.equals(geometryDataContainer1.getCoordinatesData().getBuffer(), geometryDataContainer2.getCoordinatesData().getBuffer()));
			
			assertEquals(geometryDataContainer1.hasNormals(), geometryDataContainer2.hasNormals());
			if (geometryDataContainer1.hasNormals()) {
				assertTrue(BufferUtils.equals(geometryDataContainer1.getNormalsData().getBuffer(), geometryDataContainer2.getNormalsData().getBuffer()));
			}
			
			assertEquals(geometryDataContainer1.hasColors(), geometryDataContainer2.hasColors());
			if (geometryDataContainer1.hasColors()) {
				assertTrue(BufferUtils.equals(geometryDataContainer1.getColorData().getBuffer(), geometryDataContainer2.getColorData().getBuffer()));
			}
			
			assertEquals(geometryDataContainer1.hasTextureCoordinates(), geometryDataContainer2.hasTextureCoordinates());
			if (geometryDataContainer1.hasTextureCoordinates()) {
				GeomNioFloatData[] texCoords1 = (GeomNioFloatData[]) PrivateAccessor.getPrivateField(geometryDataContainer1, "texCoords");
				GeomNioFloatData[] texCoords2 = (GeomNioFloatData[]) PrivateAccessor.getPrivateField(geometryDataContainer2, "texCoords");
				
				assertEquals(texCoords1.length, texCoords2.length);
				for (int i = 0; i < texCoords1.length; i++) {
					assertTrue(BufferUtils.equals(texCoords1[i].getBuffer(), texCoords2[i].getBuffer()));
				}
			}
			
			assertEquals(geometryDataContainer1.getVertexAttributesCount(), geometryDataContainer2.getVertexAttributesCount());
			for (int i = 0; i < geometryDataContainer1.getVertexAttributesCount(); i++) {
				assertTrue(BufferUtils.equals(geometryDataContainer1.getVertexAttribData(i).getBuffer(), geometryDataContainer2.getVertexAttribData(i).getBuffer()));
			}
		}
		
		assertTrue(Arrays.equals(geometryDataContainer1.getStripCounts(), geometryDataContainer2.getStripCounts()));
		
		int[] texCoordSetMap_nonPublic1 = (int[]) PrivateAccessor.getPrivateField(geometryDataContainer1, "texCoordSetMap");
		int[] texCoordSetMap_nonPublic2 = (int[]) PrivateAccessor.getPrivateField(geometryDataContainer2, "texCoordSetMap");
		
		assertTrue(Arrays.equals(texCoordSetMap_nonPublic1, texCoordSetMap_nonPublic2));
		assertTrue(Arrays.equals(geometryDataContainer1.getTexCoordSetMap(), geometryDataContainer2.getTexCoordSetMap()));
		
		for (int unit = 0; unit < GeometryDataContainer.TEXTURE_COORDINATES; unit++) {
			assertEquals(geometryDataContainer1.getTexCoordSize(unit), geometryDataContainer2.getTexCoordSize(unit));
		}
		
		assertEquals(geometryDataContainer1.getColorsSize(), geometryDataContainer2.getColorsSize());
		assertEquals(geometryDataContainer1.getVertexFormat(), geometryDataContainer2.getVertexFormat());
		assertEquals(geometryDataContainer1.getNormalsOffset(), geometryDataContainer2.getNormalsOffset());
		assertEquals(geometryDataContainer1.getColorsOffset(), geometryDataContainer2.getColorsOffset());
		
		for (int unit = 0; unit < GeometryDataContainer.TEXTURE_COORDINATES; unit++) {
			assertEquals(geometryDataContainer1.getTexCoordsOffset(unit), geometryDataContainer2.getTexCoordsOffset(unit));
		}
		
		for (int vertexAttributes = 0; vertexAttributes < GeometryDataContainer.VERTEX_ATTRIBUTES; vertexAttributes++) {
			assertEquals(geometryDataContainer1.getVertexAttribsOffset(vertexAttributes), geometryDataContainer2.getVertexAttribsOffset(vertexAttributes));
		}
	}
	
	@Test
	public void testShouldReadAndWriteBounds() throws IOException {
		// testing null check implementation
		SerializationHelper.writeBounds(getOutputStream(), null);
		assertNull(SerializationHelper.readBounds(getInputStream()));
		
		SerializationHelper.writeBounds(getOutputStream(), new BoundingPolytope());
		assertEquals(BoundsType.POLYTOPE, SerializationHelper.readBounds(getInputStream()).getType());
	}
	
	@Test
	public void testShouldReadAndWriteGroupAnimator() throws IOException {
		GroupAnimator groupAnimator;
		
		// testing null check implementation
		SerializationHelper.writeGroupAnimator(getOutputStream(), null);
		assertNull(SerializationHelper.readGroupAnimator(getInputStream()));
		
		// ---
		
		// testing group rotator implementation
		SerializationHelper.writeGroupAnimator(getOutputStream(), GROUP_ROTATOR1);
		groupAnimator = SerializationHelper.readGroupAnimator(getInputStream());
		
		assertEquals(GROUP_ROTATOR1.getClass(), groupAnimator.getClass());
		assertEquals(GROUP_ROTATOR1.getTransformationDirectives(), groupAnimator.getTransformationDirectives(), new TransformationDirectivesComparator());
		
		// ---
		
		// testing group translator implementation
		SerializationHelper.writeGroupAnimator(getOutputStream(), GROUP_TRANSLATOR1);
		groupAnimator = SerializationHelper.readGroupAnimator(getInputStream());
		
		assertEquals(GROUP_TRANSLATOR1.getClass(), groupAnimator.getClass());
		assertEquals(GROUP_TRANSLATOR1.getTransformationDirectives(), groupAnimator.getTransformationDirectives(), new TransformationDirectivesComparator());
	}
	
	@Test
	public void testShouldReadAndWriteTransformationDirectives() throws IOException {
		TransformationDirectives transformationDirectives;
		
		// testing null check implementation
		SerializationHelper.writeTransformationDirectives(getOutputStream(), null);
		assertNull(SerializationHelper.readTransformationDirectives(getInputStream()));
		
		// ---
		
		SerializationHelper.writeTransformationDirectives(getOutputStream(), TRANSFORMATION_DIRECTIVES1);
		transformationDirectives = SerializationHelper.readTransformationDirectives(getInputStream());
		
		assertEquals(TRANSFORMATION_DIRECTIVES1, transformationDirectives, new TransformationDirectivesComparator());
		
		// ---
		
		SerializationHelper.writeTransformationDirectives(getOutputStream(), TRANSFORMATION_DIRECTIVES2);
		transformationDirectives = SerializationHelper.readTransformationDirectives(getInputStream());
		
		assertEquals(TRANSFORMATION_DIRECTIVES2, transformationDirectives, new TransformationDirectivesComparator());
		
	}
	
}
