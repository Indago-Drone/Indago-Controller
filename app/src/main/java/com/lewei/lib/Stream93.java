package com.lewei.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import com.lewei.multiple.utils.PathConfig;
import com.lewei.multiple.view.MySurfaceView;

public class Stream93 {
    private Context context;
    private Handler handler;
    private boolean isStop93;
    private boolean is_recording_now;
    private H264Frame mFrame;
    private MySurfaceView mSurfaceView;
    private Thread mThread93;

    /* renamed from: com.lewei.lib.Stream93.1 */
    class C00431 implements Runnable {
        C00431() {
        }

        public void run() {
            Stream93.this.isStop93 = false;
            boolean has_create_bmp = false;
            Bitmap bmp = null;
            while (!Stream93.this.isStop93) {
                if (LeweiLib.LW93StartLiveStream(1, LeweiLib.HD_flag) > 0) {
                    while (!Stream93.this.isStop93) {
                        if (LeweiLib.Hardware_flag > 0) {
                            System.out.println("hard_ware");
                            Stream93.this.mFrame = LeweiLib.getH264Frame();
                            if (Stream93.this.mFrame == null) {
                                Stream93.this.msleep(5);
                            } else {
                                if (!has_create_bmp) {
                                    Stream93.this.handler.sendEmptyMessage(1);
                                    has_create_bmp = true;
                                }
                                Stream93.this.mSurfaceView.doHardDecodeDraw(Stream93.this.mFrame);
                            }
                        } else {
                            System.out.println("else");
                            int ret = LeweiLib.LW93DrawBitmapFrame(bmp);
                            if (!has_create_bmp && LeweiLib.getFrameHeight() > 0 && LeweiLib.getFrameWidth() > 0) {
                                bmp = Bitmap.createBitmap(LeweiLib.getFrameWidth(), LeweiLib.getFrameHeight(), Config.ARGB_8888);
                                has_create_bmp = true;
                                Stream93.this.handler.sendEmptyMessage(1);
                            }
                            if (ret > 0) {
                                Stream93.this.mSurfaceView.setBitmap(bmp);
                            } else if (ret == 0) {
                                Stream93.this.msleep(1);
                            } else {
                                Stream93.this.msleep(1);
                            }
                        }
                    }
                } else {
                    Stream93.this.msleep(200);
                }
            }
            Stream93.this.isStop93 = true;
            LeweiLib.LW93StopLiveStream();
            //Object a = null; a.toString();
            Stream93.this.mSurfaceView.releaseDecoder();// findme stream
        }
    }

    public Stream93(Context context, Handler handler, MySurfaceView mSurfaceView) {
        this.isStop93 = false;
        this.is_recording_now = false;
        this.context = context;
        this.handler = handler;
        this.mSurfaceView = mSurfaceView;
    }

    public void stopStream93() {
        this.isStop93 = true;
        LeweiLib.LW93StopLocalRecord();
    }

    public void startStream93() {
        if (this.mThread93 == null || !this.mThread93.isAlive()) {
            this.mThread93 = new Thread(new C00431());
            this.mThread93.start();
        }
    }

    public void takeRecord() {
        if (this.is_recording_now) {
            LeweiLib.LW93StopLocalRecord();
            this.handler.sendEmptyMessage(19);
            this.is_recording_now = false;
        } else if (LeweiLib.LW93StartLocalRecord(PathConfig.getVideoPath(), 15) > 0) {
            this.is_recording_now = true;
            this.handler.sendEmptyMessage(17);
        } else {
            this.handler.sendEmptyMessage(18);
        }
    }

    private void msleep(int ms) {
        try {
            Thread.sleep((long) ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
