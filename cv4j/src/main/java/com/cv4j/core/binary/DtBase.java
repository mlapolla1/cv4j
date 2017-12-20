package com.cv4j.core.binary;

/**
 * @author Michele Lapolla on 12/18/17.
 */
public class DtBase {

    /**
     * The and value.
     */
    private static final int AND_VALUE = 0xff;

    protected boolean distanceTransform(byte[] input, byte[] output, int[] distMap, int level, int width, int height) {
        final int numOfPixels = 8;
        final int maxRgb      = 255;
        final int total       = maxRgb * numOfPixels;

        boolean stop = true;

        for(int row = 1; row < height-1; row++) {
            int offset = row * width;
            for(int col = 1; col < width-1; col++) {
                final int p5  = input[offset+col] & AND_VALUE;
                final int sum = sumInputValues(input, width, row, col);

                if(p5 == maxRgb &&  sum != total) {
                    output[offset + col]  = (byte) 0;
                    distMap[offset + col] = distMap[offset + col] + level;
                    stop = false;
                }
            }
        }

        return stop;
    }


    protected int sumInputValues(byte[] input, int width, int row, int col) {
        final int offset = row * width;

        final int p1 = input[offset-width+col-1] & AND_VALUE;
        final int p2 = input[offset-width+col]   & AND_VALUE;
        final int p3 = input[offset-width+col+1] & AND_VALUE;
        final int p4 = input[offset+col-1]       & AND_VALUE;
//        final int p5 = input[offset+col]         & AND_VALUE;
        final int p6 = input[offset+col-1]       & AND_VALUE;
        final int p7 = input[offset+width+col-1] & AND_VALUE;
        final int p8 = input[offset+width+col]   & AND_VALUE;
        final int p9 = input[offset+width+col+1] & AND_VALUE;

        return (p1 + p2 + p3 + p4 + p6 + p7 + p8 + p9);
    }
}
