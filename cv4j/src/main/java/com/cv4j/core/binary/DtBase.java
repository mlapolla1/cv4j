package com.cv4j.core.binary;

/**
 * @author Michele Lapolla on 12/18/17.
 */
public class DtBase {

    protected boolean dt(byte[] input, byte[] output, int[] distmap, int level, int width, int height) {
        boolean stop = true;
        int numOfPixels = 8;
        int total = 255 * numOfPixels;
        int andValue = 0xff;

        for(int row = 1; row < height-1; row++) {
            int offset = row * width;
            for(int col = 1; col < width-1; col++) {
                int p5 = input[offset+col] & andValue;
                int sum = sumInputValues(input, width, row, col);

                if(p5 == 255 &&  sum != total) {
                    output[offset + col] = (byte) 0;
                    distmap[offset + col] = distmap[offset + col] + level;
                    stop = false;
                }
            }
        }

        return stop;
    }


    protected int sumInputValues(byte[] input, int width, int row, int col) {
        int offset = row * width;
        int andValue = 0xff;

        int p1 = input[offset-width+col-1] & andValue;
        int p2 = input[offset-width+col] & andValue;
        int p3 = input[offset-width+col+1] & andValue;
        int p4 = input[offset+col-1] & andValue;
        int p5 = input[offset+col] & andValue;
        int p6 = input[offset+col-1] & andValue;
        int p7 = input[offset+width+col-1] & andValue;
        int p8 = input[offset+width+col] & andValue;
        int p9 = input[offset+width+col+1] & andValue;

        return (p1 + p2 + p3 + p4 + p6 + p7 + p8 + p9);
    }
}
