package com.cv4j.app.activity.Example;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.cv4j.app.R;
import com.cv4j.app.activity.ActivityUtility;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.core.binary.ConnectedAreaLabel;
import com.cv4j.core.binary.Erode.Erode;
import com.cv4j.core.binary.Threshold;
import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.Size;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

/**
 * Created by Tony Shen on 2017/4/16.
 */

public class CoinsActivity extends BaseActivity {

    @InjectView(R.id.image0)
    ImageView image0;

    @InjectView(R.id.image1)
    ImageView image1;

    @InjectView(R.id.image2)
    ImageView image2;

    @InjectView(R.id.image3)
    ImageView image3;

    @InjectView(R.id.num)
    TextView numTextView;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coins);

        initViews();
        initData();
    }

    private void initViews() {
        final String toolbarTitle = "< " + this.title;
        this.toolbar.setTitle(toolbarTitle);
    }

    /**
     * Data initialization.
     */
    private void initData() {
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test_coins);
        final CV4JImage cv4JImage = new CV4JImage(bitmap);
        final ByteProcessor byteProcessor = (ByteProcessor) cv4JImage.getProcessor();

        initImage0(bitmap);
        initImage1(cv4JImage);
        initImage2(cv4JImage);

        ConnectedAreaLabel connectedAreaLabel = new ConnectedAreaLabel();
        int[] mask = new int[byteProcessor.getWidth() * byteProcessor.getHeight()];
        int num = connectedAreaLabel.process(byteProcessor, mask, null, false); // 获取连通组件的个数

        initImage3(cv4JImage, mask);

        if (num > 0) {
            String numString = String.valueOf(num);
            this.numTextView.setText(numString);
        }
    }

    /**
     * Initialization of image3.
     * @param cv4JImage
     * @param mask
     */
    private void initImage3(CV4JImage cv4JImage, int[] mask) {
        ActivityUtility util = new ActivityUtility();
        Bitmap newBitmap = util.subInitData(cv4JImage, mask);

        this.image3.setImageBitmap(newBitmap);
    }

    /**
     * Initialization of image2
     * @param cv4JImage
     */
    private void initImage2(CV4JImage cv4JImage) {
        Erode erode = new Erode();
        cv4JImage.resetBitmap();

        int size = 3;
        int iteration = 10;
        ByteProcessor byteProcessor = (ByteProcessor) cv4JImage.getProcessor();
        Size processSize = new Size(size);

        erode.process(byteProcessor, processSize, iteration);

        Bitmap bitmap = byteProcessor.getImage().toBitmap();
        this.image2.setImageBitmap(bitmap);
    }

    /**
     * Initialization of image1.
     * @param cv4JImage
     */
    private void initImage1(CV4JImage cv4JImage) {
        final int MAX_RGB = 255;

        Threshold threshold = new Threshold();
        threshold.process((ByteProcessor) cv4JImage.convert2Gray().getProcessor(), Threshold.THRESH_OTSU,Threshold.METHOD_THRESH_BINARY_INV, MAX_RGB);

        this.image1.setImageBitmap(cv4JImage.getProcessor().getImage().toBitmap());
    }

    /**
     * Initialization of image0.
     * @param bitmap
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
