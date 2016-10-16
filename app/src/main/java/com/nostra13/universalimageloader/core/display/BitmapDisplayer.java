package com.nostra13.universalimageloader.core.display;

import android.graphics.Bitmap;
import android.widget.ImageView;

public interface BitmapDisplayer {
    Bitmap display(Bitmap bitmap, ImageView imageView);
}
