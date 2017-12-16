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
package com.cv4j.core.hist;

import com.cv4j.core.datamodel.ByteProcessor;

/**
 * ProjectionHist.
 * The histogram projection.
 */
public class ProjectionHist {
    
    /**
     * x direction
     */
    public final static int X_DIRECTION = 1;
    
    /**
     * y direction
     */
    public final static int Y_DIRECTION = 2;

    /**
     *
     * @param src - binary image
     * @param direction - X or Y direction
     * @param bins - number of bins
     * @param output
     */
    public void projection(ByteProcessor src, int direction, int bins, double[] output) {

        if (src == null) return;

        // calculate Y Projection
        int width = src.getWidth();
        int height = src.getHeight();
        byte[] data = src.getGray();
        float xstep = width / 4.0f;
        float ystep = height / 5.0f;
        int index = 0;
        if(direction == X_DIRECTION) {
            xstep = width / bins;
            for (float x = 0; x < width; x += xstep) {
                if ((xstep + x) - width > 1)
                    continue;
                output[index] = getWeightBlackNumber(data, width, height, x, 0, xstep, height);
                index++;
            }
        } else {
            // calculate X Projection
            ystep = height / bins;
            for (float y = 0; y < height; y += ystep) {
                if ((y + ystep) - height > 1) continue;
                output[index] = getWeightBlackNumber(data, width, height, 0, y, width, ystep);
                index++;
            }
        }
    }

    /**
     * Returns the weight black number
     * @param  data   The data
     * @param  width  The width
     * @param  height The height
     * @param  x      The x
     * @param  y      The y
     * @param  xstep  The x step
     * @param  ystep  The y step
     * @return        The weight black number
     */
    private float getWeightBlackNumber(byte[] data, float width, float height, float x, float y, float xstep, float ystep) {
        float weightNum = 0;

        // 整数部分
        int nx = (int)Math.floor(x);
        int ny = (int)Math.floor(y);

        // 小数部分
        float fx = x - nx;
        float fy = y - ny;

        // 宽度与高度
        float w = x + xstep;
        float h = y + ystep;
        w = setValueMaximumIfGreater(w, width);
        h = setValueMaximumIfGreater(h, height);


        // 宽高整数部分
        int nw = (int)Math.floor(w);
        int nh = (int)Math.floor(h);

        // 小数部分
        float fw = w - nw;
        float fh = h - nh;

        // 统计黑色像素个数
        int ww = (int)width;
        int weight = numberOfBlackPixels(data, ny, nh, nx, nw, ww);

        // 计算小数部分黑色像素权重加和
        float w1 = calculateW1(data, width, fx, nx, ny, nh, ww);
        float w2 = calculateW2(data, height, fy, ny, nx, nw, ww);
        float w3 = calculateW3(data, width, fw, nw, ny, nh, ww);
        float w4 = calculateW4(data, height, fh, nh, nx, nw, ww);

        weightNum = (weight - w1 - w2 + w3 + w4);
        if(weightNum < 0) {
            weightNum = 0;
        }
        return weightNum;
    }

    /**
     * Calculation of the constant w1.
     * @param data
     * @param width
     * @param fx
     * @param nx
     * @param ny
     * @param nh
     * @param ww
     * @return       The constant w1.
     */
    private float calculateW1(byte[] data, float width, float fx, int nx, int ny, int nh, int ww) {
        float w1 = 0;

        if(fx > 0) {
            int col = nx+1;
            if(col > width - 1) {
                col = col - 1;
            }
            float count = 0;
            for(int row = ny; row < nh; row++) {
                int c = data[row*ww+col]&0xff;
                if(c == 0){
                    count++;
                }
            }

            w1 = count*fx;
        }

        return w1;
    }

    /**
     * Calculation of the constant w2.
     * @param data
     * @param height
     * @param fy
     * @param ny
     * @param nx
     * @param nw
     * @param ww
     * @return       The constant w2.
     */
    private float calculateW2(byte[] data, float height, float fy, int ny, int nx, int nw, int ww) {
        float w2 = 0;

        if(fy > 0) {
            int row = ny+1;
            if(row > height - 1) {
                row = row - 1;
            }

            float count = 0;
            for(int col = nx; col < nw; col++) {
                int c = data[row*ww+col]&0xff;
                if(c == 0){
                    count++;
                }
            }

            w2 = count*fy;
        }

        return w2;
    }

    /**
     * Calculation of the constant w3.
     * @param data
     * @param width
     * @param fw
     * @param nw
     * @param ny
     * @param nh
     * @param ww
     * @return      The constant w3.
     */
    private float calculateW3(byte[] data, float width, float fw, int nw, int ny, int nh, int ww) {
        float w3 = 0;

        if(fw > 0) {
            int col = nw + 1;
            if(col > width - 1) {
                col = col - 1;
            }

            float count = 0;
            for(int row = ny; row < nh; row++) {
                int c = data[row*ww+col] & 0xff;
                if(c == 0) {
                    count++;
                }
            }

            w3 = count*fw;
        }

        return w3;
    }

    /**
     * Calculation of the constant w4.
     * @param data
     * @param height
     * @param fh
     * @param nh
     * @param nx
     * @param nw
     * @param ww
     * @return       The constant w4.
     */
    private float calculateW4(byte[] data, float height, float fh, int nh, int nx, int nw, int ww) {
        float w4 = 0;

        if(fh > 0) {
            int row = nh+1;
            if(row > height - 1) {
                row = row - 1;
            }
            float count = 0;
            for(int col = nx; col < nw; col++) {
                int c = data[row*ww+col] & 0xff;
                if(c == 0) {
                    count++;
                }
            }

            w4 = count*fh;
        }

        return w4;
    }

    /**
     * Count the number of black pixels.
     * @param data
     * @param ny
     * @param nh
     * @param nx
     * @param nw
     * @param ww
     * @return     The number of black pixels.
     */
    private int numberOfBlackPixels(byte[] data, int ny, int nh, int nx, int nw, int ww) {
        int weight = 0;

        for(int row = ny; row < nh; row++) {
            for(int col = nx; col < nw; col++) {
                int c = data[row*ww+col] & 0xff;
                if(c == 0) {
                    weight++;
                }
            }
        }

        return weight;
    }

    /**
     * Set the maximum value if the value is greater.
     * @param value    The value.
     * @param maxValue The maximum value.
     * @return         The value corrected.
     */
    private float setValueMaximumIfGreater(float value, float maxValue) {
        if (value < maxValue) {
            value = maxValue-1;
        }

        return value;
    }
}
