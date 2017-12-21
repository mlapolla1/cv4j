package com.cv4j.app.activity.Detect;

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
import com.cv4j.core.binary.hough.HoughLinesP;
import com.cv4j.core.binary.Threshold;
import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.Line;
import com.cv4j.core.datamodel.image.ImageProcessor;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Shen on 2017/5/1.
 */

public class LineDetectionActivity extends BaseActivity {

    @InjectView(R.id.image0)
    ImageView image0;

    @InjectView(R.id.image1)
    ImageView image1;

    @InjectView(R.id.image2)
    ImageView image2;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_detection);

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

    private void initData() {
        final int width   = 4;

        initImage0();
        CV4JImage cv4JImage = initImage1();

        List<Line> lines = new ArrayList<>();
        createHoughLinesPProcess(cv4JImage, lines);

        initImage2(cv4JImage, width, lines);
    }

    private void initImage2(CV4JImage cv4JImage, int width, List<Line> lines) {
        Bitmap bitmap2 = Bitmap.createBitmap(cv4JImage.getProcessor().getImage().toBitmap());

        Canvas canvas = new Canvas(bitmap2);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(width);
        paint.setColor(Color.RED);

        for(Line line:lines) {
            canvas.drawLine(line.x1,line.y1,line.x2,line.y2,paint);
        }

        this.image2.setImageBitmap(bitmap2);
    }

    private void createHoughLinesPProcess(CV4JImage cv4JImage, List<Line> lines) {
        final int accSize = 12;
        final int minGap  = 10;
        final int minAcc  = 50;

        ByteProcessor byteProcessor = (ByteProcessor) cv4JImage.getProcessor();

        HoughLinesP houghLinesP = new HoughLinesP();
        houghLinesP.process(byteProcessor, accSize, minGap, minAcc, lines);
    }

    private CV4JImage initImage1() {
        final int maxRgb = 255;

        final Resources res = getResources();
        final Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.test_lines);

        CV4JImage cv4JImage = new CV4JImage(bitmap);
        ByteProcessor grayByteProcessor = (ByteProcessor) cv4JImage.convert2Gray().getProcessor();
        ImageProcessor imageProcessor = cv4JImage.getProcessor();

        Threshold threshold = new Threshold();
        threshold.process(grayByteProcessor, Threshold.THRESH_OTSU, Threshold.METHOD_THRESH_BINARY, maxRgb);
        this.image1.setImageBitmap(cv4JImage.getProcessor().getImage().toBitmap());

        return cv4JImage;
    }

    /**
     * Initialize image0.
     */
    private void initImage0() {
        final Resources res = getResources();
        final Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.test_lines);

        this.image0.setImageBitmap(bitmap);
    }

    @OnClick(id= R.id.toolbar)
    void clickToolbar() {

        finish();
    }
}
