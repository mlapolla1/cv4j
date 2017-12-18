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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseFragment;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.number.FloatProcessor;
import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.datamodel.Point;
import com.cv4j.core.tpl.TemplateMatch;
import com.cv4j.core.tpl.TemplateMatch2;
import com.cv4j.image.util.Tools;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

/**
 * Created by tony on 2017/9/16.
 */

public class TemplateMatchFragment extends BaseFragment {

    @InjectView(R.id.target_image)
    ImageView targetImage;

    @InjectView(R.id.template_image)
    ImageView templateImage;

    @InjectView(R.id.result)
    ImageView resultImage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_template_match, container, false);
        Injector.injectInto(this, v);

        initData();
        return v;
    }

    /**
     * Data initialization.
     */
    private void initData() {
        final ImageProcessor targetImageProcessor   = initTargetImage();
        final ImageProcessor templateImageProcessor = initTemplateImage();

        Point[] points = getPoints(targetImageProcessor, templateImageProcessor);

        Point resultPoint;
        if (points != null) {
            resultPoint = points[0];

            final Resources res = getResources();
            final Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.test_tpl_target);
            Canvas canvas = new Canvas(bitmap);

            Rect  rect  = createRect(templateImageProcessor, resultPoint);
            Paint paint = createPaint();

            canvas.drawRect(rect, paint);

            this.resultImage.setImageBitmap(bitmap);
        }
    }

    /**
     * Creation of a paint object.
     * @return The paint object.
     */
    private Paint createPaint() {
        Paint paint = new Paint();

        paint.setStyle(Paint.Style.STROKE);

        final float width = 4f;
        paint.setStrokeWidth(width);

        paint.setColor(Color.RED);

        return paint;
    }

    /**
     * Creation of a rect object.
     * @param templateImageProcessor The template image processor.
     * @param resultPoint            The point
     * @return                       The rect object.
     */
    private Rect createRect(ImageProcessor templateImageProcessor, Point resultPoint) {
        Rect rect = new Rect();

        final int width  = templateImageProcessor.getWidth();
        final int height = templateImageProcessor.getHeight();

        final int sx = resultPoint.x + (width / 2);
        final int sy = resultPoint.y - (height / 2);

        rect.set(sx, sy, sx+width, sy+height);

        return rect;
    }

    /**
     * Return the points.
     * @param targetImageProcessor   The target image processor.
     * @param templateImageProcessor The template image processor.
     * @return                       The points.
     */
    private Point[] getPoints(ImageProcessor targetImageProcessor, ImageProcessor templateImageProcessor) {
        TemplateMatch2 match = new TemplateMatch2();
        FloatProcessor floatProcessor = match.match(targetImageProcessor, templateImageProcessor, TemplateMatch.TM_CCORR_NORMED);

        final float[] gray   = floatProcessor.getGray();
        final int     width  = floatProcessor.getWidth();
        final int     height = floatProcessor.getHeight();

        return Tools.getMinMaxLoc(gray, width, height);
    }

    /**
     * Initialization of the templateImage.
     * @return The image processor of the image.
     */
    private ImageProcessor initTemplateImage() {
        final Resources res = getResources();
        final Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.tpl);

        this.templateImage.setImageBitmap(bitmap);

        CV4JImage templateCV4J = new CV4JImage(bitmap);
        return templateCV4J.convert2Gray().getProcessor();
    }

    /**
     * Inizialization of targetImage.
     * @return The image processor of the image.
     */
    private ImageProcessor initTargetImage() {
        final Resources res = getResources();
        final Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.test_tpl_target);

        this.targetImage.setImageBitmap(bitmap);

        CV4JImage cv4JImage = new CV4JImage(bitmap);

        return cv4JImage.getProcessor();
    }
}
