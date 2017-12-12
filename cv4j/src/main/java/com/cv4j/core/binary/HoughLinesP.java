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
import com.cv4j.core.datamodel.Line;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * The HoughLinesP class
 */
public class HoughLinesP {

	private static final int DEGREE_180 = 180;
	
	private double[] coslut;
	private double[] sinlut;
	private int accSize;
	private int width;
	private int height;
	private int accThreshold;

	public HoughLinesP() {
		setupCosLUT();
		setupSinLUT();
	}

	/**
	 * Normalize the values in acc.
	 * @param acc  
	 * @param rmax 
	 */
	private void normalizeAccValues(int[] acc, int rmax) {
		int value;
		int value_pos;
		int max_value = findAccMaxValue(acc, rmax);

		for (int r = 0; r < rmax; r++) {
			for (int theta = 0; theta < DEGREE_180; theta++) {
				value_pos = (r * DEGREE_180) + theta;
				value = (int) (((double) acc[value_pos] / (double) max_value) * 255.0);
				acc[value_pos] = 0xff000000 | (value << 16 | value << 8 | value);
			}
		}
	}

	/**
	 * Find the max value in acc.
	 * @param  acc  
	 * @param  rmax 
	 * @return      The max value.
	 */
	private int findAccMaxValue(int[] acc, int rmax) {
		int max_value = 0;
		int value_pos;

		for (int r = 0; r < rmax; r++) {
			for (int theta = 0; theta < DEGREE_180; theta++) {
				value_pos = (r * DEGREE_180) + theta;

				if (acc[value_pos] > max_value) {
					max_value = acc[value_pos];
				}
			}
		}

		return max_value;
	}

	private List<Line> createListOfLines(int[] results, int accSize) {
		List<Line> lines = new ArrayList<>();
		
		for (int i = accSize-1; i >= 0; i--) {
			Line line = drawPolarLine(results[i * 3], results[i * 3 + 1], results[i * 3 + 2]);
			lines.add(line);
		}

		return lines;
	}

	/**
	 * Calculate the slope.
	 * @param lines  The lines
	 * @param karray 
	 * @param labels 
	 */
	public void calculateSlope(List<Line> lines, double[] karray, int[] labels) {
		int index = 0;

		for(Line oneLine : lines) {
			labels[index] = index;
			karray[index] = oneLine.getSlope();

			index++;
		}
	}

	/**
	 * Merge
	 * @param karray 
	 * @param labels [description]
	 */
	public void merge(double[] karray, int[] labels) {
		double distance;

		for(int i = 0; i < karray.length-1; i++) {
			for(int j = i+1; j < karray.length; j++) {
				distance = Math.abs(karray[i] - karray[j]);
				if(distance < 0.1) {
					labels[i] = i;
					labels[j] = i;
				}
			}
		}
	}

	/**
	 * Get a Map from labels and lines.
	 * @param  labels The labels.
	 * @param  lines  The lines.
	 * @return        The map of labels and lines.
	 */
	public Map<Integer, Line> getLinesMap(int[] labels, int[] lines) {
		Map<Integer, Line> lineMap = new HashMap<>();
		
		for(int i = 0; i < labels.length; i++) {
			lineMap.put(labels[i], lines.get(i));
		}

		return lineMap;
	}

