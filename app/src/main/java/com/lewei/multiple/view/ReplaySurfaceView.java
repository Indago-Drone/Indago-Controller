package com.lewei.multiple.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import com.lewei.lib.LeweiLib;
import com.lewei.lib63.LeweiLib63;
import java.io.InputStream;

public class ReplaySurfaceView extends SurfaceView implements Callback {
    private static int mScreenHeight;
    private static int mScreenWidth;
    private Bitmap bitmap;
    private Canvas canvas;
    private boolean has_image;
    InputStream inputstream;
    private boolean isStop;
    private Paint f2p;
    private Rect rect;
    private SurfaceHolder sfh;

    /* renamed from: com.lewei.multiple.view.ReplaySurfaceView.1 */
    class C00951 implements Runnable {
        private final /* synthetic */ int val$end;
        private final /* synthetic */ String val$name;
        private final /* synthetic */ int val$start;

        C00951(String str, int i, int i2) {
            this.val$name = str;
            this.val$start = i;
            this.val$end = i2;
        }

        public void run() {
            ReplaySurfaceView.this.isStop = false;
            boolean has_create_bmp = false;
            Bitmap bmp = null;
            ReplaySurfaceView.this.startDrawBmpThread();
            while (!ReplaySurfaceView.this.isStop) {
                if (LeweiLib.LW93StartRecordReplay(this.val$name, this.val$start, this.val$end, 1) > 0) {
                    while (!ReplaySurfaceView.this.isStop) {
                        int ret = LeweiLib.LW93DrawBitmapFrame(bmp);
                        if (!has_create_bmp && LeweiLib.getFrameHeight() > 0 && LeweiLib.getFrameWidth() > 0) {
                            bmp = Bitmap.createBitmap(1280, 720, Config.ARGB_8888);
                            has_create_bmp = true;
                        }
                        if (ret > 0) {
                            if (LeweiLib.getFrameWidth() <= 1000) {
                                ReplaySurfaceView.this.canvas = ReplaySurfaceView.this.sfh.lockCanvas(ReplaySurfaceView.this.rect);
                                if (ReplaySurfaceView.this.canvas != null) {
                                    ReplaySurfaceView.this.canvas.drawBitmap(bmp, null, ReplaySurfaceView.this.rect, ReplaySurfaceView.this.f2p);
                                    if (ReplaySurfaceView.this.sfh != null) {
                                        ReplaySurfaceView.this.sfh.unlockCanvasAndPost(ReplaySurfaceView.this.canvas);
                                    }
                                }
                            } else if (!ReplaySurfaceView.this.has_image) {
                                ReplaySurfaceView.this.setBitmap(bmp);
                                ReplaySurfaceView.this.has_image = true;
                            }
                        } else if (ret == 0) {
                            ReplaySurfaceView.this.msleep(1);
                        } else {
                            ReplaySurfaceView.this.msleep(1);
                        }
                    }
                } else {
                    ReplaySurfaceView.this.msleep(200);
                }
            }
            ReplaySurfaceView.this.isStop = true;
            LeweiLib.LW93StopRecordReplay();
        }
    }

    /* renamed from: com.lewei.multiple.view.ReplaySurfaceView.2 */
    class C00962 implements Runnable {
        private final /* synthetic */ long val$end;
        private final /* synthetic */ long val$start;

        C00962(long j, long j2) {
            this.val$start = j;
            this.val$end = j2;
        }

        public void run() {
            ReplaySurfaceView.this.isStop = false;
            Bitmap bmp = Bitmap.createBitmap(720, 576, Config.ARGB_8888);
            while (!ReplaySurfaceView.this.isStop) {
                if (LeweiLib63.LW63StartPreview(1, this.val$start, this.val$end) > 0) {
                    while (!ReplaySurfaceView.this.isStop) {
                        if (LeweiLib63.LW63DrawBitmapFrame(bmp) > 0) {
                            ReplaySurfaceView.this.canvas = ReplaySurfaceView.this.sfh.lockCanvas(ReplaySurfaceView.this.rect);
                            if (ReplaySurfaceView.this.canvas != null) {
                                ReplaySurfaceView.this.canvas.drawBitmap(bmp, null, ReplaySurfaceView.this.rect, ReplaySurfaceView.this.f2p);
                                if (ReplaySurfaceView.this.sfh != null) {
                                    ReplaySurfaceView.this.sfh.unlockCanvasAndPost(ReplaySurfaceView.this.canvas);
                                }
                            }
                        } else {
                            ReplaySurfaceView.this.msleep(5);
                        }
                    }
                } else {
                    ReplaySurfaceView.this.msleep(1000);
                }
            }
            LeweiLib63.LW63StopPreview();
        }
    }

    /* renamed from: com.lewei.multiple.view.ReplaySurfaceView.3 */
    class C00973 implements Runnable {
        C00973() {
        }

        public void run() {
            while (!ReplaySurfaceView.this.isStop) {
                if (!ReplaySurfaceView.this.has_image) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (ReplaySurfaceView.this.bitmap != null) {
                    ReplaySurfaceView.this.canvas = ReplaySurfaceView.this.sfh.lockCanvas(ReplaySurfaceView.this.rect);
                    if (ReplaySurfaceView.this.canvas != null) {
                        ReplaySurfaceView.this.canvas.drawBitmap(ReplaySurfaceView.this.bitmap, null, ReplaySurfaceView.this.rect, ReplaySurfaceView.this.f2p);
                        if (ReplaySurfaceView.this.sfh != null) {
                            ReplaySurfaceView.this.sfh.unlockCanvasAndPost(ReplaySurfaceView.this.canvas);
                        }
                        ReplaySurfaceView.this.has_image = false;
                    } else {
                        return;
                    }
                } else {
                    continue;
                }
            }
        }
    }

    public ReplaySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.inputstream = null;
        this.isStop = false;
        this.has_image = false;
        initialize();
        this.f2p = new Paint();
        this.f2p.setAntiAlias(true);
        this.sfh = getHolder();
        this.sfh.addCallback(this);
        setKeepScreenOn(true);
        setFocusable(true);
        getWidth();
        getHeight();
    }

    public void startMySurface(String name, int start, int end) {
        new Thread(new C00951(name, start, end)).start();
    }

    public void startLW63Surface(long start, long end) {
        new Thread(new C00962(start, end)).start();
    }

    private void initialize() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        this.rect = new Rect(0, 0, mScreenWidth, mScreenHeight);
        setKeepScreenOn(true);
    }

    private void startDrawBmpThread() {
        new Thread(new C00973()).start();
    }

    public void setBitmap(Bitmap bmp) {
        this.bitmap = bmp;
    }

    private void msleep(int ms) {
        try {
            Thread.sleep((long) ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.isStop = true;
    }

    public void surfaceCreated(SurfaceHolder holder) {
    }

    public void stop() {
        this.isStop = true;
    }
}
