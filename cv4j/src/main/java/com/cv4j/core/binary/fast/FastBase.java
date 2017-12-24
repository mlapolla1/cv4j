package com.cv4j.core.binary.fast;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.Size;

/**
 * The Fast base class.
 * @author Michele Lapolla on 12/19/17.
 */
public abstract class FastBase {

    /**
     * The process
     * @param binary           The byte processor.
     * @param structureElement The structure element.
     * @param iteration        The number of times.
     */
    public void process(ByteProcessor binary, Size structureElement, int iteration) {
        final int width = binary.getWidth();
        final int height = binary.getHeight();

        byte[] data   = initData(binary);
        byte[] output = initOutput(binary, data);

        /// This place can use multi-thread.

        // X Direction
        int xr = structureElement.cols / 2;
        int shift = calculateShift(structureElement.cols);
        calculateOutputValues(output, data, width, height, xr, shift);

        // Y Direction
        int yr = structureElement.rows / 2;
        shift = calculateShift(structureElement.rows);
        calculateOutputValues(output, data, width, height, yr, shift);
    }

    /**
     * Calculates the output value.
     * @param output The output array.
     * @param data   The data array.
     * @param width  The width.
     * @param height The height
     * @param xyr    The xr or yr.
     * @param shift  The shift.
     */
    protected abstract void calculateOutputValues(byte[] output, byte[] data, int width, int height, int xyr, int shift);

    /**
     * Initialize the output array.
     * @param binary The byte processor.
     * @param data   The data array.
     * @return       The output array.
     */
    protected byte[] initOutput(ByteProcessor binary, byte[] data) {
        final int width  = binary.getWidth();
        final int height = binary.getHeight();
        final int size   = width * height;

        byte[] output = new byte[size];
        System.arraycopy(data, 0, output, 0, size);

        return output;
    }

    /**
     * Initialization of data.
     * @param binary The byte processor
     * @return       The data array.
     */
    protected byte[] initData(ByteProcessor binary) {
        return binary.getGray();
    }

    /**
     * Given a value, return it between a min and max
     * if it's not in the range.
     * @param value The value.
     * @param min   The minimum value.
     * @param max   The maximum value.
     * @return      The value in the range min-max.
     */
    protected int getValueBetween(int value, int min, int max) {
        if(value < min) {
            value = min;
        }

        if(value > max) {
            value = max;
        }

        return value;
    }

    /**
     * Calculate the shift.
     * @param value The value.
     * @return     The shift.
     */
    protected int calculateShift(int value) {
        int shift = 0;

        if(value % 2 == 0) {
            shift = 1;
        }

        return shift;
    }

}
