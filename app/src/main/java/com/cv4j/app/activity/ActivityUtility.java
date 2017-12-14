package com.cv4j.app.activity;

public class ActivityUtility {

	public void subInitData(cv4JImage cv4JImage, int[] mask) {

        SparseIntArray colors = new SparseIntArray();
        Random random = new Random();

        int height = cv4JImage.getProcessor().getHeight();
        int width = cv4JImage.getProcessor().getWidth();
        int size = height * width;
        for (int i = 0;i<size;i++) {
            int c = mask[i];
            if (c>=0) {
                colors.put(c,Color.argb(255, random.nextInt(255),random.nextInt(255),random.nextInt(255)));
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

	}
}