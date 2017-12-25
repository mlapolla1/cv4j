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
 * The Vec3i class of DataModel
 */
public class Vec3i {
	
    /**
     * Coordinate x
     */  
    public int x = 0;
    
    /**
     * Coordinate y
     */  
    public int y = 0;
    
    /**
     * Coordinate z
     */  
    public int z = 0;



    /**
     * Default constructor.
     */
    public Vec3i() {
        this(0, 0, 0);
    }

    /**
     * Constructor for Vec3i with given values.
     * @param x The x value.
     * @param y The y value.
     * @param z The z value.
     */
    public Vec3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


}
