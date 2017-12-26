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
import com.cv4j.core.utils.SafeCasting;
import com.cv4j.image.util.Tools;

/**
 * this filter illustrate the brightness and contrast of the image
 * and demo how to change the both attributes of the image.
 *
 */
public class ConBriFilter extends BaseFilter {

	/**
	 * The value of 0000FF.
	 */
	private static final int VALUE_0000FF = 0x0000ff;

	/**
	 * The contrast.
	 */
	private float contrast = 1.2f;

	/**
	 * The brightness.
	 */
	private float brightness = 0.7f;

	@Override
	public ImageProcessor doFilter(ImageProcessor src) {
        
        // calculate RED, GREEN, BLUE means of pixel
		int[] rgbMeans = new int[3];

		double redSum = 0;
		double greenSum = 0;
		double blueSum = 0;

		int total = height * width;

        for(int i=0; i<total; i++) {
			redSum   += R[i] & VALUE_0000FF;
			greenSum += G[i] & VALUE_0000FF;
			blueSum  += B[i] & VALUE_0000FF;
        }

        rgbMeans[0] = (int)(redSum / total);
        rgbMeans[1] = (int)(greenSum / total);
        rgbMeans[2] = (int)(blueSum / total);
        
        // adjust contrast and brightness algorithm, here
        setRGBArrays(total, rgbMeans);
        return src;
	}

	private void setRGBArrays(int total, int[] rgbmeans) {
		for(int i=0; i<total; i++) {
			int r = R[i] & VALUE_0000FF;
			int g = G[i] & VALUE_0000FF;
			int b = B[i] & VALUE_0000FF;

			// remove means
			r -=rgbmeans[0];
			g -=rgbmeans[1];
			b -=rgbmeans[2];

			// adjust contrast now !!!
			r *= this.contrast;
			g *= this.contrast;
			b *= this.contrast;

			// adjust brightness
			r += rgbmeans[0] * this.contrast;
			g += rgbmeans[1] * this.contrast;
			b += rgbmeans[2] * this.contrast;

			R[i] = SafeCasting.safeIntToByte(Tools.clamp(r));
			G[i] = SafeCasting.safeIntToByte(Tools.clamp(g));
			B[i] = SafeCasting.safeIntToByte(Tools.clamp(b));
        }
	}

	public float getContrast() {
		return contrast;
	}

	public void setContrast(float contrast) {
		this.contrast = contrast;
	}

	public float getBrightness() {
		return brightness;
	}

	public void setBrightness(float brightness) {
		this.brightness = brightness;
	}

}
