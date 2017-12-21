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
import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.hist.CalcHistogram;
import com.cv4j.core.hist.EqualHist;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

/**
 * Created by Tony Shen on 2017/5/14.
 */

public class HistogramEqualizationActivity extends BaseActivity {

    @InjectView(R.id.image0)
    ImageView image0;

    @InjectView(R.id.calc_image0)
    ImageView calcImage0;

    @InjectView(R.id.image1)
    ImageView image1;

    @InjectView(R.id.calc_image1)
    ImageView calcImage1;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram_equalization);

        initData();
    }

    private void initData() {

        toolbar.setTitle("< "+title);
        Resources res = getResources();
        final Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.test_hist);
        image0.setImageBitmap(bitmap);

        CV4JImage cv4jImage0 = new CV4JImage(bitmap);
        ImageProcessor imageProcessor = cv4jImage0.convert2Gray().getProcessor();
        Paint paint = new Paint();
        calcImage0.setImageBitmap(drawHist(imageProcessor,paint));

        CV4JImage cv4jImage = new CV4JImage(bitmap);
        imageProcessor = cv4jImage.convert2Gray().getProcessor();
        if (imageProcessor instanceof ByteProcessor) {
            EqualHist equalHist = new EqualHist();
            equalHist.equalize((ByteProcessor) imageProcessor);
            image1.setImageBitmap(cv4jImage.getProcessor().getImage().toBitmap());
            paint = new Paint();
            calcImage1.setImageBitmap(drawHist(imageProcessor,paint));
        }
    }

    private Bitmap drawHist(ImageProcessor imageProcessor,Paint paint) {

        CalcHistogram calcHistogram = new CalcHistogram();
        int bins = 127;
        int histNumber = 512;
        int maxRgb = 255;
        int[][] hist = new int[imageProcessor.getChannels()][bins];
        calcHistogram.calcRGBHist(imageProcessor,bins,hist,true);
        Bitmap bm = Bitmap.createBitmap(histNumber,histNumber, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        int point1 = 0;
        int point2 = 0;
        canvas.drawRect(point1,point2,histNumber,histNumber,paint);

        float histNumberFloat = 512.0f;
        float step = histNumberFloat/bins;

        int channels = imageProcessor.getChannels();

        int[] colors = new int[]{Color.argb(bins,maxRgb,point1,point2),Color.argb(bins,point1,maxRgb,point2),Color.argb(bins,point1,point2,maxRgb)};

        processForDrawHist(paint, channels, colors, bins, step, hist, histNumber, maxRgb, canvas);

        return bm;
    }

    private void processForDrawHist(Paint paint, int channels, int[] colors, int bins,
                                    float step, int[][] hist, int histNumber, int maxRgb,
                                    Canvas canvas) {
        int xoffset;
        int yoffset;

        for (int i=0;i<channels;i++) {

            paint.setColor(colors[i]);
            for (int j=0;j<bins;j++) {

                xoffset = (int)(j*step);
                yoffset = hist[i][j]*histNumber/maxRgb;
                canvas.drawRect(xoffset,histNumber-yoffset,xoffset+step,histNumber,paint);
            }
        }
    }

    @OnClick(id= R.id.toolbar)
    void clickToolbar() {

        //finish();
    }
}
