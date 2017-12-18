package com.cv4j.app.activity.filter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.core.filters.image.ColorFilter;
import com.cv4j.rxjava.RxImageData;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tony Shen on 2017/3/15.
 */

public class ColorFilterActivity extends BaseActivity {

    @InjectView(R.id.image)
    ImageView image;

    @InjectView(R.id.scroll_view)
    HorizontalScrollView scrollView;

    @InjectView(R.id.linear)
    LinearLayout linear;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;

    private Bitmap bitmap;

    private SparseArray<String> colorStyles;

    private RxImageData rxImageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_filter);

        initViews();
        initData();
    }

    /**
     * Initialize the views.
     */
    private void initViews() {
        final String toolbarTitle = "< " + this.title;
        this.toolbar.setTitle(toolbarTitle);
    }

    /**
     * Initialize the data.
     */
    private void initData() {
        initRxImageData();
        initColorStyles();

        int colorStylesLength = colorStyles.size();
        for (int i = 0; i < colorStylesLength; i++) {
            initializeLinearLayout(i);
        }
    }

    /**
     * Initialization of a linear layout.
     * @param index The index of the linear layout.
     */
    private void initializeLinearLayout(int index) {
        final int left = 5;
        final int top = 0;
        final int right = 5;
        final int bottom = 20;

        LinearLayout.LayoutParams linearLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLp.setMargins(left, top, right, bottom);

        LinearLayout myLinear = new LinearLayout(this);
        myLinear.setOrientation(LinearLayout.HORIZONTAL);
        myLinear.setTag(index);
        this.linear.addView(myLinear, linearLp);

        LinearLayout.LayoutParams textViewLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView textView = new TextView(this);
        textView.setText(this.colorStyles.get(index));
        textView.setGravity(Gravity.CENTER);

        myLinear.addView(textView, textViewLp);

        setLinearLayoutClickListener(myLinear);
    }

    /**
     * Set a click listener on a linear layout.
     * @param myLinear The linear layout.
     */
    private void setLinearLayoutClickListener(LinearLayout myLinear) {
        myLinear.setOnClickListener(view -> {
            final String tagString  = (String) view.getTag();
            final int tagNumber     = Integer.valueOf(tagString);
            final String colorStyle = colorStyles.get(tagNumber);

            Toast.makeText(ColorFilterActivity.this, colorStyle, Toast.LENGTH_SHORT).show();

            ColorFilter colorFilter = new ColorFilter();
            colorFilter.setStyle(tagNumber);

            this.rxImageData.recycle();
            this.rxImageData = RxImageData.bitmap(bitmap);

            this.rxImageData.addFilter(colorFilter);
            this.rxImageData.isUseCache(false);
            this.rxImageData.into(image);
        });
    }

    /**
     * Initialize rxImageData.
     */
    private void initRxImageData() {
        final Resources res = getResources();
        this.bitmap = BitmapFactory.decodeResource(res, R.drawable.test_color_filter);

        this.rxImageData = RxImageData.bitmap(bitmap);

        final ColorFilter colorFilter = new ColorFilter();
        this.rxImageData.addFilter(colorFilter);
        this.rxImageData.isUseCache(false);
        this.rxImageData.into(image);
    }

    /**
     * Initialize color styles.
     */
    private void initColorStyles() {
        colorStyles = new SparseArray<>(12);

        colorStyles.put(ColorFilter.AUTUMN_STYLE, " 秋天风格 ");
        colorStyles.put(ColorFilter.BONE_STYLE, " 硬朗风格 ");
        colorStyles.put(ColorFilter.COOL_STYLE, " 凉爽风格 ");
        colorStyles.put(ColorFilter.HOT_STYLE, " 热带风格 ");
        colorStyles.put(ColorFilter.HSV_STYLE, " 色彩空间变换风格 ");
        colorStyles.put(ColorFilter.JET_STYLE, " 高亮风格 ");
        colorStyles.put(ColorFilter.OCEAN_STYLE, " 海洋风格 ");
        colorStyles.put(ColorFilter.PINK_STYLE, " 粉色风格 ");
        colorStyles.put(ColorFilter.RAINBOW_STYLE, " 彩虹风格 ");
        colorStyles.put(ColorFilter.SPRING_STYLE, " 春天风格 ");
        colorStyles.put(ColorFilter.SUMMER_STYLE, " 夏天风格 ");
        colorStyles.put(ColorFilter.WINTER_STYLE, " 冬天风格 ");
    }

    /**
     * Finish when toolbar is clicked.
     */
    @OnClick(id= R.id.toolbar)
    void clickToolbar() {
        finish();
    }

    /**
     * On app destoy.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        rxImageData.recycle();
    }
}
