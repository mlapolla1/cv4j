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

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.image.util.Tools;

/**
 * The nature filter.
 */
public class NatureFilter extends BaseFilter {
	
	/**
	 * Constant of atmosphere style
	 */
	public static final int ATMOSPHERE_STYLE = 1;
	
	/**
	 * Constant of burn style
	 */
	public static final int BURN_STYLE = 2;
	
	/**
	 * Constant of fog style
	 */
	public static final int FOG_STYLE = 3;
	
	/**
	 * Constant of freeze style
	 */
	public static final int FREEZE_STYLE = 4;
	
	/**
	 * Constant of lava style
	 */
	public static final int LAVA_STYLE = 5;
	
	/**
	 * Constant of metal style
	 */
	public static final int METAL_STYLE = 6;
	
	/**
	 * Constant of ocean style
	 */
	public static final int OCEAN_STYLE = 7;
	
	/**
	 * Constant of water style
	 */
	public static final int WATER_STYLE = 8;
	
	/**
	 * The style.
	 */
	private int style;

	/**
	 * The fog lookup.
	 */
	private int[] fogLookUp;

	/**
	 * Constructor without parameters.
	 */
	public NatureFilter() {
		this(ATMOSPHERE_STYLE);
	}

	/**
	 * Constructor with a given style
	 * @param  style The style
	 */
	public NatureFilter(int style) {
		this.style = style;
		buildFogLookupTable();
	}
	
	/**
	 * Build the fog lookup table.
	 */
	private void buildFogLookupTable() {
		fogLookUp = new int[256];
		int fogLimit = 40;
		int fogLookUpLimit = 127;
		for(int i=0; i<fogLookUp.length; i++)
		{
			if(i > fogLookUpLimit)
			{
				fogLookUp[i] = i - fogLimit;
				if(fogLookUp[i] < fogLookUpLimit)
				{
					fogLookUp[i] = fogLookUpLimit;
				}
			}
			else
			{
				fogLookUp[i] = i + fogLimit;
				if(fogLookUp[i] > fogLookUpLimit)
				{
					fogLookUp[i] = fogLookUpLimit;
				}
			}
		}

	}

	/**
	 * Return the style.
	 * @return The style.
	 */
	public int getStyle() {
		return style;
	}

	/**
	 * Set the style.
	 * @param style The new style.
	 */
	public void setStyle(int style) {
		this.style = style;
	}

	@Override
	public ImageProcessor doFilter(ImageProcessor src){

		int total = width*height;
		int index = 0;
		int ta = 0;
		int tr = 0;
		int tg = 0;
		int tb = 0;
		int index0 = 0;
		int index1 = 1;
		int index2 = 2;
		for (int i=0; i<total; i++) {
			tr = R[i] & 0xff;
			tg = G[i] & 0xff;
			tb = B[i] & 0xff;
			int[] onePixel = processOnePixel(ta, tr, tg, tb);

			R[i] = (byte)onePixel[index0];
			G[i] = (byte)onePixel[index1];
			B[i] = (byte)onePixel[index2];
		}
		((ColorProcessor) src).putRGB(R, G, B);
		return src;
	}

	private int[] processOnePixel(int ta, int tr, int tg, int tb) {
		int pixelLength = 4;
		int[] pixel = new int[pixelLength];
		pixel[0] = ta;
		int processFactor = 3;
		int gray = (tr + tg + tb) / processFactor;
		int index1 = 1;
		int index2 = 2;
		int index3 = 3;
		switch (style) {
			case ATMOSPHERE_STYLE:
				int atmosphereFactor = 2;
				pixel[index1] = (tg + tb) / atmosphereFactor;
				pixel[index2] = (tr + tb) / atmosphereFactor;
				pixel[index3] = (tg + tr) / atmosphereFactor;

				break;

			case BURN_STYLE:
				int burnFactor = 3;
				pixel[index1] = Tools.clamp(gray * burnFactor);
				pixel[index2] = gray;
				pixel[index3] = gray / burnFactor;

				break;

			case FOG_STYLE:
				pixel[index1] = fogLookUp[tr];
				pixel[index2] = fogLookUp[tg];
				pixel[index3] = fogLookUp[tb];

				break;

			case FREEZE_STYLE:
				freezeFactor = 1.5;
				pixel[index1] = Tools.clamp((int)Math.abs((tr - tg - tb) * freezeFactor));
				pixel[index2] = Tools.clamp((int)Math.abs((tg - tb - pixel[index1]) * freezeFactor));
				pixel[index3] = Tools.clamp((int)Math.abs((tb - pixel[index1] - pixel[index2]) * freezeFactor));

				break;

			case LAVA_STYLE:
				int lavaFactor = lavaFactor;
				pixel[index1] = gray;
				pixel[index2] = Math.abs(tb - lavaFactor);
				pixel[index3] = Math.abs(tb - lavaFactor);

				break;

			case METAL_STYLE:
				int metalFactor = 64;
				float r = Math.abs(tr - metalFactor);
				float g = Math.abs(r - metalFactor);
				float b = Math.abs(g - metalFactor);
				int grayR = 222;
				int grayG = 707;
				int grayB = 71;
				int grayFactor = 1000;
				float grayFloat = ((grayR * r + grayG * g + grayB * b) / grayFactor);
				int r1 = 70;
				int r2 = 128;
				int rgbFactor = 100;
				float rgbFactor2 = 100f;
				r = grayFloat + r1;
				r = r + (((r - r2) * rgbFactor) / rgbFactor2);
				int g1 = 65;
				int g2 = 128;
				g = grayFloat + g1;
				g = g + (((g - g2) * rgbFactor) / rgbFactor2);
				int b1 = 75;
				int b2 = 128;
				b = grayFloat + b1;
				b = b + (((b - b2) * rgbFactor) / rgbFactor2);
				pixel[index1] = Tools.clamp((int)r);
				pixel[index2] = Tools.clamp((int)g);
				pixel[index3] = Tools.clamp((int)b);

				break;

			case OCEAN_STYLE:
				int oceanFactor = 3;
				pixel[index1] = Tools.clamp(gray / oceanFactor);
				pixel[index2] = gray;
				pixel[index3] = Tools.clamp(gray * oceanFactor);

				break;

			case WATER_STYLE:
				pixel[index1] = Tools.clamp(gray - tg - tb);
				pixel[v2] = Tools.clamp(gray - pixel[index1] - tb);
				pixel[index3] = Tools.clamp(gray - pixel[index1] - pixel[index2]);

				break;

			default:
				int defaultFactor = 2;
				pixel[index1] = (tg + tb) / defaultFactor;
				pixel[index2] = (tr + tb) / defaultFactor;
				pixel[index3] = (tg + tr) / defaultFactor;
				break;
		}

		return pixel;
	}
}
