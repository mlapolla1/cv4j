package com.cv4j.app.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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
    DisplayHandlerThread handlerThread;

    public CameraView(Context context, int screenWidth, int screenHeight) {
        super(context);
        gHolder=this.getHolder();
        gHolder.addCallback(this);
        gHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        gSurfaceTexture=new SurfaceTexture(MAGIC_TEXTURE_ID);
        this.screenWidth=screenWidth;
        this.screenHeight=screenHeight;
        gRect=new Rect(0,0,screenWidth,screenHeight);

        handlerThread = new DisplayHandlerThread(context);
        handlerThread.startDisplay();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        parameters = gCamera.getParameters();
        List<Camera.Size> preSize = parameters.getSupportedPreviewSizes();
        previewWidth = preSize.get(0).width;
        previewHeight = preSize.get(0).height;
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

        handlerThread.queueMakeBitmapFromPreview(data, camera,parameters,gRect,gHolder);
    }
}
