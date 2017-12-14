package com.cv4j.core.binary.functions;

import com.cv4j.core.datamodel.ByteProcessor;

/**
 * The functional interface for the Treshold class.
 * @author Michele Lapolla on 12/14/17.
 * @see com.cv4j.core.binary.Threshold
 */
@FunctionalInterface
public interface ThresholdFunction {
    int call(ByteProcessor processor);
}
