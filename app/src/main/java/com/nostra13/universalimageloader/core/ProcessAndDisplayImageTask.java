package com.nostra13.universalimageloader.core;

import android.graphics.Bitmap;
import android.os.Handler;
import com.nostra13.universalimageloader.utils.C0112L;

class ProcessAndDisplayImageTask implements Runnable {
    private static final String LOG_POSTPROCESS_IMAGE = "PostProcess image before displaying [%s]";
    private final Bitmap bitmap;
    private final ImageLoaderEngine engine;
    private final Handler handler;
    private final ImageLoadingInfo imageLoadingInfo;

    public ProcessAndDisplayImageTask(ImageLoaderEngine engine, Bitmap bitmap, ImageLoadingInfo imageLoadingInfo, Handler handler) {
        this.engine = engine;
        this.bitmap = bitmap;
        this.imageLoadingInfo = imageLoadingInfo;
        this.handler = handler;
    }

    public void run() {
        if (this.engine.configuration.loggingEnabled) {
            C0112L.m4i(LOG_POSTPROCESS_IMAGE, this.imageLoadingInfo.memoryCacheKey);
        }
        Bitmap processedBitmap = this.imageLoadingInfo.options.getPostProcessor().process(this.bitmap);
        if (processedBitmap != this.bitmap) {
            this.bitmap.recycle();
        }
        this.handler.post(new DisplayBitmapTask(processedBitmap, this.imageLoadingInfo, this.engine));
    }
}
