package com.nostra13.universalimageloader.core.display;

import android.graphics.Bitmap;
import android.widget.ImageView;

public final class FakeBitmapDisplayer implements BitmapDisplayer {
    public Bitmap display(Bitmap bitmap, ImageView imageView) {
        return bitmap;
    }
}
