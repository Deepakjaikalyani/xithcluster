package br.edu.univercidade.cc.xithcluster.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.BitSet;
import org.jagatoo.geometry.GeomNioFloatData;
import org.jagatoo.geometry.GeomNioIntData;
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
import org.jagatoo.opengl.enums.TextureType;
import org.openmali.spatial.bounds.BoundingBox;
import org.openmali.spatial.bounds.BoundingPolytope;
import org.openmali.spatial.bounds.BoundingSphere;
import org.openmali.spatial.bounds.Bounds;
import org.openmali.spatial.bounds.BoundsType;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple2f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.openmali.vecmath2.Vector4f;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.ColoringAttributes;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Geometry.Optimization;
import org.xith3d.scenegraph.GeometryDataContainer;
import org.xith3d.scenegraph.IndexedLineArray;
import org.xith3d.scenegraph.IndexedLineStripArray;
import org.xith3d.scenegraph.IndexedQuadArray;
import org.xith3d.scenegraph.IndexedTriangleArray;
import org.xith3d.scenegraph.IndexedTriangleFanArray;
import org.xith3d.scenegraph.IndexedTriangleStripArray;
import org.xith3d.scenegraph.LineArray;
import org.xith3d.scenegraph.LineAttributes;
import org.xith3d.scenegraph.LineStripArray;
import org.xith3d.scenegraph.Material;
import org.xith3d.scenegraph.PointArray;
import org.xith3d.scenegraph.PointAttributes;
import org.xith3d.scenegraph.PolygonAttributes;
import org.xith3d.scenegraph.QuadArray;
import org.xith3d.scenegraph.RenderingAttributes;
import org.xith3d.scenegraph.StencilFuncSeparate;
import org.xith3d.scenegraph.StencilMaskSeparate;
import org.xith3d.scenegraph.StencilOpSeparate;
import org.xith3d.scenegraph.TexCoordGeneration;
import org.xith3d.scenegraph.TexCoordGeneration.CoordMode;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Texture3D;
import org.xith3d.scenegraph.TextureAttributes;
import org.xith3d.scenegraph.TextureCubeMap;
import org.xith3d.scenegraph.TextureImage;
import org.xith3d.scenegraph.TextureImage2D;
import org.xith3d.scenegraph.TextureImage3D;
import org.xith3d.scenegraph.TextureUnit;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransparencyAttributes;
import org.xith3d.scenegraph.TriangleArray;
import org.xith3d.scenegraph.TriangleFanArray;
import org.xith3d.scenegraph.TriangleStripArray;
import org.xith3d.schedops.movement.GroupAnimator;
import org.xith3d.schedops.movement.GroupRotator;
import org.xith3d.schedops.movement.GroupTranslator;
import org.xith3d.schedops.movement.TransformationDirectives;
import org.xith3d.schedops.movement.TransformationDirectives.AxisOrder;
import br.edu.univercidade.cc.xithcluster.utils.BufferUtils;
import br.edu.univercidade.cc.xithcluster.utils.PrivateAccessor;

public class SerializationHelper {
	
	private static final int GT_LINE_STRIP_ARRAY = 1;
	
	private static final int GT_TRIANGLE_FAN_ARRAY = 2;
	
	private static final int GT_TRIANGLE_STRIP_ARRAY = 3;
	
	private static final int GT_INDEXED_LINE_STRIP_ARRAY = 4;
	
	private static final int GT_INDEXED_TRIANGLE_STRIP_ARRAY = 5;
	
	private static final int GT_INDEXED_LINE_ARRAY = 6;
	
	private static final int GT_INDEXED_QUAD_ARRAY = 7;
	
	private static final int GT_INDEXED_TRIANGLE_ARRAY = 8;
	
	private static final int GT_INDEXED_TRIANGLE_FAN_ARRAY = 9;
	
	private static final int GT_LINE_ARRAY = 10;
	
	private static final int GT_POINT_ARRAY = 11;
	
	private static final int GT_QUAD_ARRAY = 12;
	
	private static final int GT_TRIANGLE_ARRAY = 13;
	
	private static final int TIT_TEXTURE_IMAGE_2D = 1;
	
	private static final int TIT_TEXTURE_IMAGE_3D = 2;

	private static final int GA_ROTATOR = 1;

	private static final int GA_TRANSLATOR = 2;
	
	private SerializationHelper() {
	}
	
	public static <EnumType extends Enum<?>> void writeEnum(DataOutputStream out, EnumType anEnum) throws IOException {
		if (nullCheck(out, anEnum)) {
			out.writeInt(anEnum.ordinal());
		}
	}
	
	public static <E extends Enum<E>> E readEnum(DataInputStream in, E[] values) throws IOException {
		if (nullCheck(in)) {
			return values[in.readInt()];
		} else {
			return null;
		}
	}
	
	public static void writeVector3f(DataOutputStream out, Vector3f vector) throws IOException {
		if (nullCheck(out, vector)) {
			out.writeFloat(vector.getX());
			out.writeFloat(vector.getY());
			out.writeFloat(vector.getZ());
		}
	}
	
	public static void writeVector4f(DataOutputStream out, Vector4f aVector) throws IOException {
		if (nullCheck(out, aVector)) {
			out.writeFloat(aVector.getX());
			out.writeFloat(aVector.getY());
			out.writeFloat(aVector.getZ());
			out.writeFloat(aVector.getW());
		}
	}
	
	public static void writePoint3f(DataOutputStream out, Point3f point) throws IOException {
		if (nullCheck(out, point)) {
			out.writeFloat(point.getX());
			out.writeFloat(point.getY());
			out.writeFloat(point.getZ());
		}
	}
	
	public static Tuple3f readTuple3f(DataInputStream in) throws IOException {
		if (nullCheck(in)) {
			return new Tuple3f(in.readFloat(), in.readFloat(), in.readFloat());
		} else {
			return null;
		}
	}
	
	public static void writeColorf(DataOutputStream out, Colorf color) throws IOException {
		if (nullCheck(out, color)) {
			out.writeFloat(color.r());
			out.writeFloat(color.g());
			out.writeFloat(color.b());
			out.writeFloat(color.a());
			out.writeBoolean(color.hasAlpha());
		}
	}
	
	public static void writeTuple3f(DataOutputStream out, Tuple3f tuple) throws IOException {
		if (nullCheck(out, tuple)) {
			out.writeFloat(tuple.getX());
			out.writeFloat(tuple.getY());
			out.writeFloat(tuple.getZ());
		}
	}
	
	public static void writeTransform3D(DataOutputStream out, Transform3D transform) throws IOException {
		if (nullCheck(out, transform)) {
			writeMatrix4f(out, transform.getMatrix4f());
		}
	}
	
	public static void writeMatrix4f(DataOutputStream out, Matrix4f matrix) throws IOException {
		FloatBuffer floatBuffer;
		
		if (nullCheck(out, matrix)) {
			floatBuffer = FloatBuffer.allocate(16);
			matrix.writeToBuffer(floatBuffer, 0, false, false);
			writeFloatArray(out, floatBuffer.array());
		}
	}
	
	public static void writeString(DataOutputStream out, String aString) throws IOException {
		if (nullCheck(out, aString)) {
			out.writeInt(aString.length());
			out.writeBytes(aString);
		}
	}
	
	public static Colorf readColorf(DataInputStream in) throws IOException {
		Colorf color;
		
		if (nullCheck(in)) {
			color = new Colorf();
			color.set(new float[] {
			in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat()
			}, in.readBoolean());
			
			return color;
		} else {
			return null;
		}
	}
	
