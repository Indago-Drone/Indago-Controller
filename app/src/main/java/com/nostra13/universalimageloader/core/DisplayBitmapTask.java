package com.nostra13.universalimageloader.core;

import android.graphics.Bitmap;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.utils.C0112L;

final class DisplayBitmapTask implements Runnable {
    private static final String LOG_DISPLAY_IMAGE_IN_IMAGEVIEW = "Display image in ImageView [%s]";
    private static final String LOG_TASK_CANCELLED = "ImageView is reused for another image. Task is cancelled. [%s]";
    private final Bitmap bitmap;
    private final BitmapDisplayer displayer;
    private final ImageLoaderEngine engine;
    private final String imageUri;
    private final ImageView imageView;
    private final ImageLoadingListener listener;
    private boolean loggingEnabled;
    private final String memoryCacheKey;

    public DisplayBitmapTask(Bitmap bitmap, ImageLoadingInfo imageLoadingInfo, ImageLoaderEngine engine) {
        this.bitmap = bitmap;
        this.imageUri = imageLoadingInfo.uri;
        this.imageView = imageLoadingInfo.imageView;
        this.memoryCacheKey = imageLoadingInfo.memoryCacheKey;
        this.displayer = imageLoadingInfo.options.getDisplayer();
        this.listener = imageLoadingInfo.listener;
        this.engine = engine;
    }

    public void run() {
        if (isViewWasReused()) {
            if (this.loggingEnabled) {
                C0112L.m4i(LOG_TASK_CANCELLED, this.memoryCacheKey);
            }
            this.listener.onLoadingCancelled(this.imageUri, this.imageView);
            return;
        }
        if (this.loggingEnabled) {
            C0112L.m4i(LOG_DISPLAY_IMAGE_IN_IMAGEVIEW, this.memoryCacheKey);
        }
        this.listener.onLoadingComplete(this.imageUri, this.imageView, this.displayer.display(this.bitmap, this.imageView));
        this.engine.cancelDisplayTaskFor(this.imageView);
    }

    private boolean isViewWasReused() {
        return !this.memoryCacheKey.equals(this.engine.getLoadingUriForView(this.imageView));
    }

    void setLoggingEnabled(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }
}
