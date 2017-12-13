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
package com.cv4j.core.spatial.conv;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.filters.BaseFilter;
import com.cv4j.image.util.Tools;

/**
 * The sharp filter.
 */
public class SharpFilter extends BaseFilter {

    /**
     * The kernel
     */
	public static int[] kernel=new int[]{-1,-1,-1, -1, 12, -1, -1,-1,-1};

	/**
	 * The type of color red.
	 */
	private static final int TYPE_COLOR_RED   = 0;
	
	/**
	 * The type of color grren.
	 */
	private static final int TYPE_COLOR_GREEN = 1;
	
	/**
	 * The type of color blue.
	 */
	private static final int TYPE_COLOR_BLUE  = 2;

	@Override
	public ImageProcessor doFilter(ImageProcessor src){

		int total = width * height;
		byte[][] output = new byte[3][total];

		int offset = 0;
		int k0 = kernel[0];
		int k1 = kernel[1];
		int k2 = kernel[2];
		int k3 = kernel[3];
		int k4 = kernel[4];
		int k5 = kernel[5];
		int k6 = kernel[6];
		int k7 = kernel[7];
		int k8 = kernel[8];
		
		int scale = (k0 + k1 + k2 + k3 + k4 + k5 + k6 + k7 + k8);
		
		int sr = 0;
		int sg = 0;
		int sb = 0;
		
		int r = 0;
		int g = 0;
		int b = 0;
		
		for(int row=1; row<height-1; row++) {
			offset = row * width;
			for(int col=1; col<width-1; col++) {
				r = getFilteredColor(TYPE_COLOR_RED, row, col) / scale;
				g = getFilteredColor(TYPE_COLOR_GREEN, row, col) / scale;
				b = getFilteredColor(TYPE_COLOR_BLUE, row, col) / scale;
				
				int offsetOutput = offset + col;
				output[0][offsetOutput] = (byte) Tools.clamp(r);
				output[1][offsetOutput] = (byte) Tools.clamp(g);
				output[2][offsetOutput] = (byte) Tools.clamp(b);
			}
		}

		ColorProcessor colorSrc = (ColorProcessor) src;
		colorSrc.putRGB(output[0], output[1], output[2]);
		
		output = null;
		
		return src;
	}

		/**
	 * Given a type of color (red, green or blue), an offset
	 * and a column, it returns the color filtered with the
	 * laplas filter.
	 * @param  type   The type of the color.
	 * @param  offset The offset of the color.
	 * @param  col    The column of the color.
	 * @return        The filtered color.
	 */
	private int getFilteredColor(int type, int row, int col) {
		int andValue = 0xff;
		int offset = row * width;
		int[] arrayColor;

		switch (type) {
		case TYPE_COLOR_RED:
			arrayColor = R;
			break;
		case TYPE_COLOR_GREEN:
			arrayColor = G;
			break;
		case TYPE_COLOR_BLUE:
			arrayColor = B;
			break;
		}

		int color = k0 * (arrayColor[offset-width+col-1] & andValue)
		          + k1 * (arrayColor[offset-width+col] & andValue)
		          + k2 * (arrayColor[offset-width+col+1] & andValue)
		          + k3 * (arrayColor[offset+col-1] & andValue)
		          + k4 * (arrayColor[offset+col] & andValue)
		          + k5 * (arrayColor[offset+col+1] & andValue)
		          + k6 * (arrayColor[offset+width+col-1] & andValue)
		          + k7 * (arrayColor[offset+width+col] & andValue)
		          + k8 * (arrayColor[offset+width+col+1] & andValue);

		return color;
	}

}
