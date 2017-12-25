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
package com.cv4j.core.datamodel;

import com.cv4j.core.datamodel.image.ImageData;
import com.cv4j.core.datamodel.image.ImageProcessor;

/**
 * The ByteProcessor class of DataModel
 */
public class ByteProcessor implements ImageProcessor {

    /**
     * The value of 0000FF.
     */
    private static final int VALUE_0000FF = 0x0000ff;

    /**
     * The width.
     */
    private int width;

    /**
     * The height.
     */
    private int height;

    /**
     * The data.
     */
    private byte[] GRAY;

    /**
     * The image data.
     */
    private ImageData image = null;

    public ByteProcessor(int width, int height) {
        this.width = width;
        this.height = height;
        this.GRAY = new byte[width*height];
    }
    
    public ByteProcessor(byte[] data, int width, int height) {
        this.width = width;
        this.height = height;
        this.GRAY = data;

        // setup hist
        final int histSize = 256;
        int[] hist = new int[histSize];
        for (byte aData : data) {
            hist[aData & VALUE_0000FF]++;
        }
    }

    protected void setCallBack(ImageData data) {
        this.image = data;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getChannels() {
        return 1;
    }

    public void getPixel(int row, int col, byte[] rgb) {
        int index = row*width + col;
        if(rgb != null && rgb.length == 1) {
            rgb[0] = GRAY[index];
        }
    }

    public byte[] getGray() {
        return GRAY;
    }

    public void putGray(byte[] gray) {
        System.arraycopy(gray, 0, GRAY, 0, gray.length);
    }

    public int[] histogram() {
        return new int[0];
    }

    public int[] getPixels() {
        final int size = this.width * this.height;
        final int valueFF000000 = 0xff000000;
        final int value16 = 16;
        final int value8  = 8;

        int[] pixels = new int[size];

        for (int i = 0; i < size; i++){
            pixels[i] = valueFF000000 | ((GRAY[i] & VALUE_0000FF) << value16)
                                      | ((GRAY[i] & VALUE_0000FF) << value8)
                                      | (GRAY[i] & VALUE_0000FF);
        }

        return pixels;
    }
    public ImageData getImage() {
        return this.image;
    }

    @Override
    public float[] toFloat(int index) {
        float[] data = new float[GRAY.length];
        for(int i = 0; i < data.length; i++){
            data[i] = GRAY[i] & VALUE_0000FF;
        }
        return data;
    }

    @Override
    public int[] toInt(int index) {
        int[] data = new int[GRAY.length];
        for(int i=0; i<data.length; i++){
            data[i] = GRAY[i] & VALUE_0000FF;
        }
        return data;
    }

    @Override
    public byte[] toByte(int index) {
        return GRAY;
    }

}