	/**
	 * 1. 初始化霍夫变换空间
	 * 2. 将图像的2D空间转换到霍夫空间,每个像素坐标都要转换到霍夫极坐标的对应强度值
	 * 3. 找出霍夫极坐标空间的最大强度值
	 * 4. 根据最大强度值归一化,范围为0 ~ 255
	 * 5. 根据输入前accSize值,画出前accSize个信号最强的直线
	 *
	 * @return
	 */
	public void process(ByteProcessor binary, int accSize, int minGap, int minAcc, List<Line> lines) {
		this.width        = binary.getWidth();
		this.height       = binary.getHeight();
		this.accSize      = accSize; // 前K=accSize个累积值
		this.accThreshold = minAcc;// 最小累积值
		
		int rmax  = (int) Math.sqrt(width * width + height * height);
		int[] acc = new int[rmax * DEGREE_180]; // 0 ~ 180角度范围
		int r;

		byte[] input = binary.getGray();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if ((input[y * width + x] & 0xff) == 255) {
					for (int theta = 0; theta < DEGREE_180; theta++) {
						r = (int) (x * coslut[theta] + y * sinlut[theta]); // 计算出极坐标
						if ((r > 0) && (r <= rmax)) {
							acc[r * DEGREE_180 + theta] = acc[r * DEGREE_180 + theta] + 1; // 在斜率范围内的点，极坐标相同
						}
					}
				}
			}
		}

		normalizeAccValues(acc, rmax);

		// 发现前N个信号最强的点，转换为平面坐标，得到直线
		findMaxima(acc, lines);

		// filter by min gap
		// TODO: zhigaang
	}

	private void findMaxima(int[] acc, List<Line> lines) {
		// 初始化
		int   rmax    = (int) Math.sqrt(width * width + height * height);
		int[] results = new int[accSize * 3];
		
		// 开始寻找前N个最强信号点，记录极坐标坐标位置
		for (int r = 0; r < rmax; r++) {
			for (int theta = 0; theta < DEGREE_180; theta++) {
				int value = (acc[r * DEGREE_180 + theta] & 0xff);

				// if its higher than lowest value add it and then sort
				if (value > results[(accSize - 1) * 3]) {

					// add to bottom of array
					results[(accSize - 1) * 3]     = value;
					results[(accSize - 1) * 3 + 1] = r;
					results[(accSize - 1) * 3 + 2] = theta;

					// shift up until its in right place
					int i = (accSize - 2) * 3;
					while ((i >= 0) && (results[i + 3] > results[i])) {
						for (int j = 0; j < 3; j++) {
							int temp = results[i + j];
							results[i + j] = results[i + 3 + j];
							results[i + 3 + j] = temp;
						}
						i = i - 3;
						if (i < 0)
							break;
					}
				}
			}
		}


		// 绘制像素坐标
		System.out.println("Total " + accSize + " matches:");
		List<Line> tempLines = createListOfLines(results, accSize);

		// 计算斜率
		double[] karray = new double[tempLines.size()];
		int[]    labels = new int[karray.length];
		calculateSlope(tempLines, karray, labels);

		// 合并
	
		merge(karray, labels);

		Map<Integer, Line> lineMap = getLinesMap(labels, tempLines);
		lines.addAll(lineMap.values());
	}

	// 变换极坐标为平面坐标，并绘制
	private Line drawPolarLine(int value, int r, int theta) {
		int x1 = 100000;
		int y1 = 0;
		int x2 = 0;
		int y2 = 0;

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int temp = (int) (x * coslut[theta] + y * sinlut[theta]);
				if ((temp - r) == 0) {// 变换坐标并绘制
					if(x > x2) {
						x2 = x;
						y2 = y;
					}
					if(x < x1) {
						x1 = x;
						y1 = y;
					}
				}
			}
		}
		/*System.out.println(" [ x1 = " + x1 + " y1 = " + y1 + " ] ");
		System.out.println(" [ x2 = " + x2 + " y2 = " + y2 + " ] ");
		System.out.println();*/
			return new Line(x1, y1, x2, y2);
	}

	private double[] setupCosLUT () {
		coslut = new double[DEGREE_180];
		for (int theta = 0; theta < DEGREE_180; theta++) {
			coslut[theta] = Math.cos((theta * Math.PI) / (double) DEGREE_180);
		}
		return coslut;
	}

	private double[] setupSinLUT () {
		sinlut = new double[DEGREE_180];
		for (int theta = 0; theta < DEGREE_180; theta++) {
			sinlut[theta] = Math.sin((theta * Math.PI) / (double) DEGREE_180);
		}
		return sinlut;
	}

}