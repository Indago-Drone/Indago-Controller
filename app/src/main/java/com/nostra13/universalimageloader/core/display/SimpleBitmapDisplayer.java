package com.nostra13.universalimageloader.core.display;

import android.graphics.Bitmap;
import android.widget.ImageView;

public final class SimpleBitmapDisplayer implements BitmapDisplayer {
    public Bitmap display(Bitmap bitmap, ImageView imageView) {
        imageView.setImageBitmap(bitmap);
        return bitmap;
    }
}
