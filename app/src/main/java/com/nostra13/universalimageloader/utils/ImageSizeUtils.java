package com.nostra13.universalimageloader.utils;

import android.support.v4.widget.CursorAdapter;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import com.lewei.multiple.photoview.PhotoViewAttacher;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import java.lang.reflect.Field;

public final class ImageSizeUtils {

    /* renamed from: com.nostra13.universalimageloader.utils.ImageSizeUtils.1 */
    static /* synthetic */ class C01111 {
        static final /* synthetic */ int[] f6x841fdc36;

        static {
            f6x841fdc36 = new int[ViewScaleType.values().length];
            try {
                f6x841fdc36[ViewScaleType.FIT_INSIDE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f6x841fdc36[ViewScaleType.CROP.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    private ImageSizeUtils() {
    }

    public static ImageSize defineTargetSizeForView(ImageView imageView, int maxImageWidth, int maxImageHeight) {
        int height = 0;
        DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
        LayoutParams params = imageView.getLayoutParams();
        int width = params.width == -2 ? 0 : imageView.getWidth();
        if (width <= 0) {
            width = params.width;
        }
        if (width <= 0) {
            width = getImageViewFieldValue(imageView, "mMaxWidth");
        }
        if (width <= 0) {
            width = maxImageWidth;
        }
        if (width <= 0) {
            width = displayMetrics.widthPixels;
        }
        if (params.height != -2) {
            height = imageView.getHeight();
        }
        if (height <= 0) {
            height = params.height;
        }
        if (height <= 0) {
            height = getImageViewFieldValue(imageView, "mMaxHeight");
        }
        if (height <= 0) {
            height = maxImageHeight;
        }
        if (height <= 0) {
            height = displayMetrics.heightPixels;
        }
        return new ImageSize(width, height);
    }

    private static int getImageViewFieldValue(Object object, String fieldName) {
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = ((Integer) field.get(object)).intValue();
            if (fieldValue <= 0 || fieldValue >= Integer.MAX_VALUE) {
                return 0;
            }
            return fieldValue;
        } catch (Exception e) {
            C0112L.m2e(e);
            return 0;
        }
    }

    public static int computeImageSampleSize(ImageSize srcSize, ImageSize targetSize, ViewScaleType viewScaleType, boolean powerOf2Scale) {
        int srcWidth = srcSize.getWidth();
        int srcHeight = srcSize.getHeight();
        int targetWidth = targetSize.getWidth();
        int targetHeight = targetSize.getHeight();
        int scale = 1;
        int widthScale = srcWidth / targetWidth;
        int heightScale = srcHeight / targetHeight;
        switch (C01111.f6x841fdc36[viewScaleType.ordinal()]) {
            case CursorAdapter.FLAG_AUTO_REQUERY /*1*/:
                if (!powerOf2Scale) {
                    scale = Math.max(widthScale, heightScale);
                    break;
                }
                while (true) {
                    if (srcWidth / 2 < targetWidth && srcHeight / 2 < targetHeight) {
                        break;
                    }
                    srcWidth /= 2;
                    srcHeight /= 2;
                    scale *= 2;
                }
                break;
            case CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER /*2*/:
                if (!powerOf2Scale) {
                    scale = Math.min(widthScale, heightScale);
                    break;
                }
                while (srcWidth / 2 >= targetWidth && srcHeight / 2 >= targetHeight) {
                    srcWidth /= 2;
                    srcHeight /= 2;
                    scale *= 2;
                }
                break;
        }
        if (scale < 1) {
            return 1;
        }
        return scale;
    }

    public static float computeImageScale(ImageSize srcSize, ImageSize targetSize, ViewScaleType viewScaleType, boolean stretch) {
        int destWidth;
        int srcWidth = srcSize.getWidth();
        int srcHeight = srcSize.getHeight();
        int targetWidth = targetSize.getWidth();
        int targetHeight = targetSize.getHeight();
        float widthScale = ((float) srcWidth) / ((float) targetWidth);
        float heightScale = ((float) srcHeight) / ((float) targetHeight);
        int destHeight;
        if ((viewScaleType != ViewScaleType.FIT_INSIDE || widthScale < heightScale) && (viewScaleType != ViewScaleType.CROP || widthScale >= heightScale)) {
            destWidth = (int) (((float) srcWidth) / heightScale);
            destHeight = targetHeight;
        } else {
            destWidth = targetWidth;
            destHeight = (int) (((float) srcHeight) / widthScale);
        }
        if ((stretch || destWidth >= srcWidth || destHeight >= srcHeight) && (!stretch || destWidth == srcWidth || destHeight == srcHeight)) {
            return PhotoViewAttacher.DEFAULT_MIN_SCALE;
        }
        return ((float) destWidth) / ((float) srcWidth);
    }
}
