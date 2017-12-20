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
		int x0 = sobel_x[0];
		int x1 = sobel_x[1];
		int x2 = sobel_x[2];
		int x3 = sobel_x[3];
		int x4 = sobel_x[4];
		int x5 = sobel_x[5];
		int x6 = sobel_x[6];
		int x7 = sobel_x[7];
		int x8 = sobel_x[8];
		
		int k0 = sobel_y[0];
		int k1 = sobel_y[1];
		int k2 = sobel_y[2];
		int k3 = sobel_y[3];
		int k4 = sobel_y[4];
		int k5 = sobel_y[5];
		int k6 = sobel_y[6];
		int k7 = sobel_y[7];
		int k8 = sobel_y[8];

		int yr = 0;
		int yg = 0;
		int yb = 0;
		int xr = 0;
		int xg = 0;
		int xb = 0;
		int r = 0;
		int g = 0;
		int b = 0;
		for (int row = 1; row < height - 1; row++) {
			offset = row * width;
			for (int col = 1; col < width - 1; col++) {
				// red
				yr = redPhaseOneOfFilter(k0, k1, k2, k3, k4, k5, k6, k7, k8, offset, col);

				xr = redPhaseTwoOfFilter(x0, x1, x2, x3, x4, x5, x6, x7, x8, offset, col);
				
				// green
				yg = greenPhaseOneOfFilter(k0, k1, k2, k3, k4, k5, k6, k7, k8, offset, col);

				xg = greenPhaseTwoOfFilter(x0, x1, x2, x3, x4, x5, x6, x7, x8, offset, col);

				// blue
				yb = bluePhaseOneOfFilter(k0, k1, k2, k3, k4, k5, k6, k7, k8, offset, col);

				xb = bluePhaseTwoOfFilter(x0, x1, x2, x3, x4, x5, x6, x7, x8, offset, col);
				
				// magnitude 
				r = (int)Math.sqrt(yr*yr + xr*xr);
				g = (int)Math.sqrt(yg*yg + xg*xg);
				b = (int)Math.sqrt(yb*yb + xb*xb);
				
				// find edges
				output[0][offset + col] = (byte)clamp(r);
				output[1][offset + col] = (byte)clamp(g);
				output[2][offset + col] = (byte)clamp(b);

				double dy = (yr+yg+yb);
				double dx = (xr+xg+xb);
				double theta = Math.atan(dy/dx);
				
				// for next pixel
				yr = 0;
				yg = 0;
				yb = 0;
				xr=0;
				xg=0;
				xb=0;
			}
		}
		((ColorProcessor) src).putRGB(output[0], output[1], output[2]);
		output[0] = null;
		output[1] = null;
		output[2] = null;
		output = null;
		return src;
	}

	/**
	 * Red Phase One Of Filter
	 * @param k0
	 * @param k1
	 * @param k2
	 * @param k3
	 * @param k4
	 * @param k5
	 * @param k6
	 * @param k7
	 * @param k8
	 * @param offset
	 * @param col
	 * @return
	 */
	private int redPhaseOneOfFilter(int k0, int k1, int k2, int k3, int k4, int k5, int k6, int k7,
								   int k8, int offset, int col) {
		return k0 * (R[offset - width + col - 1] & 0xff)
				+ k1 * (R[offset - width + col] & 0xff)
				+ k2 * (R[offset - width + col + 1] & 0xff)
				+ k3 * (R[offset + col - 1] & 0xff)
				+ k4 * (R[offset + col] & 0xff)
				+ k5 * (R[offset + col + 1] & 0xff)
				+ k6 * (R[offset + width + col - 1] & 0xff)
				+ k7 * (R[offset + width + col] & 0xff)
				+ k8 * (R[offset + width + col + 1] & 0xff);
	}

	/**
	 * Red Phase Two Of Filter
	 * @param x0
	 * @param x1
	 * @param x2
	 * @param x3
	 * @param x4
	 * @param x5
	 * @param x6
	 * @param x7
	 * @param x8
	 * @param offset
	 * @param col
	 * @return
	 */
	private int redPhaseTwoOfFilter(int x0, int x1, int x2, int x3, int x4, int x5, int x6, int x7,
								   int x8, int offset, int col) {
		return x0 * (R[offset - width + col - 1] & 0xff)
				+ x1 * (R[offset - width + col] & 0xff)
				+ x2 * (R[offset - width + col + 1] & 0xff)
				+ x3 * (R[offset + col - 1] & 0xff)
				+ x4 * (R[offset + col] & 0xff)
				+ x5 * (R[offset + col + 1] & 0xff)
				+ x6 * (R[offset + width + col - 1] & 0xff)
				+ x7 * (R[offset + width + col] & 0xff)
				+ x8 * (R[offset + width + col + 1] & 0xff);
	}

	private int greenPhaseOneOfFilter(int k0, int k1, int k2, int k3, int k4, int k5, int k6, int k7,
									int k8, int offset, int col) {
		return k0 * (G[offset - width + col - 1] & 0xff)
				+ k1 * (G[offset - width + col] & 0xff)
				+ k2 * (G[offset - width + col + 1] & 0xff)
				+ k3 * (G[offset + col - 1] & 0xff)
				+ k4 * (G[offset + col] & 0xff)
				+ k5 * (G[offset + col + 1] & 0xff)
				+ k6 * (G[offset + width + col - 1] & 0xff)
				+ k7 * (G[offset + width + col] & 0xff)
				+ k8 * (G[offset + width + col + 1] & 0xff);
	}

	private int greenPhaseTwoOfFilter(int x0, int x1, int x2, int x3, int x4, int x5, int x6, int x7,
									int x8, int offset, int col) {
		return x0 * (G[offset - width + col - 1] & 0xff)
				+ x1 * (G[offset - width + col] & 0xff)
				+ x2 * (G[offset - width + col + 1] & 0xff)
				+ x3 * (G[offset + col - 1] & 0xff)
				+ x4 * (G[offset + col] & 0xff)
				+ x5 * (G[offset + col + 1] & 0xff)
				+ x6 * (G[offset + width + col - 1] & 0xff)
				+ x7 * (G[offset + width + col] & 0xff)
				+ x8 * (G[offset + width + col + 1] & 0xff);
	}

	private int bluePhaseOneOfFilter(int k0, int k1, int k2, int k3, int k4, int k5, int k6, int k7,
									  int k8, int offset, int col) {
		return k0 * (B[offset - width + col - 1] & 0xff)
				+ k1 * (B[offset - width + col] & 0xff)
				+ k2 * (B[offset - width + col + 1] & 0xff)
				+ k3 * (B[offset + col - 1] & 0xff)
				+ k4 * (B[offset + col] & 0xff)
				+ k5 * (B[offset + col + 1] & 0xff)
				+ k6 * (B[offset + width + col - 1] & 0xff)
				+ k7 * (B[offset + width + col] & 0xff)
				+ k8 * (B[offset + width + col + 1] & 0xff);
	}

	private int bluePhaseTwoOfFilter(int x0, int x1, int x2, int x3, int x4, int x5, int x6, int x7,
									  int x8, int offset, int col) {
		return x0 * (B[offset - width + col - 1] & 0xff)
				+ x1 * (B[offset - width + col] & 0xff)
				+ x2 * (B[offset - width + col + 1] & 0xff)
				+ x3 * (B[offset + col - 1] & 0xff)
				+ x4 * (B[offset + col] & 0xff)
				+ x5 * (B[offset + col + 1] & 0xff)
				+ x6 * (B[offset + width + col - 1] & 0xff)
				+ x7 * (B[offset + width + col] & 0xff)
				+ x8 * (B[offset + width + col + 1] & 0xff);
	}

}
