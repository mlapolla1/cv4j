package com.cv4j.app.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.filters.CompositeFilters;
import com.cv4j.core.filters.effect.NatureFilter;
import com.cv4j.core.filters.effect.SpotlightFilter;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

import thereisnospon.codeview.CodeView;
import thereisnospon.codeview.CodeViewTheme;

/**
 * Created by Tony Shen on 2017/3/11.
 */

public class CompositeFilersActivity extends BaseActivity {

    @InjectView(R.id.image1)
    ImageView image1;

    @InjectView(R.id.image2)
    ImageView image2;

    @InjectView(R.id.image3)
    ImageView image3;

    @InjectView(R.id.codeview)
    CodeView codeView;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composite_filters);

        initViews();
        initData();
    }

    private void initViews() {
        toolbar.setTitle("< "+title);
    }

    private void initData() {
        String newLine = "\r\n";
        Resources res = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.test_filters);

        CV4JImage ci1 = new CV4JImage(bitmap);
        image1.setImageBitmap(new NatureFilter().filter(ci1.getProcessor()).getImage().toBitmap());

        CV4JImage ci2 = new CV4JImage(bitmap);
        image2.setImageBitmap(new SpotlightFilter().filter(ci2.getProcessor()).getImage().toBitmap());

        CompositeFilters compositeFilters = new CompositeFilters();
        Bitmap newBitmap = compositeFilters
                        .addFilter(new NatureFilter())
                        .addFilter(new SpotlightFilter())
                        .filter(new CV4JImage(bitmap).getProcessor())
                        .getImage()
                        .toBitmap();

        image3.setImageBitmap(newBitmap);

        codeView.setTheme(CodeViewTheme.ANDROIDSTUDIO).fillColor();

        StringBuilder code = new StringBuilder(16);
        code.append("CompositeFilters compositeFilters = new CompositeFilters();")
                .append(newLine)
                .append("Bitmap newBitmap = compositeFilters")
                .append(newLine)
                .append(".addFilter(new NatureFilter())").append(newLine)
                .append(".addFilter(new SpotlightFilter())").append(newLine)
                .append(".filter(new CV4JImage(bitmap).getProcessor())").append(newLine)
                .append(".getImage()").append(newLine)
                .append(".toBitmap();").append(newLine).append(newLine)
                .append("image3.setImageBitmap(newBitmap);");

        codeView.showCode(code.toString());
    }

    @OnClick(id= R.id.toolbar)
    void clickToolbar() {

        finish();
    }
}