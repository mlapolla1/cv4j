package com.cv4j.app.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.filters.BeautySkinFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Tony Shen on 2017/4/29.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback,
        Camera.PreviewCallback {


    private static final int MAGIC_TEXTURE_ID = 10;
    public SurfaceHolder gHolder;
    public SurfaceTexture gSurfaceTexture;
    public Camera gCamera;
    public byte gBuffer[];
    public int textureBuffer[];
    //public ProcessThread gProcessThread;
    private int bufferSize;
    private Camera.Parameters parameters;
    public int previewWidth, previewHeight;
    public int screenWidth, screenHeight;
    public Bitmap gBitmap;
    private Rect gRect;

    public CameraView(Context context, int screenWidth, int screenHeight) {
        super(context);
        gHolder=this.getHolder();
        gHolder.addCallback(this);
        gHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        gSurfaceTexture=new SurfaceTexture(MAGIC_TEXTURE_ID);
        this.screenWidth=screenWidth;
        this.screenHeight=screenHeight;
        gRect=new Rect(0,0,screenWidth,screenHeight);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        parameters = gCamera.getParameters();
        List<Camera.Size> preSize = parameters.getSupportedPreviewSizes();
        previewWidth = preSize.get(0).width;
        previewHeight = preSize.get(0).height;
//        for (int i = 1; i < preSize.size(); i++) {
//            double similarity = Math
//                    .abs(((double) preSize.get(i).height / screenHeight)
//                            - ((double) preSize.get(i).width / screenWidth));
//            if (similarity < Math.abs(((double) previewHeight / screenHeight)
//                    - ((double) previewWidth / screenWidth))) {
//                previewWidth = preSize.get(i).width;
//                previewHeight = preSize.get(i).height;
//            }
//        }

        gBitmap= Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.RGB_565);
        parameters.setPreviewSize(previewWidth, previewHeight);
        gCamera.setParameters(parameters);
        bufferSize = previewWidth * previewHeight;
        textureBuffer=new int[bufferSize];
        bufferSize  = 2*bufferSize * ImageFormat.getBitsPerPixel(parameters.getPreviewFormat()) / 8;
        gBuffer = new byte[bufferSize];
        gCamera.addCallbackBuffer(gBuffer);
        gCamera.setPreviewCallbackWithBuffer(this);
        gCamera.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (gCamera == null) {
            gCamera = Camera.open();
        }
        try {
            gCamera.setPreviewTexture(gSurfaceTexture);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        gCamera.stopPreview();
        gCamera.release();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        camera.addCallbackBuffer(gBuffer);

        byte[] yuv420sp = new byte[previewWidth*previewHeight*3/2];
        NV21ToNV12(data,yuv420sp,previewWidth,previewHeight);

        YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), previewWidth, previewHeight, null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuv.compressToJpeg(new Rect(0, 0, previewWidth, previewHeight), 50, out);

        byte[] bytes = out.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        CV4JImage cv4JImage = new CV4JImage(bitmap);
        BeautySkinFilter filter = new BeautySkinFilter();
        gBitmap = filter.filter(cv4JImage.getProcessor()).getImage().toBitmap();

        synchronized (gHolder)
        {
            Canvas canvas = this.getHolder().lockCanvas();
            canvas.drawBitmap(gBitmap, null,gRect, null);
            this.getHolder().unlockCanvasAndPost(canvas);
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
}
