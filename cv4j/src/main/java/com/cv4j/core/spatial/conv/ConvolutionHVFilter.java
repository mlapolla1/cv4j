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
package com.cv4j.core.spatial.conv;

import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.filters.BaseFilter;
import com.cv4j.core.utils.SafeCasting;

/**
 * can iteration this operation multiple, make it more blur
 */
public class ConvolutionHVFilter extends BaseFilter {

    /**
     * The value of 0000FF.
     */
    private static final int VALUE_0000FF = 0x0000ff;

	@Override
	public ImageProcessor doFilter(ImageProcessor src) {
        final int total = this.width * this.height;

        byte[][] output = new byte[3][total];

		firstPhaseFilter(output);

		// Y 方向
        secondPhaseFilter(output);

		return src;
	}

    /**
     * First phase of filter.
     * @param output The output.
     */
	private void firstPhaseFilter(byte[][] output) {
        for(int row=0; row<height; row++) {
            int offset = row*width;
            for(int col=1; col<width-1; col++) {
                int sr = 0;
                int sg = 0;
                int sb = 0;

                for(int j=-1; j<=1; j++) {
                    int coffset = j+col;
                    sr += R[offset+coffset] & VALUE_0000FF;
                    sg += G[offset+coffset] & VALUE_0000FF;
                    sb += B[offset+coffset] & VALUE_0000FF;
                }

                int r = sr / 3;
                int g = sg / 3;
                int b = sb / 3;

                output[0][offset+col] = SafeCasting.safeIntToByte(r);
                output[1][offset+col] = SafeCasting.safeIntToByte(g);
                output[2][offset+col] = SafeCasting.safeIntToByte(b);
            }
        }
    }

    private void secondPhaseFilter(byte[][] output) {
        for(int col = 0; col < this.width; col++) {

            for(int row = 1; row < this.height-1; row++) {
                int sr = 0;
                int sg = 0;
                int sb = 0;

                for(int j = -1; j <= 1; j++) {
                    final int rowOffset = j + row;
                    final int outputOffset = (rowOffset * width) + col;

                    sr += output[0][outputOffset] & VALUE_0000FF;
                    sg += output[1][outputOffset] & VALUE_0000FF;
                    sb += output[2][outputOffset] & VALUE_0000FF;
                }

                int r = sr / 3;
                int g = sg / 3;
                int b = sb / 3;

                final int offset = (row * width) + col;
                this.R[offset] = SafeCasting.safeIntToByte(r);
                this.G[offset] = SafeCasting.safeIntToByte(g);
                this.B[offset] = SafeCasting.safeIntToByte(b);
            }
        }
    }

}
