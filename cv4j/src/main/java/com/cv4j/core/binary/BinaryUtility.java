package com.cv4j.core.binary;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.number.IntIntegralImage;
import com.cv4j.core.datamodel.Size;

/**
 * Binary utility.
 */
public class BinaryUtility {
	
	public void process(ByteProcessor binary, Size structureElement, int iteration, int outputNum) {
		final int width  = binary.getWidth();
		final int height = binary.getHeight();

		byte[] output = new byte[width * height];
		byte[] input = binary.getGray();

		IntIntegralImage intIntegralImage = new IntIntegralImage();

		for(int i = 0; i < iteration; i++) {
			intIntegralImage.setImage(input);
			intIntegralImage.process(width, height);
			System.arraycopy(input, 0, output, 0, input.length);

			setOutputAsOutputNum(output, outputNum, intIntegralImage, structureElement, width, height);

			System.arraycopy(output, 0, input, 0, input.length);
		}
	}

	private void setOutputAsOutputNum(byte[] output, int outputNum, IntIntegralImage intIntegralImage, Size structureElement, int width, int height) {
		final int blockSum = getBlockSum(structureElement);
		final int xr       = structureElement.cols / 2;
		final int yr       = structureElement.rows / 2;

		for(int row = 0; row < height; row++) {
			for(int col = 0; col < width; col++) {
				final int ny  = row+yr;
				final int nx  = col+xr;
				final int intIntegralImageBlockSum = intIntegralImage.getBlockSum(nx, ny, (yr * 2 + 1), (xr * 2 + 1));

				if(intIntegralImageBlockSum > 0 && intIntegralImageBlockSum < blockSum) {
					output[row*width+col] = (byte) outputNum;
				}
			}
		}
	}

	private int getBlockSum(Size structureElement) {
		final int maxRGB = 255;

		return structureElement.cols * structureElement.rows * maxRGB;
	}
}