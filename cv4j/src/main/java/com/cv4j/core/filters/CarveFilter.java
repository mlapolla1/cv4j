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
import com.cv4j.image.util.Tools;
/**
 * The CarveFilter class.
 * Emboss effect, support two types of relief, 
 * according to boolean parameters to decide which one to use
 */
public class CarveFilter extends BaseFilter{
	private boolean isCarve;
	
	public CarveFilter()
	{
		isCarve = true;
	}
	
	public void setCarve(boolean carve)
	{
		isCarve =carve;
	}
	@Override
	public ImageProcessor doFilter(ImageProcessor src) {

		byte[][] output = new byte[3][R.length];
		int halfRGB = 129;
        int index = 0;
        for(int row=1; row<height-1; row++) {
        	int tr = 0;
        	int tg = 0;
        	int tb = 0;
        	setOuts(output, index, tr, tg, tb, halfRGB, row);

        }
		((ColorProcessor) src).putRGB(output[0], output[1], output[2]);

		output = null;
		return src;
	}

	private void setOuts(byte[][] output, int index, int tr, int tg, int tb, int halfRGB, int row){
    	for(int col=1; col<width-1; col++) {
    		// Index of the pixel in the array      
    		index = row * width + col;
    		int bidx = row * width + (col - 1);
    		int aidx = row * width + (col + 1);

            int br = R[bidx] & 0xff;
            int bg = G[bidx] & 0xff;
            int bb = B[bidx] & 0xff;

            int ar = R[aidx] & 0xff;
            int ag = G[aidx] & 0xff;
            int ab = B[aidx] & 0xff;
   
            // calculate new RGB value 
            if(isCarve)
            {
                tr = ar - br + halfRGB;
                tg = ag - bg + halfRGB;
                tb = ab - bb + halfRGB; 
            }
            else
            {
                tr = br - ar + halfRGB;
                tg = bg - ag + halfRGB;
                tb = bb - ab + halfRGB; 
            }
			output[0][index] = (byte)Tools.clamp(tr);
			output[1][index] = (byte)Tools.clamp(tg);
			output[2][index] = (byte)Tools.clamp(tb);
    	}
	}


}
