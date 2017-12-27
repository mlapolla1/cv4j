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

import android.util.SparseArray;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.filters.BaseFilter;
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
	 * The index one.
	 */
	private static final int INDEX1 = 1;

	/**
	 * The index two.
	 */
	private static final int INDEX2 = 2;

	/**
	 * The index three.
	 */
	private static final int INDEX3 = 3;

	/**
	 * The process factor.
	 */
	private static final int PROCESS_FACTOR = 3;
	
	/**
	 * The style.
	 */
	private int style;

	/**
	 * The fog lookup.
	 */
	private int[] fogLookUp;

	private SparseArray<NatureFilterFunction> styles;

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
		int dim = 8;
		styles = new SparseArray<>(dim);
		styles.append(ATMOSPHERE_STYLE, this::calculateAtmosphereStyle);
		styles.append(BURN_STYLE, this::calculateBurnStyle);
		styles.append(FOG_STYLE, this::calculateFogStyle);
		styles.append(FREEZE_STYLE, this::calculateFreezeStyle);
		styles.append(LAVA_STYLE, this::calculateLavaStyle);
		styles.append(METAL_STYLE, this::calculateMetalStyle);
		styles.append(OCEAN_STYLE, this::calculateOceanStyle);
		styles.append(WATER_STYLE, this::calculateWaterStyle);
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

		NatureFilterFunction natureFilterFunction = styles.get(style);

		if (natureFilterFunction != null) {
			natureFilterFunction.calculateStyle(pixel, tr, tg, tb);
		} else {
			calculateDefaultStyle(pixel, tr, tg, tb);
		}

		return pixel;
	}

	private void calculateDefaultStyle(int[] pixel, int tr, int tg, int tb) {
		int defaultFactor = 2;
		pixel[INDEX1] = (tg + tb) / defaultFactor;
		pixel[INDEX2] = (tr + tb) / defaultFactor;
		pixel[INDEX3] = (tg + tr) / defaultFactor;
	}

	private void calculateWaterStyle(int[] pixel, int tr, int tg, int tb) {
		int gray = (tr + tg + tb) / PROCESS_FACTOR;
		pixel[INDEX1] = Tools.clamp(gray - tg - tb);
		pixel[INDEX2] = Tools.clamp(gray - pixel[INDEX1] - tb);
		pixel[INDEX3] = Tools.clamp(gray - pixel[INDEX1] - pixel[INDEX2]);
	}

	private void calculateOceanStyle(int[] pixel, int tr, int tg, int tb) {
		int oceanFactor = 3;
		int gray = (tr + tb + tg) / PROCESS_FACTOR;

		pixel[INDEX1] = Tools.clamp(gray / oceanFactor);
		pixel[INDEX2] = gray;
		pixel[INDEX3] = Tools.clamp(gray * oceanFactor);
	}

	private void calculateMetalStyle(int[] pixel, int tr, int tg, int tb) {
		System.out.println(tg+" "+tb);
		int metalFactor = 64;
		float r = Math.abs(tr - metalFactor);
		float g = Math.abs(r - metalFactor);
		float b = Math.abs(g - metalFactor);	
		float grayFloat = setGrayFloat(r, g, b);
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
		setPixel(pixel, r, g, b);
	}

	private void setPixel(int[] pixel, float r, float g, float b){
		pixel[INDEX1] = Tools.clamp((int) r);
		pixel[INDEX2] = Tools.clamp((int) g);
		pixel[INDEX3] = Tools.clamp((int) b);
	}

	private float setGrayFloat(float r, float g, float b){
		int grayR = 222;
		int grayG = 707;
		int grayB = 71;
		int grayFactor = 1000;

		return ((grayR * r + grayG * g + grayB * b) / grayFactor);
	}

	private void calculateLavaStyle(int[] pixel, int tr, int tg, int tb) {
		int lavaFactor = 128;
		int gray = (tr + tb + tg) / PROCESS_FACTOR;

		pixel[INDEX1] = gray;
		pixel[INDEX2] = Math.abs(tb - lavaFactor);
		pixel[INDEX3] = Math.abs(tb - lavaFactor);
	}

	private void calculateFreezeStyle(int[] pixel, int tr, int tg, int tb) {
		float freezeFactor = 1.5f;
		pixel[INDEX1] = Tools.clamp((int)Math.abs((tr - tg - tb) * freezeFactor));
		pixel[INDEX2] = Tools.clamp((int)Math.abs((tg - tb - pixel[INDEX1]) * freezeFactor));
		pixel[INDEX3] = Tools.clamp((int)Math.abs((tb - pixel[INDEX1] - pixel[INDEX2]) * freezeFactor));
	}

	private void calculateFogStyle(int[] pixel, int tr, int tg, int tb) {
		pixel[INDEX1] = fogLookUp[tr];
		pixel[INDEX2] = fogLookUp[tg];
		pixel[INDEX3] = fogLookUp[tb];
	}

	private void calculateBurnStyle(int[] pixel, int tr, int tg, int tb) {
		int burnFactor = 3;
		int gray = (tr + tg + tb) / PROCESS_FACTOR;

		pixel[INDEX1] = Tools.clamp(gray * burnFactor);
		pixel[INDEX2] = gray;
		pixel[INDEX3] = gray / burnFactor;

	}

	private void calculateAtmosphereStyle(int[] pixel, int tr, int tg, int tb) {
		int atmosphereFactor = 2;
		pixel[INDEX1] = (tg + tb) / atmosphereFactor;
		pixel[INDEX2] = (tr + tb) / atmosphereFactor;
		pixel[INDEX3] = (tg + tr) / atmosphereFactor;
	}


	/**
	 * The nature filter functional interface.
	 * @author Michele Lapolla on 12/16/17.
	 * @see NatureFilter
	 */
	@FunctionalInterface
	private interface NatureFilterFunction {
		void calculateStyle(int[] pixel, int tr, int tg, int tb);
	}
}
