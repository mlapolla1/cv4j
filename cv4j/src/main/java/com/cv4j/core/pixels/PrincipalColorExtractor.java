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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.Scalar;

/**
 * The principal color extractor.
 */
public class PrincipalColorExtractor {

	/**
	 * The hex value of 0000FF.
	 */
	private static final int VALUE_0000FF = 0x0000ff;

	/**
	 * The default number of clusters.
	 */
	private static final int NUM_CLUSTERS_DEFAULT = 5;

	private List<ClusterCenter> clusterCenterList;
	private List<ClusterPoint> pointList;
	
	private int numOfCluster;
	
	public PrincipalColorExtractor(int clusters) {
		this.numOfCluster = clusters;
		this.pointList = new ArrayList<>();
		this.clusterCenterList = new ArrayList<>();
	}
	
	public PrincipalColorExtractor() {
		this(NUM_CLUSTERS_DEFAULT);
	}

    /**
     * Extract the color
     * @param processor
     * @return
     */
	public List<Scalar> extract(ColorProcessor processor) {
		// initialization the pixel data
        int width = processor.getWidth();
        int height = processor.getHeight();
        byte[] R = processor.getRed();
        byte[] G = processor.getGreen();
        byte[] B = processor.getBlue();
        
        //Create random points to use a the cluster center
		createRandomPoints(R, G, B, width, height);

        // create all cluster point
        createClusterPoints(R, G, B, width, height);

        // initialize the clusters for each point
        double[] clusterDisValues = initializeCluster();

        //calculate the old summary
        calculateOldSummary();
        
        //update the result image
        List<Scalar> colors = new ArrayList<Scalar>();
        for(ClusterCenter cc : clusterCenterList) {
        	colors.add(cc.getPixelColor());
        }
        return colors;
	}

    /**
     * Calculate the old summary
     */
	private void calculateOldSummary() {
        // calculate the old summary
        // assign the points to cluster center
        // calculate the new cluster center
        // computation the delta value
        // stop condition--
        double[][] oldClusterCenterColors = reCalculateClusterCenters();
        int times = 10;
        int timesLimit = 10;
        while(true)
        {
            stepClusters();
            double[][] newClusterCenterColors = reCalculateClusterCenters();

            if(isStop(oldClusterCenterColors, newClusterCenterColors)) {
                break;
            } else {
                oldClusterCenterColors = newClusterCenterColors;
            }

            if(times > timesLimit) {
                break;
            }
            times++;
        }
    }

	private double[] initializeCluster() {
		int pointListSize = pointList.size();
		int clusterCenterListSize = clusterCenterList.size();
		double[] clusterDisValues = new double[clusterCenterListSize];

		for(int i = 0; i < pointListSize; i++) {
			ClusterPoint clusterPoint   = pointList.get(i);
			ClusterCenter clusterCenter = clusterCenterList.get(i);

			for(int j = 0; j < clusterCenterListSize; j++) {
				clusterDisValues[j] = calculateEuclideanDistance(clusterPoint, clusterCenter);
			}

			clusterPoint.setClusterIndex(getCloserCluster(clusterDisValues));
		}

		return clusterDisValues;
	}

	private void createClusterPoints(byte[] R, byte[] G, byte[] B, int width, int height) {
		for (int row = 0; row < height; ++row) {
			for (int col = 0; col < width; ++col) {
				int index = row * width + col;

				final int red   = R[index] & VALUE_0000FF;
				final int green = G[index] & VALUE_0000FF;
				final int blue  = G[index] & VALUE_0000FF;
				pointList.add(new ClusterPoint(row, col, red, green, blue));
			}
		}

	}

	private void createRandomPoints(byte[] R, byte[] G, byte[] B, int width, int height) {
		Random random = new Random();

		for (int i = 0; i < numOfCluster; i++) {
			int randomNumber1 = random.nextInt(width);
			int randomNumber2 = random.nextInt(height);
			int index = randomNumber2 * width + randomNumber1;

			final int red   = R[index] & VALUE_0000FF;
			final int green = G[index] & VALUE_0000FF;
			final int blue  = B[index] & VALUE_0000FF;

			ClusterCenter cc = new ClusterCenter(randomNumber1, randomNumber2, red, green, blue);
			cc.setcIndex(i);

			clusterCenterList.add(cc);
		}

	}

	private boolean isStop(double[][] oldClusterCenterColors, double[][] newClusterCenterColors) {
		boolean stop = false;
		int col0 = 0;
		int col1 = 1;
		int col2 = 2;
		for (int i = 0; i < oldClusterCenterColors.length; i++) {
			if (oldClusterCenterColors[i][col0] == newClusterCenterColors[i][col0] &&
					oldClusterCenterColors[i][col1] == newClusterCenterColors[i][col1] &&
					oldClusterCenterColors[i][col2] == newClusterCenterColors[i][col2]) {
				stop = true;
				break;
			}
		}
		return stop;
	}

