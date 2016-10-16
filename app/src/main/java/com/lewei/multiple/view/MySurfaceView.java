package com.lewei.multiple.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaFormat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import com.lewei.lib.H264Frame;
import com.lewei.lib.LeweiLib;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@TargetApi(16)
public class MySurfaceView extends SurfaceView implements Callback {
    private static int mScreenHeight;
    private static int mScreenWidth;
    private int Hardware_flag;
    private Canvas canvas;
    private MediaCodec decoder;
    private Bitmap drawBmp;
    private ByteBuffer[] inputBuffers;
    InputStream inputstream;
    private boolean isGetBitmap;
    private boolean isThreadStop;
    private Thread mDrawThread;
    private Paint f1p;
    private Rect rect;
    private SurfaceHolder sfh;

    /* renamed from: com.lewei.multiple.view.MySurfaceView.1 */
    class C00941 implements Runnable {
        C00941() {
        }

        public void run() {
            while (!MySurfaceView.this.isThreadStop) {
                if (MySurfaceView.this.isGetBitmap) {
                    MySurfaceView.this.isGetBitmap = false;
                    MySurfaceView.this.canvas = MySurfaceView.this.sfh.lockCanvas(MySurfaceView.this.rect);
                    if (MySurfaceView.this.canvas != null) {
                        MySurfaceView.this.canvas.drawBitmap(MySurfaceView.this.drawBmp, null, MySurfaceView.this.rect, MySurfaceView.this.f1p);
                        if (MySurfaceView.this.sfh != null) {
                            MySurfaceView.this.sfh.unlockCanvasAndPost(MySurfaceView.this.canvas);
                        }
                    } else {
                        return;
                    }
                }
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.inputstream = null;
        this.Hardware_flag = 1;
        this.isThreadStop = false;
        this.isGetBitmap = false;
        initialize();
        this.f1p = new Paint();
        this.f1p.setAntiAlias(true);
        this.sfh = getHolder();
        this.sfh.addCallback(this);
        setFocusable(true);
        this.Hardware_flag = LeweiLib.Hardware_flag;
    }

    public void setBitmap(Bitmap bmp) {
        if (bmp != null && !this.isGetBitmap) {
            this.drawBmp = bmp;
            this.isGetBitmap = true;
        }
    }

    private void startDrawThread() {
        if (this.mDrawThread == null || !this.mDrawThread.isAlive()) {
            this.mDrawThread = new Thread(new C00941());
            this.mDrawThread.start();
        }
    }

    private void initialize() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        this.rect = new Rect(0, 0, mScreenWidth, mScreenHeight);
    }

    public void doHardDecodeDraw(H264Frame mFrame) {
        if (this.decoder != null) {
            int inputBufferIndex = this.decoder.dequeueInputBuffer(0);
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = this.inputBuffers[inputBufferIndex];
                inputBuffer.clear();
                inputBuffer.put(mFrame.data, 0, mFrame.size);
                this.decoder.queueInputBuffer(inputBufferIndex, 0, mFrame.size, (long) mFrame.timestamp, 0);
            }
            BufferInfo bufferInfo = new BufferInfo();
            int outputBufferIndex = this.decoder.dequeueOutputBuffer(bufferInfo, 0);
            while (outputBufferIndex >= 0) {
                this.decoder.releaseOutputBuffer(outputBufferIndex, true);
                outputBufferIndex = this.decoder.dequeueOutputBuffer(bufferInfo, 0);
            }
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.isThreadStop = true;
    }

    public void releaseDecoder() {
        if (this.decoder != null) {
            this.decoder.stop();
            this.decoder.release();
            this.decoder = null;
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (this.Hardware_flag > 0) {
            try {
                this.decoder = MediaCodec.createDecoderByType("video/avc");
            } catch (IOException e) {

            }
            //this.decoder = MediaCodec.createDecoderByType("video/avc");
            MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", 1280, 720);
            mediaFormat.setInteger("bitrate", 125000);
            mediaFormat.setInteger("frame-rate", 20);
            mediaFormat.setInteger("color-format", 19);
            mediaFormat.setInteger("i-frame-interval", 2);
            this.decoder.configure(mediaFormat, holder.getSurface(), null, 0);
            this.decoder.start();
            this.inputBuffers = this.decoder.getInputBuffers();
        }
        startDrawThread();
    }
}
