package com.cv4j.app.fragment.Hist;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseFragment;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.hist.CalcHistogram;
import com.cv4j.core.hist.CompareHist;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

/**
 * Created by Tony Shen on 2017/6/10.
 */

public class CompareHist3Fragment extends BaseFragment {

    @InjectView(R.id.image0)
    ImageView image0;

    @InjectView(R.id.image1)
    ImageView image1;

    @InjectView(R.id.result)
    TextView result;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_compare_hist_3, container, false);
        Injector.injectInto(this, v);

        initData();
        return v;
    }

    /**
     * Data initialization.
     */
    private void initData() {
        ImageProcessor imageProcessor1 = initImage0();
        ImageProcessor imageProcessor2 = initImage1();

        final int bins = 256;
        int[][] source = new int[imageProcessor1.getChannels()][bins];
        int[][] target = new int[imageProcessor2.getChannels()][bins];

        CalcHistogram calcHistogram = new CalcHistogram();
        calcHistogram.calcRGBHist(imageProcessor1, bins, source, true);
        calcHistogram.calcRGBHist(imageProcessor2, bins, target, true);

        StringBuilder stringBuilder = compareHistograms(source, target);

        String compareHistogramsString = stringBuilder.toString();
        this.result.setText(compareHistogramsString);
    }

    /**
     * Compare histograms.
     * @param source The source histogram.
     * @param target The target histogram.
     * @return       The comparation.
     */
    private StringBuilder compareHistograms(int[][] source, int[][] target) {
        CompareHist compareHist = new CompareHist();
        StringBuilder stringBuilder = new StringBuilder(16);

        double sum1 = 0;
        double sum2 = 0;
        double sum3 = 0;

        final int sumLength = 3;
        for (int i = 0; i < sumLength; i++) {
            sum1 += compareHist.bhattacharyya(source[i],target[i]);
            sum2 += compareHist.covariance(source[i],target[i]);
            sum3 += compareHist.ncc(source[i],target[i]);
        }

        final String separator = "巴氏距离:";
        final String newLine   = "\r\n";

        stringBuilder
                .append(separator)
                .append(sum1 / sumLength)
                .append(newLine)
                .append(separator)
                .append(sum2 / sumLength)
                .append(newLine)
                .append(separator)
                .append(sum3 / sumLength);

        return stringBuilder;
    }

    /**
     * Initialization of image1.
     * @return The image processor of the image.
     */
    private ImageProcessor initImage1() {
        final Resources res = getResources();
        final Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.test_compare_hist2);

        this.image1.setImageBitmap(bitmap);

        CV4JImage cv4jImage = new CV4JImage(bitmap);
        return cv4jImage.getProcessor();
    }

    /**
     * Initialazion of image0.
     * @return The image processor of the image.
     */
    private ImageProcessor initImage0() {
        final Resources res = getResources();
        final Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.test_compare_hist1);

        this.image0.setImageBitmap(bitmap);

        CV4JImage cv4JImage = new CV4JImage(bitmap);
        return cv4JImage.getProcessor();
    }
}
