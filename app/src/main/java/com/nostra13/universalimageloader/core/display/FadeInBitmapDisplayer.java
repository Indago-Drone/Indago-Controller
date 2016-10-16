package com.nostra13.universalimageloader.core.display;

import android.graphics.Bitmap;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import com.lewei.multiple.photoview.PhotoViewAttacher;

public class FadeInBitmapDisplayer implements BitmapDisplayer {
    private final int durationMillis;

    public FadeInBitmapDisplayer(int durationMillis) {
        this.durationMillis = durationMillis;
    }

    public Bitmap display(Bitmap bitmap, ImageView imageView) {
        imageView.setImageBitmap(bitmap);
        animate(imageView, this.durationMillis);
        return bitmap;
    }

    public static void animate(ImageView imageView, int durationMillis) {
        AlphaAnimation fadeImage = new AlphaAnimation(0.0f, PhotoViewAttacher.DEFAULT_MIN_SCALE);
        fadeImage.setDuration((long) durationMillis);
        fadeImage.setInterpolator(new DecelerateInterpolator());
        imageView.startAnimation(fadeImage);
    }
}
