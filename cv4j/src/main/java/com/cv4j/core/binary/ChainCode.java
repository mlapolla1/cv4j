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
package com.cv4j.core.binary;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.exception.CV4JException;

import java.util.Arrays;
/**
 * The ChainCode class
 */
public class ChainCode extends CourtEdge {

	/**
	 * Max RGB value
	 */
	private static final short MAX_RGB = 255;

	/**
	 * Invalid column value.
	 */
	private static final short INVALID_COL_VALUE = -2;

	/**
	 * Column zero.
	 */
	private static final short COL0_VALUE = 0;

	/**
	 * Column one.
	 */
	private static final short COL1_VALUE = 1;

	/**
	 * Column two.
	 */
	private static final short COL2_VALUE = 2;

	/**
	 * Column three.
	 */
	private static final short COL3_VALUE = 3;

	/**
	 * Column four.
	 */
	private static final short COL4_VALUE = 4;

	/**
	 * Column five.
	 */
	private static final short COL5_VALUE = 5;

	/**
	 * Column six.
	 */
	private static final short COL6_VALUE = 6;


	/**
	 * Column seven.
	 */
	private static final short COL7_VALUE = 7;

	public void process(ByteProcessor binary, int[] codeMap) {
		super.process(binary);
		int width = binary.getWidth();
		int height = binary.getHeight();
		if(codeMap.length != (width*height)) {
			throw new CV4JException("chain code map length assert failure");
		}

		byte[] input = binary.getGray();
		byte[] output = new byte[width*height];
		System.arraycopy(input, 0, output, 0, input.length);
		
		// initialization code map
		Arrays.fill(codeMap, -1);
		int offset = 0;
		for(int row=0; row<height; row++) {
			offset = row*width;
			for(int col=0; col<width; col++) {
				int pv = input[offset+col]&0xff;
				if(pv == 255) {
					// do something here!!!
					int code = getRelationship(input, codeMap, row, col, width, height);
					if(code >= 0) {
						codeMap[offset+col] = code;
					}
				}
			}
		}

	}
	
	private boolean isMaxRgbAndCodeMapLessZero(int pv, int c) {
		return (pv == MAX_RGB && c < 0);
	}

	private int checkCol0Col1(byte[] pixels1, int[] codeMap, int row, int col, int width, int height) {
		int offset = row * width;

		if((col+1) < width) {
			int pv = pixels1[offset+col+1] & 0xff;
			int c = codeMap[offset+col+1];

			if(isMaxRgbAndCodeMapLessZero(pv, c)) {
				return COL0_VALUE;
			}

			if((row+1) < height) {
				pv = pixels1[offset+width+col+1] & 0xff;
				c = codeMap[offset+width+col+1];

				if(isMaxRgbAndCodeMapLessZero(pv, c)) {
					return COL1_VALUE;
				}
			}
		}

		return INVALID_COL_VALUE;
	}

	private int checkCol2Col3(byte[] pixels1, int[] codeMap, int row, int col, int width, int height) {
		int offset = row * width;

		if((row+1) < height) {
			int pv = pixels1[offset+width+col] & 0xff;
			int c = codeMap[offset+width+col];

			if(isMaxRgbAndCodeMapLessZero(pv, c)) {
				return COL2_VALUE;
			}

			if((col-1) >= 0) {
				pv = pixels1[offset+width+col-1]&0xff;
				c = codeMap[offset+width+col-1];

				if(isMaxRgbAndCodeMapLessZero(pv, c)) {
					return COL3_VALUE;
				}
			}
		}

		return INVALID_COL_VALUE;
	}

	private int checkCol4Col5(byte[] pixels1, int[] codeMap, int row, int col, int width, int height) {
		int offset = row * width;

		if((col-1) >= 0) {
			int pv = pixels1[offset+col-1]&0xff;
			int c = codeMap[offset+col-1];

			if(isMaxRgbAndCodeMapLessZero(pv, c)) {
				return COL4_VALUE;
			}

			if((row-1) >= 0) {
				pv = pixels1[offset-width+col-1] & 0xff;
				c = codeMap[offset-width+col-1];

				if(isMaxRgbAndCodeMapLessZero(pv, c)) {
					return COL5_VALUE;
				}
			}
		}

		return INVALID_COL_VALUE;
	}

	private int checkCol6Col7(byte[] pixels1, int[] codeMap, int row, int col, int width, int height) {
		int offset = row * width;

		if((row-1) >= 0) {
			int pv = pixels1[offset-width+col] & 0xff;
			int c = codeMap[offset-width+col];

			if(isMaxRgbAndCodeMapLessZero(pv, c)) {
				return COL6_VALUE;
			}

			if((row-1) >= 0 && (col+1) < width) {
				pv = pixels1[offset-width+col+1] & 0xff;
				c = codeMap[offset-width+col+1];

				if(isMaxRgbAndCodeMapLessZero(pv, c)) {
					return COL7_VALUE;
				}
			}
		}

		return INVALID_COL_VALUE;
	}

	private int getRelationship(byte[] pixels1, int[] codeMap, int row, int col, int width, int height) {
		int result;

		result = checkCol0Col1(pixels1, codeMap, row, col, width, height);
		if (result != INVALID_COL_VALUE) {
			return result;
		}

		result = checkCol2Col3(pixels1, codeMap, row, col, width, height);
		if (result != INVALID_COL_VALUE) {
			return result;
		}

		result = checkCol4Col5(pixels1, codeMap, row, col, width, height);
		if (result != INVALID_COL_VALUE) {
			return result;
		}

		result = checkCol6Col7(pixels1, codeMap, row, col, width, height);



		return result; // invalid, stop condition
	}
}
