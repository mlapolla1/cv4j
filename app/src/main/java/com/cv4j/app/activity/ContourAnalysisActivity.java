package com.cv4j.app.activity;

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
import com.cv4j.core.binary.ConnectedAreaLabel;
import com.cv4j.core.binary.Contour.ContourAnalysis;
import com.cv4j.core.binary.Threshold;
import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.MeasureData;
import com.cv4j.core.datamodel.image.ImageProcessor;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;
import com.safframework.log.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Shen on 2017/5/1.
 */

public class ContourAnalysisActivity extends BaseActivity {

    @InjectView(R.id.image0)
    ImageView image0;

    @InjectView(R.id.image1)
    ImageView image1;

    @InjectView(R.id.image2)
    ImageView image2;

    @InjectView(R.id.image3)
    ImageView image3;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contour_analysis);

        initViews();
        initData();
    }

    /**
     * Initialize the views.
     */
    private void initViews() {
        final String toolbarTitle = "< " + title;
        this.toolbar.setTitle(toolbarTitle);
    }

    /**
     * Initialize the data.
     */
    private void initData() {
        final Resources res = getResources();
        final Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.test_ca);
        final CV4JImage cv4JImage = new CV4JImage(bitmap);
        final ByteProcessor byteProcessor   = (ByteProcessor) cv4JImage.getProcessor();

        initImage0(bitmap);

        initImage1(cv4JImage);

        ConnectedAreaLabel connectedAreaLabel = new ConnectedAreaLabel();
        connectedAreaLabel.setFilterNoise(true);
        int[] mask = new int[byteProcessor.getWidth() * byteProcessor.getHeight()];
        connectedAreaLabel.process(byteProcessor, mask, null, false);

        initImage2(cv4JImage, mask);

        initImage3(cv4JImage, mask);
    }

    /**
     * Initialization of image2.
     * @param cv4JImage The image.
     * @param mask      The mask.
     */
    private void initImage3(CV4JImage cv4JImage, int[] mask) {
        ActivityUtility util = new ActivityUtility();
        Bitmap bitmap = util.subInitData(cv4JImage, mask);

        // 轮廓分析
        ContourAnalysis ca = new ContourAnalysis();
        List<MeasureData> measureData = new ArrayList<>();
        ByteProcessor grayByteProcessor = (ByteProcessor) cv4JImage.convert2Gray().getProcessor();
        ca.process(grayByteProcessor, mask, measureData);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);

        for (MeasureData data:measureData) {
            canvas.drawText(data.toString(), data.getCp().x, data.getCp().y, paint);
            L.i(data.toString());
        }

        this.image3.setImageBitmap(bitmap);
    }

    /**
     * Initialization of image2.
     * @param cv4JImage The image.
     * @param mask      The mask.
     */
    private void initImage2(CV4JImage cv4JImage, int[] mask) {
        ActivityUtility util = new ActivityUtility();
        Bitmap bitmap = util.subInitData(cv4JImage, mask);

        this.image2.setImageBitmap(bitmap);
    }

    /**
     * Initialization of image 1.
     * @param cv4JImage The image
     */
    private void initImage1(CV4JImage cv4JImage) {
        final int MAX_RGB = 255;
        final ImageProcessor imageProcessor = cv4JImage.getProcessor();
        final ByteProcessor grayByteProcessor = (ByteProcessor) cv4JImage.convert2Gray().getProcessor();

        Threshold threshold = new Threshold();
        threshold.process(grayByteProcessor, Threshold.THRESH_OTSU, Threshold.METHOD_THRESH_BINARY, MAX_RGB);

        final Bitmap bitmap = imageProcessor.getImage().toBitmap();
        this.image1.setImageBitmap(bitmap);
    }

    /**
     * Initialization of the image0 with a given bitmap.
     * @param bitmap The bitmap.
     */
    private void initImage0(Bitmap bitmap) {
        this.image0.setImageBitmap(bitmap);
    }

    /**
     * Finish when the toolbar is clicked.
     */
    @OnClick(id= R.id.toolbar)
    void clickToolbar() {
        finish();
    }
}
