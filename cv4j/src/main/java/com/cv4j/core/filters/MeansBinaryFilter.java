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
package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.utils.SafeCasting;

/**
 * The means binary filter.
 */
public class MeansBinaryFilter implements CommonFilter {

	public ImageProcessor filter(ImageProcessor src) {
	    final int value0000FF = 0x0000ff;
	    final int maxRgbValue = 255;

        if(src instanceof ColorProcessor) {
            src.getImage().convert2Gray();
            src = src.getImage().getProcessor();
        }

        int width  = src.getWidth();
        int height = src.getHeight();

        byte[] GRAY = ((ByteProcessor)src).getGray();

        float graySum = 0;
        int total = width * height;
        for(int i = 0; i < total; i++){
            graySum += GRAY[i] & value0000FF;
        }
        int means = SafeCasting.safeFloatToInt(graySum / total);
        
        // dithering
        for(int i=0; i<total; i++) {
            int c = ((GRAY[i] & value0000FF) >= means) ? maxRgbValue : 0;
            GRAY[i] = SafeCasting.safeIntToByte(c);
        }

        return src;
	}
}
