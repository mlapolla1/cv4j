/*
 * Copyright (c) 2017 - present, CV4J Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cv4j.app.fragment;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseFragment;
import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.hist.GaussianBackProjection;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

/**
 * Created by Tony Shen on 2017/8/13.
 */

public class GaussianBackFragment extends BaseFragment {

    @InjectView(R.id.result)
    ImageView result;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_back_project, container, false);
        Injector.injectInto(this, v);

        initData();
        return v;
    }

    private void initData() {
        Resources res = getResources();

        BackFragment bf = new BackFragment();
        bf.initData(res);

        ColorProcessor colorProcessor  = bf.getColorProcessor();
        ColorProcessor sampleProcessor = bf.getSampleProcessor();
        ByteProcessor  byteProcessor   = bf.getByteProcessor();

        GaussianBackProjection gaussianBackProjection = new GaussianBackProjection();
        gaussianBackProjection.backProjection(colorProcessor, sampleProcessor, byteProcessor);

        result.setImageBitmap(byteProcessor.getImage().toBitmap());
    }

}
