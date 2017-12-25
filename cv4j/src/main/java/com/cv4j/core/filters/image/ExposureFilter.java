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
package com.cv4j.core.filters.image;

import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.filters.BaseFilter;

/**
 * The Exposure filter.
 */
public class ExposureFilter extends BaseFilter {

    @Override
    public ImageProcessor doFilter(ImageProcessor src) {

        int size = R.length;
        for(int i=0; i<size; i++) {
            R[i] = (byte)~R[i];
            G[i] = (byte)~G[i];
            B[i] = (byte)~B[i];
        }
        return src;
    }
}
