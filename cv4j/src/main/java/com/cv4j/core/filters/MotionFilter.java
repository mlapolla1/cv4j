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
 * The motion filter.
 */
public class MotionFilter extends BaseFilter  {

	private float distance = 10;// default;
	private final float onePI = (float)Math.PI;
	private float angle = 0.0f;
	private final float zoom = 0.4f;

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

	private void motionBlurIterationPixels(int iteration, int row, int col, int cx, int cy, float sinAngle, float coseAngle) {
		// iterate the source pixels according to distance
		for(int i=0; i < iteration; i++) {
			int newX = col;
			int newY = row;

			// calculate the operator source pixel
			if(distance > 0) {
				newY = (int) Math.floor(newY + (i * sinAngle));
				newX = (int) Math.floor(newX + (i * coseAngle));
			}
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

			// blur the pixels, here
			count++;
			int idx = newY*width+newX;
			tr += R[idx] & 0xff;
			tg += G[idx] & 0xff;
			tb += B[idx] & 0xff;
		}
	}
	
	@Override
	public ImageProcessor doFilter(ImageProcessor src){

		int total = width * height;
		byte[][] output = new byte[3][total];

        int index = 0;
        int cx = width / 2;
        int cy = height / 2;
        
        // calculate the triangle geometry value
        float degree180 = 180.0f;
        float sinAngle = (float)Math.sin(angle/degree180 * onePI);
        float coseAngle = (float)Math.cos(angle/degree180 * onePI);
        float sinAngle = (float) Math.sin(angle/180.0f * onePI);
        float cosAngle = (float) Math.cos(angle/180.0f * onePI);
        
        // calculate the distance, same as box blur
        float imageRadius = (float) Math.sqrt(cx*cx + cy*cy);
        float maxDistance = distance + imageRadius * zoom;

        for(int row = 0; row < height; row++) {
        	int ta = 0;
        	int tr = 0;
        	int tg = 0;
        	int tb = 0;
        	for(int col = 0; col < width; col++) {
        		int count = 0;
				int newX;
        		int newY;
        		
        		motionBlurIterationPixels((int) maxDistance, row, col, cx, cy, sinAngle, cosAngle);
        		
        		// fill the destination pixel with final RGB value
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
				index++;
        	}
        }
		((ColorProcessor) src).putRGB(R, G, B);
		output = null;
		return src;
	}

}