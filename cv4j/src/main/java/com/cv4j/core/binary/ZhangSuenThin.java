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
import java.util.Arrays;
/**
 * The ZhangSuenThin class
 */
public class ZhangSuenThin {

	/**
	 * Max RGB value.
	 */
	private static final short MAX_RGB = 255;

	public void process(ByteProcessor binary) {
		int width = binary.getWidth();
		int height = binary.getHeight();
		byte[] pixels = binary.getGray();
		int[] flagmap = new int[width*height];
		Arrays.fill(flagmap, 0);

		// thinning process
		boolean stop = false;
		while(!stop) {
			// step one
			boolean s1 = step1Scan(pixels, flagmap, width, height);
			deletewithFlag(pixels, flagmap);
			Arrays.fill(flagmap, 0);
			// step two
			boolean s2 = step2Scan(pixels, flagmap, width, height);
			deletewithFlag(pixels, flagmap);
			Arrays.fill(flagmap, 0);
			if(s1 && s2) {
				stop = true;
			}
		}

	}
	
	private void deletewithFlag(byte[] pixels, int[] flagmap) {
		for(int i=0; i<pixels.length; i++) {
			if(flagmap[i] == 1) {
				pixels[i] = (byte)0;
			}
		}
		
	}

	private boolean step1Scan(byte[] input, int[] flagmap, int width, int height) {
		boolean stop = true;

		for(int row=1; row<height-1; row++) {
			for(int col=1; col<width-1; col++) {
				stop = something(flagmap, input, row, col, width, height, stop);
			}
		}

		return stop;
	}

	private boolean something(int[] flagmap, byte[] input, int row, int col, int width, int height, boolean stop) {
		int offset = row * width;

		int p1 = calculateP1(input, row, col, width);
		if(p1 == 0) {
			return stop;
		}

		int p2 = calculateP2(input, row, col, width);
		int p3 = calculateP3(input, row, col, width);
		int p4 = calculateP4(input, row, col, width);
		int p5 = calculateP5(input, row, col, width);
		int p6 = calculateP6(input, row, col, width);
		int p7 = calculateP7(input, row, col, width);
		int p8 = calculateP8(input, row, col, width);
		int p9 = calculateP9(input, row, col, width);

		// match 1 - foreground, 0 - background
		p1 = normalizeRgbZeroOne(p1);
		p2 = normalizeRgbZeroOne(p2);
		p3 = normalizeRgbZeroOne(p3);
		p4 = normalizeRgbZeroOne(p4);
		p5 = normalizeRgbZeroOne(p5);
		p6 = normalizeRgbZeroOne(p6);
		p7 = normalizeRgbZeroOne(p7);
		p8 = normalizeRgbZeroOne(p8);
		p9 = normalizeRgbZeroOne(p9);

		int con1 = p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9;
		String one = "01";

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder
				.append(String.valueOf(p2))
				.append(String.valueOf(p3))
				.append(String.valueOf(p4))
				.append(String.valueOf(p5))
				.append(String.valueOf(p6))
				.append(String.valueOf(p7))
				.append(String.valueOf(p8))
				.append(String.valueOf(p9))
				.append(String.valueOf(p2));

		String sequence = stringBuilder.toString();
		int index1 = sequence.indexOf(one);
		int index2 = sequence.lastIndexOf(one);

		int con3 = p2*p4*p6;
		int con4 = p4*p6*p8;

		if((con1 >= 2 && con1 <= 6) && (index1 == index2) && con3 == 0 && con4 == 0) {
			flagmap[offset+col] = 1;
			stop = false;
		}

		return stop;
	}

	private boolean step2Scan(byte[] input, int[] flagmap, int width, int height) {
		boolean stop = true;

		for(int row=1; row<height-1; row++) {
			for(int col=1; col<width-1; col++) {
				stop = something(flagmap, input, row, col, width, height, stop);
			}
		}
		return stop;
	}

	/**
	 * Calculation of p1
	 * @param input
	 * @param row
	 * @param col
	 * @param width
	 * @return p1
	 */
	private int calculateP1(byte[] input, int row, int col, int width) {
		int offset = width * row;
		return input[offset+col] & 0xff;
	}

	/**
	 * Calculation of p2
	 * @param input
	 * @param row
	 * @param col
	 * @param width
	 * @return p2
	 */
	private int calculateP2(byte[] input, int row, int col, int width) {
		int offset = width * row;
		return input[offset-width+col+1] & 0xff;
	}

	/**
	 * Calculation of p3
	 * @param input
	 * @param row
	 * @param col
	 * @param width
	 * @return p3
	 */
	private int calculateP3(byte[] input, int row, int col, int width) {
		int offset = width * row;
		return input[offset-width+col+1] & 0xff;
	}

	/**
	 * Calculation of p4
	 * @param input
	 * @param row
	 * @param col
	 * @param width
	 * @return p4
	 */
	private int calculateP4(byte[] input, int row, int col, int width) {
		int offset = width * row;
		return input[offset+col+1] & 0xff;
	}

	/**
	 * Calculation of p5
	 * @param input
	 * @param row
	 * @param col
	 * @param width
	 * @return p5
	 */
	private int calculateP5(byte[] input, int row, int col, int width) {
		int offset = width * row;
		return input[offset+width+col+1] & 0xff;
	}

	/**
	 * Calculation of p6
	 * @param input
	 * @param row
	 * @param col
	 * @param width
	 * @return p6
	 */
	private int calculateP6(byte[] input, int row, int col, int width) {
		int offset = width * row;
		return input[offset+width+col] & 0xff;
	}

	/**
	 * Calculation of p7
	 * @param input
	 * @param row
	 * @param col
	 * @param width
	 * @return p7
	 */
	private int calculateP7(byte[] input, int row, int col, int width) {
		int offset = width * row;
		return input[offset+width+col-1] & 0xff;
	}

	/**
	 * Calculation of p8
	 * @param input
	 * @param row
	 * @param col
	 * @param width
	 * @return p8
	 */
	private int calculateP8(byte[] input, int row, int col, int width) {
		int offset = width * row;
		return input[offset+col-1] & 0xff;
	}

	/**
	 * Calculation of p9
	 * @param input
	 * @param row
	 * @param col
	 * @param width
	 * @return p9
	 */
	private int calculateP9(byte[] input, int row, int col, int width) {
		int offset = width * row;
		return input[offset-width+col-1] & 0xff;
	}

	/**
	 * Given a RGB value, normalize it to 1 if it's MAX_RGB, otherwise 0.
	 * @param rgbValue The RGB value.
	 * @return         The normalized RGB into 1 or 0.
	 */
	private int normalizeRgbZeroOne(int rgbValue) {
		return (rgbValue == MAX_RGB ? 1 : 0);
	}

}
