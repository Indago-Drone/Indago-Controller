package com.nostra13.universalimageloader.core.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.os.Build.VERSION;
import com.lewei.multiple.photoview.PhotoViewAttacher;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.utils.C0112L;
import com.nostra13.universalimageloader.utils.ImageSizeUtils;
import com.nostra13.universalimageloader.utils.IoUtils;
import java.io.IOException;
import java.io.InputStream;

public class BaseImageDecoder implements ImageDecoder {
    protected static final String ERROR_CANT_DECODE_IMAGE = "Image can't be decoded [%s]";
    protected static final String LOG_FLIP_IMAGE = "Flip image horizontally [%s]";
    protected static final String LOG_ROTATE_IMAGE = "Rotate image on %1$d\u00b0 [%2$s]";
    protected static final String LOG_SABSAMPLE_IMAGE = "Subsample original image (%1$s) to %2$s (scale = %3$d) [%4$s]";
    protected static final String LOG_SCALE_IMAGE = "Scale subsampled image (%1$s) to %2$s (scale = %3$.5f) [%4$s]";
    protected boolean loggingEnabled;

    protected static class ExifInfo {
        final boolean flipHorizontal;
        final int rotation;

        ExifInfo() {
            this.rotation = 0;
            this.flipHorizontal = false;
        }

        ExifInfo(int rotation, boolean flipHorizontal) {
            this.rotation = rotation;
            this.flipHorizontal = flipHorizontal;
        }
    }

    protected static class ImageFileInfo {
        final ExifInfo exif;
        final ImageSize imageSize;

        ImageFileInfo(ImageSize imageSize, ExifInfo exif) {
            this.imageSize = imageSize;
            this.exif = exif;
        }
    }

    public BaseImageDecoder(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }

    public Bitmap decode(ImageDecodingInfo decodingInfo) throws IOException {
        ImageFileInfo imageInfo = defineImageSizeAndRotation(getImageStream(decodingInfo), decodingInfo.getImageUri());
        Bitmap decodedBitmap = decodeStream(getImageStream(decodingInfo), prepareDecodingOptions(imageInfo.imageSize, decodingInfo));
        if (decodedBitmap != null) {
            return considerExactScaleAndOrientaiton(decodedBitmap, decodingInfo, imageInfo.exif.rotation, imageInfo.exif.flipHorizontal);
        }
        C0112L.m1e(ERROR_CANT_DECODE_IMAGE, decodingInfo.getImageKey());
        return decodedBitmap;
    }

    protected InputStream getImageStream(ImageDecodingInfo decodingInfo) throws IOException {
        return decodingInfo.getDownloader().getStream(decodingInfo.getImageUri(), decodingInfo.getExtraForDownloader());
    }

