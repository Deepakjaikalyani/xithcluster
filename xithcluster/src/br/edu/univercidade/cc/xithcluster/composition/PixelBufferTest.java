package br.edu.univercidade.cc.xithcluster.composition;

import java.io.IOException;
import org.junit.Test;
import br.edu.univercidade.cc.xithcluster.utils.ImageUtil;
import static br.edu.univercidade.cc.xithcluster.utils.AssertExtention.assertPixelBufferRegion;
import static org.junit.Assert.assertEquals;

public class PixelBufferTest {
	
	private static final int WIDTH = 160;
	
	private static final int HEIGHT = 120;
	
	@Test
	public void testReadWritePixels() {
		PixelBuffer pixelBuffer = new PixelBuffer(WIDTH, HEIGHT);
		
		for (int i = 50; i < 100; i++) {
			pixelBuffer.put(i, 0xff00ff00);
		}
		
		for (int i = 50; i < 100; i++) {
			assertEquals(0xff00ff00, pixelBuffer.get(i));
		}
	}
	
	@Test
	public void testReadPixelRegion() throws IOException {
		String filePath = "resources/test/image1.png";
		
		PixelBuffer pixelBuffer1 = new PixelBuffer(WIDTH, HEIGHT, ImageUtil.readImageDataAsIntArray(filePath));
		PixelBuffer pixelBuffer2 = new PixelBuffer(WIDTH, HEIGHT, ImageUtil.readImageDataAsIntArray(filePath));
		
		assertPixelBufferRegion(pixelBuffer1, pixelBuffer2, 50, 50, 50, 50);
	}
	
}
