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
package com.cv4j.core.filters;

import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.datamodel.number.IntIntegralImage;
import com.cv4j.image.util.Tools;

/**
 * good method try to smooth the minor noise
 *
 */
public class FastEPFilter implements CommonFilter {

	/**
	 * The swx index.
	 */
	private static final int INDEX_SWX = 0;

	/**
	 * The swy index.
	 */
	private static final int INDEX_SWY = 1;

	/**
	 * The nex index.
	 */
	private static final int INDEX_NEX = 2;

	/**
	 * The ney index.
	 */
	private static final int INDEX_NEY = 3;

	/**
	 * The x radius.
	 */
	private int xr;

	/**
	 * The y radius.
	 */
	private int yr;

	/**
	 * The sigma.
	 */
	private float sigma;

	public FastEPFilter() {
		this.sigma = 10.0f;
		this.xr    = 0;
		this.xr    = 0;
	}
	
	public void setWinsize(int radius) {
		this.xr = radius;
		this.yr = radius;
	}

	public float getSigma() {
		return sigma;
	}

	public void setSigma(float sigma) {
		this.sigma = sigma;
	}

	@Override
	public ImageProcessor filter(ImageProcessor src) {
		// initialization parameters
		int width = src.getWidth();
		int height = src.getHeight();
		xr = yr = (int)(Math.max(width, height) * 0.02);
		sigma = 10 + sigma * sigma * 5;

		// start ep process
		byte[] output = new byte[width*height];
		IntIntegralImage ii = new IntIntegralImage();
		for(int i=0; i<src.getChannels(); i++) {
			System.arraycopy(src.toByte(i), 0, output, 0, output.length);
			ii.setImage(src.toByte(i));
			ii.process(width, height, true);
			processSingleChannel(width, height, ii, output);
			System.arraycopy(output, 0, src.toByte(i), 0, output.length);
		}

		// release memory
		output = null;
		return src;
	}

	public void processSingleChannel(int width, int height, IntIntegralImage input, byte[] output) {
		float sigma2 = sigma*sigma;
		int offset = 0;
		int wy = (yr * 2 + 1);
		int wx = (xr * 2 + 1);
		int r = 0;
		int size = 0;
		for (int row = 0; row < height; row++) {
			offset = row * width;
			for (int col = 0; col < width; col++) {
				int [] variables = setVariables(col, row, width, height);
				int swx = variables[INDEX_SWX];
				int swy = variables[INDEX_SWY];
				int nex = variables[INDEX_NEX];
				int ney = variables[INDEX_NEY];
				size = (swx - nex)*(swy - ney);
				int sr = input.getBlockSum2(ney, nex, swy, swx);
				float a = input.getBlockSquareSum(col, row, wy, wx);
				// fix issue, size is not cover the whole block
				setOutput(output, col, r, sr, size, a, sigma2, offset);
			}
		}
	}

	private void setOutput(byte[] output, int col, int r, int sr, int size, int a, float sigma2, int offset){
		float b = sr / size;
		float c = (a - (sr*sr)/size)/size;
		float d = c / (c+sigma2);
		r = (int)((1-d)*b + d*r);
		output[offset + col] = (byte) Tools.clamp(r);
	}

	private int[] setVariables(int col, int row, int width, int height){
		int swx = col + xr;
		int swy = row + yr;
		int nex = col-xr-1;
		int ney = row-yr-1;
		int dim = 4;

		int [] variables = new int[dim];
		if(swx >= width) {
			variables[INDEX_SWX] = width - 1;
		}
		if(swy >= height) {
			variables[INDEX_SWY] = height - 1;
		}
		if(nex < 0) {
			variables[INDEX_NEX] = 0;
		}
		if(ney < 0) {
			variables[INDEX_NEY] = 0;
		}

		return variables;

	}
}
