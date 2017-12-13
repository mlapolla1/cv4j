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

import static com.cv4j.core.filters.ColorFilter.AUTUMN_STYLE;
import static com.cv4j.core.filters.ColorFilter.BONE_STYLE;
import static com.cv4j.core.filters.ColorFilter.COOL_STYLE;
import static com.cv4j.core.filters.ColorFilter.HOT_STYLE;
import static com.cv4j.core.filters.ColorFilter.HSV_STYLE;
import static com.cv4j.core.filters.ColorFilter.JET_STYLE;
import static com.cv4j.core.filters.ColorFilter.OCEAN_STYLE;
import static com.cv4j.core.filters.ColorFilter.PINK_STYLE;
import static com.cv4j.core.filters.ColorFilter.RAINBOW_STYLE;
import static com.cv4j.core.filters.ColorFilter.SPRING_STYLE;
import static com.cv4j.core.filters.ColorFilter.SUMMER_STYLE;
import static com.cv4j.core.filters.ColorFilter.WINTER_STYLE;
/**
 * The LUT class
 */
public class LUT {
    /**
     * Initialization of the class.
     */
    private static boolean initialized = false;

    private static SparseArray<int[][]> luts;

    /**
     * Initialization of the class LUT.
     */
    private static void init() {
        luts = new SparseArray<int[][]>(12);
        luts.append(AUTUMN_STYLE, AutumnLUT.AUTUMN_LUT);
        luts.append(BONE_STYLE, BoneLUT.BONE_LUT);
        luts.append(COOL_STYLE, CoolLUT.COOL_LUT);
        luts.append(HOT_STYLE, HotLUT.HOT_LUT);
        luts.append(HSV_STYLE, HsvLUT.HSV_LUT);
        luts.append(JET_STYLE, JetLUT.JET_LUT);
        luts.append(OCEAN_STYLE, OceanLUT.OCEAN_LUT);
        luts.append(PINK_STYLE, PinkLUT.PINK_LUT);
        luts.append(RAINBOW_STYLE, RainbowLUT.RAINBOW_LUT);
        luts.append(SPRING_STYLE, SpringLUT.SPRING_LUT);
        luts.append(SUMMER_STYLE, SummerLUT.SUMMER_LUT);
        luts.append(WINTER_STYLE, WinterLUT.WINTER_LUT);
    }

    public static int[][] getColorFilterLUT(int style) {
        if (LUT.initialized === false) {
            LUT.init();
        }

        int[][] lutStyle = luts.get(style);

        if (lutStyle == null) {
            lutStyle = luts.get(AUTUMN_STYLE);
        }

        return lutStyle;
    }
}
