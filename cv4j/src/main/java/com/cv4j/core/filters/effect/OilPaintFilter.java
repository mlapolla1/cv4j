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

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.filters.BaseFilter;
import com.cv4j.core.utils.SafeCasting;

/**
 * The oil paint filter.
 */
public class OilPaintFilter extends BaseFilter {

    /**
     * Default radius value.
     */
    private static final int RADIUS_DEFAULT = 15;

    /**
     * Default intensity value.
     */
    private static final int INTENSITY_DEFAULT = 40;

    private int radius = 15; // default value
    private int intensity = 40; // default value

    public OilPaintFilter() {
        this(RADIUS_DEFAULT, INTENSITY_DEFAULT);
    }

    public OilPaintFilter(int radius, int graylevel) {
        this.radius    = radius;
        this.intensity = graylevel;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    @Override
    public ImageProcessor doFilter(ImageProcessor src) {

        byte[][] output = new byte[3][R.length];

        int index = 0;
        int subradius = this.radius / 2;
        int[] intensityCount = new int[intensity+1];
        int[] ravg = new int[intensity+1];
        int[] gavg = new int[intensity+1];
        int[] bavg = new int[intensity+1];

        inizialize(intensityCount, ravg, gavg, bavg);


        for(int row=0; row<height; row++) {
            int ta = 0;
            int tr = 0;
            int tg = 0;
            int tb = 0;
            setOutputs(intensityCount, ravg, gavg, bavg, output, row, width, index);
        }

        ((ColorProcessor) src).putRGB(output[0], output[1], output[2]);

        return src;
    }

    private void setOutputs(int[] intensityCount, int[] ravg, int[] gavg, int[] bavg, byte[][] output, int row, int width, int index){
        for(int col=0; col<width; col++) {
            // find the max number of same gray level pixel
            int maxIndex = findMaxNumberIndex(intensityCount);
            int maxCount = intensityCount[maxIndex];

            // get average value of the pixel
            int nr = ravg[maxIndex] / maxCount;
            int ng = gavg[maxIndex] / maxCount;
            int nb = bavg[maxIndex] / maxCount;
            index = row * width + col;
            output[0][index] = SafeCasting.safeIntToByte(nr);
            output[1][index] = SafeCasting.safeIntToByte(ng);
            output[2][index] = SafeCasting.safeIntToByte(nb);
        }
    }

    private void inizialize(int[] intensityCount, int[] ravg, int[] gavg, int[] bavg){
        for(int i=0; i<=intensity; i++) {
            intensityCount[i] = 0;
            ravg[i] = 0;
            gavg[i] = 0;
            bavg[i] = 0;
        }    
    }

    private int findMaxNumberIndex(int[] intensityCount) {
        int maxIndex = intensityCount[0];

        for(int i = 1; i < intensityCount.length; i++) {
            if(intensityCount[i] > intensityCount[maxIndex]) {
                maxIndex = i;
            }
        }

        return maxIndex;
    }

    private void setAvg(int row, int col, int subradius, int index, int tr, int tg, int tb, int [] intensityCount, int [] ravg, int [] gavg, int [] bavg){
        for(int subRow = -subradius; subRow <= subradius; subRow++) {
            for(int subCol = -subradius; subCol <= subradius; subCol++) {
                int nrow = row + subRow;
                int ncol = col + subCol;
                if(nrow >=height || nrow < 0)
                {
                    nrow = 0;
                }
                if(ncol >= width || ncol < 0)
                {
                    ncol = 0;
                }
                index = nrow * width + ncol;
                tr = R[index] & 0xff;
                tg = G[index] & 0xff;
                tb = B[index] & 0xff;
                int curIntensity = (int)(((double)((tr+tg+tb)/3)*intensity)/255.0f);
                intensityCount[curIntensity]++;
                ravg[curIntensity] += tr;
                gavg[curIntensity] += tg;
                bavg[curIntensity] += tb;
            }
        }
    }

    private void clearAll(int [] ravg, int [] gavg, int [] bavg, int [] intensityCount, int intensityValue){
        // post clear values for next pixel
        for(int i=0; i<=intensityValue; i++)
        {
            intensityCount[i] = 0;
            ravg[i] = 0;
            gavg[i] = 0;
            bavg[i] = 0;
        }
    }
}
