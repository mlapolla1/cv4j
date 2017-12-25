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

import static com.cv4j.image.util.Tools.clamp;

/**
 * The stroke area filter.
 */
public class StrokeAreaFilter extends BaseFilter {

    /**
     * The value of 0000FF.
     */
    private static final int VALUE_0000FF = 0x0000ff;

    /**
     * Index zero.
     */
    private static final int INDEX0 = 0;

    /**
     * Index one.
     */
    private static final int INDEX1 = 1;

    /**
     * Index two.
     */
    private static final int INDEX2 = 2;

    /**
     * Default size value.
     */
    private static final int SIZE_DEFAULT = 15;

    // default value, optional value 30, 15, 10, 5, 2
    private double size = 0;

    public StrokeAreaFilter() {
        this(SIZE_DEFAULT);
    }

    public StrokeAreaFilter(int strokeSize) {
        this.size = strokeSize;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    @Override
    public ImageProcessor doFilter(ImageProcessor src) {
        int dimRow = 3;
        byte[][] output = new byte[dimRow][R.length];

        int index = 0;
        int ratio = 2;
        int semiRow = (int)(size/ratio);
        int semiCol = (int)(size/ratio);

        // initialize the color RGB array with zero...
        int[] rgb = new int[dimRow];
        int[] rgb2 = new int[dimRow];
        for(int i=0; i<rgb.length; i++) {
            rgb[i] = rgb2[i] = 0;
        }

        setOutputs(width, height, index, rgb, rgb2, semiRow, semiCol, output);

        int index0 = 0;
        int index1 = 1;
        int index2 = 2;
        ((ColorProcessor) src).putRGB(output[index0], output[index1], output[index2]);
        output = null;
        return src;
    }

    private void setOutputs(int width, int height, int index, int[] rgb, int[] rgb2, int semiRow, int semiCol, byte[][] output){
        final double sizeSqaured = this.size * this.size;

        for(int row=0; row<height; row++) {
            for(int col=0; col<width; col++) {
                index = row * width + col;
                rgb[INDEX0] = R[index] & VALUE_0000FF;
                rgb[INDEX1] = G[index] & VALUE_0000FF;
                rgb[INDEX2] = B[index] & VALUE_0000FF;

                /* adjust region to fit in source image */
                // color difference and moment Image
                Double maxRGB = 255.0d;
                double moment = fitRegionInSourceImage(rgb, rgb2, semiRow, semiCol, row, col);
                // calculate the output pixel value.V
                int outPixelValue = clamp(SafeCasting.safeDoubleToInt(maxRGB * moment / (sizeSqaured)));
                output[INDEX0][index] = SafeCasting.safeIntToByte(outPixelValue);
                output[INDEX1][index] = SafeCasting.safeIntToByte(outPixelValue);
                output[INDEX2][index] = SafeCasting.safeIntToByte(outPixelValue);
            }
        }
    }

    private double fitRegionInSourceImage(int[] rgb, int[] rgb2, int semiRow, int semiCol, int row, int col) {
        double moment = 0.0d;

        for(int subRow = -semiRow; subRow <= semiRow; subRow++) {
            for(int subCol = -semiCol; subCol <= semiCol; subCol++) {
                int newY = valueBetween(row + subRow, 0, height-1);
                int newX = valueBetween(col + subCol, 0, width-1);

                int index = newY * width + newX;
                rgb2[INDEX0] = R[index] & VALUE_0000FF; // red
                rgb2[INDEX1] = G[index] & VALUE_0000FF; // green
                rgb2[INDEX2] = B[index] & VALUE_0000FF; // blue

                moment += colorDiff(rgb, rgb2);
            }
        }

        return moment;
    }

    private int valueBetween(int value, int min, int max) {
        if (value < min) {
            value = min;
        }

        if (value > max) {
            value = max;
        }

        return value;
    }

    public static double colorDiff(int[] rgb1, int[] rgb2) {
        final double d02 = 150*150;

        // (1-(d/d0)^2)^2

        double minus = 1.0d;
        double d2 = colorDistance(rgb1, rgb2);

        if (d2 >= d02) {
            return 0;
        }

        double r2 = d2 / d02;

        return ((minus - r2) * (minus - r2));
    }

    public static double colorDistance(int[] rgb1, int[] rgb2) {
        final int index0 = 0;
        final int index1 = 1;
        final int index2 = 2;

        int dr = rgb1[index0] - rgb2[index0];
        int dg = rgb1[index1] - rgb2[index1];
        int db = rgb1[index2] - rgb2[index2];

        return dr * dr + dg * dg + db * db;
    }
}