	public static Vector3f readVector3f(DataInputStream in) throws IOException {
		if (nullCheck(in)) {
			return new Vector3f(in.readFloat(), in.readFloat(), in.readFloat());
		} else {
			return null;
		}
	}
	
	public static Vector4f readVector4f(DataInputStream in) throws IOException {
		if (nullCheck(in)) {
			return new Vector4f(in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat());
		} else {
			return null;
		}
	}
	
	public static Transform3D readTransform3D(DataInputStream in) throws IOException {
		if (nullCheck(in)) {
			return new Transform3D(readMatrix4f(in));
		} else {
			return null;
		}
	}
	
	// FIXME: Optimize
	public static Matrix4f readMatrix4f(DataInputStream in) throws IOException {
		Matrix4f matrix;
		
		if (nullCheck(in)) {
			matrix = new Matrix4f();
			
			matrix.readFromBuffer(FloatBuffer.wrap(readFloatArray(in)));
			
			return matrix;
		} else {
			return null;
		}
	}
	
	public static String readString(DataInputStream in) throws IOException {
		int length;
		byte[] buffer;
		
		if (nullCheck(in)) {
			length = in.readInt();
			buffer = new byte[length];
			in.readFully(buffer);
			
			return new String(buffer);
		} else {
			return null;
		}
	}
	
