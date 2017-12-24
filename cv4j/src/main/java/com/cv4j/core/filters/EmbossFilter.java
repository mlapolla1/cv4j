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

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.utils.SafeCasting;
import com.cv4j.image.util.Tools;
/**
 * The EmbossFilter class.
 * The ginning effect, based on the relief effect, is similar, 
 * but more flexible, allowing more pixel value correction
 */
public class EmbossFilter extends BaseFilter {
	private int COLORCONSTANTS;
	private boolean out;

	public EmbossFilter() {
		this.COLORCONSTANTS = 100;
	}

	public EmbossFilter(boolean out) {
		this.out = out;
		this.COLORCONSTANTS = 100;
	}

	@Override
	public ImageProcessor doFilter(ImageProcessor src){

		int offset = 0;
		int r1=0;
		int g1=0;
		int b1=0;
		int r2=0;
		int g2=0;
		int b2=0;
		int r=0;
		int g=0;
		int b=0;

		byte[][] output = new byte[3][R.length];
		for ( int y = 1; y < height-1; y++ ) {
			offset = y*width;
			setOutput(r, g, b, r1, g1, b1, r2, g2, b2, out, output, offset);
		}
		((ColorProcessor)src).putRGB(output[0], output[1], output[2]);
		output = null;
		return src;
	}


	private void setOutput(int r, int g, int b, int r1, int g1, int b1, int r2, int g2, int b2, boolean isOut, byte [][] output, int offset){
		for ( int x = 1; x < width-1; x++ ) {
				r1 = R[offset] & 0xff;
				g1 = G[offset] & 0xff;
				b1 = B[offset] & 0xff;

				r2 = R[offset+width] & 0xff;
				g2 = G[offset+width] & 0xff;
				b2 = B[offset+width] & 0xff;

				if(isOut) {
					r = r1 - r2;
					g = g1 - g2;
					b = b1 - b2;
				} else {
					r = r2 - r1;
					g = g2 - g1;
					b = b2 - b1;
				}
				r = Tools.clamp(r+COLORCONSTANTS);
				g = Tools.clamp(g+COLORCONSTANTS);
				b = Tools.clamp(b+COLORCONSTANTS);

				output[0][offset] = SafeCasting.safeIntToByte(r);
				output[1][offset] = SafeCasting.safeIntToByte(g);
				output[2][offset] = SafeCasting.safeIntToByte(b);
				offset++;
			}
	}
	/**
	 * 
	 * @param out
	 */
	public void setOUT(boolean out) {
		this.out = out;
	}
}
