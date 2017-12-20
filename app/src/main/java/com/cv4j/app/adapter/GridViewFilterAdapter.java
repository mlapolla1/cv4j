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

import java.util.List;

import static android.support.v7.widget.RecyclerView.*;

/**
 * Created by Tony Shen on 2017/3/15.
 */

public class GridViewFilterAdapter extends Adapter<GridViewFilterAdapter.ViewHolderGFA> {

    private List<String> mList;
    private Bitmap mBitmap;

    public GridViewFilterAdapter(List<String> data, Bitmap bitmap) {
        mList = data;
        mBitmap = bitmap;
    }

    @Override
    public ViewHolderGFA onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderGFA(parent, R.layout.cell_gridview_filter);
    }

    @Override
    public void onBindViewHolder(final ViewHolderGFA holder, int position) {

        String filterName = mList.get(position);

        if (position == 0) {
            holder.getImage().setImageBitmap(mBitmap);
        } else {

            if (Preconditions.isNotBlank(filterName)) {
                CommonFilter filter = getFilter(filterName);
                RxImageData.bitmap(mBitmap)
                        .addFilter(filter)
                        .into(holder.getImage());
            }

        }

        holder.getText().setText(filterName);
    }

    private CommonFilter getFilter(String filterName) {
        CommonFilter filter = null;
        String className = filterName + "Filter";

        try {
            Class filterClass = Class.forName("com.cv4j.core.filters." + className);
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
    public class ViewHolderGFA extends ViewHolder {

        /**
         * Image view.
         */
        private ImageView image;

        /**
         * Text view.
         */
        private TextView text;

        public ViewHolderGFA(ViewGroup parent, @LayoutRes int resId) {
            super(LayoutInflater.from(parent.getContext()).inflate(resId, parent, false));

            this.image = itemView.findViewById(R.id.image);
            this.text  = itemView.findViewById(R.id.text);
        }

        public ImageView getImage() {
            return image;
        }

        public TextView getText() {
            return text;
        }
    }
}
