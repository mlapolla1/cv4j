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
        // find maximum for each space
        final int numOfTempCircles = 3;
        final int indexCircle0 = 0;
        final int indexCircle1 = 1;
        final int indexCircle2 = 2;

        int[] tempCircle = new int[numOfTempCircles];

        /// TODO: Variable not used.
        // int[] output = new int[width * height];

        for(int i = 0; i < numOfRadius; i++) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int value = (acc[i][x + (y * width)] & 0xff);

                    // if its higher than current value, swap it
                    if (maxonly && value > tempCircle[indexCircle0]) {
                        tempCircle[indexCircle0] = value; //radius?
                        tempCircle[indexCircle1] = x;     // center.x
                        tempCircle[indexCircle2] = y;     // center.y
                    } else if(value > accumulate) { // filter by threshold
                        Vec3i vec3i = createVector3i(x, y, value);
                        circles.add(vec3i);
                    }
                }
            }

            if(maxonly) {
                Vec3i vec3i = createVector3i(tempCircle[indexCircle0], tempCircle[indexCircle2], tempCircle[indexCircle0]);
                circles.add(vec3i);
            }
        }
    }

    /**
     * Create an integer vector of three elements.
     * @param x The x value.
     * @param y The y value.
     * @param z The z value.
     * @return  The vector.
     */
    private Vec3i createVector3i(int x, int y, int z) {
        Vec3i vec3i = new Vec3i();

        vec3i.x = x;
        vec3i.y = y;
        vec3i.z = z;

        return vec3i;
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
        final float angle180 = 180f;
        final int angle360 = 360;
        double[] cosLut = new double[angle360];

        for (int theta = 0; theta < angle360; theta++) {
            cosLut[theta] = Math.cos((theta * Math.PI) / angle180);
        }

        return cosLut;
    }

    /**
     * Initialization of sinLut.
     * @return The sinLut.
     */
    private double[] initSinLUT() {
        final float angle180 = 180f;
        final int angle360 = 360;
        double[] sinLut = new double[angle360];

        for (int theta = 0; theta < angle360; theta++) {
            sinLut[theta] = Math.sin((theta * Math.PI) / angle180);
        }

        return sinLut;
    }

    private void something(byte[] data, int[][] acc, double[] cosLut, double[] sinLut, int x, int y, int width, int height, int minRadius, int maxRadius) {
        final int maxRgb   = 255;
        final int andValue = 0xff;
        final int angle360 = 360;

        if ((data[y * width + x] & andValue) == maxRgb) {
            for (int theta = 0; theta < angle360; theta++) {
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
