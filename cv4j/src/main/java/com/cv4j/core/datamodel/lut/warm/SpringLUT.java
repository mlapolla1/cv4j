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
package com.cv4j.core.datamodel.lut.warm;

/**
 * The filter which is consists of colors that are shades of magenta and yellow.
 * <p>
 * For more information please see:
 * <a target="_blank" href="http://baike.baidu.com/link?url=kysXstK853g0mEbTgPIdkrqO5qTTbgfW-B0O1FJP4MYYzp
 * G5_6E_LlaP_6T9XTn2c97Ge6hJUojJzkppCdnO-mJxIee_XsNASTsy82RaQZe">About ColorMap</a> <p>
 * Or see:
 * <a target="_blank" href="http://matlab.izmiran.ru/help/techdoc/ref/colormap.html">MATLAB Function Reference - colormap</a>
 *
 */
public final class SpringLUT {
    /**
     * List of spring lut
     */
    public static final int[][] SPRING_LUT = new int[][]{
            {254, 0, 255}, {255, 1, 255}, {255, 1, 255}, {255, 3, 252}, {255, 3, 251},
            {255, 5, 248}, {255, 6, 248}, {255, 7, 247}, {255, 8, 245}, {255, 9, 246},
            {254, 10, 245}, {255, 12, 244}, {254, 12, 244}, {254, 13, 242}, {255, 14, 241},
            {255, 14, 240}, {255, 16, 239}, {255, 17, 237}, {255, 18, 236}, {255, 18, 236},
            {255, 20, 235}, {255, 21, 234}, {255, 22, 235}, {255, 23, 231}, {254, 25, 232},
            {254, 25, 229}, {255, 26, 230}, {255, 27, 228}, {255, 29, 227}, {255, 29, 227},
            {255, 30, 226}, {255, 30, 225}, {255, 31, 225}, {255, 33, 222}, {254, 34, 222},
            {255, 35, 219}, {254, 36, 219}, {255, 38, 217}, {253, 38, 217}, {254, 40, 216},
            {255, 39, 214}, {255, 40, 215}, {255, 41, 214}, {255, 43, 213}, {255, 43, 213},
            {255, 45, 210}, {255, 46, 210}, {255, 48, 208}, {254, 48, 208}, {254, 49, 206},
            {255, 50, 205}, {255, 50, 203}, {255, 51, 204}, {255, 52, 201}, {255, 54, 202},
            {255, 54, 200}, {255, 56, 199}, {255, 56, 198}, {255, 58, 199}, {255, 59, 195},
            {255, 61, 196}, {255, 61, 194}, {255, 62, 193}, {255, 63, 192}, {255, 65, 191},
            {255, 65, 189}, {255, 67, 188}, {255, 67, 188}, {253, 68, 187}, {254, 69, 186},
            {253, 70, 186}, {254, 72, 183}, {255, 71, 183}, {255, 73, 181}, {255, 74, 181},
            {255, 75, 180}, {255, 76, 178}, {255, 77, 179}, {254, 78, 177}, {255, 79, 177},
            {254, 80, 177}, {255, 82, 174}, {255, 83, 175}, {255, 83, 172}, {255, 83, 172},
            {255, 85, 169}, {255, 86, 169}, {255, 86, 167}, {255, 88, 166}, {255, 88, 166},
            {255, 90, 166}, {255, 91, 164}, {254, 92, 165}, {254, 93, 161}, {255, 94, 162},
            {255, 95, 159}, {255, 96, 160}, {255, 97, 158}, {255, 98, 157}, {255, 98, 156},
            {255, 100, 157}, {255, 101, 155}, {255, 103, 154}, {255, 103, 152}, {255, 105, 151},
            {255, 105, 150}, {255, 106, 148}, {255, 107, 147}, {255, 109, 148}, {255, 109, 146},
            {255, 109, 145}, {255, 110, 146}, {255, 111, 144}, {255, 113, 143}, {254, 114, 143},
            {255, 115, 141}, {254, 116, 139}, {255, 118, 136}, {254, 119, 136}, {255, 120, 135},
            {255, 120, 134}, {255, 121, 135}, {255, 122, 133}, {255, 122, 131}, {255, 124, 132},
            {255, 125, 131}, {255, 126, 130}, {255, 127, 128}, {254, 129, 127}, {254, 129, 125},
            {255, 130, 124}, {255, 131, 123}, {255, 132, 124}, {255, 132, 122}, {255, 134, 121},
            {255, 134, 121}, {255, 136, 120}, {255, 136, 119}, {255, 138, 120}, {255, 140, 117},
            {255, 141, 115}, {255, 142, 112}, {255, 142, 112}, {255, 143, 111}, {255, 143, 109},
            {255, 145, 110}, {255, 145, 108}, {255, 147, 108}, {254, 148, 108}, {255, 149, 107},
            {255, 150, 105}, {255, 151, 104}, {255, 151, 103}, {255, 153, 102}, {255, 154, 100},
            {255, 155, 99}, {255, 156, 99}, {255, 157, 98}, {254, 158, 97}, {255, 159, 98},
            {254, 160, 96}, {254, 161, 94}, {255, 162, 95}, {255, 162, 92}, {255, 164, 91},
            {255, 164, 87}, {255, 166, 88}, {255, 167, 87}, {255, 169, 86}, {255, 169, 86},
            {255, 171, 85}, {255, 171, 83}, {254, 173, 84}, {254, 173, 82}, {255, 174, 82},
            {255, 175, 80}, {255, 177, 79}, {255, 177, 77}, {255, 178, 77}, {255, 178, 77},
            {254, 180, 75}, {255, 181, 74}, {254, 182, 74}, {255, 183, 72}, {254, 184, 72},
            {255, 186, 69}, {255, 186, 69}, {255, 187, 68}, {254, 188, 66}, {255, 189, 67},
            {255, 189, 66}, {255, 191, 65}, {255, 192, 63}, {255, 193, 62}, {254, 194, 61},
            {255, 196, 60}, {254, 196, 60}, {254, 197, 56}, {254, 199, 57}, {254, 199, 55},
            {255, 200, 55}, {255, 200, 53}, {255, 202, 54}, {255, 202, 50}, {255, 204, 51},
            {255, 204, 50}, {255, 207, 49}, {255, 207, 47}, {254, 209, 48}, {254, 209, 45},
            {255, 210, 46}, {255, 211, 42}, {255, 212, 43}, {255, 212, 41}, {255, 214, 40},
            {255, 214, 40}, {255, 215, 39}, {255, 217, 38}, {254, 217, 38}, {255, 219, 35},
            {254, 220, 35}, {255, 222, 33}, {255, 222, 33}, {255, 223, 30}, {254, 224, 30},
            {255, 225, 29}, {255, 226, 28}, {255, 227, 29}, {255, 228, 27}, {255, 229, 26},
            {255, 230, 26}, {255, 231, 24}, {254, 232, 24}, {255, 234, 21}, {254, 235, 21},
            {254, 235, 19}, {255, 236, 19}, {255, 237, 20}, {254, 238, 18}, {254, 239, 16},
            {254, 241, 15}, {254, 241, 13}, {255, 242, 13}, {255, 243, 11}, {255, 244, 12},
            {255, 244, 8}, {255, 246, 9}, {255, 247, 8}, {255, 249, 7}, {255, 249, 5},
            {255, 251, 6}, {255, 252, 3}, {254, 253, 3}, {254, 253, 2}, {254, 254, 0},
            {255, 255, 1}
    };

}
