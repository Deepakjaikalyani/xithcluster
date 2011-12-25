package br.edu.univercidade.cc.xithcluster.composition;

import static br.edu.univercidade.cc.xithcluster.utils.AssertExtention.assertPixelBufferRegion;
import static org.junit.Assert.assertEquals;
import java.io.IOException;
import org.junit.Test;
import br.edu.univercidade.cc.xithcluster.utils.ImageUtils;

public class PixelBufferTest {
	
	private static final int WIDTH = 160;
	
	private static final int HEIGHT = 120;
	
	@Test
	public void testReadAndWritePixelsDirectly() {
		PixelBuffer pixelBuffer = new PixelBuffer(WIDTH, HEIGHT);
		
		for (int i = 0; i < 50; i++) {
			pixelBuffer.add(0xff00ff00);
		}
		
		for (int i = 0; i < 50; i++) {
			assertEquals(0xff00ff00, pixelBuffer.get(i));
		}
	}
	
	@Test
	public void testReadPixelRegion() throws IOException {
		String filePath = "resources/test/image1.png";
		
		PixelBuffer pixelBuffer1 = new PixelBuffer(WIDTH, HEIGHT, ImageUtils.readImageDataAsIntArray(filePath));
		PixelBuffer pixelBuffer2 = new PixelBuffer(WIDTH, HEIGHT, ImageUtils.readImageDataAsIntArray(filePath));
		
		assertPixelBufferRegion(pixelBuffer1, pixelBuffer2, 50, 50, 50, 50);
	}
	
}
