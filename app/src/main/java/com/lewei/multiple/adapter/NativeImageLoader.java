package com.lewei.multiple.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NativeImageLoader {
    private static NativeImageLoader mInstance;
    private ExecutorService mImageThreadPool;
    private LruCache<String, Bitmap> mMemoryCache;

    /* renamed from: com.lewei.multiple.adapter.NativeImageLoader.2 */
    class C00482 extends Handler {
        private final /* synthetic */ NativeImageCallBack val$mCallBack;
        private final /* synthetic */ String val$path;

        C00482(NativeImageCallBack nativeImageCallBack, String str) {
            this.val$mCallBack = nativeImageCallBack;
            this.val$path = str;
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            this.val$mCallBack.onImageLoader((Bitmap) msg.obj, this.val$path);
        }
    }

    /* renamed from: com.lewei.multiple.adapter.NativeImageLoader.3 */
    class C00493 implements Runnable {
        private final /* synthetic */ Handler val$mHander;
        private final /* synthetic */ Point val$mPoint;
        private final /* synthetic */ String val$path;
        private final /* synthetic */ LOAD_TYPE val$type;

        C00493(LOAD_TYPE load_type, String str, Point point, Handler handler) {
            this.val$type = load_type;
            this.val$path = str;
            this.val$mPoint = point;
            this.val$mHander = handler;
        }

        public void run() {
            Bitmap mBitmap;
            int i = 0;
            if (this.val$type == LOAD_TYPE.IMAGE) {
                NativeImageLoader nativeImageLoader = NativeImageLoader.this;
                String str = this.val$path;
                int i2 = this.val$mPoint == null ? 0 : this.val$mPoint.x;
                if (this.val$mPoint != null) {
                    i = this.val$mPoint.y;
                }
                mBitmap = nativeImageLoader.decodeThumbBitmapForFile(str, i2, i);
            } else {
                mBitmap = NativeImageLoader.this.getVideoThumbnail(this.val$path, 200, 140, 1);
            }
            Message msg = this.val$mHander.obtainMessage();
            msg.obj = mBitmap;
            this.val$mHander.sendMessage(msg);
            NativeImageLoader.this.addBitmapToMemoryCache(this.val$path, mBitmap);
        }
    }

    public enum LOAD_TYPE {
        IMAGE,
        VIDEO
    }

    public interface NativeImageCallBack {
        void onImageLoader(Bitmap bitmap, String str);
    }

    /* renamed from: com.lewei.multiple.adapter.NativeImageLoader.1 */
    class C01251 extends LruCache<String, Bitmap> {
        C01251(int $anonymous0) {
            super($anonymous0);
        }

        protected int sizeOf(String key, Bitmap bitmap) {
            return (bitmap.getRowBytes() * bitmap.getHeight()) / AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT;
        }
    }

    static {
        mInstance = new NativeImageLoader();
    }

    private NativeImageLoader() {
        this.mImageThreadPool = Executors.newFixedThreadPool(2);
        this.mMemoryCache = new C01251(((int) (Runtime.getRuntime().maxMemory() / 1024)) / 8);
    }

    public static NativeImageLoader getInstance() {
        return mInstance;
    }

    public Bitmap loadNativeImage(LOAD_TYPE type, int firstVisibleItem, int visibleItemCount, String path, NativeImageCallBack mCallBack) {
        return loadNativeImage(type, path, null, mCallBack);
    }

    @SuppressLint({"HandlerLeak"})
    public Bitmap loadNativeImage(LOAD_TYPE type, String path, Point mPoint, NativeImageCallBack mCallBack) {
        Bitmap bitmap = getBitmapFromMemCache(path);
        Handler mHander = new C00482(mCallBack, path);
        if (bitmap == null) {
            this.mImageThreadPool.execute(new C00493(type, path, mPoint, mHander));
        }
        return bitmap;
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null && bitmap != null) {
            this.mMemoryCache.put(key, bitmap);
        }
    }

    public void deleteBitmapToMemoryCache(String key) {
        if (getBitmapFromMemCache(key) != null) {
            this.mMemoryCache.remove(key);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return (Bitmap) this.mMemoryCache.get(key);
    }

    private Bitmap decodeThumbBitmapForFile(String path, int viewWidth, int viewHeight) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = computeScale(options, viewWidth, viewHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private int computeScale(Options options, int viewWidth, int viewHeight) {
        int inSampleSize = 1;
        if (viewWidth == 0 || viewWidth == 0) {
            return 1;
        }
        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;
        if (bitmapWidth > viewWidth || bitmapHeight > viewWidth) {
            int widthScale = Math.round(((float) bitmapWidth) / ((float) viewWidth));
            int heightScale = Math.round(((float) bitmapHeight) / ((float) viewWidth));
            if (widthScale < heightScale) {
                inSampleSize = widthScale;
            } else {
                inSampleSize = heightScale;
            }
        }
        return inSampleSize;
    }

    private Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        if (bitmap == null) {
            return bitmap;
        }
        System.out.println("w" + bitmap.getWidth());
        System.out.println("h" + bitmap.getHeight());
        return ThumbnailUtils.extractThumbnail(bitmap, width, height, 2);
    }

    public void CacelAllTasks() {
        this.mImageThreadPool.shutdown();
    }
}
