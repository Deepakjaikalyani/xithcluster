package br.edu.univercidade.cc.xithcluster.serial;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.BitSet;
import junit.framework.Assert;
import org.jagatoo.opengl.enums.BlendFunction;
import org.jagatoo.opengl.enums.BlendMode;
import org.jagatoo.opengl.enums.ColorTarget;
import org.jagatoo.opengl.enums.FaceCullMode;
import org.jagatoo.opengl.enums.LinePattern;
import org.jagatoo.opengl.enums.ShadeModel;
import org.junit.Test;
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
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.LineAttributes;
import org.xith3d.scenegraph.Material;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.PointAttributes;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransparencyAttributes;
import br.edu.univercidade.cc.xithcluster.util.BufferUtils;

public class SerializationHelperTest {
	
	private static final int SIZE_OF_FLOAT = 32;
	
	private static final byte BYTE_DATA = (byte) 11;
	
	private static final int INT_DATA = 11;
	
	private static final float FLOAT_DATA = 11.0f;
	
	private static final int MEM_ALLOC_UNIT = 128;
	
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
	
	private static final byte[] BYTE_ARRAY;
	
	private static final GeomNioFloatData GEOM_NIO_FLOAT_DATA;
	
	private static final GeomNioIntData GEOM_NIO_INT_DATA;
	
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
	}
	
	private ByteArrayOutputStream out;
	
	public DataOutputStream getOutputStream() {
		return new DataOutputStream(out = new ByteArrayOutputStream());
	}
	
	public DataInputStream getInputStream() {
		return new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
	}
	
	@Test
	public void testWriteReadEnum() throws IOException {
		// testing null check implementation
		SerializationHelper.writeEnum(getOutputStream(), null);
		Assert.assertNull(SerializationHelper.readEnum(getInputStream(), FaceCullMode.values()));
		
		SerializationHelper.writeEnum(getOutputStream(), FaceCullMode.BACK);
		Assert.assertEquals(FaceCullMode.BACK, SerializationHelper.readEnum(getInputStream(), FaceCullMode.values()));
	}
	
	@Test
	public void testWriteReadVector3f() throws IOException {
		// testing null check implementation
		SerializationHelper.writeVector3f(getOutputStream(), null);
		Assert.assertNull(SerializationHelper.readVector3f(getInputStream()));
		
		SerializationHelper.writeVector3f(getOutputStream(), VECTOR_3F);
		Assert.assertEquals(VECTOR_3F, SerializationHelper.readVector3f(getInputStream()));
	}
	
	@Test
	public void testWriteReadPoint() throws IOException {
		// testing null check implementation
		SerializationHelper.writePoint3f(getOutputStream(), null);
		Assert.assertNull(SerializationHelper.readPoint3f(getInputStream()));
		
		SerializationHelper.writePoint3f(getOutputStream(), POINT_3F);
		Assert.assertEquals(POINT_3F, SerializationHelper.readPoint3f(getInputStream()));
	}
	
	@Test
	public void testWriteReadTuple3f() throws IOException {
		// testing null check implementation
		SerializationHelper.writeTuple3f(getOutputStream(), null);
		Assert.assertNull(SerializationHelper.readTuple3f(getInputStream()));
		
		SerializationHelper.writeTuple3f(getOutputStream(), TUPLE_3F);
		Assert.assertEquals(TUPLE_3F, SerializationHelper.readTuple3f(getInputStream()));
	}
	
	@Test
	public void testWriteReadColor() throws IOException {
		// testing null check implementation
		SerializationHelper.writeColorf(getOutputStream(), null);
		Assert.assertNull(SerializationHelper.readColorf(getInputStream()));
		
		SerializationHelper.writeColorf(getOutputStream(), COLOR_F);
		Assert.assertEquals(COLOR_F, SerializationHelper.readColorf(getInputStream()));
	}
	
	@Test
	public void testWriteReadTransform() throws IOException {
		// testing null check implementation
		SerializationHelper.writeTransform(getOutputStream(), null);
		Assert.assertNull(SerializationHelper.readTransform(getInputStream()));
		
		SerializationHelper.writeTransform(getOutputStream(), TRANSFORM_3D);
		Assert.assertEquals(TRANSFORM_3D, SerializationHelper.readTransform(getInputStream()));
	}
	
	@Test
	public void testWriteReadMatrix4f() throws IOException {
		// testing null check implementation
		SerializationHelper.writeMatrix4f(getOutputStream(), null);
		Assert.assertNull(SerializationHelper.readMatrix4f(getInputStream()));
		
		SerializationHelper.writeMatrix4f(getOutputStream(), MATRIX_4F);
		Assert.assertEquals(MATRIX_4F, SerializationHelper.readMatrix4f(getInputStream()));
	}
	
	@Test
	public void testWriteReadString() throws IOException {
		// testing null check implementation
		SerializationHelper.writeString(getOutputStream(), null);
		Assert.assertNull(SerializationHelper.readString(getInputStream()));
		
		SerializationHelper.writeString(getOutputStream(), "something");
		Assert.assertEquals(SerializationHelper.readString(getInputStream()), "something");
	}
	
	@Test
	public void testWriteReadVector4f() throws IOException {
		// testing null check implementation
		SerializationHelper.writeVector4f(getOutputStream(), null);
		Assert.assertNull(SerializationHelper.readVector4f(getInputStream()));
		
		SerializationHelper.writeVector4f(getOutputStream(), VECTOR_4F);
		Assert.assertEquals(VECTOR_4F, SerializationHelper.readVector4f(getInputStream()));
	}
	
	@Test
	public void testWriteReadClass() throws IOException {
		// testing null check implementation
		SerializationHelper.writeClass(getOutputStream(), null);
		Assert.assertNull(SerializationHelper.readClass(getInputStream()));
		
		SerializationHelper.writeClass(getOutputStream(), Node.class);
		Assert.assertEquals(Node.class, SerializationHelper.readClass(getInputStream()));
	}
	
	@Test
	public void testWriteReadClassByType() throws IOException {
		// testing null check implementation
		SerializationHelper.writeClass(getOutputStream(), null);
		Assert.assertNull(SerializationHelper.readClass(getInputStream(), Node.class));
		
		SerializationHelper.writeClass(getOutputStream(), GroupNode.class);
		Assert.assertEquals(GroupNode.class, SerializationHelper.readClass(getInputStream(), Node.class));
	}
	
	@Test
	public void testWriteReadGeomNioFloatData() throws IOException {
		// testing null check implementation
		SerializationHelper.writeGeomNioFloatData(getOutputStream(), null);
		Assert.assertNull(SerializationHelper.readGeomNioFloatData(getInputStream()));
		
		SerializationHelper.writeGeomNioFloatData(getOutputStream(), GEOM_NIO_FLOAT_DATA);
		Assert.assertTrue(BufferUtils.equals(GEOM_NIO_FLOAT_DATA.getBuffer(), SerializationHelper.readGeomNioFloatData(getInputStream()).getBuffer()));
	}
	
	@Test
	public void testWriteReadFloatArray() throws IOException {
		// testing null check implementation
		SerializationHelper.writeFloatArray(getOutputStream(), null);
		Assert.assertNull(SerializationHelper.readFloatArray(getInputStream()));
		
		SerializationHelper.writeFloatArray(getOutputStream(), FLOAT_ARRAY);
		Assert.assertTrue(Arrays.equals(SerializationHelper.readFloatArray(getInputStream()), FLOAT_ARRAY));
	}
	
	@Test
	public void testWriteReadByteArray() throws IOException {
		// testing null check implementation
		SerializationHelper.writeByteArray(getOutputStream(), null);
		Assert.assertNull(SerializationHelper.readByteArray(getInputStream()));
		
		SerializationHelper.writeByteArray(getOutputStream(), BYTE_ARRAY);
		Assert.assertTrue(Arrays.equals(BYTE_ARRAY, SerializationHelper.readByteArray(getInputStream())));
	}
	
	@Test
	public void testWriteReadGeomNioIntData() throws IOException {
		// testing null check implementation
		SerializationHelper.writeGeomNioIntData(getOutputStream(), null);
		Assert.assertNull(SerializationHelper.readGeomNioIntData(getInputStream()));
		
		SerializationHelper.writeGeomNioIntData(getOutputStream(), GEOM_NIO_INT_DATA);
		Assert.assertTrue(BufferUtils.equals(GEOM_NIO_INT_DATA.getBuffer(), SerializationHelper.readGeomNioIntData(getInputStream()).getBuffer()));
	}
	
	@Test
	public void testWriteReadIntArray() throws IOException {
		// testing null check implementation
		SerializationHelper.writeIntArray(getOutputStream(), null);
		Assert.assertNull(SerializationHelper.readIntArray(getInputStream()));
		
		SerializationHelper.writeIntArray(getOutputStream(), INT_ARRAY);
		Assert.assertTrue(Arrays.equals(INT_ARRAY, SerializationHelper.readIntArray(getInputStream())));
	}
	
	@Test
	public void testWriteReadMaterial() throws IOException {
		Material material;
		
		// testing null check implementation
		SerializationHelper.writeMaterial(getOutputStream(), null);
		Assert.assertNull(SerializationHelper.readMaterial(getInputStream()));
		
		material = new Material(COLOR_F, COLOR_F, COLOR_F, COLOR_F, 11.0f, ColorTarget.AMBIENT_AND_DIFFUSE, false, true);
		material.setName("material1");
		
		SerializationHelper.writeMaterial(getOutputStream(), material);
		material = SerializationHelper.readMaterial(getInputStream());
		
		Assert.assertEquals("material1", material.getName());
		Assert.assertEquals(COLOR_F, material.getAmbientColor());
		Assert.assertEquals(COLOR_F, material.getEmissiveColor());
		Assert.assertEquals(COLOR_F, material.getDiffuseColor());
		Assert.assertEquals(COLOR_F, material.getSpecularColor());
		Assert.assertEquals(11.0f, material.getShininess());
		Assert.assertEquals(ColorTarget.AMBIENT_AND_DIFFUSE, material.getColorTarget());
		Assert.assertFalse(material.getNormalizeNormals());
		Assert.assertTrue(material.isLightingEnabled());
	}
	
	@Test
	public void testWriteReadTransparencyAttributes() throws IOException {
		TransparencyAttributes transparencyAttributes;
		
		// testing null check implementation
		SerializationHelper.writeTransparencyAttributes(getOutputStream(), null);
		Assert.assertNull(SerializationHelper.readTransparencyAttributes(getInputStream()));
		
		transparencyAttributes = new TransparencyAttributes(BlendMode.NICEST, 0.17f, BlendFunction.DST_ALPHA, BlendFunction.DST_COLOR, false, true);
		transparencyAttributes.setName("transparencyAttributes1");
		
		SerializationHelper.writeTransparencyAttributes(getOutputStream(), transparencyAttributes);
		transparencyAttributes = SerializationHelper.readTransparencyAttributes(getInputStream());
		
		Assert.assertEquals("transparencyAttributes1", transparencyAttributes);
		Assert.assertEquals(BlendMode.NICEST, transparencyAttributes.getMode());
		Assert.assertEquals(0.17f, transparencyAttributes.getTransparency());
		Assert.assertEquals(BlendFunction.DST_ALPHA, transparencyAttributes.getSrcBlendFunction());
		Assert.assertEquals(BlendFunction.DST_COLOR, transparencyAttributes.getDstBlendFunction());
		Assert.assertFalse(transparencyAttributes.isSortEnabled());
		Assert.assertTrue(transparencyAttributes.isEnabled());
	}
	
	@Test
	public void testWriteReadColoringAttributes() throws IOException {
		ColoringAttributes coloringAttributes;
		
		// testing null check implementation
		SerializationHelper.writeColoringAttributes(getOutputStream(), null);
		Assert.assertNull(SerializationHelper.readColoringAttributes(getInputStream()));
		
		coloringAttributes = new ColoringAttributes(1.0f, 1.0f, 1.0f, ShadeModel.NICEST);
		
		SerializationHelper.writeColoringAttributes(getOutputStream(), coloringAttributes);
		coloringAttributes = SerializationHelper.readColoringAttributes(getInputStream());
		
		Assert.assertEquals(new Colorf(1.0f, 1.0f, 1.0f), coloringAttributes.getColor());
		Assert.assertEquals(ShadeModel.NICEST, coloringAttributes.getShadeModel());
	}
	
	@Test
	public void testWriteReadLineAttributes() throws IOException {
		LineAttributes lineAttributes;
		
		// testing null check implementation
		SerializationHelper.writeLineAttributes(getOutputStream(), null);
		Assert.assertNull(SerializationHelper.readLineAttributes(getInputStream()));
		
		lineAttributes = new LineAttributes(5.0f, LinePattern.DASHED_DOTTED, 3, 7, true);
		lineAttributes.setSortEnabled(true);
		
		SerializationHelper.writeLineAttributes(getOutputStream(), lineAttributes);
		lineAttributes = SerializationHelper.readLineAttributes(getInputStream());
		
		Assert.assertEquals(5.0f, lineAttributes.getLineWidth());
		Assert.assertEquals(LinePattern.DASHED_DOTTED, lineAttributes.getLinePattern());
		Assert.assertEquals(3, lineAttributes.getPatternScaleFactor());
		Assert.assertEquals(7, lineAttributes.getPatternMask());
		Assert.assertEquals(true, lineAttributes.isLineAntialiasingEnabled());
		Assert.assertEquals(true, lineAttributes.isSortEnabled());
	}
	
	@Test
	public void testWriteReadPointAttributes() throws IOException {
		PointAttributes pointAttributes;
		
		// testing null check implementation
		SerializationHelper.writePointAttributes(getOutputStream(), null);
		Assert.assertNull(SerializationHelper.readPointAttributes(getInputStream()));
		
		pointAttributes = new PointAttributes(3.0f, true);
		pointAttributes.setSortEnabled(true);
		
		SerializationHelper.writePointAttributes(getOutputStream(), pointAttributes);
		pointAttributes = SerializationHelper.readPointAttributes(getInputStream());
		
		Assert.assertEquals(3.0f, pointAttributes.getPointSize());
		Assert.assertEquals(true, pointAttributes.isPointAntialiasingEnabled());
		Assert.assertEquals(true, pointAttributes.isSortEnabled());
	}
	
	@Test
	public void testWriteReadPolygonAttributes() {
	}
	
	@Test
	public void testWriteReadRenderingAttributes() {
	}
	
	@Test
	public void testWriteReadStencilFunctionSeparate() {
	}
	
	@Test
	public void testWriteReadStencilOperationSeparate() {
	}
	
	@Test
	public void testWriteReadStencilMaskSeparate() {
	}
	
	@Test
	public void testWriteReadTextureUnit() {
	}
	
	@Test
	public void testWriteReadTexture() {
	}
	
	@Test
	public void testWriteReadTextureImage() {
	}
	
	@Test
	public void testWriteReadDimension() {
	}
	
	@Test
	public void testWriteReadTextureAttributes() {
	}
	
	@Test
	public void testWriteReadTextCoordGeneration() {
	}
	
	@Test
	public void testWriteReadBitSet() throws IOException {
		// testing null check implementation
		SerializationHelper.writeBitSet(getOutputStream(), null);
		Assert.assertNull(SerializationHelper.readBitSet(getInputStream()));
		
		SerializationHelper.writeBitSet(getOutputStream(), BIT_SET);
		Assert.assertEquals(BIT_SET, SerializationHelper.readBitSet(getInputStream()));
	}
	
	@Test
	public void testWriteReadTuple2f() throws IOException {
		// testing null check implementation
		SerializationHelper.writeTuple2f(getOutputStream(), null);
		Assert.assertNull(SerializationHelper.readTuple2f(getInputStream()));
		
		SerializationHelper.writeTuple2f(getOutputStream(), TUPLE_2F);
		Assert.assertEquals(TUPLE_2F, SerializationHelper.readTuple2f(getInputStream()));
	}
	
	@Test
	public void testWriteReadGeometry() {
	}
	
	@Test
	public void testWriteReadBounds() {
	}
	
}
