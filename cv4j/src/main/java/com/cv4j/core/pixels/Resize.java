/*
 * Copyright (c) 2017 - present, CV4J Contributors.
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
package com.cv4j.core.pixels;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.utils.SafeCasting;
import com.cv4j.exception.CV4JException;
import com.cv4j.image.util.Tools;

/**
 * The class resize.
 */
public class Resize {

	/**
	 * The nearest iterpolate.
	 */
	public final static int NEAREST_INTEPOLATE = 1;
	
	/**
	 * The biline interpolate.
	 */
	public final static int BILINE_INTEPOLATE = 2;
	
	private float xrate;
	private float yrate;
	
	/**
	 * Constructor with a rate.
	 * @param  rate The rate
	 */
	public Resize(float rate)
	{
		xrate = rate;
		yrate = rate;
	}
	
	/**
	 * Constructor with a x and y rate.
	 * @param  xrate The x rate
	 * @param  yrate The y rate
	 */
	public Resize(float xrate, float yrate)
	{
		this.xrate = xrate;
		this.yrate = yrate;
	}

	/**
	 * Resizes a image processor with a given type.
	 * @param  processor The image processor.
	 * @param  type      The type.
	 * @return           The new image processor.
	 */
	public ImageProcessor resize(ImageProcessor processor, int type) {
		if(type == NEAREST_INTEPOLATE) {
			return nearest(processor);
		} else if(type == BILINE_INTEPOLATE) {
			return biline(processor);
		} else {
			throw new CV4JException("Unsupported resize type...");
		}
	}

	private ImageProcessor nearest(ImageProcessor processor) {
		int width = processor.getWidth();
		int height = processor.getHeight();
		int w = SafeCasting.safeFloatToInt(width * xrate);
		int h = SafeCasting.safeFloatToInt(height * yrate);
		int channels = processor.getChannels();
		ImageProcessor dst = (channels == 3) ? new ColorProcessor(w, h) : new ByteProcessor(w, h);
		int index = 0;
		for (int row = 0; row < h; row++) {
			calculateNearest(row, height, width, w, index, channels, processor, dst);
		}
		return dst;
	}

    /**
     * Calculate Nearest
     * @param row
     * @param height
     * @param width
     * @param w
     * @param index
     * @param channels
     * @param processor
     * @param dst
     */
	private void calculateNearest(int row, int height, int width, int w, int index, int channels, ImageProcessor processor,ImageProcessor dst) {
        int srcRow = Math.round(row*yrate);
        if(srcRow >=height) {
            srcRow = height - 1;
        }
        for (int col = 0; col < w; col++) {
            int srcCol = Math.round((col*xrate));
            if(srcCol >= width) {
                srcCol = width - 1;
            }
            int index2 = row * w + col;
            index = srcRow * width + srcCol;
            for(int i=0; i<channels; i++) {
                dst.toByte(index2)[i] = processor.toByte(index)[i];
            }
        }
    }
	
	/**
	 * Biline
	 * @param  processor The image processor.
	 * @return           The new image processor.
	 */
	public ImageProcessor biline(ImageProcessor processor) {
		int width = processor.getWidth();
		int height = processor.getHeight();
		int w = SafeCasting.safeFloatToInt(width * xrate);
		int h = SafeCasting.safeFloatToInt(height * yrate);
		int channels = processor.getChannels();
		ImageProcessor dst = (channels == 3) ? new ColorProcessor(w, h) : new ByteProcessor(w, h);
		int index = 0;
		for(int row=0; row<h; row++) {
			calculateBline(row, height, width, w, index, channels, processor, dst);
		}
		return dst;
	}

    /**
     * Calculate Bline
     * @param row
     * @param height
     * @param width
     * @param w
     * @param index
     * @param channels
     * @param processor
     * @param dst
     */
	private void calculateBline(int row, int height, int width, int w, int index, int channels, ImageProcessor processor,ImageProcessor dst) {
        double srcRow = row*yrate;
        // 获取整数部分坐标 row Index
        double j = Math.floor(srcRow);
        // 获取行的小数部分坐标
        double t = srcRow - j;
        for(int col=0; col<w; col++) {
            double srcCol = col*xrate;
            // 获取整数部分坐标 column Index
            double k = Math.floor(srcCol);
            // 获取列的小数部分坐标
            double u = srcCol - k;
            int[] p1 = getPixel(j, k, width, height, processor);
            int[] p2 = getPixel(j, k+1, width, height, processor);
            int[] p3 = getPixel(j+1, k, width, height, processor);
            int[] p4 = getPixel(j+1, k+1, width, height, processor);
            double a = (1.0d-t)*(1.0d-u);
            double b = (1.0d-t)*u;
            double c = (t)*(1.0d-u);
            double d = t*u;
            index = row * w + col;
            for(int i=0; i<channels; i++) {
                int pv = SafeCasting.safeDoubleToInt(p1[i] * a + p2[i] * b + p3[i] * c + p4[i] * d);
                dst.toByte(index)[i] = SafeCasting.safeIntToByte(Tools.clamp(pv));
            }
        }
    }
	
    private int[] getPixel(double j, double k, int width, int height, ImageProcessor processor) {

    	int row = SafeCasting.safeDoubleToInt(j);
    	int col = SafeCasting.safeDoubleToInt(k);

    	if(row >= height) {
    		row = height - 1;
    	}

    	if(row < 0) {
    		row = 0;
    	}

		if(col >= width) {
			col = width - 1;
		}

		if(col < 0) {
			col = 0;
		}

    	final int index    = row * width + col;
    	final int channels = processor.getChannels();

    	int[] rgb = new int[channels];

    	for(int i=0; i<channels; i++) {
    		rgb[i] = processor.toByte(index)[i]&0xff; 		
    	}

		return rgb;	
	}
    

}
