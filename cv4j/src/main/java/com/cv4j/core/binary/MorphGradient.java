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
package com.cv4j.core.binary;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.Size;
import com.cv4j.exception.CV4JException;
/**
 * The MorphGradient class
 */
public class MorphGradient {

    /**
     * Constant of the internal gradient
     */ 
    public static final int INTERNAL_GRADIENT = 1;
    /**
     * Constant of the external gradient
     */ 
    public static final int EXTERNAL_GRADIENT = 2;
    /**
     * Constant of the basic gradient
     */ 
    public static final int BASIC_GRADIENT = 3;

    /***
     *
     * @param gray
     * @param structureElement - 3, 5, 7 must be odd
     * @param gradientType
     */
    public void process(ByteProcessor gray, Size structureElement, int gradientType) {
        int width = gray.getWidth();
        int height = gray.getHeight();
        byte[] ero = new byte[width*height];
        byte[] dil = new byte[width*height];
        byte[] data = new byte[width*height];
        System.arraycopy(gray.getGray(), 0, data, 0, data.length);

        // X Direction
        int xr = structureElement.cols/2;
        int min = 0;
        int max = 0;
        System.arraycopy(data, 0, ero, 0, data.length);
        System.arraycopy(data, 0, dil, 0, data.length);
        int offset = 0;
        for(int row=0; row<height; row++) {
            // find min and max for input array
            offset = row*width;
            for(int col=0; col<width; col++) {
                min = findMinInputArray(data, row, col, width, height, xr);
                max = findMaxInputArray(data, row, col, width, height, xr);
                
                ero[offset+col] = (byte)min;
                dil[offset+col] = (byte)max;
            }
        }

        // Y Direction
        System.arraycopy(ero, 0, data, 0, data.length);
        for(int col=0; col<width; col++) {
            for(int row=0; row<height; row++) {
                // find min for input array
                min = findMinInputArray(data, row, col, width, height, xr);
                ero[row*width+col] = (byte) min;
            }
        }

        System.arraycopy(dil, 0, data, 0, data.length);
        for(int col=0; col<width; col++) {
            for(int row=0; row<height; row++) {
                // find max for input array
                max = findMaxInputArray(data, row, col, width, height, xr);
                dil[row*width+col] = (byte)max;
            }
        }

        calculateGradient(data, dil, ero, gray, gradientType);
    }

    private int findMaxInputArray(byte[] data, int row, int col, int width, int height, int xr) {
        int max = 0;

        for(int i=-xr; i<=xr; i++) {
            if(i == 0 || (row+i) < 0 || (row+i) >= height) {
                continue;
            }

            int offset = (row+i)*width;
            int dataMax = data[offset+col]&0xff;
            max = Math.max(max, dataMax);
        }

        return max;
    }

    private int findMinInputArray(byte[] data, int row, int col, int width, int height, int xr) {
        int min = 256;

        for(int i=-xr; i<=xr; i++) {
            if(i == 0 || (row+i) < 0 || (row+i) >= height) {
                continue;
            }

            int offset = (row+i)*width;
            int dataMin = data[offset+col]&0xff;
            min = Math.min(min, dataMin);
        }

        return min;
    }

    private void calculateGradient(byte[] data, byte[] dil, byte[] ero, ByteProcessor gray, int gradientType) {
        int c;

        switch (gradientType) {
            case BASIC_GRADIENT:
                calculateBasicGradient(data, dil, ero, gray);
                break;
            case EXTERNAL_GRADIENT:
                data = gray.getGray();
                calculateExternalGradient(data, dil);
                break;
            case INTERNAL_GRADIENT:
                data = gray.getGray();
                calculateInternalGradient(data, ero);
                break;
            default:
                throw new CV4JException("Unknown Gradient type, not supported...");
        }
    }

    private void calculateInternalGradient(byte[] data, byte[] ero) {
        for(int i=0; i<data.length; i++) {
            data[i] = (byte)(data[i]&0xff - ero[i] & 0xff);
        }
    }

    private void calculateExternalGradient(byte[] data, byte[] dil) {
        for(int i=0; i<data.length; i++) {
            data[i] = (byte)(dil[i]&0xff - data[i] & 0xff);
        }
    }

    private void calculateBasicGradient(byte[] data, byte[] dil, byte[] ero, ByteProcessor gray) {
        for(int i=0; i<data.length; i++) {
            int c = (dil[i] & 0xff - ero[i] & 0xff);
            data[i] = (byte) ((c > 0) ? 255 : 0);
        }
        gray.putGray(data);
    }
}
