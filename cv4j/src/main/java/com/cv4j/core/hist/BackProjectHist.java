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
import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.image.ImageProcessor;

/**
 * BackProjectHist.
 * The back projection histogram class, used
 * to calculate back projections.
 */
public class BackProjectHist {

    public void backProjection(ImageProcessor src, ByteProcessor backProj, int[] mHist, int bins) {

        int width = src.getWidth();
        int height = src.getHeight();
        ColorProcessor colorProcessor = (ColorProcessor) src;

        byte[] R = colorProcessor.getRed();
        byte[] G = colorProcessor.getGreen();
        byte[] B = colorProcessor.getBlue();

        // 计算比率脂肪图 R
        float[] rHist = calculateFatMapRatio(mHist, src, bins);

        // 根据像素值查找R，得到分布概率权重
        int index;

        float[] rimage = new float[width * height];
        setRimage(R, G, B, height, width, index, rHist, bins);

        // 计算卷积
        int offset;
        float sum = 0;
        float[] output = new float[width*height];
        System.arraycopy(rimage, 0, output, 0, output.length);
        setOut(output, width, height, offset, sum, rimage);

        // 归一化
        float min = 1000;
        float max = 0;
        for (float anOutput : output) {
            min = Math.min(min, anOutput);
            max = Math.max(max, anOutput);
        }

        float delta = max - min;
        for(int i=0; i<output.length; i++) {
            output[i] =  ((output[i] - min)/delta)*255;
        }

        thresholdBastardizationDisplay(output, backProj);
    }

    private void setRimage(byte[] R, byte[] G, byte[] B, int height, int width, int index, float[] rHist, int bins){
        int bidx;
        int tr;
        int tg;
        int tb;

        int level = 256 / bins;

        for(int row=0; row<height; row++) {
            for(int col=0; col<width; col++) {
                index = row * width + col;
                tr = R[index]&0xff;
                tg = G[index]&0xff;
                tb = B[index]&0xff;
                bidx = (tr / level) + (tg / level)*bins + (tb / level)*bins*bins;
                rimage[index] = Math.min(1, rHist[bidx]);
            }
        }
    }

    private void setOut(float[] output, int width, int height, int offset, float sum, float[] rimage){
        for(int row=1; row<height-1; row++) {
            offset = width * row;
            for(int col=1; col<width-1; col++) {
                sum += rimage[offset+col];
                sum += rimage[offset+col-1];
                sum += rimage[offset+col+1];
                sum += rimage[offset+width+col];
                sum += rimage[offset+width+col-1];

                sum += rimage[offset+width+col+1];
                sum += rimage[offset-width+col];
                sum += rimage[offset-width+col-1];
                sum += rimage[offset-width+col+1];
                output[offset+col] = sum / 9.0f;
                sum = 0f; // for next
            }
        }
    }

    private void thresholdBastardizationDisplay(float[] output, ByteProcessor backProj) {
        for(int i = 0; i < output.length; i++) {
            int pv = (int) output[i];

            if (pv > 10) {
                pv = 255;
            }

            backProj.getGray()[i] = (byte)pv;
        }
    }

    private float[] calculateFatMapRatio(int[] mHist, ImageProcessor src, int bins) {
        int[] iHist = CalcHistogram.calculateNormHist(src, bins);
        float[] rHist = new float[iHist.length];

        for(int i=0; i<iHist.length; i++) {
            float a = mHist[i];
            float b = iHist[i];
            rHist[i] = a / b;
        }

        return rHist;
    }
}
