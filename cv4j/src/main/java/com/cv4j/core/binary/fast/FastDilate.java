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
package com.cv4j.core.binary.fast;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.Size;
/**
 * The FastDilate class
 */
public class FastDilate extends FastBase {

    @Override
    protected void calculateOutputValues(byte[] output, byte[] data, int width, int height, int xyr, int shift) {
        final int size     = width * height;
        final int maxRgb   = 255;
        final int andValue = 0xff;

        for(int col = 0; col < width; col++) {
            for(int row = 0; row < height; row++) {
                int c = data[row*width+col];

                if((c & andValue) == maxRgb) {
                    continue;
                }

                for(int xy = -(xyr); xy <= (xyr - shift); xy++) {
                    if(xy == 0) {
                        continue;
                    }

                    int offset = getValueBetween(xy + row, 0, height);
                    c |= data[offset*width+col];
                }

                if(c == maxRgb){
                    output[row*width+col] = (byte) maxRgb;
                }
            }
        }

        System.arraycopy(output, 0, data, 0, size);
    }


}
