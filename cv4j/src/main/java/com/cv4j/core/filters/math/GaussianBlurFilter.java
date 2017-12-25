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
package com.cv4j.core.filters.math;

import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.filters.BaseFilter;
import com.cv4j.core.utils.SafeCasting;
import com.cv4j.image.util.TaskUtils;
import com.cv4j.image.util.Tools;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;

/**
 * The gaussian blur filter.
 */
public class GaussianBlurFilter extends BaseFilter {

    private float[] kernel;
    private double sigma = 2;
    ExecutorService mExecutor;
    CompletionService<Void> service;

    public GaussianBlurFilter() {
        kernel = new float[0];
    }

    public void setSigma(double a) {
        this.sigma = a;
    }

    @Override
    public ImageProcessor doFilter(final ImageProcessor src){

        final int size = width*height;
        int dims = src.getChannels();
        float accuracy = 0.002f;
        makeGaussianKernel(sigma, accuracy, Math.min(width, height));

        mExecutor = TaskUtils.newFixedThreadPool("cv4j",dims);
        service = new ExecutorCompletionService<>(mExecutor);

        // save result
        for(int i=0; i<dims; i++) {

            final int temp = i;
            service.submit(new Callable<Void>() {
                public Void call()  {
                    byte[] inPixels = src.toByte(temp);
                    byte[] temp = new byte[size];
                    blur(inPixels, temp, width, height); // H Gaussian
                    blur(temp, inPixels, height, width); // V Gaussain
                    return null;
                }
            });
        }

        for (int i = 0; i < dims; i++) {
            try {
                service.take();
            } catch (InterruptedException e) {
                System.out.println("InterruptedException in GaussianBlurFilter.doFilter() for service.take()");
            }
        }

        mExecutor.shutdown();
        return src;
    }

    /**
     * <p> here is 1D Gaussian        , </p>
     *
     * @param inPixels
     * @param outPixels
     * @param width
     * @param height
     */
    private void blur(byte[] inPixels, byte[] outPixels, int width, int height)
    {
        int subCol = 0;
        int index = 0;
        int index2 = 0;
        float sum = 0;
        int k = kernel.length-1;
        for(int row=0; row<height; row++) {
            int c = 0;
            index = row;
            for(int col=0; col<width; col++) {
                sum = getSum(c, inPixels, index2, row, subCol, width, col, k);
                outPixels[index] = (byte)Tools.clamp(sum);
                index += height;
            }
        }
    }

    private float getSum(int c, byte[] inPixels, int index2, int row, int subCol, int width, int col, int k){
        int sum = 0;
        for(int m = -k; m< kernel.length; m++) {
            subCol = col + m;
            if(subCol < 0 || subCol >= width) {
                subCol = 0;
            }
            index2 = row * width + subCol;
            c = inPixels[index2] & 0xff;
            sum += c * kernel[Math.abs(m)];
        }
        return sum;
    }


    public void makeGaussianKernel(final double sigmaValue, final double accuracy, int maxRadius) {
        int factor = -2;
        int radiusLimit = 50;
        float expFactor = -0.5f;

        int kRadius = (int) Math.ceil(sigmaValue * Math.sqrt(factor * Math.log(accuracy))) + 1;
        // too small maxRadius would result in inaccurate sum.
        if (maxRadius < radiusLimit) {
            maxRadius = radiusLimit;         
        }

        this.kernel = new float[kRadius];
        // Gaussian function
        for (int i=0; i<kRadius; i++){               
            this.kernel[i] = (float)(Math.exp(expFactor*i*i/sigmaValue/sigmaValue));
            this.kernel[i] = (float)(Math.exp(expFactor*i*i/sigmaValue/sigmaValue));
        }
        // sum over all kernel elements for normalization
        double sum = getSum(kRadius, maxRadius, sigmaValue);

        setKernel(kRadius, sum);
    }

    private void setKernel(int kRadius, double sum){
        for (int i = 0; i < kRadius; i++) {
            kernel[i] = SafeCasting.safeDoubleToFloat(kernel[i] / sum);
        }
    }

    private double getSum(int kRadius, int maxRadius, final double sigmaValue){
        double sum = 0;
        int sumFactor = 2;
        if (kRadius < maxRadius) {
            sum = kernel[0];
            for (int i=1; i<kRadius; i++){
                sum += sumFactor*kernel[i];
            }
        } else {
            sum = sigmaValue * Math.sqrt(sumFactor * Math.PI);
        }

        return sum;
    }
}
