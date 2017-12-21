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
package com.cv4j.core.filters.effect;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.datamodel.number.IntIntegralImage;
import com.cv4j.core.filters.BaseFilter;

/**
 * The mosaic filter.
 */
public class MosaicFilter extends BaseFilter {
	// 窗口半径大小
	private int r=1;

	public MosaicFilter() {
		r = 1;
	}

	public int getRadius() {
		return r;
	}

	public void setRadius(int r) {
		this.r = r;
	}

	@Override
	public ImageProcessor doFilter(ImageProcessor src) {

		int size = (r * 2 + 1) * (r * 2 + 1);
		int tr = 0;
		int tg = 0;
		int tb = 0;
		byte[][] output = new byte[3][R.length];

		IntIntegralImage rii = new IntIntegralImage();
		rii.setImage(R);
		rii.process(width, height);
		IntIntegralImage gii = new IntIntegralImage();
		gii.setImage(G);
		gii.process(width, height);
		IntIntegralImage bii = new IntIntegralImage();
		bii.setImage(B);
		bii.process(width, height);

		int offset = 0;
		for (int row = 0; row < height; row++) {
			offset = row*width;
			setOutputs(width, row, rii, gii, bii, tr, tg, tb, size, offset, output);
		}
		((ColorProcessor) src).putRGB(output[0], output[1], output[2]);
		output = null;
		return src;
	}

	private int setOutputs(int width, int row, IntIntegralImage rii, IntIntegralImage gii, IntIntegralImage bii, int tr, int tg, int tb, int size, int offset, byte[][] output){
		for (int col = 0; col < width; col++) {
			int dy = (row / size);
			int dx = (col / size);
			int ny = dy*size+r;
			int nx = dx*size+r;
			int sr = rii.getBlockSum(nx, ny, (r * 2 + 1), (r * 2 + 1));
			int sg = gii.getBlockSum(nx, ny, (r * 2 + 1), (r * 2 + 1));
			int sb = bii.getBlockSum(nx, ny, (r * 2 + 1), (r * 2 + 1));
			tr = sr / size;
			tg = sg / size;
			tb = sb / size;
			output[0][offset] = (byte)tr;
			output[1][offset] = (byte)tg;
			output[2][offset] = (byte)tb;
			offset++;
		}
	}
}
