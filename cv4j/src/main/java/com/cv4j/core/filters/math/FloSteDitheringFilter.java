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
package com.cv4j.core.filters.math;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.image.ImageData;
import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.filters.CommonFilter;
import com.cv4j.core.utils.SafeCasting;
import com.cv4j.image.util.Tools;

/**
 * algorithm -http://en.wikipedia.org/wiki/Floyd%E2%80%93Steinberg_dithering
 * http://en.literateprograms.org/Floyd-Steinberg_dithering_(C)
 * ******Floyd Steinberg dithering*******
 * * 0       0,       0*
 * * 0       P     7/16*
 * * 3/16, 5/16,   1/16*
 * *************************
 * * 0        0           0*
 * * 0        *      0.4375*
 * * 0.1875, 0.3125, 0.0625*
 * *************************
 */
public class FloSteDitheringFilter implements CommonFilter {

    /**
     * Constant kernel data
     */
    private final static float[] KERNEL_DATA = new float[]{0.1875f, 0.3125f, 0.0625f, 0.4375f};

    /**
     * Constant color palette
     */
    private final static int[] COLOR_PALETTE = new int[]{0, 255};

    /**
     * The value of the hex value 0000FF.
     */
    private static final int VALUE_0000FF = 0x0000ff;

    /**
     * Returns the kernel data.
     * @return The kernel data.
     */
    public static float[] getKernelData() {
        return FloSteDitheringFilter.KERNEL_DATA;
    }

    /**
     * Returns the color palette.
     * @return The color palette.
     */
    public  static int[] getColorPalette() {
        return FloSteDitheringFilter.COLOR_PALETTE;
    }

    private void algorithm(byte[] GRAY, int width, int height, int row, int col, int er) {
        if (row + 1 < height && col - 1 > 0) {
            rowSmallerHeight(row, col, GRAY, er, height, width);
        }

        if (col + 1 < width) {
            colSmallerWidth(row, col, GRAY, er, width);
        }

        if (row + 1 < height) {
            rowPlusOneSmallerHeight(row, col, GRAY, height, width, er);
        }

        if (row + 1 < height && col + 1 < width) {
            rowAndColSmaller(row, col, GRAY, height, width, er);
        }
    }

    private void rowAndColSmaller(int row, int col, byte[] GRAY, int height, int width, int er) {
        System.out.println(height);
        int k = (row + 1) * width + col + 1;
        float err = (GRAY[k] & VALUE_0000FF) + (er * KERNEL_DATA[2]);

        GRAY[k] = SafeCasting.safeIntToByte(Tools.clamp(err));
    }

    private void rowPlusOneSmallerHeight(int row, int col, byte[] GRAY, int height, int width, int er) {
        System.out.println(height);
        int k = (row + 1) * width + col;
        float err = (GRAY[k] & VALUE_0000FF) + (er * KERNEL_DATA[1]);

        GRAY[k] = SafeCasting.safeIntToByte(Tools.clamp(err));
    }

    private void rowSmallerHeight(int row, int col, byte[] GRAY, int er, int height, int width) {
        System.out.println(height);
        int k = (row + 1) * width + col - 1;
        float err = (GRAY[k] & VALUE_0000FF) + (er * KERNEL_DATA[0]);

        GRAY[k] = SafeCasting.safeIntToByte(Tools.clamp(err));
    }

    private void colSmallerWidth(int row, int col, byte[] GRAY, int er, int width) {
        int k = row * width + col + 1;
        float err = (GRAY[k] & VALUE_0000FF) + (er * KERNEL_DATA[3]);

        GRAY[k] = SafeCasting.safeIntToByte(Tools.clamp(err));
    }

    @Override
    public ImageProcessor filter(ImageProcessor src) {
        if (src instanceof ColorProcessor) {
            src.getImage().convert2Gray();
            src = src.getImage().getProcessor();
        }

        int width = src.getWidth();
        int height = src.getHeight();

        ByteProcessor byteSrc = (ByteProcessor) src;

        byte[] GRAY = byteSrc.getGray();
        byte[] output = new byte[GRAY.length];

        int gray = 0;

        for (int row = 0; row < height; row++) {
            int offset = row * width;
            for (int col = 0; col < width; col++) {
                gray = GRAY[offset] & VALUE_0000FF;
                int cIndex = getCloseColor(gray);
                output[offset] = (byte) COLOR_PALETTE[cIndex];
                int er = (gray - COLOR_PALETTE[cIndex]);

                algorithm(GRAY, width, height, row, col, er);

                offset++;
            }
        }

        byteSrc.putGray(GRAY);

        return src;
    }

    private int getCloseColor(int gray) {
        int minDistanceSquared = 255 * 255 + 1;
        int bestIndex = 0;

        for (int i = 0; i < COLOR_PALETTE.length; i++) {
            final int diff = Math.abs(gray - COLOR_PALETTE[i]);
            final int sqrtLutValue = ImageData.SQRT_LUT.get(diff);

            if (sqrtLutValue < minDistanceSquared) {
                minDistanceSquared = sqrtLutValue;
                bestIndex = i;
            }
        }

        return bestIndex;
    }
}
