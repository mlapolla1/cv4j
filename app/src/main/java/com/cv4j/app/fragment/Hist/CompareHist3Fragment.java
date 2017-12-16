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

    private void initData() {
        Resources res = getResources();
        final Bitmap bitmap1 = BitmapFactory.decodeResource(res, R.drawable.test_compare_hist1);
        final Bitmap bitmap2 = BitmapFactory.decodeResource(res, R.drawable.test_compare_hist2);

        image0.setImageBitmap(bitmap1);
        image1.setImageBitmap(bitmap2);

        CV4JImage cv4jImage1 = new CV4JImage(bitmap1);
        ImageProcessor imageProcessor1 = cv4jImage1.getProcessor();

        CV4JImage cv4jImage2 = new CV4JImage(bitmap2);
        ImageProcessor imageProcessor2 = cv4jImage2.getProcessor();

        int[][] source = null;
        int[][] target = null;

        CalcHistogram calcHistogram = new CalcHistogram();
        int bins = 256;
        source = new int[imageProcessor1.getChannels()][bins];
        calcHistogram.calcRGBHist(imageProcessor1,bins,source,true);

        target = new int[imageProcessor2.getChannels()][bins];
        calcHistogram.calcRGBHist(imageProcessor2,bins,target,true);

        CompareHist compareHist = new CompareHist();
        StringBuilder sb = new StringBuilder();

        double sum1 = 0;
        double sum2 = 0;
        double sum3 = 0;

        int sumLength = 3;
        for (int i = 0; i < sumLength; i++) {
            sum1 += compareHist.bhattacharyya(source[i],target[i]);
            sum2 += compareHist.covariance(source[i],target[i]);
            sum3 += compareHist.ncc(source[i],target[i]);
        }

        String distance = "巴氏距离:";
        String covariance = "协方差:";
        String correlation = "相关性因子:";
        String newLine = "\r\n";
        sb.append(distance)
          .append(sum1 / sumLength)
          .append(newLine)
          .append(covariance)
          .append(sum2 / sumLength)
          .append(newLine)
          .append(correlation)
          .append(sum3 / sumLength);

        result.setText(sb.toString());
    }
}
