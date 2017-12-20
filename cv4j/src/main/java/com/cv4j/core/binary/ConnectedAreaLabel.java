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
package com.cv4j.core.binary;

import android.annotation.SuppressLint;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.Rect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * It is very easy way to filter some minimum noise block by number of pixel;
 * default settings: <p>
 * - mNumOfPixels = 100 <br>
 * - mFilterNoise = false
 */
public class ConnectedAreaLabel {

    /**
     * default number of pixels
     */
    private static final int DEFAULT_PIXEL_NUM = 100;
    private static final boolean DEFAULT_FILTER_NOISE = false;

    private int mNumOfPixels;
    private boolean mFilterNoise;

    public ConnectedAreaLabel() {
        mNumOfPixels = DEFAULT_PIXEL_NUM;
        mFilterNoise = DEFAULT_FILTER_NOISE;
    }

    /**
     * init object with number of pixels and whether filter noise
     * @param numOfPixels the number of pixels, default value is 100
     * @param filterNoise whether to filter the noise of picture
     */
    public ConnectedAreaLabel(int numOfPixels, Boolean filterNoise) {
        mNumOfPixels = numOfPixels;
        mFilterNoise = filterNoise;
    }

    public void setNoiseArea(int numOfPixels) {
        this.mNumOfPixels = numOfPixels;
    }

    public void setFilterNoise(boolean filterNoise) {
        this.mFilterNoise = filterNoise;
    }

    /**
     * @param binary    - binary image data
     * @param labelMask - label for each pixel point
     * @return int - total labels of image
     */
    public int process(ByteProcessor binary, int[] labelMask) {
        return this._process(binary, labelMask, null, false);
    }

    /**
     * process noise block with labels
     * @param binary - binary image data
     * @param labelMask - label for each pixel point
     * @param rectangles - rectangles area list want to return
     * @param drawBounding - whether draw bounding
     * @return int - total labels of image
     */
    public int process(ByteProcessor binary, int[] labelMask, List<Rect> rectangles,
                       boolean drawBounding) {
        return this._process(binary,labelMask,rectangles,drawBounding);
    }

    private int _process(ByteProcessor binary, int[] labelMask, List<Rect> rectangles,
                         boolean drawBounding) {
        int width   = binary.getWidth();
        int height  = binary.getHeight();
        byte[] data = binary.getGray();

        int yMin = 1;
        int xMin = 1;

        int[] labels = initLabels(width, height);
        int[] pixels = initPixels(width, height);
        int[] twoLabels = new int[2];

        int currLabel = 0;

        for (int row = yMin; row < height; row++) {
            int offset = row * width + xMin;
            for (int col = xMin; col < width; col++) {
                calculateArrays(data, pixels, labels, offset, width, twoLabels, currLabel);
                offset++;
                currLabel++;
            }
        }

        int[] labelSet = createLabelSet(labels, currLabel);

        // 2. second pass
        // aggregation the pixels with same label index
        Map<Integer, List<PixelNode>> aggregationMap = makeAggregationMap(pixels, labelSet, width, height);

        return assignLabels(aggregationMap, labelMask, drawBounding, rectangles);
    }

    private int[] createLabelSet(int[] labels, int currLabel) {
        int[] labelSet = new int[currLabel];

        System.arraycopy(labels, 0, labelSet, 0, currLabel);

        for (int i = 2; i < labelSet.length; i++) {
            int curLabel = labelSet[i];
            int preLabel = labelSet[curLabel];
            while (preLabel != curLabel) {
                curLabel = preLabel;
                preLabel = labelSet[preLabel];
            }
            labelSet[i] = curLabel;
        }

        return labelSet;
    }

    /**
     * Initialize pixels array.
     * @param width
     * @param height
     * @return       The pixels array.
     */
    private int[] initPixels(int width, int height) {
        int[] pixels = new int[width * height];
        Arrays.fill(pixels, -1);

        return pixels;
    }

    /**
     * Initialize labels array.
     * @param width
     * @param height
     * @return       The labels array.
     */
    private int[] initLabels(int width, int height) {
        int[] labels = new int[(width * height) / 2];
        Arrays.fill(labels, -1);

        return labels;
    }

