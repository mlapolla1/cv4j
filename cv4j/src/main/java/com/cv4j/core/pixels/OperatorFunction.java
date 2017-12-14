package com.cv4j.core.pixels;

import com.cv4j.core.datamodel.ImageProcessor;

/**
 * Created by michelelapolla on 12/13/17.
 */

@FunctionalInterface
public interface OperatorFunction {
    ImageProcessor call(ImageProcessor image1, ImageProcessor image2);
}
