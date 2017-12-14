package com.cv4j.core.tpl;

import java.util.Arrays;

import com.cv4j.core.datamodel.FloatProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.datamodel.IntIntegralImage;

/**
 * The template match 2.
 */
public class TemplateMatch2 {
    
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
    
    private float[] sqrt_tpl;

    private void initParams(ImageProcessor tpl) {
    	int chs = tpl.getChannels();
        sqrt_tpl = new float[chs];
        Arrays.fill(sqrt_tpl, 0);
        int r=0;
        for(int c=0; c<chs; c++) {
        	byte[] R = tpl.toByte(c);
        	for(int i=0; i<R.length; i++) {
        		r = R[i]&0xff;
        		sqrt_tpl[c] += r*r;
        	}        	
        }
    }
    
    /**
     * Match two image processors with a method.
     * @param  target The image processor  target
     * @param  tpl    The tpl
     * @param  method The method
     * @return        The float processor
     */
    public FloatProcessor match(ImageProcessor target, ImageProcessor tpl, int method) {
        int width = target.getWidth();
        int height = target.getHeight();
        int tw = tpl.getWidth();
        int th = tpl.getHeight();
        int[] tplmask = new int[tpl.getWidth() * tpl.getHeight()];
        Arrays.fill(tplmask, 0);
        initParams(tpl);
        int offFactor = 2;
        int offx = tw/offFactor+1;
        int offy = th/offFactor+1;
        int rw = width - offx*offFactor;
        int rh = height - offy*offFactor;
        float[] result = new float[rw*rh];
        Arrays.fill(result, 0);
        FloatProcessor tpl_data = new FloatProcessor(result, rw, rh);
        int channelValue = 3;
        if(target.getChannels() == channelValue && tpl.getChannels() == channelValue) {
            for(int ch=0; ch<channelValue; ch++) {
            	FloatProcessor tmp = processSingleChannels(width, height, target.toByte(ch), tw, th, tpl.toByte(ch), ch);
            	tpl_data.addArray(tmp.toFloat(0));
            }
        } else if(target.getChannels() == 1 && tpl.getChannels() == 1) {
        	tpl_data = processSingleChannels(width, height, target.toByte(0), tw, th, tpl.toByte(0), 0);
        } else {
            throw new IllegalStateException("\nERR:Image Type is not same...\n");
        }
        return tpl_data;
    }
    
    /**
     * Process signle channels
     * @param  width    The width
     * @param  height   The height
     * @param  pixels   The pixels
     * @param  tw       The tw
     * @param  th       The th
     * @param  tpl      The tpl
     * @param  ch_index The channel index
     * @return          The float processor
     */
    public FloatProcessor processSingleChannels(int width, int height, byte[] pixels, int tw, int th, byte[] tpl, int ch_index){
        int offFactor = 2;
        int offx = tw/offFactor+1;
        int offy = th/offFactor+1;
        int[] tplmask = new int[tw * th];
        Arrays.fill(tplmask, 0);
        int rw = width - offx*offFactor;
        int rh = height - offy*offFactor;
        float[] result = new float[rw*rh];
        IntIntegralImage ii = new IntIntegralImage();
        ii.setImage(pixels);
        ii.process(width, height, true);
        for(int row=offy; row<height-offy; row++) {
            for(int col=offx; col<width-offx; col++) {
            	int[] roi = getROI(width, height, tw, th, row, col, pixels);
            	float sr = ii.getBlockSquareSum(col, row, (offx * offFactor + 1), (offy * offFactor + 1));
            	float sum = sqrt_tpl[ch_index] + sr - multplyArras(roi, tpl);
            	result[(row-offy)*rw + (col-offx)] = sum;
            }
        }

        return new FloatProcessor(result, rw, rh);
    }
    
    /**
     * Multiply arras
     * @param  roi The roi
     * @param  tpl The tpl
     * @return     The multiply.
     */
    public float multplyArras(int[] roi, byte[] tpl) {
    	int sum = 0;
        int factor = 2;
    	for(int i=0; i<roi.length; i++) {
    		sum += (tpl[i]&0xff)*roi[i];
    	}
    	return factor*sum;
    }
    
    /**
     * Returns the ROI
     * @param  w      The width
     * @param  h      The height
     * @param  tw     The tw
     * @param  th     The th
     * @param  y      The x
     * @param  x      The y
     * @param  pixels The pixels
     * @return        The ROI
     */
	private int[] getROI(int w, int h, int tw, int th, int y, int x, byte[] pixels) {
        int offFactor = 2;
		int offx = x - tw / offFactor;
		int offy = y - th / offFactor;
		int[] roidata = new int[tw*th];
		for(int row=offy; row<th+offy; row++) {
			int index1 = row*w;
			int index2 = (row-offy)*tw;
			for(int col=offx; col<tw+offx; col++) {
				roidata[index2+col-offx] = pixels[index1+col]&0xff;
			}
		}
		return roidata;
	}
}
