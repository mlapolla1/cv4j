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

import android.util.SparseArray;

import com.cv4j.core.binary.functions.ThresholdFunction;
import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.number.IntIntegralImage;
import com.cv4j.core.utils.SafeCasting;

import static android.os.Build.VERSION_CODES.N;

/**
 * The Threshold class
 */
public class Threshold {

    /**
     * Position of sum1.
     */
    private static final int SUM1_POS   = 0;

    /**
     * Position of sum2.
     */
    private static final int SUM2_POS   = 1;

    /**
     * Position of count1.
     */
    private static final int COUNT1_POS = 2;

    /**
     * Position of count2.
     */
    private static final int COUNT2_POS = 3;

    /**
     * The hex value 0000FF.
     */
    private static final int VALUE_0000FF = 0x0000ff;

    /**
     * The max RGB value.
     */
    private static final int MAX_RGB_VALUE = 255;

    /** binary image */
    public static final int METHOD_THRESH_BINARY = 0;
    /** invert binary image */
    public static final int METHOD_THRESH_BINARY_INV = 1;

    /** it is not reasonable method to convert binary image */
    public static final int THRESH_MEANS = 1;
    /** it is popular binary method in OPENCV and MATLAB */
    public static final int THRESH_OTSU = 2;
    /** histogram statistic threshold method*/
    public static final int THRESH_TRIANGLE = 3;
    /**based on 1D mean shift, CV4J custom binary method, sometimes it is very slow...*/
    public static final int THRESH_MEANSHIFT = 4;
    /**based local mean threshold method, CV4J custom binary method, sometimes it is very slow...*/
    public static final int ADAPTIVE_C_MEANS_THRESH = 5;
    /** it is not reasonable method to convert binary image */
    public static final int THRESH_VALUE = -1;

    /**
     * The count position.
     */
    private static final int COUNT_POS = 0;

    /**
     * The bmeans pos.
     */
    private static final int BMEANS_POS = 1;

    private SparseArray<ThresholdFunction> thresholds;

    public Threshold() {
        int dim = 5;
        thresholds = new SparseArray<>(dim);

        thresholds.append(THRESH_MEANS, this::getMeanThreshold);
        thresholds.append(THRESH_OTSU, this::getOTSUThreshold);
        thresholds.append(THRESH_TRIANGLE, this::getTriangleThreshold);
        thresholds.append(THRESH_MEANSHIFT, this::shift);
    }

    /**
     *
     * @param gray - gray image data, ByteProcessor type
     * @param type - binary segmentation method, int
     */
    public void process(ByteProcessor gray, int type) {
        process(gray, type, METHOD_THRESH_BINARY, 0);
    }

    public void adaptiveThresh(ByteProcessor gray, int type, int blockSize, int constant, int method) {
        final int width = gray.getWidth();
        final int height = gray.getHeight();

        // 图像灰度化
        byte[] binaryData = gray.getGray();
        IntIntegralImage grayIntIntegralImage = initGrayIntIntegralImage(binaryData, width, height);

        calculateBinaryDataAdaptiveThresh(grayIntIntegralImage, binaryData, blockSize, constant, width, height);
    }

