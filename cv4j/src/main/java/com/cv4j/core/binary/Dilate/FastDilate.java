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
package com.cv4j.core.binary.Dilate;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.Size;
/**
 * The FastDilate class
 */
public class FastDilate {

    /**
     *
     * @param binary
     * @param structureElement
     * @param iteration  number of times
     */
    public void process(ByteProcessor binary, Size structureElement, int iteration) {
        int width = binary.getWidth();
        int height = binary.getHeight();
        int size = width*height;
        byte[] data = binary.getGray();
        byte[] output = new byte[size];
        System.arraycopy(data, 0, output, 0, size);

        //TODO: This place can use multi-thread.

        // X Direction
        int xr = structureElement.cols/2;
        int shift = calculateShift(structureElement.cols);
        byte c;
        int offset = 0;

        for(int row=0; row<height; row++) {
            for(int col=0; col<width; col++) {
                c = data[row*width+col];
                if((c&0xff) == 255)continue;
                for(int x = -(xr); x <= (xr - shift); x++) {
                    if (x == 0) {
                        continue;
                    }

                    offset = getValueBetween(x + col, 0, width-1);
                    c |=data[row*width+offset];
                }

                // TODO: this seems never happen
                if(c == 255){
                    output[row*width+col] = (byte)255;
                }
            }
        }
        System.arraycopy(output, 0, data, 0, size);

        // Y Direction
        int yr = structureElement.rows/2;

        shift = calculateShift(structureElement.rows);
        shift = 0;
        if(structureElement.rows % 2 == 0) {
            shift = 1;
        }

        something(output, data, width, height, yr, shift);

        System.arraycopy(output, 0, data, 0, size);
    }

    private int getValueBetween(int value, int min, int max) {
        if(value < min) {
            value = min;
        }

        if(value > max) {
            value = max;
        }

        return value;
    }

    private int calculateShift(int rows) {
        int shift = 0;

        if(rows % 2 == 0) {
            shift = 1;
        }

        return shift;
    }

    private void something(byte[] output, byte[] data, int width, int height, int yr, int shift) {
        int c;
        int offset;

        for(int col=0; col<width; col++) {
            for(int row=0; row<height; row++) {
                c = data[row*width+col];
                if((c&0xff) == 255)continue;
                for(int y=-yr; y<=(yr-shift); y++) {
                    if(y == 0) {
                        continue;
                    }

                    offset = getValueBetween(y + row, 0, height);

                    c |= data[offset*width+col];
                }
                if(c == 255){
                    output[row*width+col] = (byte)255;
                }
            }
        }
    }


}
