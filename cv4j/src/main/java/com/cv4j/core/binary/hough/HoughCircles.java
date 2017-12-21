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

package com.cv4j.core.binary.hough;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.Vec3i;
import java.util.Arrays;
import java.util.List;

/**
 * detect circle
 *
 */
public class HoughCircles {

    /**
     * The angle value of 360.
     */
    private static final int ANGLE_360 = 360;

    /**
     * The angle value of 180.
     */
    private static final int ANGLE_180 = 180;

    /**
     * The hex value of 0000FF.
     */
    private static final int VALUE_0000FF = 0x0000ff;

    /***
     * The process.
     * @param binary - image data
     * @param circles - number of circles we can detect or find
     * @param minRadius - min radius of circle can be found from hough space
     * @param maxRadius - max radius of circle can be found from hough space
     * @param maxonly - find the max accumulate hough space point only
     * @param accumulate - find the hough space acc value which more than input threshold t
     */
    public void process(ByteProcessor binary, List<Vec3i> circles, int minRadius, int maxRadius, boolean maxonly, int accumulate) {
        final int width = binary.getWidth();
        final int height = binary.getHeight();
        byte[] data = binary.getGray();

        // initialize the polar coordinates space/Hough Space
        int numOfRadius = (maxRadius - minRadius) + 1;
        int[][] acc = initAcc(binary, data, minRadius, maxRadius);

        findCenters(circles, acc, maxonly, accumulate, numOfRadius, width, height);
    }

    /**
     * Find the center and R for each circle.
     * @param circles     The list of circles.
     * @param acc         The acc.
     * @param maxonly     If is maxonly.
     * @param accumulate  The accumulate.
     * @param numOfRadius The number of radius.
     * @param width       The width;
     * @param height      The height;
     */
    private void findCenters(List<Vec3i> circles, int[][] acc, boolean maxonly, int accumulate, int numOfRadius, int width, int height) {
        /// TODO: Variable not used.
        // int[] output = new int[width * height];

        for(int i = 0; i < numOfRadius; i++) {
            findCentersSub(circles, acc, maxonly, accumulate, width, height, i);
        }
    }

    /**
     * The sub-method, for complexity problems, of the findCenters().
     * @param circles    The circles.
     * @param acc        The acc.
     * @param maxonly    If max only.
     * @param accumulate The accumulate
     * @param width      The width.
     * @param height     The height.
     */
    private void findCentersSub(List<Vec3i> circles, int[][] acc, boolean maxonly, int accumulate, int width, int height, int index) {
        int tempValue = 0;
        int tempX     = 0;
        int tempY     = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int value = (acc[index][x + (y * width)] & VALUE_0000FF);

                // if its higher than current value, swap it
                if (maxonly && value > tempValue) {
                    tempValue = value; //radius?
                    tempX     = x;     // center.x
                    tempY     = y;     // center.y
                } else if(value > accumulate) {
                    // filter by threshold
                    addVectorToCircles(circles, x, y, value);
                }
            }
        }

        if(maxonly) {
            addVectorToCircles(circles, tempValue, tempX, tempY);
        }
    }

    /**
     * Add a vec3i to circles.
     * @param circles The circles.
     * @param x       The x value.
     * @param y       The y value.
     * @param z       The z value.
     */
    private void addVectorToCircles(List<Vec3i> circles, int x, int y, int z) {
        Vec3i vec3i = new Vec3i(x, y, z);
        circles.add(vec3i);
    }


    /**
     * Initialization of acc.
     * @param binary    The byte processor.
     * @param data      The data.
     * @param minRadius The min radius.
     * @param maxRadius The max radius.
     * @return          The acc.
     */
    private int[][] initAcc(ByteProcessor binary, byte[] data, int minRadius, int maxRadius) {
        final int width  = binary.getWidth();
        final int height = binary.getHeight();
        final int numOfRadius = (maxRadius - minRadius) + 1;;

        int[][] acc = new int[numOfRadius][width * height];

        for(int i = 0; i < numOfRadius; i++) {
            Arrays.fill(acc[i], 0);
        }

        double[] cosLut = initCosLUT();
        double[] sinLut = initSinLUT();

        // convert to hough space and calculate accumulate
        calculateAccumulate(data, acc, cosLut, sinLut, width, height, minRadius, maxRadius);

        return acc;
    }

    /**
     * Calculate the acc.
     * @param data
     * @param acc
     * @param cosLut
     * @param sinLut
     * @param width
     * @param height
     * @param minRadius
     * @param maxRadius
     */
    private void calculateAccumulate(byte[] data, int[][] acc, double[] cosLut, double[] sinLut, int width, int height, int minRadius, int maxRadius) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                something(data, acc, cosLut, sinLut, x, y, width, height, minRadius, maxRadius);
            }
        }
    }

    /**
     * Initialization of cosLut.
     * @return The cosLut.
     */
    private double[] initCosLUT() {
        double[] cosLut = new double[ANGLE_360];

        for (int theta = 0; theta < ANGLE_360; theta++) {
            cosLut[theta] = Math.cos((theta * Math.PI) / (float) ANGLE_180);
        }

        return cosLut;
    }

    /**
     * Initialization of sinLut.
     * @return The sinLut.
     */
    private double[] initSinLUT() {
        double[] sinLut = new double[ANGLE_360];

        for (int theta = 0; theta < ANGLE_360; theta++) {
            sinLut[theta] = Math.sin((theta * Math.PI) / (float) ANGLE_180);
        }

        return sinLut;
    }

    private void something(byte[] data, int[][] acc, double[] cosLut, double[] sinLut, int x, int y, int width, int height, int minRadius, int maxRadius) {
        final int maxRgb   = 255;

        if ((data[y * width + x] & VALUE_0000FF) == maxRgb) {
            for (int theta = 0; theta < ANGLE_360; theta++) {
                for(int r=minRadius; r<=maxRadius; r++) {
                    int x0 = (int) Math.round(x - r * cosLut[theta]);
                    int y0 = (int) Math.round(y - r * sinLut[theta]);

                    if (x0 < width && x0 > 0 && y0 < height && y0 > 0) {
                        acc[r-minRadius][x0 + (y0 * width)] += 1;
                    }
                }
            }
        }
    }

}
