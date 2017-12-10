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
     * Coordinate x point one
     */
    public int x1 = 0;
    
    /**
     * Coordinate y point one
     */
    public int y1 = 0;
    
    /**
     * Coordinate x point two
     */
    public int x2 = 0;
    
    /**
     * Coordinate y point two
     */
    public int y2 = 0;

    /**
     * Set line's coordinates.
     * @param Coordinate x point one, Coordinate y point one, Coordinate x point two, Coordinate y point two
     */
    public Line(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /**
     * Constructor without parameters.
     */
    public Line() {}

    /**
     * Return the slope.
     * @return the slople
     */
    public double getSlope() {
        double dy = y2 - y1;
        double dx = x2 - x1;
        if(dx == 0) {
            return Double.NaN;
        }
        return (dy/dx);
    }

    /**
     * Return point one
     * @return point one
     */
    public Point getPoint1() {
        return new Point(x1, y1);
    }

    /**
     * Return point two
     * @return point two
     */
    public Point getPoint2() {
        return new Point(x2, y2);
    }

}
