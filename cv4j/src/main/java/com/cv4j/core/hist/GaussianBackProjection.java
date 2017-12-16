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
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.exception.CV4JException;
import com.cv4j.image.util.Tools;

/**
 * GaussianBackProjection.
 * The gaussian back projection.
 */
public class GaussianBackProjection {

    /**
     * Index of the red color.
     */
    private static final int INDEX_R = 0;

    /**
     * Index of the green color.
     */
    private static final int INDEX_G = 1;

    /**
     * Index of the blue color.
     */
    private static final int INDEX_B = 2;

    public void backProjection(ImageProcessor src, ImageProcessor model, ByteProcessor dst) {
        if(src.getChannels() == 1 || model.getChannels() == 1) {
            throw new CV4JException("did not support image type : single-channel...");
        }

        float[] R = model.toFloat(0);
        float[] G = model.toFloat(1);
        int r = 0;
        int g = 0;
        int  b = 0;
        float sum = 0;
        int index;

        calculateMeanRGB(model, R, G);


        // 计算均值与标准方差
        float[] rmdev = Tools.calcMeansAndDev(R);
        float[] gmdev = Tools.calcMeansAndDev(G);

        int width = src.getWidth();
        int height = src.getHeight();

        // 反向投影
        float pr = 0;
        float pg = 0;
        float[] result = new float[width*height];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                index = row*width + col;
                b = src.toByte(INDEX_B)[index]&0xff;
                g = src.toByte(INDEX_G)[index]&0xff;
                r = src.toByte(INDEX_R)[index]&0xff;
                sum = b + g + r;
                float red = r / sum;
                float green = g / sum;
                int factor = 2;
                pr = (float)((1.0 / (rmdev[1]*Math.sqrt(factor * Math.PI)))*Math.exp(-(Math.pow((red - rmdev[0]), factor)) / (factor * Math.pow(rmdev[1], factor))));
                pg = (float)((1.0 / (gmdev[1]*Math.sqrt(factor * Math.PI)))*Math.exp(-(Math.pow((green - gmdev[0]),factor)) / (factor * Math.pow(gmdev[1], factor))));
                sum = pr*pg;

                if(Float.isNaN(sum)){
                    result[index] = 0;
                    continue;
                }

                result[index] = sum;

            }
        }

        // 归一化显示高斯反向投影
        float min = 1000;
        float max = 0;
        for (float aResult : result) {
            min = Math.min(min, aResult);
            max = Math.max(max, aResult);
        }

        float delta = max - min;
        int maxRgb = 255;
        for(int i=0; i<result.length; i++) {
            dst.getGray()[i] =  (byte)(((result[i] - min)/delta)*maxRgb);
        }
    }

    /**
     * Calculate the mean value of RGB.
     * @param model The model image processor
     * @param R     The red.
     * @param G     The green.
     */
    private void calculateMeanRGB(ImageProcessor model, float[] R, float[] G) {
        int width = model.getWidth();
        int height = model.getHeight();

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int index = row*width + col;
                int b = model.toByte(INDEX_B)[index] & 0xff;
                int g = model.toByte(INDEX_G)[index] & 0xff;
                int r = model.toByte(INDEX_R)[index] & 0xff;
                int sum = b + g + r;
                R[index] = r / sum;
                G[index] = g / sum;
            }
        }
    }
}
