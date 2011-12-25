package br.edu.univercidade.cc.xithcluster.utils;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.nio.FloatBuffer;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

public class BufferUtilsTest {
	
	private float[] testData;
	
	@Before
	public void setUp() {
		testData = new float[128];
		for (int i = 0; i < testData.length; i++) {
			testData[i] = 11.0f;
		}
	}
	
	@Test
	public void testShouldConvertFloatBufferToFloatArray() {
		FloatBuffer buffer;
		
		buffer = FloatBuffer.allocate(128);
		for (int i = 0; i < 128; i++) {
			buffer.put(i, 11.0f);
		}
		
		assertTrue(Arrays.equals(testData, BufferUtils.safeBufferRead(buffer)));
	}
	
	@Test
	public void testShouldConvertIntBufferToIntArray() {
		FloatBuffer buffer;
		
		buffer = FloatBuffer.allocate(128);
		for (int i = 0; i < 128; i++) {
			buffer.put(i, 11.0f);
		}
		
		assertTrue(Arrays.equals(testData, BufferUtils.safeBufferRead(buffer)));
	}
	
	@Test
	public void testShouldCompareFloatBuffers() {
		fail();
	}
	
	@Test
	public void testShouldCompareIntBuffers() {
		fail();
	}
	
}
