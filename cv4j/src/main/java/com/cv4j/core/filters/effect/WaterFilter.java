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
package com.cv4j.core.filters.effect;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.filters.BaseFilter;
import com.cv4j.core.utils.FloatingPointsUtils;
import com.cv4j.core.utils.SafeCasting;
import com.cv4j.image.util.Tools;

/**
 * The water filter.
 */
public class WaterFilter extends BaseFilter {

	/**
	 * The output index 0.
	 */
	private static final int OUTPUT_INDEX0 = 0;

	/**
	 * The output index 1.
	 */
	private static final int OUTPUT_INDEX1 = 1;

	/**
	 * The output index 2.
	 */
	private static final int OUTPUT_INDEX2 = 2;

	private float wavelength = 16;
	private float radius = 50;
	private float radius2 = 0;
	private float iCentreX;
	private float iCentreY;

	/**
	 * Set the radius.
	 * @param radius The radius.
	 */
	public void setRadius(float radius) {
		this.radius = radius;
	}

	/**
	 * Returns the radius.
	 * @return The radius.
	 */
	public float getRadius() {
		return radius;
	}

	/**
	 * Set the wave length.
	 * @param wavelength The wave length to set.
	 */
	public void setWavelength(float wavelength) {
		this.wavelength = wavelength;
	}

	/**
	 * Return the wave length.
	 * @return The wave length.
	 */
	public float getWavelength() {
		return wavelength;
	}

	@Override
	public ImageProcessor doFilter(ImageProcessor src){
		final float centreX = 0.5f;
		final float centreY = 0.5f;

		final int output1dSize = 3;
		final int total = width*height;

		int[] inPixels = src.getPixels();
		byte[][] output = new byte[output1dSize][total];

		this.iCentreX = this.width * centreX;
		this.iCentreY = this.height * centreY;
		if (FloatingPointsUtils.nearlyEqauls(radius, 0)) {
			radius = Math.min(iCentreX, iCentreY);
		}
		this.radius2 = this.radius * this.radius;

		final int outSize = 2;
        float[] out = new float[outSize];

        doFilterLoop(0, out, output, inPixels);

		ColorProcessor colorProcessor = (ColorProcessor) src;
		colorProcessor.putRGB(output[OUTPUT_INDEX0], output[OUTPUT_INDEX1], output[OUTPUT_INDEX2]);

        return src;
	}

	private void doFilterLoop(int index, float[] out, byte[][] output, int[] inPixels){
        for(int row=0; row<height; row++) {
        	for(int col=0; col<width; col++) {
        		index = row * width + col;

				// 获取水波的扩散位置，最重要的一步
        		generateWaterRipples(col, row, out);
				int srcX = (int)Math.floor( out[0] );
				int srcY = (int)Math.floor( out[1] );
				float xWeight = out[0]-srcX;
				float yWeight = out[1]-srcY;

				// 获取周围四个像素，插值用，
				setOuts(output, index, xWeight, yWeight,srcX, srcY, inPixels);
        	}
        }
	}

	private void setOuts(byte[][] output, int index, float xWeight, float yWeight, int srcX, int srcY, int[] inPixels){
		final int value0000FF = 0xff;
		final int value16     = 16;
		final int value8      = 8;

		int nw = 0;
		int ne = 0;
		int sw = 0;
		int se = 0;

		if ( srcX >= 0 && srcX < width-1 && srcY >= 0 && srcY < height-1) {
			// Easy case, all corners are in the image
			int i = width*srcY + srcX;
			nw = inPixels[i];
			ne = inPixels[i+1];
			sw = inPixels[i+width];
			se = inPixels[i+width+1];
		} else {
			// Some of the corners are off the image
			nw = getPixel( inPixels, srcX, srcY, width, height );
			ne = getPixel( inPixels, srcX+1, srcY, width, height );
			sw = getPixel( inPixels, srcX, srcY+1, width, height );
			se = getPixel( inPixels, srcX+1, srcY+1, width, height );
		}

		// 取得对应的振幅位置P(x, y)的像素，使用双线性插值
		int p = Tools.bilinearInterpolate(xWeight, yWeight, nw, ne, sw, se);
		int r = (p >> value16) & value0000FF;
		int g = (p >> value8) & value0000FF;
		int b = (p) & value0000FF;

		output[0][index] = SafeCasting.safeIntToByte(r);
		output[1][index] = SafeCasting.safeIntToByte(g);
		output[2][index] = SafeCasting.safeIntToByte(b);
	}

	private int getPixel(int[] pixels, int x, int y, int width, int height) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			return 0; // 有点暴力啦，懒得管啦
		}
		return pixels[ y*width+x ];
	}

	protected void generateWaterRipples(int x, int y, float[] out) {
		final float amplitude = 10;
		final float phase = 0;
		float dx = x- iCentreX;
		float dy = y- iCentreY;
		float distance2 = dx*dx + dy*dy;
		// 确定 water ripple的半径，如果在半径之外，就直接获取原来位置，不用计算迁移量
		if (distance2 > radius2) { 
			out[0] = x;
			out[1] = y;
		} else {
			// 如果在radius半径之内，计算出来
			float distance = (float)Math.sqrt(distance2);
			// 计算改点振幅
			float amount = amplitude * (float)Math.sin(distance / wavelength * Tools.TWO_PI - phase);
			// 计算能量损失，
			amount *= (radius-distance)/radius; // 计算能量损失，
			if (!FloatingPointsUtils.nearlyEqauls(distance, 0)) {
				amount *= wavelength / distance;
			}

			// 得到water ripple 最终迁移位置
			out[0] = x + dx*amount;
			out[1] = y + dy*amount;
		}
	}
	
}
