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
package com.cv4j.core.filters.gradients;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.image.util.Tools;

/**
 * The gradient filter.
 */
public class GradientFilter {
    
    /**
     * Sobel operator x
     */
	public final static int[][] SOBEL_X = new int[][]{{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
    
    /**
     * Sobel operator x
     */
	public final static int[][] SOBEL_Y = new int[][]{{-1, -2, -1}, {0,  0,  0}, {1,  2,  1}};
	
    /**
     * Direction parameter x
     */
	public final static int X_DIRECTION = 0;
    
    /**
     * Direction parameter y
     */
	public final static int Y_DIRECTION = 2;
    
    /**
     * Direction parameter xy
     */
	public final static int XY_DIRECTION = 4;
	
    /**
     * The direction.
     */
    private int direction;

    /**
     * Is sobel.
     */
	private boolean isSobel;

	private double xred = 0;
    private int xgreen = 0;
    private int xblue = 0;

    private double yred = 0;
    private int ygreen = 0;
    private int yblue = 0;
    /**
     * Constructor initialization of the gradient
     * filter.
     */
	public GradientFilter() {
		direction = XY_DIRECTION;
		isSobel = true;
	}
    
    /**
     * Sets the sobel.
     * @param sobel The sobel.
     */
	public void setSoble(boolean sobel) {
		this.isSobel = sobel;
	}

    /**
     * Returns the direction.
     * @return The direction.
     */
	public int getDirection() {
		return direction;
	}

    /**
     * Sets the direction.
     * @param direction The new direction.
     */
	public void setDirection(int direction) {
		this.direction = direction;
	}

	private float[] subRowColAlgorithm(byte[] intput, int width, int height, int row, int col) {
		float xred = 0;
		float yred = 0;

		for(int subrow = -1; subrow <= 1; subrow++) {
			for(int subcol = -1; subcol <= 1; subcol++) {
				int newRow = row + subrow;
				int newCol = col + subcol;

				if(newRow < 0 || newRow >= height) {
					newRow = row;
				}

				if(newCol < 0 || newCol >= width) {
					newCol = col;
				}

				int index2 = newRow * width + newCol;
				int pv = intput[index2] & 0xff;

				xred += (SOBEL_X[subrow + 1][subcol + 1] * pv);
				yred += (SOBEL_Y[subrow + 1][subcol + 1] * pv);
			}
		}

		return new float[] {xred, yred};
	}

    /**
     * Returns a gradient.
     * @param  src The byte processor source.
     * @return     The gradient.
     */
	public int[] gradient(ByteProcessor src){
		int width  = src.getWidth();
        int height = src.getHeight();

		int[] outPixels = new int[width * height];

		int index;

        int newRow;
        int newCol;

        float min = 255;
        float max = 0;

        byte[] intput = src.getGray();

        for(int row = 0; row < height; row++) {
        	for(int col = 0; col < width; col++) {
				index = (row * width) + col;

				subRowColAlgorithm(intput, width, height, row, col);
        		
                double mred = Math.sqrt(xred * xred + yred * yred);
                max = Math.max(Tools.clamp((int)mred) , max);
                min = Math.min(Tools.clamp((int)mred) , min);

                setOutPixels(direction, index, mred, outPixels);


				// cleanup for next loop
                xred = 0;
                yred = 0;
                
        	}
        }
		return outPixels;
	}

    private void setOutPixels(int direction, int index, double mred, int[] outPixels){
        switch (direction) {
            case X_DIRECTION:
                outPixels[index] = Tools.clamp((int) yred);
                break;
            case Y_DIRECTION:
                outPixels[index] = Tools.clamp((int) xred);
                break;
            default:
                // XY_DIRECTION
                outPixels[index] = Tools.clamp((int) mred);
                break;

        }
    }

}
