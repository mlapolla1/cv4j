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
 * The lapas filter.
 */
public class LaplasFilter extends BaseFilter {
    
    /**
     * Constant of four
     */
	public static int[] FOUR = new int[] { 0, -1, 0, -1, 4, -1, 0, -1, 0 };
    
    /**
     * Constant of eight
     */
	public static int[] EIGHT = new int[] { -1, -1, -1, -1, 8, -1, -1, -1, -1};
	
	private boolean _4direction;

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

	public LaplasFilter() {
		_4direction = true;
	}

	/**
	 * @return Is four direct
	 */
	public boolean is4direct() {
		return _4direction;
	}

	/**
	 * Set 4 direct
	 * @param xdirect The x direct
	 */
	public void set4direct(boolean xdirect) {
		this._4direction = xdirect;
	}

	@Override
	public ImageProcessor doFilter(ImageProcessor src){

		int total = width * height;

		int offset = 0;
		int k0;
		int k1;
		int k2;
		int k3;
		int k4;
		int k5;
		int k6;
		int k7;
		int k8;

		byte[][] output = new byte[3][total];
		
		if(_4direction) {
			k0 = FOUR[0];
			k1 = FOUR[1];
			k2 = FOUR[2];
			k3 = FOUR[3];
			k4 = FOUR[4];
			k5 = FOUR[5];
			k6 = FOUR[6];
			k7 = FOUR[7];
			k8 = FOUR[8];
		} else {
			k0 = EIGHT[0];
			k1 = EIGHT[1];
			k2 = EIGHT[2];
			k3 = EIGHT[3];
			k4 = EIGHT[4];
			k5 = EIGHT[5];
			k6 = EIGHT[6];
			k7 = EIGHT[7];
			k8 = EIGHT[8];
		}
		
		int r = 0;
		int g = 0;
		int b = 0;

		for (int row = 1; row < height - 1; row++) {
			offset = row * width;
			for (int col = 1; col < width - 1; col++) {
				r = getFilteredColor(TYPE_COLOR_RED, row, col);
				g = getFilteredColor(TYPE_COLOR_GREEN, row, col);
				b = getFilteredColor(TYPE_COLOR_BLUE, row, col);

				int outputOffset = offset + col;
				output[0][outputOffset] = (byte) Tools.clamp(r);
				output[1][outputOffset] = (byte) Tools.clamp(g);
				output[2][outputOffset] = (byte) Tools.clamp(b);
			}
		}
		
		ColorProcessor colorSource = (ColorProcessor) src;
		colorSource.putRGB(output[0], output[1], output[2]);
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
		int shiftValue;
		int[] arrayColor;

		switch (type) {
		case TYPE_COLOR_RED:
			shiftValue = 16;
			arrayColor = R;
			break;
		case TYPE_COLOR_GREEN:
			shiftValue = 0;
			arrayColor = G;
			break;
		case TYPE_COLOR_BLUE:
			shiftValue = 0
			arrayColor = B;
			break;
		}

		int color = k0 * ((arrayColor[offset - width + col - 1] >> shiftValue) & andValue)
		          + k1 * (arrayColor[offset - width + col] & andValue)
		          + k2 * (arrayColor[offset - width + col + 1] & andValue)
		          + k3 * (arrayColor[offset + col - 1] & andValue)
		          + k4 * (arrayColor[offset + col] & andValue)
		          + k5 * (arrayColor[offset + col + 1] & andValue)
		          + k6 * (arrayColor[offset + width + col - 1] & andValue)
		          + k7 * (arrayColor[offset + width + col] & andValue)
		          + k8 * (arrayColor[offset + width + col + 1] & andValue);

		return color;
	}

}
