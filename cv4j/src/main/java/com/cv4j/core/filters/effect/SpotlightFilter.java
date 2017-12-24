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

import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.filters.BaseFilter;
import com.cv4j.core.utils.SafeCasting;

/**
 * The spotlight filter.
 */
public class SpotlightFilter extends BaseFilter {
	// attenuation coefficient, default is 1 means line decrease...
	private int factor;
	public SpotlightFilter() {
		factor = 1;
	}
	
	public void setFactor(int coefficient) {
		this.factor = coefficient;
	}

	@Override
	public ImageProcessor doFilter(ImageProcessor src){
        final int centerX     = this.width / 2;
        final int centerY     = this.height / 2;
        final int value0000FF = 0x0000ff;

        final double maxDistance = Math.sqrt(centerX * centerX + centerY * centerY);


        for(int row=0; row<height; row++) {
        	int offset = row * width;
        	for(int col=0; col<width; col++) {
                int tr = R[offset] & value0000FF;
                int tg = G[offset] & value0000FF;
                int tb = B[offset] & value0000FF;

                double scale = 1.0 - getDistance(centerX, centerY, col, row) / maxDistance;

                for(int i=0; i < factor; i++) {
                	scale = scale * scale;
                }

                setRGB(tr, tg, tb, scale, offset);
				offset++;
        	}
        }

        return src;
	}

	private void setRGB(int tr, int tg, int tb, double scale, int offset){
    	tr = SafeCasting.safeDoubleToInt(scale * tr);
    	tg = SafeCasting.safeDoubleToInt(scale * tg);
    	tb = SafeCasting.safeDoubleToInt(scale * tb);

		R[offset] = SafeCasting.safeIntToByte(tr);
		G[offset] = SafeCasting.safeIntToByte(tg);
		B[offset] = SafeCasting.safeIntToByte(tb);
	}
	
	private double getDistance(int centerX, int centerY, int px, int py) {
		double xx = (centerX - px)*(centerX - px);
		double yy = (centerY - py)*(centerY - py);
		return Math.sqrt(xx + yy);
	}

}
