/*
 * Copyright (c) 2017-present, CV4J Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cv4j.core.spatial.conv.edge;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.filters.BaseFilter;

import static com.cv4j.image.util.Tools.clamp;

/**
 * The find-edge filter.
 */
public class FindEdgeFilter extends BaseFilter {

	/**
	 * The horizontal Sobel operator filter is used for edge detection
	 * <p>
	 * This is a 3x3 filter:<br>
	 * -1 -2 -1 <br>
	 *  0  0  0 <br>
	 *  1  2  1 <br>
	 */
	public static final int[] sobel_x = new int[] { -1, -2, -1, 0, 0, 0, 1, 2, 1 };
	/**
	 * Vertical Sobel operator filters for edge detection
	 * <p>
	 * This is a 3x3 filter:<br>
	 * -1  0  1 <br>
	 * -2  0  2 <br>
	 * -1  0  1 <br>
	 */
	public static final int[] sobel_y = new int[] { -1, 0, 1, -2, 0, 2, -1, 0, 1 };

	@Override
	public ImageProcessor doFilter(ImageProcessor src) {

		int total = width*height;
		byte[][] output = new byte[3][total];

		int offset = 0;

		int r = 0;
		int g = 0;
		int b = 0;
		for (int row = 1; row < height - 1; row++) {
			offset = row * width;
			for (int col = 1; col < width - 1; col++) {
				// magnitude
				r = (int)Math.sqrt(Math.pow(redPhaseOneOfFilter(sobel_y, offset, col), 2) + Math.pow(redPhaseTwoOfFilter(sobel_x, offset, col), 2));
                g = (int)Math.sqrt(Math.pow(greenPhaseOneOfFilter(sobel_y, offset, col), 2) + Math.pow(greenPhaseTwoOfFilter(sobel_x, offset, col), 2));
                b = (int)Math.sqrt(Math.pow(bluePhaseOneOfFilter(sobel_y, offset, col), 2) + Math.pow(bluePhaseTwoOfFilter(sobel_x, offset, col), 2));
				
				// find edges
                double dy = redPhaseOneOfFilter(sobel_y, offset, col) + greenPhaseOneOfFilter(sobel_y, offset, col)+ bluePhaseOneOfFilter(sobel_y, offset, col);
                double dx = redPhaseTwoOfFilter(sobel_y, offset, col) + greenPhaseTwoOfFilter(sobel_y, offset, col)+ bluePhaseTwoOfFilter(sobel_y, offset, col);
                double theta = Math.atan(dy/dx);

				output[0][offset + col] = (byte)clamp(r);
				output[1][offset + col] = (byte)clamp(g);
				output[2][offset + col] = (byte)clamp(b);

			}
		}
		((ColorProcessor) src).putRGB(output[0], output[1], output[2]);
		return src;
	}

	/**
	 * Red Phase One Of Filter
	 * @param sobel_y
	 * @param offset
	 * @param col
	 * @return
	 */
	private int redPhaseOneOfFilter(int[] sobel_y, int offset, int col) {
		return    sobel_y[0] * (R[offset - width + col - 1] & 0xff)
				+ sobel_y[1] * (R[offset - width + col] & 0xff)
				+ sobel_y[2] * (R[offset - width + col + 1] & 0xff)
				+ sobel_y[3] * (R[offset + col - 1] & 0xff)
				+ sobel_y[4] * (R[offset + col] & 0xff)
				+ sobel_y[5] * (R[offset + col + 1] & 0xff)
				+ sobel_y[6] * (R[offset + width + col - 1] & 0xff)
				+ sobel_y[7] * (R[offset + width + col] & 0xff)
				+ sobel_y[8] * (R[offset + width + col + 1] & 0xff);
	}

