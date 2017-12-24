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

import android.graphics.Color;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.filters.BaseFilter;
import com.cv4j.core.utils.SafeCasting;
import com.cv4j.image.util.Tools;

/**
 * Vignette - a photograph whose edges shade off gradually
 * 
 */
public class VignetteFilter extends BaseFilter {
		
	private int vignetteWidth;
	private int fade;
	private int vignetteColor;
	
	public VignetteFilter() {
		vignetteWidth = 50;
		fade = 35;
		vignetteColor = Color.BLACK;
	}
	
	@Override
	public ImageProcessor doFilter(ImageProcessor src){

		int total = width*height;
		byte[][] output = new byte[3][total];

		int index = 0;
		for(int row=0; row<height; row++) {
			for(int col=0; col<width; col++) {

				int dX = Math.min(col, width - col);
				int dY = Math.min(row, height - row);
				index = row * width + col;
				int tr = R[index] & 0xff;
				int tg = G[index] & 0xff;
				int tb = B[index] & 0xff;

				checkSetOuts(dX, dY, output, index, tr, tg, tb);
			}
		}
        
        ((ColorProcessor) src).putRGB(output[0], output[1], output[2]);

        return src;
	}

	private void checkSetOuts(int dX, int dY, byte[][] output, int index, int tr, int tg, int tb){
		if ((dY <= vignetteWidth) & (dX <= vignetteWidth))
		{
			double k = 1 - (double)(Math.min(dY, dX) - vignetteWidth + fade) / (double)fade;
			setOuts(output, index, tr, tg, tb, k);

			/// Continue without loop, error?
			// continue;
		}

		if ((dX < (vignetteWidth - fade)) | (dY < (vignetteWidth - fade)))
		{
			setOutsColor(output, index);
		}
		else
		{
			if ((dX < vignetteWidth)&(dY>vignetteWidth))
			{
				double k = 1 - (double)(dX - vignetteWidth + fade) / (double)fade;
				setOuts(output, index, tr, tg, tb, k);
			}
			else
			{
				if ((dY < vignetteWidth)&(dX > vignetteWidth))
				{
					double k = 1 - (double)(dY - vignetteWidth + fade) / (double)fade;
					setOuts(output, index, tr, tg, tb, k);
				}
				else
				{
					setOutsTRGB(output, tr, tg, tb, index);
				}
			}
		}
	}

	private void setOutsTRGB(byte[][] output, int tr, int tg, int tb, int index){
		output[0][index] = SafeCasting.safeIntToByte(tr);
		output[1][index] = SafeCasting.safeIntToByte(tg);
		output[2][index] = SafeCasting.safeIntToByte(tb);
	}

	private void setOutsColor(byte[][] output, int index){
		output[0][index] = SafeCasting.safeIntToByte(Color.red(vignetteColor));
		output[1][index] = SafeCasting.safeIntToByte(Color.green(vignetteColor));
		output[2][index] = SafeCasting.safeIntToByte(Color.blue(vignetteColor));
	}
	private void setOuts(byte[][] output, int index, int tr, int tg, int tb, double k){
		int[] rgb = superpositionColor(tr, tg, tb, k);
		output[0][index] = SafeCasting.safeIntToByte(rgb[0]);
		output[1][index] = SafeCasting.safeIntToByte(rgb[1]);
		output[2][index] = SafeCasting.safeIntToByte(rgb[2]);
	}
	
	public int[] superpositionColor(int red, int green, int blue, double k) {
		red   = SafeCasting.safeDoubleToInt(Color.red(vignetteColor)   * k + red *(1.0-k));
		green = SafeCasting.safeDoubleToInt(Color.green(vignetteColor) * k + green *(1.0-k));
		blue  = SafeCasting.safeDoubleToInt(Color.blue(vignetteColor)  * k + blue *(1.0-k));

		return new int[]{ Tools.clamp(red), Tools.clamp(green), Tools.clamp(blue) };
	}
	
	public int getVignetteWidth() {
		return vignetteWidth;
	}

	public void setVignetteWidth(int vignetteWidth) {
		this.vignetteWidth = vignetteWidth;
	}

	public int getFade() {
		return fade;
	}

	public void setFade(int fade) {
		this.fade = fade;
	}
	
	public int getVignetteColor() {
		return vignetteColor;
	}

	public void setVignetteColor(int vignetteColor) {
		this.vignetteColor = vignetteColor;
	}
	
}
