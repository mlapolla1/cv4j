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

package com.cv4j.app.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.image.ImageData;
import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.datamodel.Rect;
import com.cv4j.core.pixels.Operator;
import com.cv4j.core.pixels.OperatorFunction;
import com.cv4j.exception.CV4JException;

import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

/**
 * Created by tony on 2017/11/5.
 */

public class PixelOperatorActivity extends BaseActivity {

    @InjectView(R.id.image1)
    ImageView image1;

    @InjectView(R.id.image2)
    ImageView image2;

    @InjectView(R.id.result_image)
    ImageView result;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;

    @InjectExtra(key = "Type")
    int type;

    SparseArray<OperatorFunction> operationsTwoImages;

    /**
     * Add operator.
     */
    public static final int ADD = 1;

    /**
     * Subtract operator.
     */
    public static final int SUBTRACT = 2;

    /**
     * Multiply operator.
     */
    public static final int MULTIPLE = 3;

    /**
     * Division operator.
     */
    public static final int DIVISION = 4;

    /**
     * Bitwise and operator.
     */
    public static final int BITWISE_AND = 5;

    /**
     * Bitwise or operator.
     */
    public static final int BITWISE_OR = 6;

    /**
     * Bitwise not operator.
     */
    public static final int BITWISE_NOT = 7;

    /**
     * Bitwise xor operator.
     */
    public static final int BITWISE_XOR = 8;

    /**
     * Add wright operator.
     */
    public static final int ADD_WEIGHT = 9;

    /**
     * Sub image operator.
     */
    public static final int SUB_IMAGE = 10;


    /**
     * Creation of the app.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pixel_operator);

        initViews();
        initData();
    }

    private void initViews() {
        final String toolbarTitle = "< " + this.title;
        this.toolbar.setTitle(toolbarTitle);
    }

    private void initData() {
        initOperationsTwoImages();

        ImageProcessor imageProcessor1 = initImage1();
        ImageProcessor imageProcessor2 = initImage2();

        ImageProcessor imageProcessor = executeOperationByType(this.type, imageProcessor1, imageProcessor2);

        if (imageProcessor != null) {
            final int   width  = imageProcessor.getWidth();
            final int   height = imageProcessor.getHeight();
            final int[] pixels = imageProcessor.getPixels();

            CV4JImage resultCV4JImage = new CV4JImage(width, height, pixels);

            Bitmap bitmap = resultCV4JImage.getProcessor().getImage().toBitmap();
            this.result.setImageBitmap(bitmap);
        }
    }

    private ImageProcessor executeOperationByType(int operationType, ImageProcessor imageProcessor1, ImageProcessor imageProcessor2) {
        ImageProcessor imageProcessor = null;
        OperatorFunction operator = operationsTwoImages.get(operationType);

        if (operator != null) {
            imageProcessor  = operator.call(imageProcessor1, imageProcessor2);
        } else {
            switch (operationType) {
                case BITWISE_NOT:
                    imageProcessor = calculateBitwiseNot(imageProcessor1);
                    break;
                case ADD_WEIGHT:
                    imageProcessor = calculateAddWeight(imageProcessor1, imageProcessor2);
                    break;
                case SUB_IMAGE:
                    imageProcessor = calculateSubImage(imageProcessor1);
                    break;

                default:
                    imageProcessor = calculateAdd(imageProcessor1, imageProcessor2);
                    break;
            }
        }

        return imageProcessor;
    }

    /**
     * Add operation.
     * @param imageProcessor1 The first image processor.
     * @param imageProcessor2 The second image processor.
     * @return                The result of the add operation.
     */
    private ImageProcessor calculateAdd(ImageProcessor imageProcessor1, ImageProcessor imageProcessor2) {
        return Operator.add(imageProcessor1,imageProcessor2);
    }

    /**
     * Sub image calculation.
     * @param imageProcessor1 The image processor.
     * @return                The result of the sub image operation.
     */
    private ImageProcessor calculateSubImage(ImageProcessor imageProcessor1) {
        ImageProcessor imageProcessor = null;

        Rect rect = new Rect();

        rect.x = rect.y = 0;
        rect.width = rect.height = 300;

        try {
            imageProcessor = Operator.subImage(imageProcessor1, rect);
        } catch (CV4JException e) {
            System.out.println("CV4J error on sub image operator.");
        }

        return imageProcessor;
    }

    /**
     * Bitwise not calculation.
     * @param imageProcessor1 The image processor.
     * @return                The result of the bitwise not operation.
     */
    private ImageProcessor calculateBitwiseNot(ImageProcessor imageProcessor1) {
        return Operator.bitwise_not(imageProcessor1);
    }

    /**
     * Add weight calculation.
     * @param imageProcessor1 The first image processor.
     * @param imageProcessor2 The second image processor.
     * @return                The result of the add weight operation.
     */
    private ImageProcessor calculateAddWeight(ImageProcessor imageProcessor1, ImageProcessor imageProcessor2) {
        final float weight1 = 2.0f;
        final float weight2 = 1.0f;
        final int gamma = 4;

        return Operator.addWeight(imageProcessor1, weight1, imageProcessor2, weight2, gamma);
    }

    /**
     * Initialization of operationsTwoImages variable.
     */
    private void initOperationsTwoImages() {
        final int dimTwoImages = 8;
        operationsTwoImages = new SparseArray<>(dimTwoImages);

        operationsTwoImages.put(ADD, Operator::add);
        operationsTwoImages.put(SUBTRACT, Operator::subtract);
        operationsTwoImages.put(MULTIPLE, Operator::multiple);
        operationsTwoImages.put(DIVISION, Operator::division);
        operationsTwoImages.put(BITWISE_AND, Operator::bitwise_and);
        operationsTwoImages.put(BITWISE_OR, Operator::bitwise_or);
    }

    /**
     * Initialization of image2.
     * @return The image processor of the image.
     */
    private ImageProcessor initImage2() {
        final Resources res = getResources();
        final Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.pixel_test_2);

        this.image2.setImageBitmap(bitmap);

        CV4JImage cv4JImage = new CV4JImage(bitmap);
        return cv4JImage.getProcessor();
    }

    /**
     * Initialization of image1.
     * @return The image processor of the image.
     */
    private ImageProcessor initImage1() {
        final Resources res = getResources();
        final Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.pixel_test_1);

        this.image1.setImageBitmap(bitmap);

        CV4JImage cv4JImage = new CV4JImage(bitmap);
        return cv4JImage.getProcessor();
    }

    @OnClick(id= R.id.toolbar)
    void clickToolbar() {
        finish();
    }
}
