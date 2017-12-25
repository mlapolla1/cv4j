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
 * The Point class of DataModel
 */
public class Point {
	
	/**
	 * Coordinate x.
	 */
    public int x = 0;

	/**
	 * Coordinate y.
	 */    
    public int y = 0;

	/**
	 * Constructor without parameters.
	 */    
    public Point() {
    	this(0, 0);
    }

	/**
	 * Constructor with inizialization of the coordinates.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 */    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

}
