package com.cv4j.app.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.SparseIntArray;

import com.cv4j.core.datamodel.CV4JImage;

import java.util.Random;

public class ActivityUtility {

	public Bitmap subInitData(CV4JImage cv4JImage, int[] mask) {

        SparseIntArray colors = new SparseIntArray();
        Random random = new Random();

        int height = cv4JImage.getProcessor().getHeight();
        int width = cv4JImage.getProcessor().getWidth();
        int size = height * width;
        int maxRGB = 255;
        for (int i = 0;i<size;i++) {
            int c = mask[i];
            if (c>=0) {
                colors.put(c, Color.argb(maxRGB, random.nextInt(maxRGB),random.nextInt(maxRGB),random.nextInt(maxRGB)));
            }
        }

        cv4JImage.resetBitmap();
        Bitmap newBitmap = cv4JImage.getProcessor().getImage().toBitmap();

        for(int row=0; row<height; row++) {
            for (int col = 0; col < width; col++) {

                int c = mask[row*width+col];
                if (c>=0) {
                    newBitmap.setPixel(col,row,colors.get(c));
                }
            }
        }
        return newBitmap;
	}
}