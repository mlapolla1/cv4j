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
 * The Line class of DataModel
 */
public class Line {
    
    /**
     * Coordinate x, point one.
     */
    public int x1;
    
    /**
     * Coordinate y, point one.
     */
    public int y1;
    
    /**
     * Coordinate x, point two.
     */
    public int x2;
    
    /**
     * Coordinate y, point two.
     */
    public int y2;

    /**
     * Set line's coordinates.
     * @param xp1 Coordinate x, point one.
     * @param yp1 Coordinate y, point two.
     * @param xp2 Coordinate x, point two.
     * @param yp2 Coordinate y, point two.
     */
    public Line(int xp1, int yp1, int xp2, int yp2) {
        this.x1 = xp1;
        this.y1 = yp1;
        this.x2 = xp2;
        this.y2 = yp2;
    }

    /**
     * Constructor without parameters.
     */
    public Line() {
        this(0, 0, 0, 0);
    }

    /**
     * Return the slope.
     * @return The slope.
     */
    public double getSlope() {
        final double dx = x2 - x1;
        final double dy = y2 - y1;

        if(Double.compare(dx,0) == 0) {
            return Double.NaN;
        }

        return dy / dx;
    }

    /**
     * Return the point one.
     * @return The point one.
     */
    public Point getPoint1() {
        return new Point(x1, y1);
    }

    /**
     * Return the point two.
     * @return The point two.
     */
    public Point getPoint2() {
        return new Point(x2, y2);
    }

}
