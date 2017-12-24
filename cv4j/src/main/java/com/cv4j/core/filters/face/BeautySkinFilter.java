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
package com.cv4j.core.filters.face;

import com.cv4j.core.binary.Erode.Erode;
import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.datamodel.number.IntIntegralImage;
import com.cv4j.core.datamodel.Size;
import com.cv4j.core.filters.CommonFilter;
import com.cv4j.core.filters.FastEPFilter;
import com.cv4j.core.filters.gradients.GradientFilter;
import com.cv4j.core.utils.SafeCasting;

import java.util.Arrays;

/**
 * The beauty skin filter.
 */
public class BeautySkinFilter implements CommonFilter {

    /**
     * The max RGB value.
     */
    private static final int MAX_RGB_VALUE = 255;

    /**
     * The value of 0000FF.
     */
    private static final int VALUE_0000FF = 0x0000ff;

    /**
     * The index zero.
     */
    private static final int INDEX0 = 0;

    /**
     * The index one.
     */
    private static final int INDEX1 = 1;

    /**
     * The index two.
     */
    private static final int INDEX2 = 2;

    @Override
    public ImageProcessor filter(ImageProcessor src) {
        int width = src.getWidth();
        int height = src.getHeight();
        int length = width * height;
        byte[] R = new byte[length];
        byte[] G = new byte[length];
        byte[] B = new byte[length];

        copyArray(src, R, G, B);

        FastEPFilter epFilter = new FastEPFilter();
        epFilter.filter(src);

        ISkinDetection skinDetector = new DefaultSkinDetection();
        byte[] mask = new byte[length];
        Arrays.fill(mask, (byte) 0);

        setMask(R, G, B, skinDetector, mask);

        Erode erode = new Erode();
        int size = 5;
        erode.process(new ByteProcessor(mask, width, height), new Size(size));
        setSrcByte(src, R, G, B, mask);

        setMask(R, G, B, mask);

        setMask(R.length, mask, width, height);

        // 遮罩层模糊
        byte[] blurmask = createBlurMask(mask, width, height);

        // alpha blend
        alphaBlend(src, blurmask, R, G, B);

        return src;
    }

    private void copyArray(ImageProcessor src, byte[] R, byte[] G, byte[] B){
        System.arraycopy(src.toByte(INDEX0), 0, R, 0, R.length);
        System.arraycopy(src.toByte(INDEX1), 0, G, 0, G.length);
        System.arraycopy(src.toByte(INDEX2), 0, B, 0, B.length);
    }

    private void setSrcByte(ImageProcessor src, byte[] R, byte[] G, byte[] B, byte[] mask){
        for (int i = 0; i < mask.length; i++) {
            int c = mask[i] & 0xff;
            if (c > 0) {
                src.toByte(INDEX0)[i] = R[i];
                src.toByte(INDEX1)[i] = G[i];
                src.toByte(INDEX2)[i] = B[i];
            }
        }
    }

    private void setMask(byte [] R, byte [] G, byte [] B, ISkinDetection skinDetector, byte[] mask){
        for (int i = 0; i < R.length; i++) {
            int r = R[i] & VALUE_0000FF;
            int g = G[i] & VALUE_0000FF;
            int b = B[i] & VALUE_0000FF;

            if (!skinDetector.isSkin(r, g, b)) {
                mask[i] = SafeCasting.safeIntToByte(MAX_RGB_VALUE);
            }
        }
    }

    private void setMask(byte [] R, byte [] G, byte [] B, byte[] mask) {
        final double redConst   = 0.299;
        final double greenConst = 0.587;
        final double blueConst  = 0.115;

        for (int i = 0; i < R.length; i++) {
            int r = R[i] & VALUE_0000FF;
            int g = G[i] & VALUE_0000FF;
            int b = B[i] & VALUE_0000FF;

            int c = SafeCasting.safeDoubleToInt(redConst * r + greenConst * g + blueConst * b);
            mask[i] = SafeCasting.safeIntToByte(c);
        }
    }

    private void setMask(int length, byte [] mask, int width, int height){
        GradientFilter gradientFilter = new GradientFilter();
        int[] gradient = gradientFilter.gradient(new ByteProcessor(mask, width, height));
        Arrays.fill(mask, (byte) 0);
        for (int i = 0; i < length; i++) {
            if (gradient[i] > 35) {
                mask[i] = SafeCasting.safeIntToByte(MAX_RGB_VALUE);
            }
        }
    }

    private byte[] createBlurMask(byte[] mask, int width, int height) {
        IntIntegralImage ii = new IntIntegralImage();
        ii.setImage(mask);
        ii.process(width, height);
        byte[] blurmask = new byte[mask.length];
        int offset = 0;
        int swx = 5;
        int swy = 5;
        for (int row = 1; row < height - 1; row++) {
            offset = row * width;
            for (int col = 1; col < width - 1; col++) {
                int sr = ii.getBlockSum(col, row, swx, swy);
                int srdiv25 = sr / 25;
                blurmask[offset + col] = SafeCasting.safeIntToByte(srdiv25);
            }
        }

        return blurmask;
    }


    private void alphaBlend(ImageProcessor src, byte[] blurMask, byte[] R, byte[] G, byte[] B) {
        float w = 0;
        int wc = 0;
        for (int i = 0; i < blurMask.length; i++) {
            wc = blurMask[i] & 0xff;
            w = wc / 255.0f;

            int r = SafeCasting.safeFloatToInt((R[i] & VALUE_0000FF) * w + (src.toByte(INDEX0)[i] & VALUE_0000FF) * (1.0f - w));
            int g = SafeCasting.safeFloatToInt((G[i] & VALUE_0000FF) * w + (src.toByte(INDEX1)[i] & VALUE_0000FF) * (1.0f - w));
            int b = SafeCasting.safeFloatToInt((B[i] & VALUE_0000FF) * w + (src.toByte(INDEX2)[i] & VALUE_0000FF) * (1.0f - w));

            src.toByte(INDEX0)[i] = SafeCasting.safeIntToByte(r);
            src.toByte(INDEX1)[i] = SafeCasting.safeIntToByte(g);
            src.toByte(INDEX2)[i] = SafeCasting.safeIntToByte(b);
        }
    }
}
