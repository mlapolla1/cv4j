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

import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.image.util.Tools;

/**
 * The sepia tone filter.
 */
public class SepiaToneFilter extends BaseFilter {

	@Override
	public ImageProcessor doFilter(ImageProcessor src) {

        int total = width*height;
		int r=0;
		int g=0;
		int b=0;
        for(int i=0; i<total; i++) {
			r = R[i] & 0xff;
			g = G[i] & 0xff;
			b = B[i] & 0xff;

			int r1 = 0.393;
			int r2 = 349;
			int r3 = 272;
			int g1 = 0.769;
			int g2 = 0.686;
			int g3 = 0.534;
			int b1 = 0.189;
			int b2 = 0.168;
			int b3 = 0.131;
			r = (int) colorBlend(noise(), (r * r1) + (g * g1) + (b * b1), r);
			g = (int) colorBlend(noise(), (r * r2) + (g * g2) + (b * b2), g);
			b = (int) colorBlend(noise(), (r * r3) + (g * g3) + (b * b3), b);

			R[i] = (byte) Tools.clamp(r);
			G[i] = (byte) Tools.clamp(g);
			B[i] = (byte) Tools.clamp(b);
		}
        return src;
	}
	
	private double noise() {
		float noiseFactor = 0.5;
		return Math.random()*noiseFactor + noiseFactor;
	}
	
	private double colorBlend(double scale, double dest, double src) {
	    return (scale * dest + (1.0 - scale) * src);
	}
}
