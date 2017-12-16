package com.cv4j.app.fragment.Back;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.ColorProcessor;
import com.safframework.injectview.annotations.InjectView;

public class BackFragment {

    /**
     * The byte processor.
     */
    private ByteProcessor byteProcessor;

    /**
     * The color processor.
     */
    private ColorProcessor colorProcessor;

    /**
     * The sample processor.
     */
    private ColorProcessor sampleProcessor;

    @InjectView(R.id.target_image)
    ImageView targetImage;

    @InjectView(R.id.sample_image)
    ImageView sampleImage;

	public void initData(Resources res) {
		Bitmap bitmap1 = BitmapFactory.decodeResource(res, R.drawable.test_project_target);
        targetImage.setImageBitmap(bitmap1);

        Bitmap bitmap2 = BitmapFactory.decodeResource(res, R.drawable.test_project_sample);
        sampleImage.setImageBitmap(bitmap2);

        CV4JImage cv4jImage = new CV4JImage(bitmap1);
        this.colorProcessor = (ColorProcessor)cv4jImage.getProcessor();

        int w = colorProcessor.getWidth();
        int h = colorProcessor.getHeight();

        // 反向投影结果
        CV4JImage resultCV4JImage = new CV4JImage(w,h);
        this.byteProcessor = (ByteProcessor)resultCV4JImage.getProcessor();

        // sample
        CV4JImage sample = new CV4JImage(bitmap2);
        this.sampleProcessor = (ColorProcessor)sample.getProcessor();
	}

	public ByteProcessor getByteProcessor() {
	    return byteProcessor;
    }

    public ColorProcessor getColorProcessor() {
	    return colorProcessor;
    }

    public ColorProcessor getSampleProcessor() {
	    return sampleProcessor;
    }
}