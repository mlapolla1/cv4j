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
package com.cv4j.core.binary.Contour;

import com.cv4j.core.binary.GeoMoments;
import com.cv4j.core.binary.PixelNode;
import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.MeasureData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * The Analysis of Contour
 */
public class ContourAnalysis {

    public void process(ByteProcessor binary, int[] labels, List<MeasureData> measureDataList) {
        int width  = binary.getWidth();
        int height = binary.getHeight();

        Map<Integer, List<PixelNode>> aggregationMap = makeAggregationMap(labels, width, height);
        assignLabels(aggregationMap, measureDataList);
    }

    private void assignLabels(Map<Integer, List<PixelNode>> aggregationMap, List<MeasureData> measureDataList) {
        Integer[] keys = aggregationMap.keySet().toArray(new Integer[0]);
        List<PixelNode> pixelList = null;
        GeoMoments moments = new GeoMoments();

        for(Integer key : keys) {
            pixelList = aggregationMap.get(key);
            measureDataList.add(moments.calculate(pixelList));
        }
    }

    private Map<Integer, List<PixelNode>> makeAggregationMap(int[] labels, int width, int height) {
        Map<Integer, List<PixelNode>> aggregationMap = new HashMap<>();
        int offset = 0;

        for (int i = 0; i < height; i++) {
            offset = i * width;

            for (int j = 0; j < width; j++) {
                int pixelLabel = labels[offset+j];

                // skip background
                if(pixelLabel < 0) {
                    continue;
                }

                // label each area
                List<PixelNode> pixelList = aggregationMap.get(pixelLabel);
                if(pixelList == null) {
                    pixelList = new ArrayList<>();
                    aggregationMap.put(pixelLabel, pixelList);
                }

                PixelNode pixelNode = new PixelNode(i, j, offset+j);
                pixelList.add(pixelNode);
            }
        }

        return aggregationMap;
    }
}