	/**
	 * Red Phase Two Of Filter
	 * @param sobel_x
	 * @param offset
	 * @param col
	 * @return
	 */
	private int redPhaseTwoOfFilter(int[] sobel_x, int offset, int col) {
		return    sobel_x[0] * (R[offset - width + col - 1] & 0xff)
				+ sobel_x[1] * (R[offset - width + col] & 0xff)
				+ sobel_x[2] * (R[offset - width + col + 1] & 0xff)
				+ sobel_x[3] * (R[offset + col - 1] & 0xff)
				+ sobel_x[4] * (R[offset + col] & 0xff)
				+ sobel_x[5] * (R[offset + col + 1] & 0xff)
				+ sobel_x[6] * (R[offset + width + col - 1] & 0xff)
				+ sobel_x[7] * (R[offset + width + col] & 0xff)
				+ sobel_x[8] * (R[offset + width + col + 1] & 0xff);
	}

	private int greenPhaseOneOfFilter(int[] sobel_y, int offset, int col) {
        return    sobel_y[0] * (G[offset - width + col - 1] & 0xff)
                + sobel_y[1] * (G[offset - width + col] & 0xff)
                + sobel_y[2] * (G[offset - width + col + 1] & 0xff)
                + sobel_y[3] * (G[offset + col - 1] & 0xff)
                + sobel_y[4] * (G[offset + col] & 0xff)
                + sobel_y[5] * (G[offset + col + 1] & 0xff)
                + sobel_y[6] * (G[offset + width + col - 1] & 0xff)
                + sobel_y[7] * (G[offset + width + col] & 0xff)
                + sobel_y[8] * (G[offset + width + col + 1] & 0xff);
	}

	private int greenPhaseTwoOfFilter(int[] sobel_x, int offset, int col) {
		return    sobel_x[0] * (G[offset - width + col - 1] & 0xff)
				+ sobel_x[1] * (G[offset - width + col] & 0xff)
				+ sobel_x[2] * (G[offset - width + col + 1] & 0xff)
				+ sobel_x[3] * (G[offset + col - 1] & 0xff)
				+ sobel_x[4] * (G[offset + col] & 0xff)
				+ sobel_x[5] * (G[offset + col + 1] & 0xff)
				+ sobel_x[6] * (G[offset + width + col - 1] & 0xff)
				+ sobel_x[7] * (G[offset + width + col] & 0xff)
				+ sobel_x[8] * (G[offset + width + col + 1] & 0xff);
	}

	private int bluePhaseOneOfFilter(int[] sobel_y, int offset, int col) {
		return    sobel_y[0] * (B[offset - width + col - 1] & 0xff)
				+ sobel_y[1] * (B[offset - width + col] & 0xff)
				+ sobel_y[2] * (B[offset - width + col + 1] & 0xff)
				+ sobel_y[3] * (B[offset + col - 1] & 0xff)
				+ sobel_y[4] * (B[offset + col] & 0xff)
				+ sobel_y[5] * (B[offset + col + 1] & 0xff)
				+ sobel_y[6] * (B[offset + width + col - 1] & 0xff)
				+ sobel_y[7] * (B[offset + width + col] & 0xff)
				+ sobel_y[8] * (B[offset + width + col + 1] & 0xff);
	}

	private int bluePhaseTwoOfFilter(int[] sobel_x, int offset, int col) {
		return    sobel_x[0] * (B[offset - width + col - 1] & 0xff)
				+ sobel_x[1] * (B[offset - width + col] & 0xff)
				+ sobel_x[2] * (B[offset - width + col + 1] & 0xff)
				+ sobel_x[3] * (B[offset + col - 1] & 0xff)
				+ sobel_x[4] * (B[offset + col] & 0xff)
				+ sobel_x[5] * (B[offset + col + 1] & 0xff)
				+ sobel_x[6] * (B[offset + width + col - 1] & 0xff)
				+ sobel_x[7] * (B[offset + width + col] & 0xff)
				+ sobel_x[8] * (B[offset + width + col + 1] & 0xff);
	}

}
