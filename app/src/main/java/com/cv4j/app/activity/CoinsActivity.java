package com.cv4j.app.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseIntArray;
import android.widget.ImageView;
import android.widget.TextView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.core.binary.ConnectedAreaLabel;
import com.cv4j.core.binary.Erode;
import com.cv4j.core.binary.Threshold;
import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.Size;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;
import java.util.Random;

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

        initData();
    }

    private void initData() {
        final int MAX_RGB = 255;

        toolbar.setTitle("< "+title);
        Resources res = getResources();
        final Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.test_coins);
        image0.setImageBitmap(bitmap);

        CV4JImage cv4JImage = new CV4JImage(bitmap);
        Threshold threshold = new Threshold();
        threshold.process((ByteProcessor)(cv4JImage.convert2Gray().getProcessor()),Threshold.THRESH_OTSU,Threshold.METHOD_THRESH_BINARY_INV, MAX_RGB);
        image1.setImageBitmap(cv4JImage.getProcessor().getImage().toBitmap());

        Erode erode = new Erode();
        cv4JImage.resetBitmap();
        int size = 3;
        int iteration = 10;
        erode.process((ByteProcessor)cv4JImage.getProcessor(),new Size(size), iteration);
        image2.setImageBitmap(cv4JImage.getProcessor().getImage().toBitmap());

        ConnectedAreaLabel connectedAreaLabel = new ConnectedAreaLabel();
        int[] mask = new int[cv4JImage.getProcessor().getWidth() * cv4JImage.getProcessor().getHeight()];

        int num = connectedAreaLabel.process((ByteProcessor)cv4JImage.getProcessor(),mask,null,false); // 获取连通组件的个数

        ActivityUtility util = new ActivityUtility();
        util.subInitData(cv4JImage, mask);

        int height = cv4JImage.getProcessor().getHeight();
        int width = cv4JImage.getProcessor().getWidth();
        int size = height * width;
        for (int i = 0;i<size;i++) {
            int c = mask[i];
            if (c>=0) {
                colors.put(c,Color.argb(MAX_RGB, random.nextInt(MAX_RGB),random.nextInt(MAX_RGB),random.nextInt(MAX_RGB)));
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

        image3.setImageBitmap(newBitmap);

        if (num>0)
            numTextView.setText(String.format("总计识别出%d个硬币",num));
    }

    @OnClick(id= R.id.toolbar)
    void clickToolbar() {

        finish();
    }
}
