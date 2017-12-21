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
import com.cv4j.core.filters.BaseFilter;
import com.cv4j.image.util.Tools;

/**
 * The motion filter.
 */
public class MotionFilter extends BaseFilter {

	/**
	 * Index position of count;
	 */
	private static final int COUNT_POS = 0;

	/**
	 * Index position of tr;
	 */
	private static final int TR_POS = 1;

	/**
	 * Index position of tg;
	 */
	private static final int TG_POS = 2;

	/**
	 * Index position of tb;
	 */
	private static final int TB_POS = 3;

	private float distance = 10;// default;
	private float angle = 0.0f;

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	private int[] motionBlurIterationPixels(int iteration, int row, int col, int cx, int cy, float sinAngle, float cosAngle) {
		// iterate the source pixels according to distance
		final float zoom = 0.4f;

		int count = 0;
		int tr = 0;
		int tg = 0;
		int tb = 0;

		for(int i=0; i < iteration; i++) {
			int newX = col;
			int newY = row;

			// calculate the operator source pixel
			if(distance > 0) {
				newY = (int) Math.floor(newY + (i * sinAngle));
				newX = (int) Math.floor(newX + (i * cosAngle));
			}

			int idx = getIdx(newX, newY, width, height, cx, cy, zoom, i, iteration);

			// blur the pixels, here
			count++;

			tr += R[idx] & 0xff;
			tg += G[idx] & 0xff;
			tb += B[idx] & 0xff;
		}

		int[] results = setResults(tr, tg, tb, count);

		return results;
	}

	private int[] setResults(int tr, int rg, int tb, int count){
		final int numResults = 4;
		int[] results = new int[numResults];
		results[COUNT_POS] = count;
		results[TR_POS] = tr;
		results[TG_POS] = tg;
		results[TB_POS] = tb;
	}

	private int getIdx(int newX, int newY, int width, int height, int cx, int cy, float zoom, int i, int iteration){
		float f = (float) (i / iteration);
		if (newX < 0 || newX >= width) {
			break;
		}
		if (newY < 0 || newY >= height) {
			break;
		}

		// scale the pixels
		float scale = 1-zoom*f;
		float m11 = cx - (cx * scale);
		float m22 = cy - (cy * scale);
		newY = (int)(newY * scale + m22);
		newX = (int)(newX * scale + m11);
		return (newY * width) + newX;
	}
	
	@Override
	public ImageProcessor doFilter(ImageProcessor src){
		final float onePI = (float)Math.PI;
		final float zoom = 0.4f;
		int total = width * height;
		byte[][] output = new byte[3][total];

        int index = 0;
        int cx = width / 2;
        int cy = height / 2;
        
        // calculate the triangle geometry value
		float degree180 = 180.0f;
        float sinAngle = (float)Math.sin(angle/degree180 * onePI);
        float cosAngle = (float)Math.cos(angle/degree180 * onePI);
        
        // calculate the distance, same as box blur
        float imageRadius = (float) Math.sqrt(cx*cx + cy*cy);
        float maxDistance = distance + imageRadius * zoom;
    	int tr = 0;
    	int tg = 0;
    	int tb = 0;
        setOuts(width, height, cx, cy, output, index, onePI, zoom, degree180, sinAngle, cosAngle, imageRadius, maxDistance);

		((ColorProcessor) src).putRGB(R, G, B);

		return src;
	}

	private void setOuts(int tr, int tg, int tb, int int width, int height, int cx, int cy, byte[][] output, int index, float onePI, float zoom, float degree180, float sinAngle, float cosAngle, float imageRadius, float maxDistance){
        for(int row = 0; row < height; row++) {
        	for(int col = 0; col < width; col++) {
        		int count = 0;
				int newX;
        		int newY;
        		
        		int[] mbip = motionBlurIterationPixels((int) maxDistance, row, col, cx, cy, sinAngle, cosAngle);
        		count += mbip[COUNT_POS];
				tr += mbip[TR_POS];
				tg += mbip[TG_POS];
				tb += mbip[TB_POS];

        		// fill the destination pixel with final RGB value
				setOutRGB(output, index, tr, tg, tb, count);
				index++;
        	}
        }
	}

	private void setOutRGB(byte[][] output, int index, int tr, int tg, int tb, int count){
    	if (count == 0) {
			output[0][index] = R[index];
			output[1][index] = G[index];
			output[2][index] = B[index];
		} else {
			tr = Tools.clamp(tr / count);
			tg = Tools.clamp(tg / count);
			tb = Tools.clamp(tb/count);

			output[0][index] = (byte) tr;
			output[1][index] = (byte) tg;
			output[2][index] = (byte) tb;
		}
	}

}