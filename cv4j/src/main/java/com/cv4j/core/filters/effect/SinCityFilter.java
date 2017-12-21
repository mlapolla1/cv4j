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

import android.graphics.Color;

import com.cv4j.core.datamodel.image.ImageData;
import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.filters.BaseFilter;

/**
 * The Sin City filter.
 */
public class SinCityFilter extends BaseFilter {

	int maxRgb = 255;
	private int mainColor = Color.argb(maxRgb, maxRgb, 0, 0);

	public void setMainColor(int argb) {
		this.mainColor = argb;
	}

	@Override
	public ImageProcessor doFilter(ImageProcessor src) {
		final double threshold = 200; // default value
        int total = width * height;
		int tr;
		int tg;
		int tb;

        for(int i=0; i<total; i++) {
			tr = R[i] & 0xff;
			tg = G[i] & 0xff;
			tb = B[i] & 0xff;

			float r1 = 0.299f;
			float g1 = 0.587f;
			float b1 = 0.114f;

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
				setsRGB(tr, tg, tb);
			} else {
				setsRGB(gray, gray, gray);
			}
        }
        return src;
	}

	private void setsRGB(int rset, int gset, int bset){
		R[i] = (byte)rset;
		G[i] = (byte)gset;
		B[i] = (byte)bset;
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
