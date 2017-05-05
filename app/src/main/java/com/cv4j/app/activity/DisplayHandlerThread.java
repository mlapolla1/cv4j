package com.cv4j.app.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.filters.BeautySkinFilter;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;

/**
 * Created by Tony Shen on 2017/5/5.
 */

public class DisplayHandlerThread extends HandlerThread {

    private static final int WHAT_CREATE_BITMAP_FROM_PREVIEW = 3;

    private Handler mHandler = null;
    WeakReference<Context> weakReference = null;
    private static String TAG = "DisplayHandlerThread";
    private SurfaceHolder holder;

    DisplayHandlerThread(Context context) {
        super(TAG);
        start();
        mHandler = new Handler(getLooper());
        weakReference = new WeakReference<>(context);
    }

    public void startDisplay() {
        mHandler = new Handler(getLooper(), new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Context context = weakReference.get();
                if (context == null) {

                    return false;
                }

                if (msg.what == WHAT_CREATE_BITMAP_FROM_PREVIEW) {
                    BitmapThreadPool.post(new Runnable() {
                        MakeBitmapData data;

                        @Override
                        public void run() {
                            makePreviewBitmap(data);
                        }

                        public Runnable init(MakeBitmapData data) {
                            this.data = data;
                            return this;
                        }
                    }.init((MakeBitmapData) msg.obj));
                }
                return true;
            }
        });
    }

    public void queueMakeBitmapFromPreview(byte[] data, Camera camera, Camera.Parameters parameters, Rect rect, SurfaceHolder holder) {

        MakeBitmapData bitmapData = new MakeBitmapData();
        bitmapData.data = data;
        bitmapData.parameters = parameters;
        bitmapData.rect = rect;
        this.holder = holder;
        Context context = weakReference.get();
        if (context != null) {
            mHandler.obtainMessage(WHAT_CREATE_BITMAP_FROM_PREVIEW, bitmapData)
                    .sendToTarget();
        }
    }

    private void makePreviewBitmap(MakeBitmapData bitmapData) {
        Log.i(TAG, "Called make preview bitmap");
        Context context = weakReference.get();
        if (context != null) {

            int previewWidth = bitmapData.parameters.getPreviewSize().width;
            int previewHeight = bitmapData.parameters.getPreviewSize().height;

            byte[] yuv420sp = new byte[previewWidth * previewHeight * 3 / 2];
            NV21ToNV12(bitmapData.data, yuv420sp, previewWidth, previewHeight);

            YuvImage yuv = new YuvImage(bitmapData.data, bitmapData.parameters.getPreviewFormat(), previewWidth,
                    previewHeight, null);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            yuv.compressToJpeg(new Rect(0, 0, previewWidth, previewHeight), 50, out);

            byte[] bytes = out.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            CV4JImage cv4JImage = new CV4JImage(bitmap);
            BeautySkinFilter filter = new BeautySkinFilter();
            bitmap = filter.filter(cv4JImage.getProcessor()).getImage().toBitmap();

            synchronized (holder) {
                Canvas canvas = holder.lockCanvas();
                canvas.drawBitmap(bitmap, null, bitmapData.rect, null);
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }


    private void NV21ToNV12(byte[] nv21,byte[] nv12,int width,int height){
        if(nv21 == null || nv12 == null)return;
        int framesize = width*height;
        int i = 0,j = 0;
        System.arraycopy(nv21, 0, nv12, 0, framesize);
        for(i = 0; i < framesize; i++){
            nv12[i] = nv21[i];
        }
        for (j = 0; j < framesize/2; j+=2)
        {
            nv12[framesize + j-1] = nv21[j+framesize];
        }
        for (j = 0; j < framesize/2; j+=2)
        {
            nv12[framesize + j] = nv21[j+framesize-1];
        }
    }


    private static class MakeBitmapData {
        byte[] data;
        Camera.Parameters parameters;
        Rect rect;
    }
}
