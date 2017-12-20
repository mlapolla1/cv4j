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
package com.cv4j.core.binary.Contour;

import com.cv4j.core.binary.Erode.Erode;
import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.Size;

/**
 * The CourtEdge class.
 */
public class CourtEdge {

	public void process(ByteProcessor binary) {
		int width  = binary.getWidth();
		int height = binary.getHeight();

		byte[] input1 = initInput1(binary);
		byte[] input2 = initInput2(binary);

		initErode(binary);

		byte[] output = calculateOutput(input1, input2, width, height);
		binary.putGray(output);
	}

	private byte[] calculateOutput(byte[] input1, byte[] input2, int width, int height) {
		int offset;
		int p1;
		int p2;

		byte[] output = initOutput(input1);

		for(int row=1; row < height-1; row++) {
			offset = row * width;

			for(int col=1; col < width-1; col++) {
				p1 = input1[offset+col] & 0xff;
				p2 = input2[offset+col] & 0xff;

				if(p1 == p2) {
					output[offset+col] = (byte) 0;
				} else {
					output[offset+col] = (byte) 255;
				}
			}
		}

		return output;
	}

	/**
	 * Initialization of an Erode object.
	 * @param binary The byte processor.
	 */
	private void initErode(ByteProcessor binary) {
		final int width  = 3;
		final int height = 3;
		final Size size  = new Size(width, height);

		Erode erode = new Erode();
		erode.process(binary, size);
	}

	/**
	 * Initialization of output.
	 * @param input1 The input1.
	 * @return       The output.
	 */
	private byte[] initOutput(byte[] input1) {
		byte[] output = new byte[input1.length];
		System.arraycopy(input1, 0, output, 0, input1.length);

		return output;
	}

	/**
	 * Initialization of input2.
	 * @param binary The byte processor.
	 * @return       The input2.
	 */
	private byte[] initInput2(ByteProcessor binary) {
		return binary.getGray();
	}

	/**
	 * Initialization of input1.
	 * @param binary The byte processor.
	 * @return       The input1.
	 */
	private byte[] initInput1(ByteProcessor binary) {
		final int width  = binary.getWidth();
		final int height = binary.getHeight();

		byte[] input1 = new byte[width * height];
		System.arraycopy(binary.getGray(), 0, input1, 0, input1.length);

		return input1;
	}

}
