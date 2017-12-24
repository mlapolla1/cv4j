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
import com.cv4j.core.utils.SafeCasting;

import java.util.Arrays;

/**
 * The DistanceTransform class
 */
public class DistanceTransform extends DtBase {

	/**
	 * The maximum RGB value.
	 */
	private static final int MAX_RGB = 255;

	public void process(ByteProcessor binary) {
		final int width     = binary.getWidth();
		final int height    = binary.getHeight();

		byte[] output = initOutput(binary);
		byte[] pixels = initPixels(binary, output);

		int[] distMap = initDistMap(binary, pixels);

		distanceTransformStage(output, pixels, distMap, width, height);
		
		binary.putGray(output);
	}

	/**
	 * The distance transform stage.
	 * @param output  The output.
	 * @param pixels  The pixels.
	 * @param distMap The distance map.
	 * @param width   The width.
	 * @param height  The height.
	 */
	private void distanceTransformStage(byte[] output, byte[] pixels, int[] distMap, int width, int height) {
		boolean stop = false;
		int level = 0;
		while(!stop) {
			stop = distanceTransform(pixels, output, distMap, level, width, height);
			System.arraycopy(output, 0, pixels, 0, output.length);
			level++;
		}

		assignGrayValuesByDistance(output, distMap, width, height, level);
	}

	/**
	 * Assign different gray value by distance value.
	 * @param output  The output.
	 * @param distMap The distance map.
	 * @param width   The width.
	 * @param height  The height.
	 * @param level   The level.
	 */
	private void assignGrayValuesByDistance(byte[] output, int[] distMap, int width, int height, int level) {
		int step = MAX_RGB / level;
		Arrays.fill(output, (byte) 0);

		for(int row = 0; row < height; row++) {
			int offset = row * width;
			for(int col = 0; col < width; col++) {
				int dis = distMap[offset+col];
				if(dis > 0) {
					int gray = (dis * step);
					output[offset+col] = SafeCasting.safeIntToByte(gray);
				}
			}
		}
	}

	/**
	 * Initialization of distMap with their values.
	 * @param binary The byte processor.
	 * @return       The distMap.
	 */
	private int[] initDistMap(ByteProcessor binary, byte[] pixels) {
		final int width  = binary.getWidth();
		final int height = binary.getHeight();

		int[] distMap = new int[width*height];
		Arrays.fill(distMap, 0);

		// initialize distance value
		for(int row=0; row<height; row++) {
			int offset = row*width;
			for(int col=0; col<width; col++) {
				int pv = pixels[offset+col];

				if(pv == MAX_RGB) {
					distMap[offset+col] = 1;
				}
			}
		}

		return distMap;
	}

	/**
	 * Initialization of pixels.
	 * @param binary The byte processor.
	 * @param output The output.
	 * @return       The pixels.
	 */
	private byte[] initPixels(ByteProcessor binary, byte[] output) {
		byte[] pixels = binary.getGray();
		System.arraycopy(pixels, 0, output, 0, output.length);

		return pixels;
	}

	/**
	 * Initialization of output.
	 * @param binary The byte processor.
	 * @return       The output.
	 */
	private byte[] initOutput(ByteProcessor binary) {
		final int width = binary.getWidth();
		final int height = binary.getHeight();

		return new byte[width * height];
	}

}
