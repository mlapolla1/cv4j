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
package com.cv4j.core.hist;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.image.util.Preconditions;

import java.util.Arrays;

/**
 * EqualHist.
 * The purpose of the class is the equalization
 * of histograms.
 */
public class EqualHist {

    public void equalize(ByteProcessor src) {

        if (src == null) return;

        int rgb = 256, index = 0;
        int[] inputBins = new int[rgb]; // RGB
        int[] outputBins = new int[rgb]; // after HE
        Arrays.fill(inputBins, 0);
        Arrays.fill(outputBins, 0);

        byte[] data = src.getGray();
        if (Preconditions.isNotBlank(data)) {
            for (byte d:data) {
                inputBins[d & 0xff]++;
            }
        }

        // generate original source image RGB histogram
        generateHEData(inputBins, outputBins, data.length, rgb);
        for(int row=0, pv=0; row<src.getHeight(); row++) {
            for(int col=0; col<src.getWidth(); col++) {
                index = row * src.getWidth() + col;
                pv = data[index]&0xff;
                data[index] = (byte)outputBins[pv];
            }
        }
    }

    /**
     *
     * @param input
     * @param output
     * @param numOfPixels
     * @param grayLevel
     */
    private void generateHEData(int[] input, int[] output, int numOfPixels, int grayLevel) {
        for(int i=0; i<grayLevel; i++) {
            output[i] = getNewintensityRate(input, numOfPixels, i);
        }
    }

    private int getNewintensityRate(int[] grayHis, double total, int index) {
        double sum = 0;
        float maxRgb = 255f;
        for(int i=0; i<=index; i++) {
            sum += ((double)grayHis[i])/total;
        }
        return (int)(sum * maxRgb);
    }
}
