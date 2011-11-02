package br.edu.univercidade.cc.xithcluster.composition;

import static br.edu.univercidade.cc.xithcluster.utils.AssertExtention.assertPixelBufferRegion;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.Before;
import org.junit.Test;

public class ImageOrderAndZBufferStrategyTest {
	
	private static final int HEIGHT = 120;
	
	private static final int WIDTH = 160;
	
	private static final String[] IMAGES_FILE_PATH = {
	"resources/test/image1.png", "resources/test/image2.png", "resources/test/image3.png"
	};
	
	private static String COMPOSITION_RESULT_IMAGE_FILE_PATH = "resources/test/compositionResult1.png";
	
	@Before
	public void setUp() throws Exception {
		byte[][] colorAndAlphaBuffersData = new byte[IMAGES_FILE_PATH.length][];
		
		int i = 0;
		for (String testImageFilePath : IMAGES_FILE_PATH) {
			colorAndAlphaBuffersData[i++] = readImageDataAsByteArray(testImageFilePath);
		}
		
		float[][] depthBuffersData = new float[IMAGES_FILE_PATH.length][];
		i = 0;
		for (String testImageFilePath : IMAGES_FILE_PATH) {
			float value = (3 - i) * (0.5f / (float) IMAGES_FILE_PATH.length);
			System.out.println(value);
			depthBuffersData[i] = normalizeArray(readImageDataAsIntArray(testImageFilePath), 0, 1.0f, value);
			i++;
		}
		
		CompositionContext.setDefaultBufferReadOrderClass(DirectBufferReadOrder.class);
		
		CompositionContext context = CompositionContext.getInstance(WIDTH, HEIGHT);
		
		context.setColorAndAlphaBuffers(ColorAndAlphaBufferList.wrap(colorAndAlphaBuffersData));
		context.setDepthBuffers(DepthBufferList.wrap(depthBuffersData));
	}
	
	private byte[] readImageDataAsByteArray(String imageFilePath) throws IOException {
		if (imageFilePath == null || imageFilePath.isEmpty()) {
			throw new IllegalArgumentException();
		}
		
		BufferedImage image = ImageIO.read(new FileInputStream(imageFilePath));
		BufferedImage imageCopy = createImageCopy(image, BufferedImage.TYPE_4BYTE_ABGR);
		
		byte[] buffer = ((DataBufferByte) imageCopy.getRaster().getDataBuffer()).getData();
		convertABGRtoARGB(buffer);
		
		return buffer;
	}
	
	private void convertABGRtoARGB(byte[] byteArray) {
		for (int i = 0; i < byteArray.length; i += 4) {
			byte red = byteArray[i + 3];
			byteArray[i + 3] = byteArray[i + 1];
			byteArray[i + 1] = red;
		}
	}
	
	private float[] normalizeArray(int[] array, int limit, float min, float max) {
		if (array == null || array.length == 0) {
			throw new IllegalArgumentException();
		}
		
		float[] normalizedArray = new float[array.length];
		for (int i = 0; i < array.length; i++) {
			if (array[i] > limit) {
				normalizedArray[i] = max;
			} else {
				normalizedArray[i] = min;
			}
		}
		
		return normalizedArray;
	}
	
	private int[] readImageDataAsIntArray(String imageFilePath) throws IOException {
		if (imageFilePath == null || imageFilePath.isEmpty()) {
			throw new IllegalArgumentException();
		}
		
		BufferedImage image = ImageIO.read(new FileInputStream(imageFilePath));
		BufferedImage imageCopy = createImageCopy(image, BufferedImage.TYPE_INT_RGB);
		
		return ((DataBufferInt) imageCopy.getRaster().getDataBuffer()).getData();
	}
	
	private BufferedImage createImageCopy(BufferedImage image, int imageType) {
		BufferedImage imageCopy;
		
		imageCopy = new BufferedImage(image.getWidth(), image.getHeight(), imageType);
		Graphics graphics = imageCopy.getGraphics();
		graphics.drawImage(image, 0, 0, null);
		image.flush();
		
		return imageCopy;
	}
	
	@Test
	public void testCompose() throws IOException {
		ImageOrderAndZBufferStrategy compositionStrategy = new ImageOrderAndZBufferStrategy();
		CompositionContext context = CompositionContext.getInstance(160, 120);
		
		compositionStrategy.compose(context);
		
		int[] actualPixelBuffer = context.getPixelBuffer().toIntArray();
		int[] expectedPixelBuffer = readImageDataAsIntArray(COMPOSITION_RESULT_IMAGE_FILE_PATH);
		
		// DEBUG:
		//PixelBufferUtil.dumpPixelBufferToFile(WIDTH, HEIGHT, actualPixelBuffer, "tmp/compositionResult1.png");
		
		// Asserting critical pixel regions that must be equal
		assertPixelBufferRegion(expectedPixelBuffer, actualPixelBuffer, 50, 50, 10, 10);
	}
	
}