    /**
     * Calculate arrays.
     * @param data
     * @param pixels
     * @param labels
     * @param offset
     * @param width
     * @param twoLabels
     * @param currlabel
     */
    private void calculateArrays(byte[] data, int[] pixels, int[] labels, int offset, int width, int[] twoLabels, int currlabel) {
        int p1 = data[offset] & 0xff;
        int p2 = data[offset - 1] & 0xff; // left
        int p3 = data[offset - width] & 0xff; // upper
        Arrays.fill(twoLabels, -1);
        int ll = -1;
        int ul = -1;

        if (p1 == 255) {
            if (p1 == p2) {
                ll = pixels[offset - 1] < 0 ? -1 : labels[pixels[offset - 1]];
                twoLabels[0] = ll;
            }
            if (p1 == p3) {
                ul = pixels[offset - width] < 0 ? -1 : labels[pixels[offset - width]];
                twoLabels[1] = ul;
            }

            if (ll < 0 && ul < 0) {
                pixels[offset] = currlabel;
                labels[currlabel] = currlabel;
            } else {
                smallestLabel(pixels, labels, twoLabels, offset);
            }
        }
    }

    /**
     * Smallest label.
     * @param pixels
     * @param labels
     * @param twoLabels
     * @param offset
     */
    private void smallestLabel(int[] pixels, int[] labels, int[] twoLabels, int offset) {
        Arrays.sort(twoLabels);
        int smallestLabel = twoLabels[0];
        if (twoLabels[0] < 0) {
            smallestLabel = twoLabels[1];
        }
        pixels[offset] = smallestLabel;

        for (int twoLabel : twoLabels) {
            if (twoLabel < 0) {
                continue;
            }
            int oldSmallestLabel = labels[twoLabel];
            if (oldSmallestLabel > smallestLabel) {
                labels[oldSmallestLabel] = smallestLabel;
                labels[twoLabel] = smallestLabel;
            } else if (oldSmallestLabel < smallestLabel) {
                labels[smallestLabel] = oldSmallestLabel;
            }
        }
    }

    /**
     * Aggregate pixels with same label index.
     * @param pixels
     * @param labelSet
     * @param width
     * @param height
     */
    private Map<Integer, List<PixelNode>> makeAggregationMap(int[] pixels, int[] labelSet, int width, int height) {
        @SuppressLint("UseSparseArrays")
        Map<Integer, List<PixelNode>> aggregationMap = new HashMap<>();

        for (int i = 0; i < height; i++) {
            int offset = i * width;

            for (int j = 0; j < width; j++) {
                int pixelLabel = pixels[offset + j];

                // skip background
                if (pixelLabel < 0) {
                    continue;
                }

                // label each area
                final int label = labelSet[pixelLabel];
                pixels[offset + j] = label;
                List<PixelNode> pixelList = aggregationMap.get(label);

                if (pixelList == null) {
                    pixelList = new ArrayList<>();
                    aggregationMap.put(labelSet[pixelLabel], pixelList);
                }

                PixelNode pixelNode = new PixelNode(i, j, offset+j);
                pixelList.add(pixelNode);
            }
        }

        return aggregationMap;
    }

    /**
     * Assign labels.
     * @param aggregationMap The aggregation map.
     * @param labelMask      The label mask.
     * @param drawBounding   The draw bounding.
     * @param rectangles     The rectangles.
     * @return               The number of labels.
     */
    private int assignLabels(Map<Integer, List<PixelNode>> aggregationMap, int[] labelMask, boolean drawBounding, List<Rect> rectangles) {
        int number = 0;

        Integer[] keys = aggregationMap.keySet().toArray(new Integer[0]);
        Arrays.fill(labelMask, -1);
        List<PixelNode> pixelList;

        for (Integer key : keys) {
            pixelList = aggregationMap.get(key);
            if (mFilterNoise && pixelList.size() < mNumOfPixels) {
                continue;
            }
            // tag each pixel
            for (PixelNode pixelNode : pixelList) {
                labelMask[pixelNode.index] = key;
            }

            // return each label rectangle
            if (drawBounding && rectangles != null) {
                Rect bounding = boundingRect(pixelList);
                bounding.labelIdx = key;
                rectangles.add(bounding);
            }
            number++;
        }

        return number;
    }

    /**
     * Bounding rect.
     * @param pixelList The pixel list.
     * @return          The rect.
     */
    private Rect boundingRect(List<PixelNode> pixelList) {
        int minX = 10000;
        int maxX = 0;

        int minY = 10000;
        int maxY = 0;

        for (PixelNode pixelNode : pixelList) {
            minX = Math.min(pixelNode.col, minX);
            maxX = Math.max(pixelNode.col, maxX);

            minY = Math.min(pixelNode.row, minY);
            maxY = Math.max(pixelNode.row, maxY);
        }

        int dx = maxX - minX;
        int dy = maxY - minY;

        Rect roi = new Rect();

        roi.x      = minX;
        roi.y      = minY;
        roi.width  = dx;
        roi.height = dy;

        return roi;
    }
}
