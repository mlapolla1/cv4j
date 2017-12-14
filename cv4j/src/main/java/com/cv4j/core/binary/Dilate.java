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

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.IntIntegralImage;
import com.cv4j.core.datamodel.Size;

/**
 * dilate, replace min with max value
 *
 */
public class Dilate {

	/**
	 *
	 * @param binary - image data
	 * @param structureElement - structure element for morphology operator
     */
	public void process(ByteProcessor binary, Size structureElement)
	{
		process(binary, structureElement, 1);
	}

	/**
	 *
	 * @param binary
	 * @param structureElement, 3, 5, 7, 9, 11, x y, must be odd
	 * @param iteration - 1 as default, better less than 10, for the sake of time consume
     */
	public void process(ByteProcessor binary, Size structureElement, int iteration){
		binaryUtility bU = new binaryUtility();
		bU.process(binary, structureElement, iteration, 255);
	}
}
