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
import com.cv4j.core.filters.math.GaussianBlurFilter;
import com.cv4j.core.utils.SafeCasting;
import com.cv4j.image.util.Tools;

/**
 * The glow filter.
 */
public class GlowFilter extends GaussianBlurFilter {

	private float amount = 0.2f;
	//private int radius;
	
	public void setAmount( float amount ) {
		this.amount = amount;
	}
	
	public float getAmount() {
		return amount;
	}

	@Override
	public ImageProcessor doFilter(ImageProcessor src){

		int total = width*height;
		byte[] R1 = new byte[total];
		byte[] G1 = new byte[total];
		byte[] B1 = new byte[total];
		System.arraycopy(R, 0, R1, 0, total);
		System.arraycopy(G, 0, G1, 0, total);
		System.arraycopy(B, 0, B1, 0, total);

		// 高斯模糊
		super.doFilter(src);

		setRGB(R, G, B, R1, G1, B1);

		return src;
    }

    private void setRGB(byte[] R, byte[] G, byte[] B, byte[] R1, byte[] G1, byte[] B1){
		final float a = 4 * this.amount;
		final int value0000FF = 0x0000ff;

		int index = 0;
		for ( int y = 0; y < height; y++ ) {
			for (int x = 0; x < this.width; x++) {
				int r1 = R[index] & value0000FF;
				int g1 = G[index] & value0000FF;
				int b1 = B[index] & value0000FF;

				int r2 = R1[index] & value0000FF;
				int g2 = G1[index] & value0000FF;
				int b2 = B1[index] & value0000FF;

				R[index] = SafeCasting.safeIntToByte(Tools.clamp(SafeCasting.safeFloatToInt(r1 + a * r2)));
				G[index] = SafeCasting.safeIntToByte(Tools.clamp(SafeCasting.safeFloatToInt(g1 + a * g2)));
				B[index] = SafeCasting.safeIntToByte(Tools.clamp(SafeCasting.safeFloatToInt(b1 + a * b2)));

				index++;
			}
		}
    }
}
