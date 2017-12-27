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
package com.cv4j.core.datamodel.number;

/**
 * The IntIntegralImage class of DataModel
 */
public class IntIntegralImage {

	/**
	 * The hex value 0000FF.
	 */
	private static final int VALUE_0000FF = 0x0000ff;

	/**
	 * Sum index tables.
 	 */
	private int[] sum;

	/**
	 * The image data.
 	 */
	private byte[] image;

	/**
	 * The square sum index table.
	 */
	private float[] squareSum;

	/**
	 * The width.
	 */
	private int width;

	/**
	 * The height.
	 */
	private int height;

	/**
	 * @return The image data.
	 */
	public byte[] getImage() {
		return image;
	}

	/**
	 * Set the image data.
	 * @param imageData The new image data.
	 */
	public void setImage(byte[] imageData) {
		this.image = imageData;
	}

	/**
	 * Return the block sum 2.
	 * @param ney
	 * @param nex
	 * @param swy
	 * @param swx
	 * @return    The block sum.
	 */
	public int getBlockSum2(int ney, int nex, int swy, int swx) {
		int sum1 = sum[ney*width+nex];
		int sum4 = sum[swy*width+swx];
		int sum2 = sum[swy*width+nex];
		int sum3 = sum[ney*width+swx];

		return ((sum1 + sum4) - sum2 - sum3);
	}

	/**
	 * Returns the block sum.
	 * @param x
	 * @param y
	 * @param m
	 * @param n
	 * @return  The block sum.
	 */
	public int getBlockSum(int x, int y, int m, int n) {
		int swx = x + n/2;
		int swy = y + m/2;
		int nex = x-n/2-1;
		int ney = y-m/2-1;

		if(swx >= width) {
			swx = width - 1;
		}

		if(swy >= height) {
			swy = height - 1;
		}

		if(nex < 0) {
			nex = 0;
		}

		if(ney < 0) {
			ney = 0;
		}

		int sum1 = sum[ney*width+nex];
		int sum4 = sum[swy*width+swx];
		int sum2 = sum[swy*width+nex];
		int sum3 = sum[ney*width+swx];

		return ((sum1 + sum4) - sum2 - sum3);
	}

	/**
	 * Returns the block square sum.
	 * @param x
	 * @param y
	 * @param m
	 * @param n
	 * @return  The block square sum.
	 */
	public float getBlockSquareSum(int x, int y, int m, int n) {
		int swx = x + n/2;
		int swy = y + m/2;
		int nex = x-n/2-1;
		int ney = y-m/2-1;

		if(swx >= width) {
			swx = width - 1;
		}

		if(swy >= height) {
			swy = height - 1;
		}

		if(nex < 0) {
			nex = 0;
		}

		if(ney < 0) {
			ney = 0;
		}

		float sum1 = squareSum[ney*width+nex];
		float sum4 = squareSum[swy*width+swx];
		float sum2 = squareSum[swy*width+nex];
		float sum3 = squareSum[ney*width+swx];

		return ((sum1 + sum4) - sum2 - sum3);
	}

	/**
	 * Calculate the sum offset value.
	 * @param row The row.
	 * @param col The column.
	 * @return    The sum offset value.
	 */
	private int calculateSumOffsetValue(int row, int col, int offset) {
		final int upRow   = row - 1;
		final int leftCol = col - 1;

		int p1 = image[offset] & VALUE_0000FF;                           // p(x, y)
		int p2 = (leftCol < 0 ? 0 : sum[offset-1]);                      // p(x-1, y)
		int p3 = (upRow < 0 ? 0 : sum[offset-width]);                    // p(x, y-1);
		int p4 = ((upRow < 0 || leftCol < 0) ? 0 : sum[offset-width-1]); // p(x-1, y-1);

		return p1 + p2 + p3 - p4;
	}

	private float calculateSquareSumOffsetValue(int row, int col, int offset) {
		final int upRow   = row - 1;
		final int leftCol = col - 1;

		int p1 = image[offset] & VALUE_0000FF;                           // p(x, y)

		// FIX: these values are unused.
		//int p2 = (leftCol < 0 ? 0 : sum[offset-1]);                      // p(x-1, y)
		//int p3 = (upRow < 0 ? 0 : sum[offset-width]);                    // p(x, y-1);
		//int p4 = ((upRow < 0 || leftCol < 0) ? 0 : sum[offset-width-1]); // p(x-1, y-1);

		float sp2 = (leftCol < 0 ? 0 : squareSum[offset-1]);                      // p(x-1, y)
		float sp3 = (upRow < 0   ? 0 : squareSum[offset-width]);                  // p(x, y-1);
		float sp4 = ((upRow < 0 || leftCol < 0) ? 0 : squareSum[offset-width-1]); // p(x-1, y-1);

		return (p1 * p1) + sp2 + sp3 - sp4;
	}

	public void process(int distance, int elevation) {
		this.width  = distance;
		this.height = elevation;
		this.sum    = new int[this.width * this.height];

		for(int row = 0; row < this.height; row++ ) {
			int offset = (row * this.width);

			for(int col = 0; col < this.width; col++) {
				this.sum[offset] = calculateSumOffsetValue(row, col, offset);
				offset++;
			}
		}
	}

	public void process(int distance, int elevation, boolean includeSqrt) {
		final int size = (this.width * this.height);
		System.out.println(includeSqrt);

		this.width     = distance;
		this.height    = elevation;
		this.sum       = new int[size];
		this.squareSum = new float[size];

		for(int row = 0; row < this.height; row++ ) {
			int offset = (row * this.width);
			for(int col = 0; col < this.width; col++) {
				this.sum[offset]       = calculateSumOffsetValue(row, col, offset);
				this.squareSum[offset] = calculateSquareSumOffsetValue(row, col, offset);
				offset++;
			}
		}
	}
}