	public static void writeClass(DataOutputStream out, Class<?> clazz) throws IOException {
		if (nullCheck(out, clazz)) {
			writeString(out, clazz.getName());
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <Type> Class<? extends Type> readClass(DataInputStream in, Class<Type> clazz) throws IOException {
		return (Class<? extends Type>) readClass(in);
	}
	
	public static Class<?> readClass(DataInputStream in) throws IOException {
		String className;
		
		if (nullCheck(in)) {
			className = readString(in);
			
			if (className != null) {
				try {
					return Class.forName(className);
				} catch (ClassNotFoundException e) {
					throw new IOException("Unknown serialized class: " + className);
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public static void writeGeomNioFloatData(DataOutputStream out, org.xith3d.scenegraph.GeomNioFloatData data) throws IOException {
		writeGeomNioFloatDataJagatoo(out, data);
	}
	
	public static void writeGeomNioFloatDataJagatoo(DataOutputStream out, GeomNioFloatData data) throws IOException {
		if (nullCheck(out, data)) {
			out.writeInt(data.getMaxElements());
			out.writeInt(data.getElemSize());
			out.writeInt(data.getStride());
			out.writeBoolean(data.isReversed());
			writeFloatBuffer(out, data.getBuffer());
		}
	}
	
	public static org.xith3d.scenegraph.GeomNioFloatData readGeomNioFloatData(DataInputStream in) throws IOException {
		org.xith3d.scenegraph.GeomNioFloatData data;
		
		if (nullCheck(in)) {
			data = new org.xith3d.scenegraph.GeomNioFloatData(in.readInt(), in.readInt(), in.readInt(), in.readBoolean());
			data.setBuffer(readFloatBuffer(in));
			
			return data;
		} else {
			return null;
		}
	}
	
	public static GeomNioFloatData readGeomNioFloatDataJagatoo(DataInputStream in) throws IOException {
		GeomNioFloatData data;
		
		if (nullCheck(in)) {
			data = new GeomNioFloatData(in.readInt(), in.readInt(), in.readInt(), in.readBoolean());
			data.setBuffer(readFloatBuffer(in));
			
			return data;
		} else {
			return null;
		}
	}
	
	public static void writeFloatBuffer(DataOutputStream out, FloatBuffer buffer) throws IOException {
		if (nullCheck(out, buffer)) {
			writeFloatArray(out, BufferUtils.safeBufferRead(buffer));
		}
	}
	
	public static FloatBuffer readFloatBuffer(DataInputStream in) throws IOException {
		FloatBuffer buffer;
		float[] array;
		
		if (nullCheck(in)) {
			array = readFloatArray(in);
			buffer = BufferUtils.createFloatBuffer(array);
			buffer.rewind();
			
			return buffer;
		} else {
			return null;
		}
	}
	
	public static void writeFloatArray(DataOutputStream out, float[] array) throws IOException {
		if (nullCheck(out, array)) {
			out.writeInt(array.length);
			// FIXME: ugly
			for (float f : array) {
				out.writeFloat(f);
			}
		}
	}
	
	public static float[] readFloatArray(DataInputStream in) throws IOException {
		int length;
		float[] array;
		
		if (nullCheck(in)) {
			length = in.readInt();
			array = new float[length];
			// FIXME: ugly
			for (int i = 0; i < length; i++) {
				array[i] = in.readFloat();
			}
			
			return array;
		} else {
			return null;
		}
	}
	
	public static void writeByteArray(DataOutputStream out, byte[] array) throws IOException {
		if (nullCheck(out, array)) {
			out.writeInt(array.length);
			out.write(array);
		}
	}
	
	public static byte[] readByteArray(DataInputStream in) throws IOException {
		int length;
		byte[] anArray;
		
		if (nullCheck(in)) {
			length = in.readInt();
			anArray = new byte[length];
			
			in.read(anArray);
			
			return anArray;
		} else {
			return null;
		}
	}
	
	public static void writeGeomNioIntData(DataOutputStream out, GeomNioIntData data) throws IOException {
		if (nullCheck(out, data)) {
			out.writeInt(data.getMaxElements());
			out.writeInt(data.getElemSize());
			out.writeInt(data.getStride());
			out.writeBoolean(data.isReversed());
			writeIntBuffer(out, data.getBuffer());
		}
	}
	
	public static void writeIntBuffer(DataOutputStream out, IntBuffer buffer) throws IOException {
		if (nullCheck(out, buffer)) {
			writeIntArray(out, BufferUtils.safeBufferRead(buffer));
		}
	}
	
	public static GeomNioIntData readGeomNioIntData(DataInputStream in) throws IOException {
		GeomNioIntData data;
		IntBuffer buffer;
		int[] array;
		
		if (nullCheck(in)) {
			data = new GeomNioIntData(in.readInt(), in.readInt(), in.readInt(), in.readBoolean());
			buffer = readIntBuffer(in);
			array = BufferUtils.safeBufferRead(buffer);
			data.set(array, 0, array.length);
			
			return data;
		} else {
			return null;
		}
	}
	
	public static IntBuffer readIntBuffer(DataInputStream in) throws IOException {
		IntBuffer buffer;
		int[] array;
		
		if (nullCheck(in)) {
			array = readIntArray(in);
			buffer = BufferUtils.createIntBuffer(array);
			buffer.rewind();
			
			return buffer;
		} else {
			return null;
		}
	}
	
	public static void writeIntArray(DataOutputStream out, int[] anArray) throws IOException {
		if (nullCheck(out, anArray)) {
			out.writeInt(anArray.length);
			// FIXME: ugly
			for (int i : anArray) {
				out.writeInt(i);
			}
		}
	}
	
	public static int[] readIntArray(DataInputStream in) throws IOException {
		int length;
		int[] anArray;
		
		if (nullCheck(in)) {
			length = in.readInt();
			anArray = new int[length];
			for (int i = 0; i < length; i++) {
				anArray[i] = in.readInt();
			}
			
			return anArray;
		} else {
			return null;
		}
	}
	
	public static void writeMaterial(DataOutputStream out, Material material) throws IOException {
		if (nullCheck(out, material)) {
			writeString(out, material.getName());
			writeColorf(out, material.getAmbientColor());
			writeColorf(out, material.getEmissiveColor());
			writeColorf(out, material.getDiffuseColor());
			writeColorf(out, material.getSpecularColor());
			out.writeFloat(material.getShininess());
			out.writeBoolean(material.isLightingEnabled());
			out.writeBoolean(material.getNormalizeNormals());
			writeEnum(out, material.getColorTarget());
		}
	}
	
	public static Material readMaterial(DataInputStream in) throws IOException {
		Material material;
		
		if (nullCheck(in)) {
			material = new Material();
			
			material.setName(readString(in));
			material.setAmbientColor(readColorf(in));
			material.setEmissiveColor(readColorf(in));
			material.setDiffuseColor(readColorf(in));
			material.setSpecularColor(readColorf(in));
			material.setShininess(in.readFloat());
			material.setLightingEnabled(in.readBoolean());
			material.setNormalizeNormals(in.readBoolean());
			material.setColorTarget(readEnum(in, ColorTarget.values()));
			
			return material;
		} else {
			return null;
		}
	}
	
	public static void writeTransparencyAttributes(DataOutputStream out, TransparencyAttributes transparencyAttributes) throws IOException {
		if (nullCheck(out, transparencyAttributes)) {
			writeString(out, transparencyAttributes.getName());
			writeEnum(out, transparencyAttributes.getMode());
			writeEnum(out, transparencyAttributes.getSrcBlendFunction());
			writeEnum(out, transparencyAttributes.getDstBlendFunction());
			out.writeFloat(transparencyAttributes.getTransparency());
			out.writeBoolean(transparencyAttributes.isEnabled());
		}
	}
	
	public static TransparencyAttributes readTransparencyAttributes(DataInputStream in) throws IOException {
		TransparencyAttributes transparencyAttributes;
		
		if (nullCheck(in)) {
			transparencyAttributes = new TransparencyAttributes();
			
			transparencyAttributes.setName(readString(in));
			transparencyAttributes.setMode(readEnum(in, BlendMode.values()));
			transparencyAttributes.setSrcBlendFunction(readEnum(in, BlendFunction.values()));
			transparencyAttributes.setDstBlendFunction(readEnum(in, BlendFunction.values()));
			transparencyAttributes.setTransparency(in.readFloat());
			transparencyAttributes.setEnabled(in.readBoolean());
			
			return transparencyAttributes;
		} else {
			return null;
		}
	}
	
	public static void writeColoringAttributes(DataOutputStream out, ColoringAttributes coloringAttributes) throws IOException {
		if (nullCheck(out, coloringAttributes)) {
			writeString(out, coloringAttributes.getName());
			writeColorf(out, coloringAttributes.getColor());
			writeEnum(out, coloringAttributes.getShadeModel());
		}
	}
	
	public static ColoringAttributes readColoringAttributes(DataInputStream in) throws IOException {
		ColoringAttributes coloringAttributes;
		
		if (nullCheck(in)) {
			coloringAttributes = new ColoringAttributes();
			
			coloringAttributes.setName(readString(in));
			coloringAttributes.setColor(readColorf(in));
			coloringAttributes.setShadeModel(readEnum(in, ShadeModel.values()));
			
			return coloringAttributes;
		} else {
			return null;
		}
	}
	
	public static void writeLineAttributes(DataOutputStream out, LineAttributes lineAttributes) throws IOException {
		if (nullCheck(out, lineAttributes)) {
			writeString(out, lineAttributes.getName());
			out.writeFloat(lineAttributes.getLineWidth());
			writeEnum(out, lineAttributes.getLinePattern());
			out.writeInt(lineAttributes.getPatternMask());
			out.writeInt(lineAttributes.getPatternScaleFactor());
			out.writeBoolean(lineAttributes.isLineAntialiasingEnabled());
		}
	}
	
	public static LineAttributes readLineAttributes(DataInputStream in) throws IOException {
		LineAttributes lineAttributes;
		
		if (nullCheck(in)) {
			lineAttributes = new LineAttributes();
			
			lineAttributes.setName(readString(in));
			lineAttributes.setLineWidth(in.readFloat());
			lineAttributes.setLinePattern(readEnum(in, LinePattern.values()));
			lineAttributes.setPatternMask(in.readInt());
			lineAttributes.setPatternScaleFactor(in.readInt());
			lineAttributes.setLineAntialiasingEnabled(in.readBoolean());
			
			return lineAttributes;
		} else {
			return null;
		}
	}
	
	public static void writePointAttributes(DataOutputStream out, PointAttributes pointAttributes) throws IOException {
		if (nullCheck(out, pointAttributes)) {
			writeString(out, pointAttributes.getName());
			out.writeFloat(pointAttributes.getPointSize());
			out.writeBoolean(pointAttributes.isPointAntialiasingEnabled());
		}
	}
	
	public static PointAttributes readPointAttributes(DataInputStream in) throws IOException {
		PointAttributes pointAttributes;
		
		if (nullCheck(in)) {
			pointAttributes = new PointAttributes();
			
			pointAttributes.setName(readString(in));
			pointAttributes.setPointSize(in.readFloat());
			pointAttributes.setPointAntialiasingEnabled(in.readBoolean());
			
			return pointAttributes;
		} else {
			return null;
		}
	}
	
	public static void writePolygonAttributes(DataOutputStream out, PolygonAttributes polygonAttributes) throws IOException {
		if (nullCheck(out, polygonAttributes)) {
			writeString(out, polygonAttributes.getName());
			writeEnum(out, polygonAttributes.getFaceCullMode());
			writeEnum(out, polygonAttributes.getDrawMode());
			out.writeFloat(polygonAttributes.getPolygonOffset());
			out.writeFloat(polygonAttributes.getPolygonOffsetFactor());
			out.writeBoolean(polygonAttributes.getBackFaceNormalFlip());
			out.writeBoolean(polygonAttributes.isPolygonAntialiasingEnabled());
		}
	}
	
	public static PolygonAttributes readPolygonAttributes(DataInputStream in) throws IOException {
		PolygonAttributes polygonAttributes;
		
		if (nullCheck(in)) {
			polygonAttributes = new PolygonAttributes();
			
			polygonAttributes.setName(readString(in));
			polygonAttributes.setFaceCullMode(readEnum(in, FaceCullMode.values()));
			polygonAttributes.setDrawMode(readEnum(in, DrawMode.values()));
			polygonAttributes.setPolygonOffset(in.readFloat());
			polygonAttributes.setPolygonOffsetFactor(in.readFloat());
			polygonAttributes.setBackFaceNormalFlip(in.readBoolean());
			polygonAttributes.setPolygonAntialiasingEnabled(in.readBoolean());
			
			return polygonAttributes;
		} else {
			return null;
		}
	}
	
	public static void writeRenderingAttributes(DataOutputStream out, RenderingAttributes renderingAttributes) throws IOException {
		if (nullCheck(out, renderingAttributes)) {
			writeString(out, renderingAttributes.getName());
			out.writeBoolean(renderingAttributes.isDepthBufferEnabled());
			out.writeBoolean(renderingAttributes.isDepthBufferWriteEnabled());
			out.writeFloat(renderingAttributes.getAlphaTestValue());
			writeEnum(out, renderingAttributes.getAlphaTestFunction());
			writeStencilFunctionSeparate(out, renderingAttributes.getStencilFuncSeparate());
			writeStencilOperationSeparate(out, renderingAttributes.getStencilOpSeparate());
			writeStencilMaskSeparate(out, renderingAttributes.getStencilMaskSeparate());
			writeEnum(out, renderingAttributes.getDepthTestFunction());
			out.writeBoolean(renderingAttributes.getIgnoreVertexColors());
			out.writeBoolean(renderingAttributes.isStencilEnabled());
			writeEnum(out, renderingAttributes.getStencilOpFail());
			writeEnum(out, renderingAttributes.getStencilOpZFail());
			writeEnum(out, renderingAttributes.getStencilOpZPass());
			writeEnum(out, renderingAttributes.getStencilTestFunction());
			out.writeInt(renderingAttributes.getStencilRef());
			out.writeInt(renderingAttributes.getStencilMask());
			out.writeBoolean(renderingAttributes.hasColorWriteMask());
			if (renderingAttributes.hasColorWriteMask()) {
				out.writeInt(renderingAttributes.getColorWriteMask());
			}
		}
	}
	
	public static RenderingAttributes readRenderingAttributes(DataInputStream in) throws IOException {
		RenderingAttributes renderingAttributes;
		boolean hasColorWriteMask;
		
		if (nullCheck(in)) {
			renderingAttributes = new RenderingAttributes();
			
			renderingAttributes.setName(readString(in));
			renderingAttributes.setDepthBufferEnabled(in.readBoolean());
			renderingAttributes.setDepthBufferWriteEnabled(in.readBoolean());
			renderingAttributes.setAlphaTestValue(in.readFloat());
			renderingAttributes.setAlphaTestFunction(readEnum(in, TestFunction.values()));
			renderingAttributes.setStencilFuncSeparate(readStencilFunctionSeparate(in));
			renderingAttributes.setStencilOpSeparate(readStencilOperationSeparate(in));
			renderingAttributes.setStencilMaskSeparate(readStencilMaskSeparate(in));
			renderingAttributes.setDepthTestFunction(readEnum(in, TestFunction.values()));
			renderingAttributes.setIgnoreVertexColors(in.readBoolean());
			renderingAttributes.setStencilEnabled(in.readBoolean());
			renderingAttributes.setStencilOpFail(readEnum(in, StencilOperation.values()));
			renderingAttributes.setStencilOpZFail(readEnum(in, StencilOperation.values()));
			renderingAttributes.setStencilOpZPass(readEnum(in, StencilOperation.values()));
			renderingAttributes.setStencilTestFunction(readEnum(in, TestFunction.values()));
			renderingAttributes.setStencilRef(in.readInt());
			renderingAttributes.setStencilMask(in.readInt());
			hasColorWriteMask = in.readBoolean();
			if (hasColorWriteMask) {
				renderingAttributes.setColorWriteMask(in.readInt());
			}
			
			return renderingAttributes;
		} else {
			return null;
		}
	}
	
	public static void writeStencilFunctionSeparate(DataOutputStream out, StencilFuncSeparate stencilFuncSeparate) throws IOException {
		if (nullCheck(out, stencilFuncSeparate)) {
			writeEnum(out, stencilFuncSeparate.getFace());
			writeEnum(out, stencilFuncSeparate.getTestFunction());
			out.writeInt(stencilFuncSeparate.getRef());
			out.writeInt(stencilFuncSeparate.getMask());
		}
	}
	
	public static StencilFuncSeparate readStencilFunctionSeparate(DataInputStream in) throws IOException {
		StencilFuncSeparate stencilFuncSeparate;
		StencilFace stencilFace;
		
		if (nullCheck(in)) {
			stencilFace = readEnum(in, StencilFace.values());
			
			stencilFuncSeparate = new StencilFuncSeparate(stencilFace);
			
			stencilFuncSeparate.setTestFunction(readEnum(in, TestFunction.values()));
			stencilFuncSeparate.setRef(in.readInt());
			stencilFuncSeparate.setMask(in.readInt());
			
			return stencilFuncSeparate;
		} else {
			return null;
		}
	}
	
	public static void writeStencilOperationSeparate(DataOutputStream out, StencilOpSeparate stencilOpSeparate) throws IOException {
		if (nullCheck(out, stencilOpSeparate)) {
			writeEnum(out, stencilOpSeparate.getFace());
			writeEnum(out, stencilOpSeparate.getSFail());
			writeEnum(out, stencilOpSeparate.getDPFail());
			writeEnum(out, stencilOpSeparate.getDPPass());
		}
	}
	
	public static StencilOpSeparate readStencilOperationSeparate(DataInputStream in) throws IOException {
		StencilOpSeparate stencilOpSeparate;
		StencilFace face;
		
		if (nullCheck(in)) {
			face = readEnum(in, StencilFace.values());
			
			stencilOpSeparate = new StencilOpSeparate(face);
			
			stencilOpSeparate.setSFail(readEnum(in, StencilOperation.values()));
			stencilOpSeparate.setDPFail(readEnum(in, StencilOperation.values()));
			stencilOpSeparate.setDPPass(readEnum(in, StencilOperation.values()));
			
			return stencilOpSeparate;
		} else {
			return null;
		}
	}
	
	public static void writeStencilMaskSeparate(DataOutputStream out, StencilMaskSeparate stencilMaskSeparate) throws IOException {
		if (nullCheck(out, stencilMaskSeparate)) {
			writeEnum(out, stencilMaskSeparate.getFace());
			out.writeInt(stencilMaskSeparate.getMask());
		}
	}
	
	public static StencilMaskSeparate readStencilMaskSeparate(DataInputStream in) throws IOException {
		StencilMaskSeparate stencilMaskSeparate;
		StencilFace face;
		
		if (nullCheck(in)) {
			face = readEnum(in, StencilFace.values());
			
			stencilMaskSeparate = new StencilMaskSeparate(face);
			stencilMaskSeparate.setMask(in.readInt());
			
			return stencilMaskSeparate;
		} else {
			return null;
		}
	}
	
	public static void writeTextureUnit(DataOutputStream out, TextureUnit textureUnit) throws IOException {
		if (nullCheck(out, textureUnit)) {
			writeString(out, textureUnit.getName());
			writeTexture(out, textureUnit.getTexture());
			writeTextureAttributes(out, textureUnit.getTextureAttributes());
			writeTexCoordGeneration(out, textureUnit.getTexCoordGeneration());
		}
	}
	
	public static TextureUnit readTextureUnit(DataInputStream in) throws IOException {
		TextureUnit textureUnit;
		
		if (nullCheck(in)) {
			textureUnit = new TextureUnit();
			
			textureUnit.setName(readString(in));
			textureUnit.setTexture(readTexture(in));
			textureUnit.setTextureAttributes(readTextureAttributes(in));
			textureUnit.setTexCoordGeneration(readTexCoordGeneration(in));
			
			return textureUnit;
		} else {
			return null;
		}
	}
	
	public static void writeTexture(DataOutputStream out, Texture texture) throws IOException {
		TextureImage[][] images;
		
		if (nullCheck(out, texture)) {
			writeString(out, texture.getName());
			writeEnum(out, texture.getType());
			writeEnum(out, texture.getFormat());
			
			switch (texture.getType()) {
			case TEXTURE_2D:
				out.writeBoolean(((Texture2D) texture).hasUpdateList());
				
				break;
			case TEXTURE_3D:
				writeEnum(out, ((Texture3D) texture).getBoundaryModeR());
				
				break;
			case TEXTURE_CUBE_MAP:
				// Copying texture images exclusively for TextureCubeMap!
				images = (TextureImage[][]) PrivateAccessor.getPrivateField((TextureCubeMap) texture, "images");
				// TODO: Is it ok?
				for (int j = 0; j < 6; j++) {
					writeTextureImage(out, images[j][0]);
				}
				
				break;
			default:
				// TODO:
				throw new RuntimeException("Unknown texture type");
			}
			
			if (!texture.getType().equals(TextureType.TEXTURE_CUBE_MAP)) {
				// Copying texture images for Texture2D and Texture3D!
				
				out.writeInt(texture.getImagesCount());
				for (int i = 0; i < texture.getImagesCount(); i++) {
					writeTextureImage(out, texture.getImage(i));
				}
			}
			
			out.writeBoolean(texture.isEnabled());
			
			writeEnum(out, texture.getBoundaryModeS());
			writeEnum(out, texture.getBoundaryModeT());
			writeColorf(out, texture.getBoundaryColor());
			
			out.writeInt(texture.getBoundaryWidth());
			
			writeEnum(out, texture.getFilter());
		}
	}
	
	public static Texture readTexture(DataInputStream in) throws IOException {
		Texture texture;
		String name;
		TextureType type;
		TextureFormat format;
		
		if (nullCheck(in)) {
			name = readString(in);
			type = readEnum(in, TextureType.values());
			format = readEnum(in, TextureFormat.values());
			
			switch (type) {
			case TEXTURE_2D:
				texture = new Texture2D(format);
				
				((Texture2D) texture).setHasUpdateList(in.readBoolean());
				
				break;
			case TEXTURE_3D:
				texture = new Texture3D(format);
				
				((Texture3D) texture).setBoundaryModeR(readEnum(in, TextureBoundaryMode.values()));
				
				break;
			case TEXTURE_CUBE_MAP:
				// TODO: How to read texture images and use textures here?
				// CubeTextureSet cubeTextureSet;
				// cubeTextureSet = new CubeTextureSet(texFront, texRight,
				// texBack, texLeft, texTop, texBottom);
				// texture = new TextureCubeMap(format, 0, cubeTextureSet);
				// break;
			default:
				// TODO:
				throw new RuntimeException("Unknown texture type");
			}
			
			texture.setName(name);
			
			if (!texture.getType().equals(TextureType.TEXTURE_CUBE_MAP)) {
				int imagesCount = in.readInt();
				
				for (int i = 0; i < imagesCount; i++) {
					texture.setImage(i, readTextureImage(in));
				}
			}
			
			texture.setEnabled(in.readBoolean());
			
			texture.setBoundaryModeS(readEnum(in, TextureBoundaryMode.values()));
			texture.setBoundaryModeT(readEnum(in, TextureBoundaryMode.values()));
			texture.setBoundaryColor(readColorf(in));
			
			texture.setBoundaryWidth(in.readInt());
			
			texture.setFilter(readEnum(in, TextureFilter.values()));
			
			return texture;
		} else {
			return null;
		}
	}
	
	public static void writeTextureImage(DataOutputStream out, TextureImage textureImage) throws IOException {
		if (nullCheck(out, textureImage)) {
			writeString(out, textureImage.getName());
			writeEnum(out, textureImage.getFormat());
			out.writeInt(textureImage.getWidth());
			out.writeInt(textureImage.getHeight());
			writeByteArray(out, BufferUtils.unsafeBufferRead(textureImage.getDataBuffer()));
			
			if (textureImage instanceof TextureImage2D) {
				out.writeInt(TIT_TEXTURE_IMAGE_2D);
				out.writeInt(textureImage.getOriginalWidth());
				out.writeInt(textureImage.getOriginalHeight());
				out.writeBoolean((Boolean) PrivateAccessor.getPrivateField(textureImage, "yUp"));
				writeEnum(out, textureImage.getInternalFormat());
			} else if (textureImage instanceof TextureImage3D) {
				out.writeInt(TIT_TEXTURE_IMAGE_3D);
				out.writeInt(((TextureImage3D) textureImage).getDepth());
			} else {
				throw new RuntimeException("Unknown texture image type");
			}
		}
	}
	
	public static TextureImage readTextureImage(DataInputStream in) throws IOException {
		TextureImage textureImage;
		String name;
		TextureImageFormat format;
		int width;
		int height;
		int originalWidth;
		int originalHeight;
		boolean yUp;
		TextureImageInternalFormat internalFormat;
		int depth;
		byte[] imageData;
		int textureImageType;
		
		if (nullCheck(in)) {
			name = readString(in);
			format = readEnum(in, TextureImageFormat.values());
			width = in.readInt();
			height = in.readInt();
			imageData = readByteArray(in);
			
			textureImageType = in.readInt();
			switch (textureImageType) {
			case TIT_TEXTURE_IMAGE_2D:
				originalWidth = in.readInt();
				originalHeight = in.readInt();
				yUp = in.readBoolean();
				internalFormat = readEnum(in, TextureImageInternalFormat.values());
				
				textureImage = new TextureImage2D(format, width, height, originalWidth, originalHeight, yUp, internalFormat);
				
				((TextureImage2D) textureImage).setImageData(imageData);
				
				break;
			case TIT_TEXTURE_IMAGE_3D:
				depth = in.readInt();
				
				textureImage = new TextureImage3D(format, width, height, depth);
				
				break;
			default:
				throw new RuntimeException("Unknown texture image type");
			}
			
			textureImage.setName(name);
			
			return textureImage;
		} else {
			return null;
		}
	}
	
	public static void writeTextureAttributes(DataOutputStream out, TextureAttributes textureAttributes) throws IOException {
		if (nullCheck(out, textureAttributes)) {
			writeString(out, textureAttributes.getName());
			writeEnum(out, textureAttributes.getTextureMode());
			writeColorf(out, textureAttributes.getTextureBlendColor());
			writeEnum(out, textureAttributes.getPerspectiveCorrectionMode());
			writeTransform3D(out, textureAttributes.getTextureTransform());
			writeEnum(out, textureAttributes.getCombineRGBMode());
			writeEnum(out, textureAttributes.getCombineAlphaMode());
			
			for (int i = 0; i < 3; i++) {
				writeEnum(out, textureAttributes.getCombineRGBSource(i));
				writeEnum(out, textureAttributes.getCombineAlphaSource(i));
				writeEnum(out, textureAttributes.getCombineRGBFunction(i));
				writeEnum(out, textureAttributes.getCombineAlphaFunction(i));
			}
			
			out.writeInt(textureAttributes.getCombineRGBScale());
			out.writeInt(textureAttributes.getCombineAlphaScale());
			writeEnum(out, textureAttributes.getCompareMode());
			writeEnum(out, textureAttributes.getCompareFunction());
		}
	}
	
	public static TextureAttributes readTextureAttributes(DataInputStream in) throws IOException {
		TextureAttributes textureAttributes;
		
		if (nullCheck(in)) {
			textureAttributes = new TextureAttributes();
			
			textureAttributes.setName(readString(in));
			textureAttributes.setTextureMode(readEnum(in, TextureMode.values()));
			textureAttributes.setTextureBlendColor(readColorf(in));
			textureAttributes.setPerspectiveCorrectionMode(readEnum(in, PerspectiveCorrectionMode.values()));
			textureAttributes.setTextureTransform(readTransform3D(in));
			textureAttributes.setCombineRGBMode(readEnum(in, TextureCombineMode.values()));
			textureAttributes.setCombineAlphaMode(readEnum(in, TextureCombineMode.values()));
			
			for (int i = 0; i < 3; i++) {
				textureAttributes.setCombineRGBSource(i, readEnum(in, TextureCombineSource.values()));
				textureAttributes.setCombineAlphaSource(i, readEnum(in, TextureCombineSource.values()));
				textureAttributes.setCombineRGBFunction(i, readEnum(in, TextureCombineFunction.values()));
				textureAttributes.setCombineAlphaFunction(i, readEnum(in, TextureCombineFunction.values()));
			}
			
			textureAttributes.setCombineRGBScale(in.readInt());
			textureAttributes.setCombineAlphaScale(in.readInt());
			textureAttributes.setCompareMode(readEnum(in, TextureCompareMode.values()));
			textureAttributes.setCompareFunction(readEnum(in, CompareFunction.values()));
			
			return textureAttributes;
		} else {
			return null;
		}
	}
	
	public static void writeTexCoordGeneration(DataOutputStream out, TexCoordGeneration texCoordGeneration) throws IOException {
		if (nullCheck(out, texCoordGeneration)) {
			writeString(out, texCoordGeneration.getName());
			writeEnum(out, texCoordGeneration.getGenMode());
			writeEnum(out, texCoordGeneration.getFormat());
			writeVector4f(out, texCoordGeneration.getPlaneS());
			writeVector4f(out, texCoordGeneration.getPlaneT());
			writeVector4f(out, texCoordGeneration.getPlaneR());
			writeVector4f(out, texCoordGeneration.getPlaneQ());
			out.writeBoolean(texCoordGeneration.isEnabled());
		}
	}
	
	public static TexCoordGeneration readTexCoordGeneration(DataInputStream in) throws IOException {
		TexCoordGeneration texCoordGeneration;
		
		if (nullCheck(in)) {
			texCoordGeneration = new TexCoordGeneration();
			
			texCoordGeneration.setName(readString(in));
			texCoordGeneration.setGenMode(readEnum(in, TexCoordGenMode.values()));
			texCoordGeneration.setFormat(readEnum(in, CoordMode.values()));
			texCoordGeneration.setPlaneS(readVector4f(in));
			texCoordGeneration.setPlaneT(readVector4f(in));
			texCoordGeneration.setPlaneR(readVector4f(in));
			texCoordGeneration.setPlaneQ(readVector4f(in));
			texCoordGeneration.setEnabled(in.readBoolean());
			
			return texCoordGeneration;
		} else {
			return null;
		}
	}
	
	public static boolean nullCheck(DataOutputStream arg0, Object arg1) throws IOException {
		arg0.writeBoolean(arg1 != null);
		return (arg1 != null);
	}
	
	public static boolean nullCheck(DataInputStream in) throws IOException {
		return in.readBoolean();
	}
	
	public static void writeBitSet(DataOutputStream out, BitSet bitSet) throws IOException {
		if (nullCheck(out, bitSet)) {
			writeString(out, bitSet.toString());
		}
	}
	
	public static BitSet readBitSet(DataInputStream in) throws IOException {
		String bitSetString;
		BitSet bitSet;
		
		if (nullCheck(in)) {
			bitSetString = readString(in);
			
			bitSet = new BitSet();
			if (bitSetString != null) {
				bitSetString = bitSetString.substring(1, bitSetString.length() - 1);
				for (String bitPosition : bitSetString.split(", ")) {
					bitSet.set(Integer.parseInt(bitPosition));
				}
			}
			
			return bitSet;
		} else {
			return null;
		}
	}
	
	public static void writeTuple2f(DataOutputStream out, Tuple2f tuple) throws IOException {
		if (nullCheck(out, tuple)) {
			out.writeFloat(tuple.getX());
			out.writeFloat(tuple.getY());
		}
	}
	
	public static Tuple2f readTuple2f(DataInputStream in) throws IOException {
		if (nullCheck(in)) {
			return new Tuple2f(in.readFloat(), in.readFloat());
		} else {
			return null;
		}
	}
	
	public static Point3f readPoint3f(DataInputStream in) throws IOException {
		if (nullCheck(in)) {
			return new Point3f(in.readFloat(), in.readFloat(), in.readFloat());
		} else {
			return null;
		}
	}
	
	public static void writeGeometry(DataOutputStream out, Geometry geometry) throws IOException {
		int geometryType;
		GeometryDataContainer geometryDataContainer;
		
		if (nullCheck(out, geometry)) {
			writeString(out, geometry.getName());
			
			// geometry type/class
			if (geometry instanceof LineStripArray) {
				geometryType = GT_LINE_STRIP_ARRAY;
			} else if (geometry instanceof TriangleFanArray) {
				geometryType = GT_TRIANGLE_FAN_ARRAY;
			} else if (geometry instanceof TriangleStripArray) {
				geometryType = GT_TRIANGLE_STRIP_ARRAY;
			} else if (geometry instanceof IndexedLineStripArray) {
				geometryType = GT_INDEXED_LINE_STRIP_ARRAY;
			} else if (geometry instanceof IndexedTriangleStripArray) {
				geometryType = GT_INDEXED_TRIANGLE_STRIP_ARRAY;
			} else if (geometry instanceof IndexedLineArray) {
				geometryType = GT_INDEXED_LINE_ARRAY;
			} else if (geometry instanceof IndexedQuadArray) {
				geometryType = GT_INDEXED_QUAD_ARRAY;
			} else if (geometry instanceof IndexedTriangleArray) {
				geometryType = GT_INDEXED_TRIANGLE_ARRAY;
			} else if (geometry instanceof IndexedTriangleFanArray) {
				geometryType = GT_INDEXED_TRIANGLE_FAN_ARRAY;
			} else if (geometry instanceof LineArray) {
				geometryType = GT_LINE_ARRAY;
			} else if (geometry instanceof PointArray) {
				geometryType = GT_POINT_ARRAY;
			} else if (geometry instanceof QuadArray) {
				geometryType = GT_QUAD_ARRAY;
			} else if (geometry instanceof TriangleArray) {
				geometryType = GT_TRIANGLE_ARRAY;
			} else {
				// TODO:
				throw new RuntimeException("Unknown geometry type");
			}
			
			out.writeInt(geometryType);
			
			out.writeInt(geometry.getCoordinatesSize());
			out.writeInt(geometry.getVertexCount());
			writeEnum(out, geometry.getOptimization());
			
			geometryDataContainer = (GeometryDataContainer) PrivateAccessor.getPrivateField(geometry, "dataContainer");
			
			out.writeInt(geometryDataContainer.getValidVertexCount());
			out.writeLong(geometryDataContainer.getCoordinatesOffset());
			out.writeInt((Integer) PrivateAccessor.getPrivateField(geometryDataContainer, "numIndices"));
			out.writeInt((Integer) geometryDataContainer.getInitialIndex());
			
			out.writeBoolean(geometryDataContainer.hasIndex());
			if (geometryDataContainer.hasIndex()) {
				writeGeomNioIntData(out, geometryDataContainer.getIndexData());
			}
			
			out.writeBoolean(geometryDataContainer.isInterleaved());
			if (geometryDataContainer.isInterleaved()) {
				writeGeomNioFloatDataJagatoo(out, geometryDataContainer.getInterleavedData());
			} else {
				if (nullCheck(out, geometryDataContainer.getCoordinatesData())) {
					writeGeomNioFloatDataJagatoo(out, geometryDataContainer.getCoordinatesData());
				}
				
				out.writeBoolean(geometryDataContainer.hasNormals());
				if (geometryDataContainer.hasNormals()) {
					writeGeomNioFloatDataJagatoo(out, geometryDataContainer.getNormalsData());
				}
				
				out.writeBoolean(geometryDataContainer.hasColors());
				if (geometryDataContainer.hasColors()) {
					writeGeomNioFloatDataJagatoo(out, geometryDataContainer.getColorData());
				}
				
				out.writeBoolean(geometryDataContainer.hasTextureCoordinates());
				if (geometryDataContainer.hasTextureCoordinates()) {
					GeomNioFloatData[] texCoords = (GeomNioFloatData[]) PrivateAccessor.getPrivateField(geometryDataContainer, "texCoords");
					
					out.writeInt(texCoords.length);
					for (int i = 0; i < texCoords.length; i++) {
						writeGeomNioFloatDataJagatoo(out, texCoords[i]);
					}
				}
				
				out.writeInt(geometryDataContainer.getVertexAttributesCount());
				for (int i = 0; i < geometryDataContainer.getVertexAttributesCount(); i++) {
					writeGeomNioFloatDataJagatoo(out, geometryDataContainer.getVertexAttribData(i));
				}
			}
			
			writeIntArray(out, geometryDataContainer.getStripCounts());
			
			int[] texCoordSetMap_nonPublic = (int[]) PrivateAccessor.getPrivateField(geometryDataContainer, "texCoordSetMap");
			writeIntArray(out, texCoordSetMap_nonPublic);
			
			writeIntArray(out, geometryDataContainer.getTexCoordSetMap());
			
			for (int unit = 0; unit < GeometryDataContainer.TEXTURE_COORDINATES; unit++) {
				out.writeInt(geometryDataContainer.getTexCoordSize(unit));
			}
			
			out.writeInt(geometryDataContainer.getColorsSize());
			out.writeInt(geometryDataContainer.getVertexFormat());
			out.writeLong(geometryDataContainer.getNormalsOffset());
			out.writeLong(geometryDataContainer.getColorsOffset());
			
			for (int unit = 0; unit < GeometryDataContainer.TEXTURE_COORDINATES; unit++) {
				out.writeLong(geometryDataContainer.getTexCoordsOffset(unit));
			}
			
			for (int vertexAttributes = 0; vertexAttributes < GeometryDataContainer.VERTEX_ATTRIBUTES; vertexAttributes++) {
				out.writeLong(geometryDataContainer.getVertexAttribsOffset(vertexAttributes));
			}
		}
	}
	
	public static void writeAppearance(DataOutputStream out, Appearance appearance) throws IOException {
		if (nullCheck(out, appearance)) {
			writeString(out, appearance.getName());
			writeMaterial(out, appearance.getMaterial());
			writeTransparencyAttributes(out, appearance.getTransparencyAttributes());
			writeColoringAttributes(out, appearance.getColoringAttributes());
			writeLineAttributes(out, appearance.getLineAttributes());
			writePointAttributes(out, appearance.getPointAttributes());
			writePolygonAttributes(out, appearance.getPolygonAttributes());
			writeRenderingAttributes(out, appearance.getRenderingAttributes());
			// TODO: Serialize shader program context!!!
			
			out.writeInt(appearance.getTextureUnitsCount());
			for (int i = 0; i < appearance.getTextureUnitsCount(); i++) {
				writeTextureUnit(out, appearance.getTextureUnit(i));
			}
		}
	}
	
	public static Geometry readGeometry(DataInputStream in) throws IOException {
		Geometry geometry;
		String name;
		int geometryType;
		int coordsSize;
		int vertexCount;
		Optimization optimization;
		GeometryDataContainer geometryDataContainer;
		boolean hasIndex;
		boolean isInterleaved;
		boolean hasNormals;
		boolean hasColors;
		boolean hasTextureCoordinates;
		int vertexAttributesCount;
		GeomNioFloatData[] vertexAttribs;
		int[] textureUnitSize;
		long[] vertexAttribsOffsets;
		long[] texCoordsOffsets;
		GeomNioFloatData data;
		
		if (nullCheck(in)) {
			name = readString(in);
			geometryType = in.readInt();
			coordsSize = in.readInt();
			vertexCount = in.readInt();
			optimization = readEnum(in, Optimization.values());
			
			// geometry type/class
			switch (geometryType) {
			case GT_LINE_STRIP_ARRAY:
				geometry = new LineStripArray(coordsSize, vertexCount);
				break;
			case GT_TRIANGLE_FAN_ARRAY:
				geometry = new TriangleFanArray(coordsSize, vertexCount);
				break;
			case GT_TRIANGLE_STRIP_ARRAY:
				geometry = new TriangleStripArray(coordsSize, vertexCount);
				break;
			case GT_INDEXED_LINE_STRIP_ARRAY:
				geometry = new IndexedLineStripArray(coordsSize, vertexCount);
				break;
			case GT_INDEXED_TRIANGLE_STRIP_ARRAY:
				geometry = new IndexedTriangleStripArray(coordsSize, vertexCount);
				break;
			case GT_INDEXED_LINE_ARRAY:
				geometry = new IndexedLineArray(coordsSize, vertexCount);
				break;
			case GT_INDEXED_QUAD_ARRAY:
				geometry = new IndexedQuadArray(coordsSize, vertexCount);
				break;
			case GT_INDEXED_TRIANGLE_ARRAY:
				geometry = new IndexedTriangleArray(coordsSize, vertexCount);
				break;
			case GT_INDEXED_TRIANGLE_FAN_ARRAY:
				// TODO: See {@link IndexedTriangleFanArray} constructor!
				geometry = new IndexedTriangleFanArray(coordsSize, vertexCount, null);
				break;
			case GT_LINE_ARRAY:
				geometry = new LineArray(coordsSize, vertexCount);
				break;
			case GT_POINT_ARRAY:
				geometry = new PointArray(coordsSize, vertexCount);
				break;
			case GT_QUAD_ARRAY:
				geometry = new QuadArray(coordsSize, vertexCount);
				break;
			case GT_TRIANGLE_ARRAY:
				geometry = new TriangleArray(coordsSize, vertexCount);
				break;
			default:
				// TODO:
				throw new RuntimeException("");
			}
			
			geometry.setName(name);
			
			geometryDataContainer = (GeometryDataContainer) PrivateAccessor.getPrivateField(geometry, "dataContainer");
			
			geometryDataContainer.setValidVertexCount(in.readInt());
			PrivateAccessor.setPrivateField(geometryDataContainer, "coordsOffset", in.readLong());
			PrivateAccessor.setPrivateField(geometryDataContainer, "numIndices", in.readInt());
			geometryDataContainer.setInitialIndex(in.readInt());
			
			hasIndex = in.readBoolean();
			if (hasIndex) {
				PrivateAccessor.setPrivateField(geometryDataContainer, "hasIndex", true);
				PrivateAccessor.setPrivateField(geometryDataContainer, "indexData", readGeomNioIntData(in));
			} else {
				PrivateAccessor.setPrivateField(geometryDataContainer, "hasIndex", false);
			}
			
			isInterleaved = in.readBoolean();
			if (isInterleaved) {
				PrivateAccessor.setPrivateField(geometryDataContainer, "isInterleaved", true);
				PrivateAccessor.setPrivateField(geometryDataContainer, "interleavedData", readGeomNioFloatData(in));
			} else {
				PrivateAccessor.setPrivateField(geometryDataContainer, "isInterleaved", false);
				
				if (nullCheck(in)) {
					PrivateAccessor.setPrivateField(geometryDataContainer, "coords", readGeomNioFloatData(in));
				}
				
				hasNormals = in.readBoolean();
				if (hasNormals) {
					// Don't need to set hasNormals to true
					geometryDataContainer.setNormalData(readGeomNioFloatData(in));
				}
				
				hasColors = in.readBoolean();
				if (hasColors) {
					// Don't need to set hasColors to true
					geometryDataContainer.setColorData(readGeomNioFloatData(in));
				}
				
				hasTextureCoordinates = in.readBoolean();
				if (hasTextureCoordinates) {
					int texCoordsLength;
					texCoordsLength = in.readInt();
					for (int i = 0; i < texCoordsLength; i++) {
						data = readGeomNioFloatData(in);
						
						// FIXME:
						geometryDataContainer.setTextureCoordinate(i, 0, new float[] {
						0.0f, 0.0f
						});
						
						if (data != null) {
							geometryDataContainer.setTexCoordData(i, data);
						} else {
							geometryDataContainer.setTexCoordData(i, null);
						}
					}
				}
				
				vertexAttributesCount = in.readInt();
				vertexAttribs = new GeomNioFloatData[vertexAttributesCount];
				for (int i = 0; i < vertexAttributesCount; i++) {
					vertexAttribs[i] = readGeomNioFloatData(in);
				}
				
				PrivateAccessor.setPrivateField(geometryDataContainer, "vertexAttribs", vertexAttribs);
			}
			
			geometryDataContainer.setStripCounts(readIntArray(in));
			
			PrivateAccessor.setPrivateField(geometryDataContainer, "texCoordSetMap", readIntArray(in));
			PrivateAccessor.setPrivateField(geometryDataContainer, "texCoordSetMap_public", readIntArray(in));
			
			textureUnitSize = new int[GeometryDataContainer.TEXTURE_COORDINATES];
			for (int unit = 0; unit < GeometryDataContainer.TEXTURE_COORDINATES; unit++) {
				textureUnitSize[unit] = in.readInt();
			}
			PrivateAccessor.setPrivateField(geometryDataContainer, "textureUnitSize", textureUnitSize);
			
			PrivateAccessor.setPrivateField(geometryDataContainer, "colorsSize", in.readInt());
			PrivateAccessor.setPrivateField(geometryDataContainer, "vertexFormat", in.readInt());
			PrivateAccessor.setPrivateField(geometryDataContainer, "normalsOffset", in.readLong());
			PrivateAccessor.setPrivateField(geometryDataContainer, "colorsOffset", in.readLong());
			
			texCoordsOffsets = new long[GeometryDataContainer.TEXTURE_COORDINATES];
			for (int unit = 0; unit < GeometryDataContainer.TEXTURE_COORDINATES; unit++) {
				texCoordsOffsets[unit] = in.readLong();
			}
			PrivateAccessor.setPrivateField(geometryDataContainer, "texCoordsOffsets", texCoordsOffsets);
			
			vertexAttribsOffsets = new long[GeometryDataContainer.VERTEX_ATTRIBUTES];
			for (int vertexAttributes = 0; vertexAttributes < GeometryDataContainer.VERTEX_ATTRIBUTES; vertexAttributes++) {
				vertexAttribsOffsets[vertexAttributes] = in.readLong();
			}
			PrivateAccessor.setPrivateField(geometryDataContainer, "vertexAttribsOffsets", vertexAttribsOffsets);
			
			geometry.setOptimization(optimization);
			
			geometry.setBoundsDirty();
			
			return geometry;
		} else {
			return null;
		}
	}
	
	public static Appearance readAppearance(DataInputStream in) throws IOException {
		Appearance appearance;
		int textureUnitsCount;
		
		if (nullCheck(in)) {
			appearance = new Appearance();
			
			appearance.setName(readString(in));
			appearance.setMaterial(readMaterial(in));
			appearance.setTransparencyAttributes(readTransparencyAttributes(in));
			appearance.setColoringAttributes(readColoringAttributes(in));
			appearance.setLineAttributes(readLineAttributes(in));
			appearance.setPointAttributes(readPointAttributes(in));
			appearance.setPolygonAttributes(readPolygonAttributes(in));
			appearance.setRenderingAttributes(readRenderingAttributes(in));
			
			textureUnitsCount = in.readInt();
			for (int i = 0; i < textureUnitsCount; i++) {
				appearance.setTextureUnit(i, readTextureUnit(in));
			}
			
			// TODO: Deserialize shader program context!!!
			
			return appearance;
		} else {
			return null;
		}
	}
	
	public static void writeBounds(DataOutputStream out, Bounds bounds) throws IOException {
		if (nullCheck(out, bounds)) {
			// TODO: Serialize bounds properly
			writeEnum(out, bounds.getType());
		}
	}
	
	public static Bounds readBounds(DataInputStream in) throws IOException {
		BoundsType type;
		Bounds bounds;
		
		if (nullCheck(in)) {
			type = readEnum(in, BoundsType.values());
			
			switch (type) {
			case AABB:
				bounds = new BoundingBox();
				break;
			case POLYTOPE:
				bounds = new BoundingPolytope();
				break;
			default:
				bounds = new BoundingSphere();
			}
			
			return bounds;
		} else {
			return null;
		}
	}
	
	public static void writeGroupAnimator(DataOutputStream out, GroupAnimator groupAnimator) throws IOException {
		int type;
		
		if (nullCheck(out, groupAnimator)) {
			if (groupAnimator instanceof GroupRotator) {
				type = GA_ROTATOR;
			} else if (groupAnimator instanceof GroupTranslator) {
				type = GA_TRANSLATOR;
			} else {
				throw new UnsupportedOperationException("Unsupported group animator type: " + groupAnimator.getClass().getName());
			}
			
			out.writeInt(type);
			writeTransformationDirectives(out, groupAnimator.getTransformationDirectives());
			writeTransform3D(out, groupAnimator.getTransform());
		}
	}
	
	public static GroupAnimator readGroupAnimator(DataInputStream in) throws IOException {
		int type;
		TransformationDirectives transformationDirectives;
		Transform3D transform;
		GroupAnimator groupAnimator;
		
		if (nullCheck(in)) {
			type = in.readInt();
			transformationDirectives = readTransformationDirectives(in);
			transform = readTransform3D(in);
			
			switch (type) {
			case GA_ROTATOR:
				groupAnimator = new GroupRotator(transformationDirectives);
				break;
			case GA_TRANSLATOR:
				groupAnimator = new GroupTranslator(transformationDirectives);
				break;
			default:
				throw new IOException("Unexpected group animator type: " + type);
			}
			
			groupAnimator.setTransform(transform);
			
			return groupAnimator;
		} else {
			return null;
		}
	}

	public static void writeTransformationDirectives(DataOutputStream out, TransformationDirectives transformationDirectives) throws IOException {
		if (nullCheck(out, transformationDirectives)) {
			writeVector3f(out, transformationDirectives.getUserAxis());
			
			if (transformationDirectives.getUserAxis() == null) {
				out.writeFloat(transformationDirectives.getInitValueX());
				out.writeFloat(transformationDirectives.getInitValueY());
				out.writeFloat(transformationDirectives.getInitValueZ());
				out.writeFloat(transformationDirectives.getSpeedX());
				out.writeFloat(transformationDirectives.getSpeedY());
				out.writeFloat(transformationDirectives.getSpeedZ());
				writeEnum(out, transformationDirectives.getAxisOrder());
			} else {
				out.writeFloat(transformationDirectives.getInitValueUser());
				out.writeFloat(transformationDirectives.getSpeedUser());
			}
		}
	}
	
	public static TransformationDirectives readTransformationDirectives(DataInputStream in) throws IOException {
		Vector3f userAxis;
		float initValueX;
		float initValueY;
		float initValueZ;
		float speedX;
		float speedY;
		float speedZ;
		AxisOrder axisOrder;
		float initValueUser;
		float speedUser;
		
		if (nullCheck(in)) {
			userAxis = readVector3f(in);
			
			if (userAxis == null) {
				initValueX = in.readFloat();
				initValueY = in.readFloat();
				initValueZ = in.readFloat();
				speedX = in.readFloat();
				speedY = in.readFloat();
				speedZ = in.readFloat();
				axisOrder = readEnum(in, AxisOrder.values());
				
				return new TransformationDirectives(initValueX, initValueY, initValueZ, speedX, speedY, speedZ, axisOrder);
			} else {
				initValueUser = in.readFloat();
				speedUser = in.readFloat();
				
				return new TransformationDirectives(userAxis, initValueUser, speedUser);
			}
		} else {
			return null;
		}
	}
	
}
