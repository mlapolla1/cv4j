package com.cv4j.app.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView.RecycledViewPool;
import android.support.v7.widget.Toolbar;

import com.cv4j.app.R;
import com.cv4j.app.adapter.GridViewFilterAdapter;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.app.ui.DividerGridItemDecoration;
import com.cv4j.app.ui.GridRecyclerView;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tony Shen on 2017/3/15.
 */

public class GridViewFilterActivity extends BaseActivity {

    @InjectView(R.id.recyclerview)
    GridRecyclerView recyclerview;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gridview_filter);

        initViews();
        initData();
    }

    private void initViews() {

        toolbar.setTitle("< "+title);
    }

    private void initData() {
        Resources res = getResources();
        String[] filterNames = res.getStringArray(R.array.filterNames);
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.test_mm);
        List<String> list = new ArrayList<>();

        list.addAll(Arrays.asList(filterNames));
        int gridLayoutManagerNumber = 3;
        GridLayoutManager manager = new GridLayoutManager(GridViewFilterActivity.this, gridLayoutManagerNumber);
        manager.setRecycleChildrenOnDetach(true);
        recyclerview.setLayoutManager(manager);
        recyclerview.setAdapter(new GridViewFilterAdapter(list,bitmap));
        recyclerview.addItemDecoration(new DividerGridItemDecoration(GridViewFilterActivity.this));

        RecycledViewPool myPool = new RecycledViewPool();
        final int maxRecycledViews1 = 0;
        final int maxRecycledViews2 = 10;
        myPool.setMaxRecycledViews(maxRecycledViews1, maxRecycledViews2);

        recyclerview.setRecycledViewPool(myPool);
    }

    @OnClick(id= R.id.toolbar)
    void clickToolbar() {

        finish();
    }

}
