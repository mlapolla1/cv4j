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
import com.cv4j.core.datamodel.lut.LUT;
import com.cv4j.core.filters.BaseFilter;

/**
 * The ColorFilter class.
 * Color matching, support 12 color style conversion (equivalent to 12 filters), 
 * based on the look-up table to achieve very fast
 */
public class ColorFilter extends BaseFilter {
    /**
     * Constant autmun style
     */
    public static final int AUTUMN_STYLE = 0;
    
    /**
     * Constant bone style
     */
    public static final int BONE_STYLE = 1;
    
    /**
     * Constant cool style
     */
    public static final int COOL_STYLE = 2;
    
    /**
     * Constant hot style
     */
    public static final int HOT_STYLE = 3;
    
    /**
     * Constant hsv style
     */
    public static final int HSV_STYLE = 4;
    
    /**
     * Constant jet style
     */
    public static final int JET_STYLE = 5;
    
    /**
     * Constant ocean style
     */
    public static final int OCEAN_STYLE = 6;
    
    /**
     * Constant pink style
     */
    public static final int PINK_STYLE = 7;
    
    /**
     * Constant rainbow style
     */
    public static final int RAINBOW_STYLE = 8;
    
    /**
     * Constant spring style
     */
    public static final int SPRING_STYLE = 9;
    
    /**
     * Constant summer style
     */
    public static final int SUMMER_STYLE = 10;
    
    /**
     * Constant winter style
     */
    public static final int WINTER_STYLE = 11;

    /**
     * The style.
     */
    private int style;

    /**
     * Set autumn style ad default style
     */
    public ColorFilter() {
        style = AUTUMN_STYLE;
    }

    /**
     * Set the style
     */
    public void setStyle(int style) {
        this.style = style;
    }

    @Override
    public ImageProcessor doFilter(ImageProcessor src) {

        int tr=0;
        int tg=0;
        int tb=0;
        int[][] lut = getStyleLUT(style);
        int size = R.length;
        for(int i=0; i<size; i++) {
            tr = R[i] & 0xff;
            tg = G[i] & 0xff;
            tb = B[i] & 0xff;

            R[i] = (byte)lut[tr][0];
            G[i] = (byte)lut[tg][1];
            B[i] = (byte)lut[tb][2];
        }
        return src;
    }

    /**
     * Returns the LUT with a given style.
     * @param  quality The LUT style.
     * @return       The LUT.
     */
    private int[][] getStyleLUT(int quality) {
        return LUT.getColorFilterLUT(quality);
    }
}