    private void calculateBinaryDataAdaptiveThresh(IntIntegralImage grayIntIntegralImage, byte[] binaryData, int constant, int blockSize, int width, int height) {
        final int doubleOp = 2;
        final int size = (blockSize * doubleOp + 1)*(blockSize * doubleOp + 1);

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int index = row * width + col;

                // 计算均值
                int sr = grayIntIntegralImage.getBlockSum(col, row, (blockSize * doubleOp + 1), (blockSize * doubleOp + 1));
                int mean = sr / size;
                int pixel = binaryData[index] & VALUE_0000FF;

                // 二值化
                if(pixel > (mean-constant)) {
                    binaryData[row * width + col] = SafeCasting.safeIntToByte(MAX_RGB_VALUE);
                } else {
                    binaryData[row * width + col] = 0;
                }
            }
        }
    }

    /**
     * Pre-calculate integral image.
     * @param binaryData The binary data.
     * @param width      The width.
     * @param height     The height.
     * @return           The grayIntIntegralImage.
     */
    private IntIntegralImage initGrayIntIntegralImage(byte[] binaryData, int width, int height) {
        IntIntegralImage grayIntIntegralImage = new IntIntegralImage();

        grayIntIntegralImage.setImage(binaryData);
        grayIntIntegralImage.process(width, height);

        return grayIntIntegralImage;
    }

    /**
     *
     * @param gray - gray image data, ByteProcessor type
     * @param type - binary segmentation method, int
     * @param thresh - threshold value you are going to use it if type = 0;
     */
    public void process(ByteProcessor gray, int type, int method, int thresh) {
        int thresholdValue = 0;
        ThresholdFunction thresholdFunction = this.thresholds.get(type);

        if (thresholdFunction != null) {
            thresholdValue = thresholdFunction.call(gray);
        } else {
            thresholdValue = thresh;
        }

        byte[] data = gray.getGray();
        byte minValue = (byte) 0;

        for(int i = 0; i < data.length; i++) {
            int c = data[i] & VALUE_0000FF;

            // FIX: Same for if and else -> error?
            if(c <= thresholdValue) {
                data[i] = (method == METHOD_THRESH_BINARY_INV ? SafeCasting.safeIntToByte(MAX_RGB_VALUE) : minValue);
            } else {
                data[i] = (method == METHOD_THRESH_BINARY_INV ? SafeCasting.safeIntToByte(MAX_RGB_VALUE) : minValue);
            }

        }
    }

    private int getMeanThreshold(ByteProcessor gray) {
        int meanThreshold = 0;
        byte[] data = gray.getGray();

        int sum = 0;
        for (byte aData : data) {
            sum += aData & VALUE_0000FF;
        }

        meanThreshold = sum / data.length;

        return meanThreshold;
    }

    private int getOTSUThreshold(ByteProcessor gray) {
        // 获取直方图
        final int dim = 256;
        int[] histogram = new int[dim];
        byte[] data = gray.getGray();

        increaseHistogramFromData(histogram, data);

        double[] variances = calculateVariances(histogram, data, dim);

        // find the minimum within class variance
        int threshold = findMinPosWithinClassVariance(variances);

        // 二值化
        System.out.println("final threshold value : " + threshold);

        return threshold;
    }

    private double[] calculateVariances(int[] histogram, byte[] data, int dim) {
        // 图像二值化 - OTSU 阈值化方法
        double total = data.length;
        double[] variances = new double[dim];

        for(int i = 0; i < variances.length; i++) {
            int[] results = calculateCountAndBmeans(histogram, i);
            int count  = results[COUNT_POS];
            int bMeans = results[BMEANS_POS];
            double bw = count / total;

            double bVariance = calculateBvariance(histogram, i, bMeans, count);

            count = 0;
            double fMeans = 0;

            for(int t = i; t < histogram.length; t++) {
                count += histogram[t];
                fMeans += histogram[t] * t;
            }

            fMeans = (count == 0 ? 0 : (fMeans / count));
            double fw = count / total;
            double fVariance = calculateFvariance(histogram, i, fMeans, count);

            variances[i] = bw * bVariance + fw * fVariance;
        }

        return variances;
    }

    private double calculateFvariance(int[] histogram, int i, double fmeans, int count) {
        int fvariance = 0;
        int power = 2;
        for(int t = i; t < histogram.length; t++) {
            fvariance += (Math.pow((t-fmeans),power) * histogram[t]);
        }

        fvariance = (count == 0) ? 0 : (fvariance / count);

        return fvariance;
    }

    private double calculateBvariance(int[] histogram, int i, int bmeans, int count) {
        int bvariance = 0;
        int power = 2;
        for(int t = 0; t < i; t++) {
            bvariance += (Math.pow((t-bmeans),power) * histogram[t]);
        }

        bvariance = (count == 0) ? 0 : (bvariance / count);

        return bvariance;
    }

    private int[] calculateCountAndBmeans(int[] histogram, int i) {
        int count = 0;
        int bmeans = 0;

        for(int t = 0; t < i; t++) {
            count += histogram[t];
            bmeans += histogram[t] * t;
        }

        bmeans = (count == 0) ? 0 : (bmeans / count);

        final int size = 2;
        int[] results = new int[size];
        results[COUNT_POS] = count;
        results[BMEANS_POS] = bmeans;

        return results;
    }

    private int findMinPosWithinClassVariance(double[] variances) {
        double min = variances[0];
        int threshold = 0;

        for(int i = 1; i < variances.length; i++) {
            if(min > variances[i]) {
                threshold = i;
                min = variances[i];
            }
        }

        return threshold;
    }

    private int getTriangleThreshold(ByteProcessor gray) {
        // 获取直方图
        final int dim = 256;

        int[] histogram = new int[dim];
        byte[] data = gray.getGray();

        increaseHistogramFromData(histogram, data);

        int leftBound = findHistogramLeftBoundIndex(histogram, dim);
        int rightBound = findHistogramRightBoundIndex(histogram, dim);

        int maxHistogramIndex     = findMaxHistogramIndex(histogram, dim);
        int maxHistogramValue     = histogram[maxHistogramIndex];

        // 如果最大值落在靠左侧这样就无法满足三角法求阈值，所以要检测是否最大值是否靠近左侧
        // 如果靠近左侧则通过翻转到右侧位置。
        boolean isFlipped = false;
        if((maxHistogramIndex - leftBound) < (rightBound - maxHistogramIndex)) {
            isFlipped = true;

            flipHistogram(histogram, dim);

            leftBound = dim - 1 - rightBound;
            maxHistogramIndex    = dim - 1 - maxHistogramIndex;
        }

        return (int) calculateThreshValue(histogram, leftBound, maxHistogramValue, maxHistogramIndex, isFlipped);
    }

    private void flipHistogram(int[] histogram, int dim) {
        int i = 0;
        int j = dim - 1;

        while(i < j) {
            // 左右交换
            int temp = histogram[i];
            histogram[i] = histogram[j];
            histogram[j] = temp;

            i++;
            j--;
        }
    }

    private double calculateThreshValue(int[] histogram, int leftBound, int max, int max_ind, boolean isFlipped) {
        // 计算求得阈值
        double thresh = leftBound;
        double a = max;
        double b = leftBound - max_ind;
        double dist = 0;
        double tempDist = 0;

        for(int i = leftBound+1; i <= max_ind; i++) {
            // 计算距离 - 不需要真正计算
            tempDist = a*i + b*histogram[i];
            if(tempDist > dist) {
                dist = tempDist;
                thresh = i;
            }
        }
        thresh--;

        // 对已经得到的阈值T,如果前面已经翻转了，则阈值要用255-T
        if (isFlipped) {
            thresh = N - 1 - thresh;
        }

        return thresh;
    }

    private int findHistogramRightBoundIndex(int[] histogram, int n) {
        int rightBound = -1;

        for (int i = n-1; i > 0; i--) {
            if(histogram[i] > 0) {
                rightBound = i;
                break;
            }
        }

        // 位置再移动一个步长，即为最右侧零位置
        if(rightBound < N-1) {
            rightBound++;
        }

        return rightBound;
    }

    private int findHistogramLeftBoundIndex(int[] histogram, int n) {
        int leftBound = -1;

        for (int i = 0; i < n; i++) {
            if (histogram[i] > 0) {
                leftBound = i;
                break;
            }
        }

        // 位置再移动一个步长，即为最左侧零位置
        if(leftBound > 0) {
            leftBound--;
        }

        return leftBound;
    }

    private int shift(ByteProcessor gray) {
        // find threshold
        int t = 127;
        int[] data = gray.toInt(0);

        while(true) {
            int[] results = calculateSumAndCountFromData(data, t);

            int sum1   = results[SUM1_POS];
            int sum2   = results[SUM2_POS];
            int count1 = results[COUNT1_POS];
            int count2 = results[COUNT2_POS];

            int m1 = sum1 / count1;
            int m2 = sum2 / count2;

            final int ratio = 2;
            int nt = (m1 + m2) / ratio;

            if(t == nt) {
                break;
            } else {
                t = nt;
            }
        }

        return t;
    }

    private int[] calculateSumAndCountFromData(int[] data, int t) {
        final int numValues = 4;
        int[] values = new int[numValues];

        int sum1   = 0;
        int sum2   = 0;
        int count1 = 0;
        int count2 = 0;

        for (int aData : data) {
            if (aData > t) {
                sum1 += aData;
                count1++;
            } else {
                sum2 += aData;
                count2++;
            }
        }

        values[SUM1_POS]   = sum1;
        values[SUM2_POS]   = sum2;
        values[COUNT1_POS] = count1;
        values[COUNT2_POS] = count2;

        return values;
    }

    private void increaseHistogramFromData(int[] histogram, byte[] data) {
        int index = 0;

        for (byte aData : data) {
            index = aData & VALUE_0000FF;
            histogram[index]++;
        }
    }

    private int findMaxHistogramIndex(int[] histogram, int N) {
        int index = 0;
        int max = histogram[index];

        for (int i = 1; i < N; i++) {
            if (histogram[i] > max) {
                index = i;
            }
        }

        return index;
    }
}
