package br.edu.univercidade.cc.xithcluster.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

public final class BufferUtils {
	
	private static boolean useDirectBuffers = true;
	
	private static ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
	
	private BufferUtils() {
	}
	
	public static void setUseDirectBuffers(boolean b) {
		BufferUtils.useDirectBuffers = b;
	}
	
	public static void setByteOrder(ByteOrder byteOrder) {
		BufferUtils.byteOrder = byteOrder;
	}
	
	public static final boolean getUseDirectBuffers() {
		return BufferUtils.useDirectBuffers;
	}
	
	public static ByteOrder getByteOrder() {
		return BufferUtils.byteOrder;
	}
	
	public static ByteBuffer createByteBuffer(int size) {
		if (BufferUtils.useDirectBuffers) {
			return ByteBuffer.allocateDirect(size).order(BufferUtils.byteOrder);
		} else {
			return ByteBuffer.allocate(size).order(BufferUtils.byteOrder);
		}
	}
	
	public static ByteBuffer createByteBuffer(byte[] values) {
		ByteBuffer buffer;
		
		if (values == null) {
			throw new IllegalArgumentException();
		}
		
		buffer = createByteBuffer(values.length);
		buffer.put(values);
		buffer.flip();
		
		return buffer;
	}
	
	public static ShortBuffer createShortBuffer(int size) {
		return createByteBuffer(size << 1).asShortBuffer();
	}
	
	public static ShortBuffer createShortBuffer(short[] values) {
		ShortBuffer buffer;
		
		if (values == null) {
			throw new IllegalArgumentException();
		}
		
		buffer = createShortBuffer(values.length);
		buffer.put(values);
		buffer.flip();
		
		return buffer;
	}
	
	public static CharBuffer createCharBuffer(int size) {
		return createByteBuffer(size << 1).asCharBuffer();
	}
	
	public static CharBuffer createCharBuffer(char[] values) {
		CharBuffer buffer;
		
		if (values == null) {
			throw new IllegalArgumentException();
		}
		
		buffer = createCharBuffer(values.length);
		buffer.put(values);
		buffer.flip();
		
		return buffer;
	}
	
	public static IntBuffer createIntBuffer(int size) {
		return createByteBuffer(size << 2).asIntBuffer();
	}
	
	public static IntBuffer createIntBuffer(int[] values) {
		IntBuffer buffer;
		
		if (values == null) {
			throw new IllegalArgumentException();
		}
		
		buffer = createIntBuffer(values.length);
		buffer.put(values);
		buffer.flip();
		
		return buffer;
	}
	
	public static LongBuffer createLongBuffer(int size) {
		return createByteBuffer(size << 3).asLongBuffer();
	}
	
	public static LongBuffer createLongBuffer(long[] values) {
		LongBuffer buffer;
		
		if (values == null) {
			throw new IllegalArgumentException();
		}
		
		buffer = createLongBuffer(values.length);
		buffer.put(values);
		buffer.flip();
		
		return buffer;
	}
	
	public static FloatBuffer createFloatBuffer(int size) {
		return createByteBuffer(size << 2).asFloatBuffer();
	}
	
	public static FloatBuffer createFloatBuffer(float[] values) {
		FloatBuffer buffer;
		
		if (values == null) {
			throw new IllegalArgumentException();
		}
		
		buffer = createFloatBuffer(values.length);
		buffer.put(values);
		buffer.flip();
		
		return buffer;
	}
	
	public static DoubleBuffer createDoubleBuffer(int size) {
		return createByteBuffer(size << 3).asDoubleBuffer();
	}
	
	public static DoubleBuffer createDoubleBuffer(double[] values) {
		DoubleBuffer buffer;
		
		if (values == null) {
			throw new IllegalArgumentException();
		}
		
		buffer = createDoubleBuffer(values.length);
		buffer.put(values);
		buffer.flip();
		
		return buffer;
	}
	
	public static ByteBuffer wrapAndRewind(byte[] arg0) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(arg0);
		byteBuffer.order(BufferUtils.byteOrder);
		byteBuffer.rewind();
		
		return byteBuffer;
	}
	
	public static float[] safeBufferRead(FloatBuffer arg0) {
		float[] buffer;
		
		if (arg0 == null) {
			throw new IllegalArgumentException();
		}
	
		arg0.rewind();
		
		buffer = new float[arg0.limit()];
		arg0.get(buffer);
		arg0.rewind();
		
		return buffer;
	}
	
	public static int[] safeBufferRead(IntBuffer arg0) {
		int[] buffer;
		
		if (arg0 == null) {
			throw new IllegalArgumentException();
		}
		
		arg0.rewind();
		
		buffer = new int[arg0.limit()];
		arg0.get(buffer);
		arg0.rewind();
		
		return buffer;
	}
	
	public static byte[] unsafeBufferRead(ByteBuffer arg0) {
		byte[] buffer;
		
		if (arg0 == null) {
			return null;
		}
		
		arg0.rewind();
		
		buffer = new byte[arg0.limit()];
		arg0.get(buffer);
		arg0.rewind();
		
		return buffer;
	}
	
	public static byte[] safeBufferRead(ByteBuffer arg0) {
		byte[] buffer;
		
		if (arg0 == null) {
			throw new IllegalArgumentException();
		}
		
		arg0.rewind();
		
		buffer = new byte[arg0.limit()];
		arg0.get(buffer);
		arg0.rewind();
		
		return buffer;
	}
	
	public static boolean equals(FloatBuffer arg0, FloatBuffer arg1) {
		return Arrays.equals(safeBufferRead(arg0), safeBufferRead(arg1));
	}
	
	public static boolean equals(IntBuffer arg0, IntBuffer arg1) {
		return Arrays.equals(safeBufferRead(arg0), safeBufferRead(arg1));
	}
	
	public static boolean equals(ByteBuffer arg0, ByteBuffer arg1) {
		return Arrays.equals(safeBufferRead(arg0), safeBufferRead(arg1));
	}
	
	public static FloatBuffer wrapAsFloatBuffer(byte[] arg0) {
		return BufferUtils.wrapAndRewind(arg0).asFloatBuffer();
	}
	
}