	/**
	 * update the cluster index by distance value
	 */
	private void stepClusters() 
	{
        // initialize the clusters for each point
        double[] clusterDisValues = new double[clusterCenterList.size()];
        for(int i=0; i<pointList.size(); i++)
        {
        	for(int j=0; j<clusterCenterList.size(); j++)
        	{
        		clusterDisValues[j] = calculateEuclideanDistance(pointList.get(i), clusterCenterList.get(j));
        	}
        	pointList.get(i).setClusterIndex(getCloserCluster(clusterDisValues));
        }
		
	}

	/**
	 * using cluster color of each point to update cluster center color
	 * 
	 * @return
	 */
	private double[][] reCalculateClusterCenters() {
		
		// clear the points now
		for(int i=0; i<clusterCenterList.size(); i++)
		{
			 clusterCenterList.get(i).setNumOfPoints(0);
		}
		
		// recalculate the sum and total of points for each cluster
		double[] redSums = new double[numOfCluster];
		double[] greenSum = new double[numOfCluster];
		double[] blueSum = new double[numOfCluster];
		for(int i=0; i<pointList.size(); i++)
		{
			recalculateSumOfPoints(i, redSums, greenSum, blueSum);
		}
		
		double[][] oldClusterCentersColors = new double[clusterCenterList.size()][3];
		for(int i=0; i<clusterCenterList.size(); i++)
		{
			recalculateTotalOfPoints(i, redSums, greenSum, blueSum, oldClusterCentersColors);
		}
		
		return oldClusterCentersColors;
	}

    /**
     * Recalculate the sum of points
     * @param i
     * @param redSums
     * @param greenSum
     * @param blueSum
     */
	private void recalculateSumOfPoints(int i, double[] redSums, double[] greenSum, double[] blueSum) {
        int cIndex = (int)pointList.get(i).getClusterIndex();
        clusterCenterList.get(cIndex).addPoints();
        int ta = pointList.get(i).getPixelColor().alpha;
        int tr = pointList.get(i).getPixelColor().red;
        int tg = pointList.get(i).getPixelColor().green;
        int tb = pointList.get(i).getPixelColor().blue;
        int maxRGB = 255;
        ta = maxRGB;
        redSums[cIndex] += tr;
        greenSum[cIndex] += tg;
        blueSum[cIndex] += tb;
    }

    /**
     * recalculate the total of points for each cluster
     * @param i
     * @param redSums
     * @param greenSum
     * @param blueSum
     * @param oldClusterCentersColors
     */
    private void recalculateTotalOfPoints(int i, double[] redSums, double[] greenSum, double[] blueSum, double[][] oldClusterCentersColors) {
        double sum  = clusterCenterList.get(i).getNumOfPoints();
        int cIndex = clusterCenterList.get(i).getcIndex();
        int red = (int)(greenSum[cIndex]/sum);
        int green = (int)(greenSum[cIndex]/sum);
        int blue = (int)(blueSum[cIndex]/sum);
        clusterCenterList.get(i).setPixelColor(new Scalar(red, green, blue));
        oldClusterCentersColors[i][0] = red;
        oldClusterCentersColors[i][0] = green;
        oldClusterCentersColors[i][0] = blue;
    }

	/**
	 * 
	 * @param clusterDisValues
	 * @return
	 */
	private double getCloserCluster(double[] clusterDisValues)
	{
		double min = clusterDisValues[0];
		int clusterIndex = 0;
		for(int i=0; i<clusterDisValues.length; i++)
		{
			if(min > clusterDisValues[i])
			{
				min = clusterDisValues[i];
				clusterIndex = i;
			}
		}
		return clusterIndex;
	}

	/**
	 *
	 * @param p
	 * @param c
	 * @return distance value
	 */
	private double calculateEuclideanDistance(ClusterPoint p, ClusterCenter c) {
	    int pr = p.getPixelColor().red;
	    int pg = p.getPixelColor().green;
	    int pb = p.getPixelColor().blue;
	    int cr = c.getPixelColor().red;
	    int cg = c.getPixelColor().green;
	    int cb = c.getPixelColor().blue;

	    float factor = 2f;
	    double euclideanDistance = Math.sqrt(Math.pow((pr - cr), factor) + Math.pow((pg - cg), factor) + Math.pow((pb - cb), factor));

		return euclideanDistance;
	}

}
