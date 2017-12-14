package com.cv4j.app.adapter;

import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cv4j.app.R;
import com.cv4j.core.filters.CommonFilter;
import com.cv4j.rxjava.RxImageData;
import com.safframework.tony.common.utils.Preconditions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tony Shen on 2017/3/27.
 */

public class SpitalConvAdapter extends RecyclerView.Adapter<SpitalConvAdapter.ViewHolder> {

    private List<String> mList;
    private Bitmap mBitmap;
    private Map<Integer,String> map;

    public SpitalConvAdapter(List<String> data, Bitmap bitmap) {

        mList = data;
        mBitmap = bitmap;
        map = new HashMap<>();
        int index0 = 0;
        map.put(index0, "原图");
        int index1 = 1;
        map.put(index1, "卷积");
        int index2 = 2;
        map.put(index2, "最大最小值滤波");
        int index3 = 3;
        map.put(index3, "椒盐噪声");
        int index4 = 4;
        map.put(index4, "锐化");
        int index5 = 5;
        map.put(index5, "中值滤波");
        int index6 = 6;
        map.put(index6, "拉普拉斯");
        int index7 = 7;
        map.put(index7, "寻找边缘");
        int index8 = 8;
        map.put(index8, "梯度");
        int index9 = 9;
        map.put(index9, "方差滤波");
        int index10 = 10;
        map.put(index10, "马尔操作");
        int index11 = 11;
        map.put(index11, "USM");
    }

    @Override
    public SpitalConvAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SpitalConvAdapter.ViewHolder(parent, R.layout.cell_gridview_filter);
    }

    @Override
    public void onBindViewHolder(final SpitalConvAdapter.ViewHolder holder, int position) {

        if (position == 0) {
            holder.image.setImageBitmap(mBitmap);
        } else {
            String filterName = mList.get(position);
            if (Preconditions.isNotBlank(filterName)) {
                CommonFilter filter = getFilter(filterName);
                RxImageData.bitmap(mBitmap)
//                        .placeHolder(R.drawable.test_spital_conv)
                        .addFilter(filter)
                        .into(holder.image);
            }
        }

        holder.text.setText(map.get(position));
    }

    private CommonFilter getFilter(String filterName) {
        CommonFilter filter = null;
        String className = filterName + "Filter";

        try {
            Class filterClass = Class.forName("com.cv4j.core.spatial.conv." + className);
            filter = (CommonFilter) filterClass.newInstance();
        } catch (ClassNotFoundException e) {
            System.out.println("Class " + filter + " not found");
        } catch (InstantiationException e) {
            System.out.println("Instantiation error for class " + className);
        } catch (IllegalAccessException e) {
            System.out.println("Illegal acces error for class " + className);
        }

        return filter;
    }

    @Override
    public int getItemCount() {
        return mList!=null?mList.size():0;
    }
    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView. 
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;

        private TextView text;

        public ViewHolder(ViewGroup parent, @LayoutRes int resId) {
            super(LayoutInflater.from(parent.getContext()).inflate(resId, parent, false));

            image = (ImageView)itemView.findViewById(R.id.image);

            text = (TextView)itemView.findViewById(R.id.text);
        }
    }
}
