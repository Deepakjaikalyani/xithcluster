package br.edu.univercidade.cc.xithcluster.utils;

import java.nio.FloatBuffer;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import br.edu.univercidade.cc.xithcluster.utils.BufferUtils;

public class BufferUtilsTest {
	
	private static final float[] FLOAT_ARRAY;
	
	static {
		FLOAT_ARRAY = new float[128];
		for (int i = 0; i < FLOAT_ARRAY.length; i++) {
			FLOAT_ARRAY[i] = 11.0f;
		}
	}
	
	@Test
	public void testSafeFloatBufferRead() {
		FloatBuffer buffer;
		
		buffer = FloatBuffer.allocate(128);
		for (int i = 0; i < 128; i++) {
			buffer.put(i, 11.0f);
		}
		
		Assert.assertTrue(Arrays.equals(FLOAT_ARRAY, BufferUtils.safeBufferRead(buffer)));
	}
	
	@Test
	public void testSafeIntBufferRead() {
		FloatBuffer buffer;
		
		buffer = FloatBuffer.allocate(128);
		for (int i = 0; i < 128; i++) {
			buffer.put(i, 11.0f);
		}
		
		Assert.assertTrue(Arrays.equals(FLOAT_ARRAY, BufferUtils.safeBufferRead(buffer)));
	}
	
	@Test
	public void testFloatBufferEquals() {
	}
	
	@Test
	public void testIntBufferEquals() {
	}
	
}
