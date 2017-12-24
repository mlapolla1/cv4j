package com.cv4j.core.utils;

/**
 * Floating point utils, like comparing.
 * @author Michele Lapolla on 12/24/17.
 */
public class FloatingPointsUtils {

    /**
     * Compare the equality of two floating point values.
     * @param fp1 The first floating point value.
     * @param fp2 The second floating point value.
     * @return    The equality.
     */
    public static boolean nearlyEqauls(double fp1, double fp2) {
        final double epsilon = 0.0000001;
        final double absFp1  = Math.abs(fp1);
        final double absFp2  = Math.abs(fp2);

        double maxFpValue = Math.max(absFp1, absFp2);

        return Math.abs(fp1 - fp2) <= epsilon * Math.max(1.0f, maxFpValue);
    }
}
