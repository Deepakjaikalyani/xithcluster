package br.edu.univercidade.cc.xithcluster.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public final class PixelBufferUtil {
	
	public static void dumpPixelBufferToFile(int width, int height, int[] pixelBuffer, String imageFileName) throws IOException {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0, 0, width, height, pixelBuffer, 0, width);
		ImageIO.write(image, "png", new File(imageFileName));
	}
	
}
