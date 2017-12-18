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

	private void extractSkeletonFromDtImage(byte[] output, int[] distmap, int width, int height) {
		int offset;
		int dis;
		int p1;
		int p2;
		int p3;
		int p4;

		for(int row = 1; row < height-1; row++) {
			offset = row * width;
			for(int col = 1; col < width-1; col++) {
				dis = distmap[offset+col];
				p1  = distmap[offset+col-1];
				p2  = distmap[offset+col+1];
				p3  = distmap[offset-width+col];
				p4  = distmap[offset+width+col];

				if(dis == 0 || dis < p1 || dis < p2 || dis < p3 || dis < p4) {
					output[offset+col] = (byte) 0;
				} else {
					output[offset+col] = (byte) 255;
				}
			}
		}
	}

	public void process(ByteProcessor binary) {
		int width = binary.getWidth();
		int height = binary.getHeight();
		byte[] pixels = binary.getGray();
		byte[] output = new byte[width*height];
		int[] distmap = new int[width*height];

		System.arraycopy(pixels, 0, output, 0, output.length);
		Arrays.fill(distmap, 0);
		
		// initialize distance value
		int offset;
		int pv;

		for(int row = 0; row < height; row++) {
			offset = row * width;
			for(int col = 0; col < width; col++) {
				pv = pixels[offset + col];
				if(pv == 255) {
					distmap[offset + col] = 1;
				}
			}
		}

		// distance transform stage
		boolean stop = false;
		int level = 0;
		while(!stop) {
			stop = dt(pixels, output, distmap, level, width, height);
			System.arraycopy(output, 0, pixels, 0, output.length);
			level++;
		}

		Arrays.fill(output, (byte) 0);
		extractSkeletonFromDtImage(output, distmap, width, height);

		binary.putGray(output);
	}

}
