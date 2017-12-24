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
package com.cv4j.core.datamodel.lut;

import android.util.SparseArray;

import com.cv4j.core.datamodel.lut.warm.AutumnLUT;
import com.cv4j.core.datamodel.lut.warm.HotLUT;
import com.cv4j.core.datamodel.lut.warm.SpringLUT;

import static com.cv4j.core.filters.image.ColorFilter.AUTUMN_STYLE;
import static com.cv4j.core.filters.image.ColorFilter.BONE_STYLE;
import static com.cv4j.core.filters.image.ColorFilter.COOL_STYLE;
import static com.cv4j.core.filters.image.ColorFilter.HOT_STYLE;
import static com.cv4j.core.filters.image.ColorFilter.HSV_STYLE;
import static com.cv4j.core.filters.image.ColorFilter.JET_STYLE;
import static com.cv4j.core.filters.image.ColorFilter.OCEAN_STYLE;
import static com.cv4j.core.filters.image.ColorFilter.PINK_STYLE;
import static com.cv4j.core.filters.image.ColorFilter.RAINBOW_STYLE;
import static com.cv4j.core.filters.image.ColorFilter.SPRING_STYLE;
import static com.cv4j.core.filters.image.ColorFilter.SUMMER_STYLE;
import static com.cv4j.core.filters.image.ColorFilter.WINTER_STYLE;
/**
 * The LUT class
 */
public class LUT {

    /**
     * The LUTs list.
     */
    private static SparseArray<int[][]> luts;

    static {
        final int dimLut = 12;
        LUT.luts = new SparseArray<>(dimLut);

        LUT.luts.put(AUTUMN_STYLE, AutumnLUT.AUTUMN_LUT);
        LUT.luts.put(BONE_STYLE, BoneLUT.BONE_LUT);
        LUT.luts.put(COOL_STYLE, CoolLUT.COOL_LUT);
        LUT.luts.put(HOT_STYLE, HotLUT.HOT_LUT);
        LUT.luts.put(HSV_STYLE, HsvLUT.HSV_LUT);
        LUT.luts.put(JET_STYLE, JetLUT.JET_LUT);
        LUT.luts.put(OCEAN_STYLE, OceanLUT.OCEAN_LUT);
        LUT.luts.put(PINK_STYLE, PinkLUT.PINK_LUT);
        LUT.luts.put(RAINBOW_STYLE, RainbowLUT.RAINBOW_LUT);
        LUT.luts.put(SPRING_STYLE, SpringLUT.SPRING_LUT);
        LUT.luts.put(SUMMER_STYLE, SummerLUT.SUMMER_LUT);
        LUT.luts.put(WINTER_STYLE, WinterLUT.WINTER_LUT);
    }

    /**
     * Return the color filter LUT.
     * @param style The style of the filter.
     * @return      The LUT.
     */
    public static int[][] getColorFilterLUT(int style) {
        int[][] lutStyle = luts.get(style);

        if (lutStyle == null) {
            lutStyle = luts.get(AUTUMN_STYLE);
        }

        return lutStyle;
    }
}
