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
import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.filters.math.GaussianBlurFilter;
import com.cv4j.image.util.Tools;

/**
 * The maer operator filter.
 */
public class MaerOperatorFilter extends GaussianBlurFilter {
    
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

	/**
	 * Constructor.
	 */
	public MaerOperatorFilter() {
		_4direction = true;
	}

	/**
	 * @return Is 4 direct.
	 */
	public boolean is4direct() {
		return _4direction;
	}

	/**
	 * Set 4 direct.
	 * @param xdirect The x direct
	 */
	public void set4direct(boolean xdirect) {
		this._4direction = xdirect;
	}

	@Override
	public ImageProcessor doFilter(ImageProcessor src){

		// 高斯模糊
		src = super.doFilter(src);
		
		// 拉普拉斯算子 ，发现边缘

		int total = width * height;
		byte[][] output = new byte[3][total];

		int offset = 0;
		
		for (int row = 1; row < height - 1; row++) {
			offset = row * width;
			for (int col = 1; col < width - 1; col++) {
                calculateFilter(row, col, offset, output);
			}
		}

		ColorProcessor colorSrc = (ColorProcessor) src;
		colorSrc.putRGB(output[0], output[1], output[2]);
		
		output = null;
		
		return src;
	}

    private void calculateFilter(int row, int col, int offset, byte[][] output) {
        int r = 0;
        int g = 0;
        int b = 0;

        r = getFilteredColor(TYPE_COLOR_RED, row, col);
        g = getFilteredColor(TYPE_COLOR_GREEN, row, col);
        b = getFilteredColor(TYPE_COLOR_BLUE, row, col);

        int offsetOutput = offset + col;
        output[0][offsetOutput] = (byte) Tools.clamp(r);
        output[1][offsetOutput] = (byte) Tools.clamp(g);
        output[2][offsetOutput] = (byte) Tools.clamp(b);
    }

	/**
	 * Given a type of color (red, green or blue), an offset
	 * and a column, it returns the color filtered with the
	 * laplas filter.
	 * @param  type   The type of the color.
	 * @param  row    The row of the color.
	 * @param  col    The column of the color.
	 * @return        The filtered color.
	 */
	private int getFilteredColor(int type, int row, int col) {
		int andValue = 0xff;
		int offset = row * width;

		int shiftValue = 0;
		byte[] arrayColor = null;

		switch (type) {
		case TYPE_COLOR_RED:
			arrayColor = R;
			shiftValue = 16;
			break;
		case TYPE_COLOR_GREEN:
			arrayColor = G;
			shiftValue = 0;
			break;
		case TYPE_COLOR_BLUE:
			arrayColor = B;
			shiftValue = 0;
			break;
		default:
			;
			break;
		}

		int[] constants = new int[1];
		if (_4direction) {
			constants = FOUR;
		} else {
			constants = EIGHT;
		}

		int color = constants[0] * (arrayColor[offset - width + col - 1] & andValue)
		          + constants[1] * (arrayColor[offset - width + col] & andValue)
		          + constants[2] * (arrayColor[offset - width + col + 1] & andValue)
		          + constants[3] * (arrayColor[offset + col - 1] & andValue)
		          + constants[4] * (arrayColor[offset + col] & andValue)
		          + constants[5] * (arrayColor[offset + col + 1] & andValue)
		          + constants[6] * (arrayColor[offset + width + col - 1] & andValue)
		          + constants[7] * (arrayColor[offset + width + col] >> shiftValue & andValue)
		          + constants[8] * (arrayColor[offset + width + col + 1] >> shiftValue & andValue);

		return color;
	}

}
