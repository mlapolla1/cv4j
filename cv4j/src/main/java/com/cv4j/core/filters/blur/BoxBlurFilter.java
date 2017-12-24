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
package com.cv4j.core.filters.blur;

import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.filters.BaseFilter;
import com.cv4j.core.utils.SafeCasting;
import com.cv4j.image.util.Tools;
/**
 * The BloxBlurFilter class.
*  Blurred boxes, horizontal and vertical warp support,
*  usually fuzzy numbers 1, 3, 5, 7, 9, 11, 15
 */

public class BoxBlurFilter extends BaseFilter {

	private int hRadius=5;
	private int vRadius=5;
	private int iterations = 1;

    @Override
    public ImageProcessor doFilter(ImageProcessor src) {

        byte[][] output = new byte[3][width*height];
        byte[][] input = new byte[][]{R, G, B};
        for (int i = 0; i < iterations; i++ ) {
            blur( input, output, width, height, hRadius );
            blur( output, input, height, width, vRadius );
        }
        return src;
    }

    private void blur( byte[][] in, byte[][] out, int width, int height, int radius ) {
        int widthMinus1 = width-1;
        int tableSize = 2*radius+1;
        int maxRGB = 256;
        int divide[] = new int[maxRGB*tableSize];

        // the value scope will be 0 to 255, and number of 0 is table size
        // will get means from index not calculate result again since 
        // color value must be  between 0 and 255.
        for ( int i = 0; i < maxRGB*tableSize; i++ ){
            divide[i] = i/tableSize; 
        }

        int inIndex = 0;

        // 每一行
        for ( int y = 0; y < height; y++ ) {
            int outIndex = y;
            int tr = 0;
            int tg = 0;
            int tb = 0;

            // 初始化盒子里面的像素和
            for ( int i = -radius; i <= radius; i++ ) {
                int offset = inIndex + Tools.clamp(i, 0, width-1);
                tr += in[0][offset] & 0xff;
                tg += in[1][offset] & 0xff;
                tb += in[2][offset] & 0xff;
            }

            // 每一列，每一个像素
            outIndex = setOut(divide, outIndex, tr, tg, tb, radius, widthMinus1, height, out);
            // 继续到下一行
            inIndex += width;
        }
    }

    private int setOut(int[] divide, int outIndex, int tr, int tg, int tb, int radius, int widthMinus1, int height, byte[][] out){
        for ( int x = 0; x < width; x++ ) {
            // 赋值到输出像素
            out[0][outIndex] = SafeCasting.safeIntToByte(divide[tr]);
            out[1][outIndex] = SafeCasting.safeIntToByte(divide[tg]);
            out[2][outIndex] = SafeCasting.safeIntToByte(divide[tb]);

            // 移动盒子一个像素距离
            int i1 = x+radius+1;
            // 检测是否达到边缘
            if ( i1 > widthMinus1 ) {
                i1 = widthMinus1;
            }
            // 将要移出的一个像素
            int i2 = x-radius;
            if ( i2 < 0 ) {
                i2 = 0;
            }

            // 继续到下一行
            outIndex += height;
        }
        return outIndex;
    }


        
	public void setHRadius(int hRadius) {
		this.hRadius = hRadius;
	}
	
	public int getHRadius() {
		return hRadius;
	}
	
	public void setVRadius(int vRadius) {
		this.vRadius = vRadius;
	}
	
	public int getVRadius() {
		return vRadius;
	}
	
	public void setRadius(int radius) {
		this.hRadius = this.vRadius = radius;
	}
	
	public int getRadius() {
		return hRadius;
	}
	
	public void setIterations(int iterations) {
		this.iterations = iterations;
	}
	
	public int getIterations() {
		return iterations;
	}
}
