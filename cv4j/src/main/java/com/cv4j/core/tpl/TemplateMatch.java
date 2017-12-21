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
package com.cv4j.core.tpl;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.number.FloatProcessor;
import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.datamodel.Point;
import com.cv4j.image.util.Tools;

import java.util.Arrays;
import java.util.List;

/**
 * The template match
 */
public class TemplateMatch {

    /**
     * Sq diff normed.
     */
    public static final int TM_SQDIFF_NORMED = 2;
    
    /**
     * Ccorr normed.
     */
    public static final int TM_CCORR_NORMED = 4;
    
    /**
     * coeff normed
     */
    public static final int TM_CCOEFF_NORMED = 6;

    /**
     * Channel target 3 and tpl 3
     * @param target [description]
     * @param tpl    [description]
     */
    private void channel3x3(ImageProcessor target, ImageProcessor tpl, int width, int height, int offx, int offy) {
        byte[] R = ((ColorProcessor) target).getRed();
        byte[] G = ((ColorProcessor) target).getGreen();
        byte[] B = ((ColorProcessor) target).getBlue();
        
        for(int row = offy; row < (height - offy); row++) {
            for(int col = offx; col < (width - offx); col++) {
                // ...
            }
        }
    }

    
    public void something(int[] tplmask, byte[] data, int raidus_height, int raidus_width, int th, int tw, int width, int row, int col) {
        Arrays.fill(tplmask, 0);
        int wrow = 0;

        for(int subrow = -(raidus_height); subrow <= raidus_height; subrow++ ) {
            int wcol = 0;
            for(int subcol = -(raidus_width); subcol <= raidus_width; subcol++ ) {
                if(wrow >= th || wcol >= tw) {
                    continue;
                }

                tplmask[(wrow * tw) + wcol] = data[((row + subrow) * width) + (col + subcol)] & 0xff;
                wcol++;
            }
            wrow++;
        }
    }

    /**
     *
     * @param target - source image contain template or not
     * @param tpl - template
     * @param method
     * @return FloatProcessor -
     */
    public FloatProcessor match(ImageProcessor target, ImageProcessor tpl, int method) {
        int width         = target.getWidth();
        int height        = target.getHeight();
        int tw            = tpl.getWidth();
        int th            = tpl.getHeight();
        int offx          = tpl.getWidth()/2+1;
        int offy          = tpl.getHeight()/2+1;
        int raidus_width  = tpl.getWidth() / 2;
        int raidus_height = tpl.getHeight()/2;
        int rw            = width - (offx * 2);
        int rh            = height - (offy * 2);

        int[] tplmask     = new int[tpl.getWidth() * tpl.getHeight()];
        Arrays.fill(tplmask, 0);
        
        float[] result = new float[rw * rh];

        processMatchFloat(target, tpl, method, width, height, offx, offy, tplmask, result);

        return new FloatProcessor(result, rw, rh);
    }

    private void processMatchFloat(ImageProcessor target, ImageProcessor tpl, int method, int width, int height,
                              int offx, int offy, int[] tplmask, float[] result) {

        if(target.getChannels() == 3 && tpl.getChannels() == 3) {
            channel3x3(target, tpl, width, height, offx, offy);
        } else if(target.getChannels() == 1 && tpl.getChannels() == 1) {
            if(method == TM_CCORR_NORMED) {
                generateNCCResult(target, tpl, result, tplmask);
            } else if (method == TM_SQDIFF_NORMED) {
                // TODO:zhigang
            }
        } else {
            throw new IllegalStateException("\nERR:Image Type is not same...\n");
        }

    }

    /**
     *
     * @param target - source image contain template or not
     * @param tpl - template
     * @param locations, left-upper corner with match template location
     * @param method, support TM_SQDIFF\TM_SQDIFF_NORMED\TM_CCORR\TM_CCORR_NORMED
     *                TM_CCOEFF\TM_CCOEFF_NORMED
     * @param threhold
     */
    public void match(ImageProcessor target, ImageProcessor tpl, List<Point> locations, int method,double threhold) {
        int width         = target.getWidth();
        int height        = target.getHeight();
        int tw            = tpl.getWidth();
        int th            = tpl.getHeight();
        int offx          = (tpl.getWidth() / 2) + 1;
        int offy          = (tpl.getHeight() / 2) + 1;
        int raidus_width  = tpl.getWidth() / 2;
        int raidus_height = tpl.getHeight() / 2;

        int[] tplmask = new int[tpl.getWidth() * tpl.getHeight()];
        Arrays.fill(tplmask, 0);

        if(target.getChannels() == 3 && tpl.getChannels() == 3) {
            channel3x3(target, tpl, width, height, offx, offy);
        } else if(target.getChannels() == 1 && tpl.getChannels() == 1) {
            processMatchElse(target, tpl, threhold, locations, height, width, offx, offy, tplmask,
                            raidus_width, raidus_height,tw, th);
        } else {
            // do nothing and throw exception later on...
            System.err.println("\nERR:could not match input image type...\n");
        }
    }

