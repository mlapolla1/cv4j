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
package com.cv4j.core.pixels;

import com.cv4j.core.datamodel.Scalar;

/**
 * The cluster center.
 */
public class ClusterCenter {

	protected double x;
	protected double y;
	protected Scalar color;
	protected int cIndex;
	protected int numOfPoints;

	public ClusterCenter(int x, int y, int red, int green, int blue) {
		this.x = x;
		this.y = y;
		this.color = new Scalar(red, green, blue);
		this.cIndex = 0;
		this.numOfPoints = 0;
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public Scalar getPixelColor() {
		return color;
	}

	public void setPixelColor(Scalar pixelColor) {
		this.color = pixelColor;
	}

	public int getcIndex() {
		return cIndex;
	}

	public void setcIndex(int cIndex) {
		this.cIndex = cIndex;
	}
	
	public int getNumOfPoints() {
		return numOfPoints;
	}
	
	public void addPoints()
	{
		numOfPoints++;
	}

	public void setNumOfPoints(int numOfPoints) {
		this.numOfPoints = numOfPoints;
	}
}
