package br.edu.univercidade.cc.xithcluster.util;

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
	
	private BufferUtils() {
	}
	
	private static boolean useDirectBuffers = true;
	
	public static void setUseDirectBuffers(boolean b) {
		useDirectBuffers = b;
	}
	
	public static final boolean getUseDirectBuffers() {
		return useDirectBuffers;
	}
	
	public static ByteBuffer createByteBuffer(int size) {
		if (useDirectBuffers) {
			return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
		} else {
			return ByteBuffer.allocate(size).order(ByteOrder.nativeOrder());
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
	
	public static float[] safeBufferRead(FloatBuffer arg0) {
		float[] buffer;
		
		if (arg0 == null) {
			throw new IllegalArgumentException();
		}
		
		if (arg0.isDirect()) {
			buffer = new float[arg0.limit()];
			arg0.rewind();
			arg0.get(buffer);
			arg0.rewind();
			
			return buffer;
		} else {
			return arg0.array();
		}
	}
	
	public static int[] safeBufferRead(IntBuffer arg0) {
		int[] buffer;
		
		if (arg0 == null) {
			throw new IllegalArgumentException();
		}
		
		if (arg0.isDirect()) {
			buffer = new int[arg0.limit()];
			arg0.rewind();
			arg0.get(buffer);
			arg0.rewind();
			
			return buffer;
		} else {
			return arg0.array();
		}
	}
	
	public static byte[] safeBufferRead(ByteBuffer arg0) {
		byte[] buffer;
		
		if (arg0 == null) {
			throw new IllegalArgumentException();
		}
		
		if (arg0.isDirect()) {
			buffer = new byte[arg0.limit()];
			arg0.rewind();
			arg0.get(buffer);
			arg0.rewind();
			
			return buffer;
		} else {
			return arg0.array();
		}
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
	
}
