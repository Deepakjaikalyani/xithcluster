package br.edu.univercidade.cc.xithcluster;

public final class LinearCompositionStrategy implements CompositionStrategy {
	
	@Override
	public int[] compose(int width, int height, int numImages, byte[][] colorAndAlphaBuffer, byte[][] depthBuffer) {
		int[] imageData;
		byte z;
		int k;
		int l;
		
		imageData = new int[width * height];
		for (int i = 0; i < imageData.length; i++) {
			z = Byte.MIN_VALUE;
			k = -1;
			
			for (int j = 0; j < numImages; j++) {
				if (z < depthBuffer[j][i]) {
					k = j;
					z = depthBuffer[j][i];
				}
			}
			
			l = i * 4;
			
			imageData[i] = toInt(colorAndAlphaBuffer[k][l + 1], colorAndAlphaBuffer[k][l + 2], colorAndAlphaBuffer[k][l + 3]);
		}
		
		return imageData;
	}
	
	public static final int toInt(byte... b) 
	{
	    int i = 0;
	    
	    i |= b[0] & 0xFF;
	    i <<= 8;
	    i |= b[1] & 0xFF;
	    i <<= 8;
	    i |= b[2] & 0xFF;
	    
	    return i;
	}
	
}
