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
import com.cv4j.image.util.Tools;

/**
 * The sobel filter.
 */
public class SobelFilter extends BaseFilter {
    
    /**
     *Sobel filter y
     */
	public static int[] sobel_y = new int[] { -1, -2, -1, 0, 0, 0, 1, 2, 1 };
    
    /**
     *Sobel filter x
     */
	public static int[] sobel_x = new int[] { -1, 0, 1, -2, 0, 2, -1, 0, 1 };
	
	private boolean xdirect;

	/**
	 * Constructor.
	 */
	public SobelFilter() {
		xdirect = true;
	}

	/**
	 * @return Is X direct
	 */
	public boolean isXdirect() {
		return xdirect;
	}

	/**
	 * Set x direct
	 * @param xdirect The x direct.
	 */
	public void setXdirect(boolean xdirect) {
		this.xdirect = xdirect;
	}

	@Override
	public ImageProcessor doFilter(ImageProcessor src){

		byte[][] output = new byte[3][width*height];
		int[] sobel;
		int offset = 0;

		if(xdirect)
			sobel = sobel_x;
		else
			sobel = sobel_y;

		int r = 0;
		int g = 0;
		int b = 0;
		for (int row = 1; row < height - 1; row++) {
			offset = row * width;
			for (int col = 1; col < width - 1; col++) {
				// red
				r = redPhaseOfFilter(sobel, offset, col);

				// green
				g = greenPhaseOfFilter(sobel, offset, col);

				// blue
				b = bluePhaseOfFilter(sobel, offset, col);

				output[0][offset + col] = (byte)Tools.clamp(r);
				output[1][offset + col] = (byte)Tools.clamp(g);
				output[2][offset + col] = (byte)Tools.clamp(b);

			}
		}

		((ColorProcessor) src).putRGB(output[0], output[1], output[2]);
		return src;
	}

    private int redPhaseOfFilter(int[] sobel, int offset, int col) {
	    return    sobel[0] * (R[offset - width + col - 1] & 0xff)
                + sobel[1] * (R[offset - width + col] & 0xff)
                + sobel[2] * (R[offset - width + col + 1] & 0xff)
                + sobel[3] * (R[offset + col - 1] & 0xff)
                + sobel[4] * (R[offset + col] & 0xff)
                + sobel[5] * (R[offset + col + 1] & 0xff)
                + sobel[6] * (R[offset + width + col - 1] & 0xff)
                + sobel[7] * (R[offset + width + col] & 0xff)
                + sobel[8] * (R[offset + width + col + 1] & 0xff);
    }

    private int greenPhaseOfFilter(int[] sobel, int offset, int col) {
	    return    sobel[0] * (G[offset - width + col - 1]  & 0xff)
                + sobel[1] * (G[offset - width + col] & 0xff)
                + sobel[2] * (G[offset - width + col + 1] & 0xff)
                + sobel[3] * (G[offset + col - 1] & 0xff)
                + sobel[4] * (G[offset + col] & 0xff)
                + sobel[5] * (G[offset + col + 1] & 0xff)
                + sobel[6] * (G[offset + width + col - 1] & 0xff)
                + sobel[7] * (G[offset + width + col] & 0xff)
                + sobel[8] * (G[offset + width + col + 1] & 0xff);
    }

    private int bluePhaseOfFilter(int[] sobel, int offset, int col) {
	    return    sobel[0] * (B[offset - width + col - 1] & 0xff)
                + sobel[1] * (B[offset - width + col] & 0xff)
                + sobel[2] * (B[offset - width + col + 1] & 0xff)
                + sobel[3] * (B[offset + col - 1] & 0xff)
                + sobel[4] * (B[offset + col] & 0xff)
                + sobel[5] * (B[offset + col + 1] & 0xff)
                + sobel[6] * (B[offset + width + col - 1] & 0xff)
                + sobel[7] * (B[offset + width + col] & 0xff)
                + sobel[8] * (B[offset + width + col + 1] & 0xff);
    }

}
