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
package com.cv4j.core.filters.image;

import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.filters.BaseFilter;

/**
 * The white image filter.
 */
public class WhiteImageFilter extends BaseFilter {
	private double beta;

	public WhiteImageFilter() {
		this.beta = 1.1;
	}

	public double getBeta() {
		return beta;
	}

	public void setBeta(double beta) {
		this.beta = beta;
	}

	@Override
	public ImageProcessor doFilter(ImageProcessor src) {
		int maxRgb = 255;
		maxRgb++;
		// make LUT
		int[] lut = new int[maxRgb];
		for(int i=0; i<maxRgb; i++) {
			lut[i] = imageMath(i);
		}

		int index = 0;

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				index = row * width + col;
				R[index] = (byte)lut[R[index] & 0xff];
				G[index] = (byte)lut[G[index] & 0xff];
				B[index] = (byte)lut[B[index] & 0xff];
			}
		}
		return src;
	}

	private int imageMath(int gray) {
		int maxRgb = 255;
		double scale = maxRgb / (Math.log(maxRgb * (this.beta -1) + 1) / Math.log(this.beta));
		double p1 = Math.log(gray * (this.beta -1) + 1);
		double np = p1 / Math.log(this.beta);
		return (int)(np * scale);
	}

}
