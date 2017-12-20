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
 * The Skeleton class
 */
public class Skeleton extends DtBase {

	private static final int MAX_RGB = 255;

	/**
	 * The process.
	 * @param binary The byte processor.
	 */
	public void process(ByteProcessor binary) {
		final int width = binary.getWidth();
		final int height = binary.getHeight();

		byte[] pixels = binary.getGray();
		byte[] output = initOutput(pixels);
		int[] distMap = initDistMap(binary);

		for(int row = 0; row < height; row++) {
			int offset = row * width;
			for(int col = 0; col < width; col++) {
				int pv = pixels[offset + col];

				if(pv == MAX_RGB) {
					distMap[offset + col] = 1;
				}
			}
		}

		distanceTransformStage(pixels, output, distMap, width, height);

		binary.putGray(output);
	}

	/**
	 * Extract the skeleton form a distance transform image.
	 * @param output  The output array.
	 * @param distMap The distMap array.
	 * @param width   The width.
	 * @param height  The height.
	 */
	private void extractSkeletonFromDtImage(byte[] output, int[] distMap, int width, int height) {
		for(int row = 1; row < height-1; row++) {
			int offset = row * width;

			for(int col = 1; col < width-1; col++) {
				int dis = distMap[offset+col];
				int p1  = distMap[offset+col-1];
				int p2  = distMap[offset+col+1];
				int p3  = distMap[offset-width+col];
				int p4  = distMap[offset+width+col];

				if(dis == 0 || dis < p1 || dis < p2 || dis < p3 || dis < p4) {
					output[offset+col] = (byte) 0;
				} else {
					output[offset+col] = (byte) MAX_RGB;
				}
			}
		}
	}

	/**
	 * The distance transform stage.
	 * @param pixels  The pixels array.
	 * @param output  The output array.
	 * @param distMap The distMap array.
	 * @param width   The width.
	 * @param height  The height.
	 */
	private void distanceTransformStage(byte[] pixels, byte[] output, int[] distMap, int width, int height) {
		boolean stop = false;
		int level = 0;
		while(!stop) {
			stop = distanceTransform(pixels, output, distMap, level, width, height);
			System.arraycopy(output, 0, pixels, 0, output.length);
			level++;
		}

		Arrays.fill(output, (byte) 0);
		extractSkeletonFromDtImage(output, distMap, width, height);
	}

	/**
	 * Initialize the distMap array.
	 * @param binary The byte processor.
	 * @return       The distMap array.
	 */
	private int[] initDistMap(ByteProcessor binary) {
		final int width  = binary.getWidth();
		final int height = binary.getHeight();

		int[] distMap = new int[width * height];
		Arrays.fill(distMap, 0);

		return distMap;
	}

	/**
	 * Initialize the output array.
	 * @param pixels The pixels array.
	 * @return       The output array.
	 */
	private byte[] initOutput(byte[] pixels) {
		byte[] output = new byte[pixels.length];
		System.arraycopy(pixels, 0, output, 0, output.length);

		return output;
	}

}
