package com.cv4j.app.activity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;

/**
 * Created by Tony Shen on 2017/4/29.
 */

public class RealTimeFilterActivity extends BaseActivity {

    public int screenWidth, screenHeight;
    private CameraView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_filter);
        initData();
    }

    private void initData() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        cameraView= new CameraView(this,dm.widthPixels,dm.heightPixels);
        FrameLayout root = (FrameLayout) findViewById(R.id.root);
        //'index' indicates the order of the view. 0 means the view will behind all
        //other views. root.getChildCount() means the top
        root.addView(cameraView,0);
    }

}