    private void processMatchElse(ImageProcessor target, ImageProcessor tpl, double threhold, List<Point> locations,
                                  int height, int width, int offx, int offy, int[] tplmask,
                                  int raidus_width, int raidus_height, int tw, int th) {

        byte[]   data     = ((ByteProcessor)target).getGray();
        byte[]   tdata    = ((ByteProcessor)tpl).getGray();
        float[]  meansdev = Tools.calcMeansAndDev(((ByteProcessor)tpl).toFloat(0));
        double[] tDiff    = calculateDiff(tdata, meansdev[0]);

        for(int row = offy; row < height-offy; row += 2) {
            for(int col = offx; col < width-offx; col += 2) {
                something(tplmask, data, raidus_height, raidus_width, th, tw, width, row, col);

                // calculate the ncc
                float[] _meansDev = Tools.calcMeansAndDev(tplmask);
                double[] diff = calculateDiff(tplmask, _meansDev[0]);
                double ncc = calculateNcc(tDiff, diff, _meansDev[1], meansdev[1]);

                if(ncc > threhold) {
                    Point mpoint = new Point();
                    mpoint.x = (col - raidus_width);
                    mpoint.y  = (row - raidus_height);
                    locations.add(mpoint);
                }
            }
        }
    }

    private void generateNCCResult(ImageProcessor target, ImageProcessor tpl, float[] result, int[] tplmask) {
        int width = target.getWidth();
        int height = target.getHeight();
        int tw = tpl.getWidth();
        int th = tpl.getHeight();
        int offx = tpl.getWidth()/2+1;
        int offy = tpl.getHeight()/2+1;
        int raidus_width = tpl.getWidth() / 2;
        int raidus_height = tpl.getHeight()/2;
        byte[] data = ((ByteProcessor)target).getGray();
        byte[] tdata = ((ByteProcessor)tpl).getGray();
        float[] meansdev = Tools.calcMeansAndDev(((ByteProcessor)tpl).toFloat(0));
        double[] tDiff = calculateDiff(tdata, meansdev[0]);

        int rw = width - offx*2;
        int rh = height - offy*2;


        processForGenerateNCCResult(result, tplmask, offx, offy, width, height, tw, th, raidus_width,
                                    raidus_height, data, meansdev, tDiff, rw);
    }

    private void processForGenerateNCCResult(float[] result, int[] tplmask, int offx, int offy,
                                             int width, int height, int tw, int th, int raidus_width,
                                             int raidus_height, byte[] data, float[] meansdev,
                                             double[] tDiff, int rw) {

        for(int row=offy; row<height-offy; row+=2) {
            for(int col=offx; col<width-offx; col+=2) {
                int wrow = 0;
                Arrays.fill(tplmask, 0);
                for(int subrow = -raidus_height; subrow <= raidus_height; subrow++ )
                {
                    int wcol = 0;
                    for(int subcol = -raidus_width; subcol <= raidus_width; subcol++ )
                    {
                        if(wrow >= th || wcol >= tw)
                        {
                            continue;
                        }
                        tplmask[wrow * tw + wcol] = data[(row+subrow)*width + (col+subcol)]&0xff;
                        wcol++;
                    }
                    wrow++;
                }
                // calculate the ncc
                float[] _meansDev = Tools.calcMeansAndDev(tplmask);
                double[] diff = calculateDiff(tplmask, _meansDev[0]);
                double ncc = calculateNcc(tDiff, diff, _meansDev[1], meansdev[1]);
                result[(row-offy)*rw + (col-offx)] = (float) ncc;
            }
        }
    }

    private double[] calculateDiff(byte[] pixels, float mean) {
        double[] diffs = new double[pixels.length];
        int length = diffs.length;
        for (int i = 0; i < length; i++) {
            diffs[i] = (int) (pixels[i] & 0xff) - mean;
        }
        return diffs;
    }

    private double[] calculateDiff(int[] pixels, float mean) {
        double[] diffs = new double[pixels.length];
        int length = diffs.length;
        for (int i = 0; i < length; i++) {
            diffs[i] = pixels[i] - mean;
        }
        return diffs;
    }

    private double calculateNcc(double[] tDiff, double[] diff, double dev1, double dev2) {
        double sum = 0.0d;
        double count = diff.length;
        for (int i = 0; i < diff.length; i++) {
            sum += ((tDiff[i] * diff[i]) / (dev1 * dev2));
        }
        return (sum / count);
    }

}
