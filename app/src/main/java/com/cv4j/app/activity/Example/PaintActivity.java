package com.cv4j.app.activity.Example;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.cv4j.app.R;
import com.cv4j.app.adapter.PaintAdapter;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.app.fragment.PaintFragment;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Shen on 2017/5/7.
 */

public class PaintActivity extends BaseActivity {

    @InjectView(R.id.tablayout)
    TabLayout mTabLayout;

    @InjectView(R.id.viewpager)
    ViewPager mViewPager;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oil_paint);

        initData();
    }

    private void initData() {
        List<Fragment> mList = new ArrayList();
        toolbar.setTitle("< "+title);
        int instance0 = 0;
        int instance1 = 1;
        int instance2 = 2;
        mList.add(PaintFragment.newInstance(instance0));
        mList.add(PaintFragment.newInstance(instance1));
        mList.add(PaintFragment.newInstance(instance2));
        mViewPager.setAdapter(new PaintAdapter(this.getSupportFragmentManager(),mList));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @OnClick(id= R.id.toolbar)
    void clickToolbar() {

        finish();
    }
}
