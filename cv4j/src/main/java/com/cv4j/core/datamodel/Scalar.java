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
package com.cv4j.core.datamodel;

/**
 * The Scalar class of DataModel
 */
public class Scalar {
	
	/**
	 * The red color value of RGBA.
	 */
	public int red;

	/**
	 * The green color value of RGBA.
	 */
	public int green;

	/**
	 * The blue color value of RGBA.
	 */
	public int blue;

	/**
	 * The alpha value of RGBA.
	 */
	public int alpha;

	/**
	 * Constructor with RGB values.
	 * @param  re  The red value.
	 * @param  g The green value.
	 * @param  b  The blue value.
	 */
	public Scalar(int r, int g, int b) {
		this.red = r;
		this.green = g;
		this.blue = b;
		this.alpha = 255;
	}

	/**
	 * Constructor with RGBA initializations.
	 */
	public Scalar() {
		red = 0;
		green = 0;
		blue = 0;
		alpha = 255;
	}
	
	/**
	 * Return a scalar with a given ARGB
	 * @param  alpha The alpha value.
	 * @param  r     The red value.
	 * @param  g     The green value.
	 * @param  b 	 The blue value.
	 * @return       The scalar RGB.
	 */
	public static Scalar argb(int a, int r, int g, int b){
		return new Scalar(r, g, b);
	}
	
	/**
	 * Return a scalar with a given RGB.
	 * @param  r     The red value.
	 * @param  g     The green value.
	 * @param  b     The blue value.
	 * @return       The scalar RGB.
	 */
	public static Scalar rgb(int r, int g, int b){
		return new Scalar(r, g, b);
	}
	
}
