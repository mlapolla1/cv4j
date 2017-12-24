package com.cv4j.core.utils;

/**
 * Safe casting from higher precisiont o lower precision.
 * @author Michele Lapolla on 12/23/17.
 */
public class SafeCasting {

    /**
     * Safe casting of an integer value to a byte value.
     * @param value The integer value.
     * @return      The byte-casted value.
     */
    public static byte safeIntToByte(int value) {
        final int maxByte = Byte.MAX_VALUE - Byte.MIN_VALUE;

        if (value < -(maxByte) || value > maxByte) {
            throw new IllegalArgumentException(value + " cannot be cast to byte without changing its value.");
        }

        return (byte) value;
    }

    /**
     * Safe casting of a double value to a float value.
     * @param value The double value.
     * @return      The float-casted value.
     */
    public static float safeDoubleToFloat(double value) {
        if (value < Float.MIN_VALUE || value > Float.MAX_VALUE) {
            throw new IllegalArgumentException(value + " cannot be cast to float without changing its value.");
        }

        return (float) value;
    }

    /**
     * Safe casting of a double value to an integer value.
     * @param value The double value.
     * @return      The integer-casted value.
     */
    public static int safeDoubleToInt(double value) {
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(value + " cannot be cast to int without changing its value.");
        }

        return (int) value;
    }

    /**
     * Safe casting of a float value to an integer value.
     * @param value The float value.
     * @return      The integer-casted value.
     */
    public static int safeFloatToInt(float value) {
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(value + " cannot be cast to int without changing its value.");
        }

        return (int) value;
    }
}
