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

import com.cv4j.core.binary.Erode;
import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.datamodel.IntIntegralImage;
import com.cv4j.core.datamodel.Size;
import com.cv4j.core.filters.CommonFilter;
import com.cv4j.core.filters.FastEPFilter;
import com.cv4j.core.filters.GradientFilter;

import java.util.Arrays;

/**
 * The beauty skin filter.
 */
public class BeautySkinFilter implements CommonFilter {

    @Override
    public ImageProcessor filter(ImageProcessor src) {
        int width = src.getWidth();
        int height = src.getHeight();
        int length = width * height;
        byte[] R = new byte[length];
        byte[] G = new byte[length];
        byte[] B = new byte[length];

        int index0 = 0;
        int index1 = 1;
        int index2 = 2;

        System.arraycopy(src.toByte(index0), 0, R, 0, R.length);
        System.arraycopy(src.toByte(index1), 0, G, 0, G.length);
        System.arraycopy(src.toByte(index2), 0, B, 0, B.length);

        FastEPFilter epFilter = new FastEPFilter();
        epFilter.filter(src);

        ISkinDetection skinDetector = new DefaultSkinDetection();
        byte[] mask = new byte[length];
        Arrays.fill(mask, (byte) 0);
        int r = 0;
        int g = 0;
        int b = 0;
        byte maxRgb = (byte) 255;
        for (int i = 0; i < R.length; i++) {
            r = R[i] & 0xff;
            g = G[i] & 0xff;
            b = B[i] & 0xff;
            if (!skinDetector.isSkin(r, g, b)) {
                mask[i] = (byte) maxRgb;
            }
        }
        Erode erode = new Erode();
        int size = 5;
        erode.process(new ByteProcessor(mask, width, height), new Size(size));
        for (int i = 0; i < mask.length; i++) {
            int c = mask[i] & 0xff;
            if (c > 0) {
                src.toByte(index0)[i] = R[i];
                src.toByte(index1)[i] = G[i];
                src.toByte(index2)[i] = B[i];
            }
        }

        int c = 0;
        for (int i = 0; i < R.length; i++) {
            r = R[i] & 0xff;
            g = G[i] & 0xff;
            b = B[i] & 0xff;

            c = (int) (0.299 * r + 0.587 * g + 0.114 * b);
            mask[i] = (byte) c;
        }

        GradientFilter gradientFilter = new GradientFilter();
        int[] gradient = gradientFilter.gradient(new ByteProcessor(mask, width, height));
        Arrays.fill(mask, (byte) 0);
        for (int i = 0; i < R.length; i++) {
            if (gradient[i] > 35) {
                mask[i] = (byte) 255;
            }
        }
        gradient = null;

        // 遮罩层模糊
        IntIntegralImage ii = new IntIntegralImage();
        ii.setImage(mask);
        ii.process(width, height);
        byte[] blurmask = new byte[mask.length];
        int offset;
        int swx = 5;
        int swy = 5;
        for (int row = 1; row < height - 1; row++) {
            offset = row * width;
            for (int col = 1; col < width - 1; col++) {
                int sr = ii.getBlockSum(col, row, swx, swy);
                int srdiv25 = sr / 25;
                blurmask[offset + col] = (byte) (srdiv25);
            }
        }

        // alpha blend
        float w;
        int wc;
        for (int i = 0; i < blurmask.length; i++) {
            wc = blurmask[i] & 0xff;
            w = wc / 255.0f;

            r = (int) ((R[i] & 0xff) * w + (src.toByte(index0)[i] & 0xff) * (1.0f - w));
            g = (int) ((G[i] & 0xff) * w + (src.toByte(index1)[i] & 0xff) * (1.0f - w));
            b = (int) ((B[i] & 0xff) * w + (src.toByte(index2)[i] & 0xff) * (1.0f - w));

            src.toByte(index0)[i] = (byte) r;
            src.toByte(index1)[i] = (byte) g;
            src.toByte(index2)[i] = (byte) b;
        }

        return src;
    }
}
