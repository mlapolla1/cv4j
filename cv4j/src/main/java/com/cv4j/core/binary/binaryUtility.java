package com.cv4j.core.binary;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.IntIntegralImage;
import com.cv4j.core.datamodel.Size;

public class binaryUtility {
	
	public void process(ByteProcessor binary, Size structureElement, int iteration, int outputNum) {
		int width = binary.getWidth();
		int height = binary.getHeight();
		byte[] output = new byte[width*height];
		byte[] input = binary.getGray();
		IntIntegralImage ii = new IntIntegralImage();
		int blocksum = structureElement.cols*structureElement.rows*255;
		for(int i=0; i<iteration; i++) {
			ii.setImage(input);
			ii.process(width, height);
			System.arraycopy(input, 0, output, 0, input.length);
			for(int row=0; row<height; row++) {
				for(int col=0; col<width; col++) {
					int xr = structureElement.cols/2;
					int yr = structureElement.rows/2;
					int ny = row+yr;
					int nx = col+xr;
					int sum = ii.getBlockSum(nx, ny, (yr * 2 + 1), (xr * 2 + 1));
					if(sum > 0 && sum < blocksum) {
						output[row*width+col] = (byte)outputNum;
					}
				}
			}
			System.arraycopy(output, 0, input, 0, input.length);
		}

		// try to release memory
		output = null;
	}
}