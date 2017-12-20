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
import com.cv4j.exception.CV4JException;

/**
 * The ColorProcessor class of DataModel
 */
public class ColorProcessor implements ImageProcessor {

    /**
     * The hex value 0000FF.
     */
    private static final int VALUE_0000FF = 0x0000ff;

    /**
     * The integer value 16.
     */
    private static final int VALUE_16 = 16;

    /**
     * The integer value 8.
     */
    private static final int VALUE_8  = 8;

    /**
     * The number of channels.
     */
    private static final int NUM_CHANNELS = 3;

    /**
     * Index of the red channel.
     */
    private static final int RED_CHANNEL_INDEX   = 0;

    /**
     * Index of the green channel.
     */
    private static final int GREEN_CHANNEL_INDEX = 1;

    /**
     * Index of the blue channel
     */
    private static final int BLUE_CHANNEL_INDEX  = 2;

    /**
     * The R values from RGB.
     */
    private byte[] R;

    /**
     * The G values from RGB.
     */
    private byte[] G;

    /**
     * The B values from RGB.
     */
    private byte[] B;

    /**
     * The image data.
     */
    private ImageData image;

    /**
     * The image's width.
     */
    private int width;

    /**
     * The image's height.
     */
    private int height;

    /**
     * The ColorProcessor constructor.
     * @param w The width.
     * @param h The height.
     */
    public ColorProcessor(int w, int h) {
        this(null, w, h);
    }

    /**
     * The color processor constructor.
     * @param pixels The pixels.
     * @param w      The width.
     * @param h      The height.
     */
    public ColorProcessor(int[] pixels, int w, int h) {
        final int size = w * h;

        this.width  = w;
        this.height = h;

        this.R = new byte[size];
        this.G = new byte[size];
        this.B = new byte[size];

        if (pixels != null) {
            backFillData(pixels);
        }
    }

    private void backFillData(int[] input) {
        final int valueFF0000 = 0xff0000;
        final int value00FF00 = 0x00ff00;

        for(int i=0; i < input.length; i++) {
            int c = input[i];

            int red   = (c & valueFF0000) >> VALUE_16;
            int green = (c & value00FF00) >> VALUE_8;
            int blue  = (c & VALUE_0000FF);

            this.R[i] = (byte) red;
            this.G[i] = (byte) green;
            this.B[i] = (byte) blue;
        }
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
        return NUM_CHANNELS;
    }

    /**
     * @return The red array.
     */
    public byte[] getRed() {
        return this.R;
    }

    /**
     * @return The green array.
     */
    public byte[] getGreen() {
        return this.G;
    }

    /**
     * @return The blue array.
     */
    public byte[] getBlue() {
        return B;
    }

    @Override
    public void getPixel(int row, int col, byte[] rgb) {
        final int index = (row * this.width) + col;

        if(rgb != null && rgb.length == NUM_CHANNELS) {
            rgb[RED_CHANNEL_INDEX]   = this.R[index];
            rgb[GREEN_CHANNEL_INDEX] = this.G[index];
            rgb[BLUE_CHANNEL_INDEX]  = this.B[index];
        }
    }

    /**
     * Copy RGB values.
     * @param red   The red array to copy.
     * @param green The green array to copy.
     * @param blue  The blue array to copy.
     */
    public void putRGB(byte[] red, byte[] green, byte[] blue) {
        System.arraycopy(red, 0, R, 0, red.length);
        System.arraycopy(green, 0, G, 0, green.length);
        System.arraycopy(blue, 0, B, 0, blue.length);
    }

    /**
     * @return The pixels array.
     */
    public int[] getPixels() {
        final int valueFF000000 = 0xff000000;

        int[] pixels = new int[width * height];
        for (int i=0; i < pixels.length; i++){
            pixels[i] = valueFF000000 | ((R[i] & VALUE_0000FF) << VALUE_16)
                                      | ((G[i] & VALUE_0000FF)<< VALUE_8)
                                      |   B[i] & VALUE_0000FF;
        }

        return pixels;
    }

    /**
     * Set the call back.
     * @param data The data image.
     */
    protected void setCallBack(ImageData data) {
        this.image = data;
    }

    /**
     * @return The image.
     */
    public ImageData getImage() {
        return this.image;
    }

    @Override
    public float[] toFloat(int index) {
        float[] data;

        switch (index) {
            case RED_CHANNEL_INDEX:
                data = colorDataToFloat(this.R);
                break;

            case GREEN_CHANNEL_INDEX:
                data = colorDataToFloat(this.G);
                break;

            case BLUE_CHANNEL_INDEX:
                data = colorDataToFloat(this.B);
                break;

            default:
                throw new CV4JException("Invalid argument...");
        }

        return data;
    }

    @Override
    public int[] toInt(int index) {
        int[] data;

        switch (index) {
            case RED_CHANNEL_INDEX:
                data = colorDataToInt(this.R);
                break;

            case GREEN_CHANNEL_INDEX:
                data = colorDataToInt(this.G);
                break;

            case BLUE_CHANNEL_INDEX:
                data = colorDataToInt(this.B);
                break;

            default:
                throw new CV4JException("Invalid argument...");
        }

        return data;
    }

    @Override
    public byte[] toByte(int index) {
        byte[] data;

        switch (index) {
            case RED_CHANNEL_INDEX:
                data = this.R;
                break;

            case GREEN_CHANNEL_INDEX:
                data = this.G;
                break;

            case BLUE_CHANNEL_INDEX:
                data = this.B;
                break;

            default:
                throw new CV4JException("invalid argument...");
        }

        return data;
    }

    /**
     * Convert a color data to a float color data.
     * @param colorData The color data (R, G, B)
     * @return          The float color data.
     */
    private float[] colorDataToFloat(byte[] colorData) {
        float[] floatColorData = new float[colorData.length];

        for(int i = 0; i < floatColorData.length; i++){
            floatColorData[i] = colorData[i] & VALUE_0000FF;
        }

        return floatColorData;
    }

    /**
     * Convert a color data to an int color data.
     * @param colorData The color data (R, G, B)
     * @return          The int color data.
     */
    private int[] colorDataToInt(byte[] colorData) {
        int[] intColorData = new int[colorData.length];

        for(int i = 0; i < intColorData.length; i++){
            intColorData[i] = colorData[i] & VALUE_0000FF;
        }

        return intColorData;
    }
}
