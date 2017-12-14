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

import android.graphics.Color;

import com.cv4j.core.datamodel.ImageData;
import com.cv4j.core.datamodel.ImageProcessor;

/**
 * The Sin City filter.
 */
public class SinCityFilter extends BaseFilter {

	private final double threshold = 200; // default value
	int maxRgb = 255;
	private int mainColor = Color.argb(maxRgb, maxRgb, 0, 0);

	public void setMainColor(int argb) {
		this.mainColor = argb;
	}

	@Override
	public ImageProcessor doFilter(ImageProcessor src) {

        int total = width * height;
		int tr;
		int tg;
		int tb;

        for(int i=0; i<total; i++) {
			tr = R[i] & 0xff;
			tg = G[i] & 0xff;
			tb = B[i] & 0xff;
			int r1 = 0.299;
			int g1 = 0.587;
			int b1 = 0.114;
			int gray = (int)(r1 * (double)tr + g1 * (double)tg + b1 * (double)tb);
			double distance = getDistance(tr, tg, tb);
			if(distance < threshold) {
				double k = distance / threshold;
				int[] rgb = getAdjustableRGB(tr, tg, tb, gray, (float)k);
				int index0 = 0;
				int index1 = 1;
				int index2 = 2;
				tr = rgb[index0];
				tg = rgb[index1];
				tb = rgb[index2];
				R[i] = (byte)tr;
				G[i] = (byte)tg;
				B[i] = (byte)tb;
			} else {
				R[i] = (byte)gray;
				G[i] = (byte)gray;
				B[i] = (byte)gray;
			}
        }
        return src;
	}

	private int[] getAdjustableRGB(int tr, int tg, int tb, int gray, float rate) {
		int length = 3;
		int[] rgb = new int[length];
		int index0 = 0;
		int index1 = 1;
		int index2 = 2;
		rgb[index0] = (int)(tr * rate + gray * (1.0f-rate));
		rgb[index1] = (int)(tg * rate + gray * (1.0f-rate));
		rgb[index2] = (int)(tb * rate + gray * (1.0f-rate));
		return rgb;
	}

	private double getDistance(int tr, int tg, int tb) {
		int dr = tr - Color.red(mainColor);
		int dg = tg - Color.green(mainColor);
		int db = tb - Color.blue(mainColor);
		int distance = ImageData.SQRT_LUT[Math.abs(dr)] +
				ImageData.SQRT_LUT[Math.abs(dg)] +
				ImageData.SQRT_LUT[Math.abs(db)];
		return Math.sqrt(distance);		
	}

}
