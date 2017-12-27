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
package com.cv4j.core.filters.math;

import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.filters.BaseFilter;
import com.cv4j.core.utils.SafeCasting;
import com.cv4j.image.util.Tools;

import java.util.Random;

/**
 * 随机噪声滤镜
 */
public class GaussianNoiseFilter extends BaseFilter {

	private static final int SIGMA_DEFAULT_VALUE = 25;

	/**
	 * The sigma value.
	 */
	private int sigma;

	/**
	 * The constructor.
	 */
	public GaussianNoiseFilter() {
		sigma = SIGMA_DEFAULT_VALUE;
	}

	@Override
	public ImageProcessor doFilter(ImageProcessor src) {
		final int value0000FF = 0x0000ff;
		final int total = width * height;

		final Random random = new Random();

		for(int i = 0; i < total; i++) {
			int r= R[i] & value0000FF;
			int g= G[i] & value0000FF;
			int b= B[i] & value0000FF;

			// add Gaussian noise
			r += this.sigma * random.nextGaussian();
			g += this.sigma * random.nextGaussian();
			b += this.sigma * random.nextGaussian();

			this.R[i] = SafeCasting.safeIntToByte(Tools.clamp(r));
			this.G[i] = SafeCasting.safeIntToByte(Tools.clamp(g));
			this.B[i] = SafeCasting.safeIntToByte(Tools.clamp(b));
		}

		return src;
	}

	/**
	 * Return the sigma.
	 * @return The sigma.
	 */
	public int getSigma() {
		return sigma;
	}

	/**
	 * Sets the sigma.
	 * @param sigma The new sigma to set.
	 */
	public void setSigma(int sigma) {
		this.sigma = sigma;
	}

}
