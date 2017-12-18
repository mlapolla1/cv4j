package com.cv4j.app.activity.Histogram;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.hist.CalcHistogram;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

/**
 * Created by Tony Shen on 2017/5/14.
 */

public class HistogramDemoActivity extends BaseActivity {

    @InjectView(R.id.image0)
    ImageView image0;

    @InjectView(R.id.image1)
    ImageView image1;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;
    private int[] colors;

    /**
     * Creation of the app.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram_demo);

        initViews();
        initData();
    }

    /**
     * Initialization of the views.
     */
    private void initViews() {
        final String toolbarTitle = "< " + this.title;
        this.toolbar.setTitle(toolbarTitle);
    }

    /**
     * Initialization of the data.
     */
    private void initData() {
        final Resources res = getResources();
        final Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.test_hist2);
        image0.setImageBitmap(bitmap);

        CV4JImage cv4jImage = new CV4JImage(bitmap);
        Paint paint = new Paint();
        final Bitmap histBitmap = drawHist(cv4jImage.getProcessor(), paint);
        image1.setImageBitmap(histBitmap);
    }

    /**
     * Draw the histogram.
     * @param imageProcessor The image processor.
     * @param paint          The paint object.
     * @return               The drawed histogram.
     */
    private Bitmap drawHist(ImageProcessor imageProcessor, Paint paint) {
        final int maxRgb = 255;
        final int bins = 127;

        final int width = imageProcessor.getWidth();
        final int height = imageProcessor.getHeight();
        final int channels = imageProcessor.getChannels();

        int[][] hist = new int[channels][bins];

        CalcHistogram calcHistogram = new CalcHistogram();
        calcHistogram.calcRGBHist(imageProcessor, bins, hist, true);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = createCanvas(imageProcessor, paint, bitmap);

        float step = width / bins;
        int[] colors = getColors();

        for (int i = 0; i < channels; i++) {
            paint.setColor(colors[i]);

            for (int j = 0; j < bins; j++) {
                int xOffset = (int) (j * step);
                int yOffset = hist[i][j] * height / maxRgb;
                canvas.drawRect(xOffset, height - yOffset, xOffset + step, height, paint);
            }
        }

        return bitmap;
    }

    /**
     * Create the canvas.
     * @param imageProcessor The image processor
     * @param paint          The paint object.
     * @param bitmap         The bitmap
     * @return               The canvas.
     */
    private Canvas createCanvas(ImageProcessor imageProcessor, Paint paint, Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);

        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        canvas.drawRect(0,0,imageProcessor.getWidth(),imageProcessor.getHeight(),paint);

        return canvas;
    }

    @OnClick(id= R.id.toolbar)
    void clickToolbar() {

        finish();
    }

    public int[] getColors() {
        final int color1 = 77;
        final int color2 = 255;
        final int color3 = 0;

        int[] colors = new int[] {
                Color.argb(color1, color2, color3, color3),
                Color.argb(color1, color3, color2, color3),
                Color.argb(color1, color3, color3, color2)
        };

        return colors;
    }
}
