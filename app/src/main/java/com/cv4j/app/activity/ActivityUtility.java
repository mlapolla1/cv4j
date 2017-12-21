package com.cv4j.app.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.SparseIntArray;

import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.image.ImageProcessor;

import java.util.Random;

/**
 * Activity utility.
 */
public class ActivityUtility {

	public Bitmap subInitData(CV4JImage cv4JImage, int[] mask) {
        final ImageProcessor imageProcessor = cv4JImage.getProcessor();

        final int height = imageProcessor.getHeight();
        final int width  = imageProcessor.getWidth();
        final int size   = width * height;

        SparseIntArray colors = initColors(mask, size);

        return getBitmapFromColors(cv4JImage, mask, colors, width, height);
	}

    private Bitmap getBitmapFromColors(CV4JImage cv4JImage, int[] mask, SparseIntArray colors, int width, int height) {
        Bitmap bitmap = cv4JImage.getProcessor().getImage().toBitmap();

        for(int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int c = mask[row*width+col];

                if (c >= 0) {
                    bitmap.setPixel(col, row, colors.get(c));
                }
            }
        }

        return bitmap;
    }

    private SparseIntArray initColors(int[] mask, int size) {
        SparseIntArray colors = new SparseIntArray();

        for (int i = 0;i<size;i++) {
            int c = mask[i];

            if (c >= 0) {
                colors.put(c, getRandomColor());
            }
        }

        return colors;
    }

    private int getRandomColor() {
	    final Random random = new Random();
        final int maxRgbValue = 255;

        final int redValue   = random.nextInt(maxRgbValue);
        final int greenValue = random.nextInt(maxRgbValue);
        final int blueValue  = random.nextInt(maxRgbValue);

        return Color.argb(maxRgbValue, redValue, greenValue, blueValue);
    }
}