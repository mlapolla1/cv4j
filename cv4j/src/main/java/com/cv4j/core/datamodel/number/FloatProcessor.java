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
package com.cv4j.core.datamodel.number;

import com.cv4j.core.datamodel.image.ImageData;
import com.cv4j.core.datamodel.image.ImageProcessor;

/**
 * The FloatProcessor class of DataModel
 */
public class FloatProcessor implements ImageProcessor {

    /**
     * The image width.
     */
    private int width;

    /**
     * The image height.
     */
    private int height;

    /**
     * The data.
     */
    private float[] GRAY;

    /**
     * The image data.
     */
    private ImageData image = null;

    public FloatProcessor(float[] data, int w, int h) {
        this.GRAY   = data;
        this.width  = w;
        this.height = h;
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

    @Override
    public void getPixel(int row, int col, byte[] rgb) {

    }

    @Override
    public int[] getPixels() {
        return new int[0];
    }

    /**
     * Returns the pixel from an RGB value, with a given
     * row and a given column.
     * @param row The row.
     * @param col The column.
     * @param rgb The RGB value.
     */
    public void getPixel(int row, int col, float[] rgb) {
        final int index = (row * this.width) + col;

        assert rgb != null;

        if(rgb.length == 1) {
            rgb[0] = this.GRAY[index];
        }
    }

    /**
     * Returns the gray data.
     * @return The data.
     */
    public float[] getGray() {
        return GRAY;
    }

    /**
     * Puts the gray data.
     * @param gray The gray data.
     */
    public void putGray(float[] gray) {
        System.arraycopy(gray, 0, GRAY, 0, gray.length);
    }

    /**
     * Add an array to the gray data.
     * @param fa The array to add.
     */
    public void addArray(float[] fa) {
        for(int i = 0; i < fa.length; i++) {
            GRAY[i] += fa[i];
        }
    }

    @Override
    public ImageData getImage() {
        return this.image;
    }

    @Override
    public float[] toFloat(int index) {
        return GRAY;
    }

    @Override
    public int[] toInt(int index) {
        int[] data = new int[GRAY.length];
        for(int i=0; i<data.length; i++){
            data[i] = (int)GRAY[i];
        }
        return data;
    }

    @Override
    public byte[] toByte(int index) {
        throw new IllegalStateException("Invalid data type, not support this type!!!");
    }

}
