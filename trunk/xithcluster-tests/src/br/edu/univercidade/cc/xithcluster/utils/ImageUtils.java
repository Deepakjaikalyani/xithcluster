package br.edu.univercidade.cc.xithcluster.utils;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public final class ImageUtils {
	
	public static byte[] readImageDataAsByteArray(String imageFilePath) throws IOException {
		if (imageFilePath == null || imageFilePath.isEmpty()) {
			throw new IllegalArgumentException();
		}
		
		BufferedImage image = ImageIO.read(new FileInputStream(imageFilePath));
		BufferedImage imageCopy = createImageCopy(image, BufferedImage.TYPE_4BYTE_ABGR);
		
		byte[] data = ((DataBufferByte) imageCopy.getRaster().getDataBuffer()).getData();
		convertABGRtoARGB(data);
		
		return data;
	}
	
	private static void convertABGRtoARGB(byte[] data) {
		if (data == null || data.length == 0) {
			throw new IllegalArgumentException();
		}
		
		for (int i = 0; i < data.length; i += 4) {
			byte alpha = data[i];
			byte blue = data[i + 1];
			byte green = data[i + 2];
			byte red = data[i + 3];
			data[i] = alpha;
			data[i + 1] = red;
			data[i + 2] = green;
			data[i + 3] = blue;
		}
	}
	
	public static int[] readImageDataAsIntArray(String filePath) throws IOException {
		if (filePath == null || filePath.isEmpty()) {
			throw new IllegalArgumentException();
		}
		
		BufferedImage image = ImageIO.read(new FileInputStream(filePath));
		BufferedImage imageCopy = createImageCopy(image, BufferedImage.TYPE_INT_ARGB);
		
		return ((DataBufferInt) imageCopy.getRaster().getDataBuffer()).getData();
	}
	
	private static BufferedImage createImageCopy(BufferedImage image, int imageType) {
		BufferedImage imageCopy;
		
		imageCopy = new BufferedImage(image.getWidth(), image.getHeight(), imageType);
		Graphics graphics = imageCopy.getGraphics();
		graphics.drawImage(image, 0, 0, null);
		imageCopy.flush();
		
		return imageCopy;
	}
	
	public static void dumpImageDataToFile(int width, int height, int[] imageData, String imageFileName) throws IOException {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0, 0, width, height, imageData, 0, width);
		ImageIO.write(image, "png", new File(imageFileName));
	}
	
}