    protected ImageFileInfo defineImageSizeAndRotation(InputStream imageStream, String imageUri) throws IOException {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        try {
            ExifInfo exif;
            BitmapFactory.decodeStream(imageStream, null, options);
            if (VERSION.SDK_INT >= 5) {
                exif = defineExifOrientation(imageUri, options.outMimeType);
            } else {
                exif = new ExifInfo();
            }
            return new ImageFileInfo(new ImageSize(options.outWidth, options.outHeight, exif.rotation), exif);
        } finally {
            IoUtils.closeSilently(imageStream);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected com.nostra13.universalimageloader.core.decode.BaseImageDecoder.ExifInfo defineExifOrientation(String r9, String r10) {
        /*
        r8 = this;
        r7 = 1;
        r4 = 0;
        r3 = 0;
        r5 = "image/jpeg";
        r5 = r5.equalsIgnoreCase(r10);
        if (r5 == 0) goto L_0x0028;
    L_0x000b:
        r5 = com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme.ofUri(r9);
        r6 = com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme.FILE;
        if (r5 != r6) goto L_0x0028;
    L_0x0013:
        r1 = new android.media.ExifInterface;	 Catch:{ IOException -> 0x003d }
        r5 = com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme.FILE;	 Catch:{ IOException -> 0x003d }
        r5 = r5.crop(r9);	 Catch:{ IOException -> 0x003d }
        r1.<init>(r5);	 Catch:{ IOException -> 0x003d }
        r5 = "Orientation";
        r6 = 1;
        r2 = r1.getAttributeInt(r5, r6);	 Catch:{ IOException -> 0x003d }
        switch(r2) {
            case 1: goto L_0x002f;
            case 2: goto L_0x002e;
            case 3: goto L_0x0036;
            case 4: goto L_0x0035;
            case 5: goto L_0x0039;
            case 6: goto L_0x0032;
            case 7: goto L_0x0031;
            case 8: goto L_0x003a;
            default: goto L_0x0028;
        };
    L_0x0028:
        r5 = new com.nostra13.universalimageloader.core.decode.BaseImageDecoder$ExifInfo;
        r5.<init>(r4, r3);
        return r5;
    L_0x002e:
        r3 = 1;
    L_0x002f:
        r4 = 0;
        goto L_0x0028;
    L_0x0031:
        r3 = 1;
    L_0x0032:
        r4 = 90;
        goto L_0x0028;
    L_0x0035:
        r3 = 1;
    L_0x0036:
        r4 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        goto L_0x0028;
    L_0x0039:
        r3 = 1;
    L_0x003a:
        r4 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        goto L_0x0028;
    L_0x003d:
        r0 = move-exception;
        r5 = "Can't read EXIF tags from file [%s]";
        r6 = new java.lang.Object[r7];
        r7 = 0;
        r6[r7] = r9;
        com.nostra13.universalimageloader.utils.C0112L.m5w(r5, r6);
        goto L_0x0028;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nostra13.universalimageloader.core.decode.BaseImageDecoder.defineExifOrientation(java.lang.String, java.lang.String):com.nostra13.universalimageloader.core.decode.BaseImageDecoder$ExifInfo");
    }

    protected Options prepareDecodingOptions(ImageSize imageSize, ImageDecodingInfo decodingInfo) {
        ImageScaleType scaleType = decodingInfo.getImageScaleType();
        ImageSize targetSize = decodingInfo.getTargetSize();
        int scale = 1;
        if (scaleType != ImageScaleType.NONE) {
            boolean powerOf2;
            if (scaleType == ImageScaleType.IN_SAMPLE_POWER_OF_2) {
                powerOf2 = true;
            } else {
                powerOf2 = false;
            }
            scale = ImageSizeUtils.computeImageSampleSize(imageSize, targetSize, decodingInfo.getViewScaleType(), powerOf2);
            if (this.loggingEnabled) {
                C0112L.m4i(LOG_SABSAMPLE_IMAGE, imageSize, imageSize.scaleDown(scale), Integer.valueOf(scale), decodingInfo.getImageKey());
            }
        }
        Options decodingOptions = decodingInfo.getDecodingOptions();
        decodingOptions.inSampleSize = scale;
        return decodingOptions;
    }

    protected Bitmap decodeStream(InputStream imageStream, Options decodingOptions) throws IOException {
        try {
            Bitmap decodeStream = BitmapFactory.decodeStream(imageStream, null, decodingOptions);
            return decodeStream;
        } finally {
            IoUtils.closeSilently(imageStream);
        }
    }

    protected Bitmap considerExactScaleAndOrientaiton(Bitmap subsampledBitmap, ImageDecodingInfo decodingInfo, int rotation, boolean flipHorizontal) {
        Matrix m = new Matrix();
        ImageScaleType scaleType = decodingInfo.getImageScaleType();
        if (scaleType == ImageScaleType.EXACTLY || scaleType == ImageScaleType.EXACTLY_STRETCHED) {
            float scale = ImageSizeUtils.computeImageScale(new ImageSize(subsampledBitmap.getWidth(), subsampledBitmap.getHeight(), rotation), decodingInfo.getTargetSize(), decodingInfo.getViewScaleType(), scaleType == ImageScaleType.EXACTLY_STRETCHED);
            if (Float.compare(scale, PhotoViewAttacher.DEFAULT_MIN_SCALE) != 0) {
                m.setScale(scale, scale);
                if (this.loggingEnabled) {
                    C0112L.m4i(LOG_SCALE_IMAGE, 1, 1, Float.valueOf(scale), decodingInfo.getImageKey());
                }//findme                   1-> srcSize  1 ->  srcSize.scale(scale)
            }
        }
        if (flipHorizontal) {
            m.postScale(-1.0f, PhotoViewAttacher.DEFAULT_MIN_SCALE);
            if (this.loggingEnabled) {
                C0112L.m4i(LOG_FLIP_IMAGE, decodingInfo.getImageKey());
            }
        }
        if (rotation != 0) {
            m.postRotate((float) rotation);
            if (this.loggingEnabled) {
                C0112L.m4i(LOG_ROTATE_IMAGE, Integer.valueOf(rotation), decodingInfo.getImageKey());
            }
        }
        Bitmap finalBitmap = Bitmap.createBitmap(subsampledBitmap, 0, 0, subsampledBitmap.getWidth(), subsampledBitmap.getHeight(), m, true);
        if (finalBitmap != subsampledBitmap) {
            subsampledBitmap.recycle();
        }
        return finalBitmap;
    }

    public void setLoggingEnabled(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }
}
