package com.cv4j.app.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.core.filters.face.BeautySkinFilter;
import com.cv4j.rxjava.RxImageData;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

/**
 * Created by Tony Shen on 2017/4/23.
 */
public class BeautySkinActivity extends BaseActivity {

    @InjectView(R.id.origin_image1)
    ImageView originImage1;

    @InjectView(R.id.image1)
    ImageView image1;

    @InjectView(R.id.origin_image2)
    ImageView originImage2;

    @InjectView(R.id.image2)
    ImageView image2;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;

    private RxImageData rxImageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beauty_skin);

        initViews();
        initData();
    }

    private void initViews() {
        final String toolbarTitle = "< " + title;
        this.toolbar.setTitle(toolbarTitle);
    }

    private void initData() {
        Bitmap bitmap1 = createBitmapTestBeautySkin1();
        originImage1.setImageBitmap(bitmap1);
        applyFilterIntoImage1(bitmap1);

        Bitmap bitmap2 = createBitmapTestBeautySkin2();
        originImage2.setImageBitmap(bitmap2);
        applyFilterIntoImage2(bitmap2);
    }

    /**
     * Apply filter BeautySkinFilter to image2.
     * @see BeautySkinFilter
     * @param bitmap The bitmap to apply the filter.
     */
    private void applyFilterIntoImage2(Bitmap bitmap) {
        rxImageData = RxImageData.bitmap(bitmap);

        BeautySkinFilter beautySkinFilter = new BeautySkinFilter();

        rxImageData.addFilter(beautySkinFilter);
        rxImageData.into(image2);
    }

    /**
     * Apply filter BeautySkinFilter to image1.
     * @see BeautySkinFilter
     * @param bitmap The bitmap to apply the filter.
     */
    private void applyFilterIntoImage1(Bitmap bitmap) {
        rxImageData = RxImageData.bitmap(bitmap);

        BeautySkinFilter beautySkinFilter = new BeautySkinFilter();

        rxImageData.addFilter(beautySkinFilter);
        rxImageData.into(image1);
    }

    /**
     * Create the bitmap from test_beauty_skin2.
     * @return The bitmap.
     */
    private Bitmap createBitmapTestBeautySkin2() {
        final Resources res = getResources();
        return BitmapFactory.decodeResource(res, R.drawable.test_beauty_skin2);
    }

    /**
     * Create the bitmap from test_beauty_skin1.
     * @return The bitmap.
     */
    private Bitmap createBitmapTestBeautySkin1() {
        final Resources res = getResources();
        return BitmapFactory.decodeResource(res, R.drawable.test_beauty_skin1);
    }

    @OnClick(id= R.id.toolbar)
    void clickToolbar() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rxImageData.recycle();
    }
}
