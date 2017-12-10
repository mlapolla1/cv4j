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
 * The Size class of DataModel
 */
public class Size {
    
    /**
     * Columns
     */
    public int cols;
    
    /**
     * Rows
     */
    public int rows;

    /**
     * Set the size when cols are equals to rows (ex. square)
     * @param num The size of the rows and columns. 
     */    
    public Size(int num) {
        this.cols = num;
        this.rows = num;
    }

    /**
     * Set the size when cols are not equals to rows (ex. rectangle)
     * @param width  The number of rows
     * @param height The number of columns
     */  
    public Size(int width, int height) {
        this.cols = width;
        this.rows = height;
    }
}
