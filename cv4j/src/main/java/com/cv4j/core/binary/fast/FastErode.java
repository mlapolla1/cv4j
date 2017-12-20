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
package com.cv4j.core.binary.fast;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.Size;

/**
 * cascade erode operation on binary image
 *
 */
public class FastErode extends FastBase {

	@Override
	protected void calculateOutputValues(byte[] output, byte[] data, int width, int height, int xyr, int shift) {
		final int valueFF  = 0xff;

		for(int col = 0; col < width; col++) {
			for(int row = 0; row < height; row++) {
				final int offsetRowWidthCol = (row * width) + col;
				int c = data[offsetRowWidthCol];

				if((c & valueFF) == 0) {
					continue;
				}

				c = calculateDataValue(data, c, row, col, width, height, xyr, shift);

				if(c == 0){
					output[offsetRowWidthCol] = (byte) 0;
				}
			}
		}

		System.arraycopy(output, 0, data, 0, width * height);
	}

	/**
	 * Calculate data value.
	 * @param data   The data.
	 * @param c      The value.
	 * @param row    The row.
	 * @param col    The column.
	 * @param width  The width.
	 * @param height The height.
	 * @param xyr    The xy radius.
	 * @param shift  The shift.
	 * @return       The data value.
	 */
	private int calculateDataValue(byte[] data, int c, int row, int col, int width, int height, int xyr, int shift) {
		for(int i = -(xyr); i <= (xyr - shift); i++) {
			if(i == 0) {
				continue;
			}

			int offset = getValueBetween(i + row, 0, height);
			c &= data[offset*width+col];
		}

		return c;
	}
}
