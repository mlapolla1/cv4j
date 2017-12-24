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
package com.cv4j.core.binary;

import java.io.Serializable;

/**
 * The PixelNode class
 */
public class PixelNode implements Comparable<PixelNode>, Serializable {

    /**
	 * Index number
	 */	
	public int index;
	/**
	 * Row number
	 */	
	public int row;
	/**
	 * Column number
	 */	
	public int col;

	/**
	 * Default constructor.
	 */
	public PixelNode() {
		this(0, 0, 0);
	}

	/**
	 * Constructor for PixelNode.
	 * @param c The column.
	 * @param r The row.
	 * @param i The index.
	 */
	public PixelNode(int c, int r, int i) {
		this.col   = c;
		this.row   = r;
		this.index = i;
	}
	
	@Override
	public int compareTo(PixelNode p) {
		if (index > p.index) {
			return 1;
		} else if (index < p.index) {
			return -1;
		} else {
			return 0;
		}
	}

	public boolean equals(Object o) {
	    // self check
	    if (this == o){
	        return true;
	    }
	    // null check
	    if (o == null){
	        return false;
	    }
	    // type check and cast
	    if (getClass() != o.getClass()){
	        return false;
	    }
	    PixelNode PixelNodeParam = (PixelNode) o;
	    // field comparison
	    return (this.index == PixelNodeParam.index) && 
	    	   (this.row == PixelNodeParam.row)     &&
	    	   (this.col == PixelNodeParam.col); 
	}
}
