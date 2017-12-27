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

	/**
	 * The integer value of 0000FF.
	 */
	private static final int VALUE_0000FF = 0x0000ff;

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
				//stop = something(flagmap, input, row, col, width, height, stop);
				stop = something(flagmap, input, row, col, width, stop);
			}
		}

		return stop;
	}

	private boolean something(int[] flagmap, byte[] input, int row, int col, int width, boolean stop) {
		int offset = row * width;

		int p1 = calculateP1(input, row, col, width);
		if(p1 == 0) {
			return stop;
		}

		int[] p = firstPhaseOfSometing(input, row, col, width, p1);

		int[] result = secondPhaseOfSometing(p);
		int con1 = result[0];
		int index1 = result[1];
        int index2 = result[2];

		int con3 = p[1]*p[3]*p[5];
        int con4 = p[3]*p[5]*p[7];
		int limitDown = 2;
		int limitUp = 6;

		if((con1 >= limitDown && con1 <= limitUp) && (index1 == index2) && con3 == 0 && con4 == 0) {
			flagmap[offset+col] = 1;
			stop = false;
		}

		return stop;
	}

	private int[] firstPhaseOfSometing(byte[] input, int row, int col, int width, int p1) {
	    int[] p = new int[9];

        int p2 = calculateP2(input, row, col, width);
        int p3 = calculateP3(input, row, col, width);
        int p4 = calculateP4(input, row, col, width);
        int p5 = calculateP5(input, row, col, width);
        int p6 = calculateP6(input, row, col, width);
        int p7 = calculateP7(input, row, col, width);
        int p8 = calculateP8(input, row, col, width);
        int p9 = calculateP9(input, row, col, width);

        // match 1 - foreground, 0 - background
        p[0] = normalizeRgbZeroOne(p1); //p1
        p[1] = normalizeRgbZeroOne(p2); //p2
        p[2] = normalizeRgbZeroOne(p3); //p3
        p[3] = normalizeRgbZeroOne(p4); //p4
        p[4] = normalizeRgbZeroOne(p5); //p5
        p[5] = normalizeRgbZeroOne(p6); //p6
        p[6] = normalizeRgbZeroOne(p7); //p7
        p[7] = normalizeRgbZeroOne(p8); //p8
        p[8] = normalizeRgbZeroOne(p9); //p9

        return p;
    }

    private int[] secondPhaseOfSometing(int[] p) {
        int[] result = new int[3];

        String one = "01";
        result[0] = p[1] + p[2] + p[3] + p[4] + p[5] + p[6] + p[7] + p[8]; //con1

        StringBuilder stringBuilder = new StringBuilder(16);
        stringBuilder
                .append(String.valueOf(p[1]))
                .append(String.valueOf(p[2]))
                .append(String.valueOf(p[3]))
                .append(String.valueOf(p[4]))
                .append(String.valueOf(p[5]))
                .append(String.valueOf(p[6]))
                .append(String.valueOf(p[7]))
                .append(String.valueOf(p[8]))
                .append(String.valueOf(p[1]));

        String sequence = stringBuilder.toString();
        result[1] = sequence.indexOf(one); //index1
        result[2] = sequence.lastIndexOf(one); //index2

        return result;
    }

	private boolean step2Scan(byte[] input, int[] flagmap, int width, int height) {
		boolean stop = true;

		for(int row=1; row<height-1; row++) {
			for(int col=1; col<width-1; col++) {
				stop = something(flagmap, input, row, col, width, stop);
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
		return input[offset+col] & VALUE_0000FF;
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
		return input[offset-width+col+1] & VALUE_0000FF;
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
		return input[offset-width+col+1] & VALUE_0000FF;
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
		return input[offset+col+1] & VALUE_0000FF;
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
		return input[offset+width+col+1] & VALUE_0000FF;
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
		return input[offset+width+col] & VALUE_0000FF;
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
		return input[offset+width+col-1] & VALUE_0000FF;
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
		return input[offset+col-1] & VALUE_0000FF;
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
		return input[offset-width+col-1] & VALUE_0000FF;
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