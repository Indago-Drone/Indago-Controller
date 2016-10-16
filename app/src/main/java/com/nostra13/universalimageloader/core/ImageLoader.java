package com.nostra13.universalimageloader.core;

import android.graphics.Bitmap;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.MemoryCacheUtil;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FakeBitmapDisplayer;
import com.nostra13.universalimageloader.utils.C0112L;
import com.nostra13.universalimageloader.utils.ImageSizeUtils;

public class ImageLoader {
    private static final String ERROR_INIT_CONFIG_WITH_NULL = "ImageLoader configuration can not be initialized with null";
    private static final String ERROR_NOT_INIT = "ImageLoader must be init with configuration before using";
    private static final String ERROR_WRONG_ARGUMENTS = "Wrong arguments were passed to displayImage() method (ImageView reference must not be null)";
    static final String LOG_DESTROY = "Destroy ImageLoader";
    static final String LOG_INIT_CONFIG = "Initialize ImageLoader with configuration";
    static final String LOG_LOAD_IMAGE_FROM_MEMORY_CACHE = "Load image from memory cache [%s]";
    public static final String TAG;
    private static final String WARNING_RE_INIT_CONFIG = "Try to initialize ImageLoader which had already been initialized before. To re-init ImageLoader with new configuration call ImageLoader.destroy() at first.";
    private static volatile ImageLoader instance;
    private ImageLoaderConfiguration configuration;
    private final ImageLoadingListener emptyListener;
    private ImageLoaderEngine engine;
    private final BitmapDisplayer fakeBitmapDisplayer;

    static {
        TAG = ImageLoader.class.getSimpleName();
    }

    public static ImageLoader getInstance() {
        if (instance == null) {
            synchronized (ImageLoader.class) {
                if (instance == null) {
                    instance = new ImageLoader();
                }
            }
        }
        return instance;
    }

    protected ImageLoader() {
        this.emptyListener = new SimpleImageLoadingListener();
        this.fakeBitmapDisplayer = new FakeBitmapDisplayer();
    }

    public synchronized void init(ImageLoaderConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException(ERROR_INIT_CONFIG_WITH_NULL);
        } else if (this.configuration == null) {
            if (configuration.loggingEnabled) {
                C0112L.m0d(LOG_INIT_CONFIG, new Object[0]);
            }
            this.engine = new ImageLoaderEngine(configuration);
            this.configuration = configuration;
        } else {
            C0112L.m5w(WARNING_RE_INIT_CONFIG, new Object[0]);
        }
    }

    public boolean isInited() {
        return this.configuration != null;
    }

    public void displayImage(String uri, ImageView imageView) {
        displayImage(uri, imageView, null, null);
    }

    public void displayImage(String uri, ImageView imageView, DisplayImageOptions options) {
        displayImage(uri, imageView, options, null);
    }

    public void displayImage(String uri, ImageView imageView, ImageLoadingListener listener) {
        displayImage(uri, imageView, null, listener);
    }

    public void displayImage(String uri, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener) {
        checkConfiguration();
        if (imageView == null) {
            throw new IllegalArgumentException(ERROR_WRONG_ARGUMENTS);
        }
        if (listener == null) {
            listener = this.emptyListener;
        }
        if (options == null) {
            options = this.configuration.defaultDisplayImageOptions;
        }
        if (uri == null || uri.length() == 0) {
            this.engine.cancelDisplayTaskFor(imageView);
            listener.onLoadingStarted(uri, imageView);
            if (options.shouldShowImageForEmptyUri()) {
                imageView.setImageResource(options.getImageForEmptyUri());
            } else {
                imageView.setImageBitmap(null);
            }
            listener.onLoadingComplete(uri, imageView, null);
            return;
        }
        ImageSize targetSize = ImageSizeUtils.defineTargetSizeForView(imageView, this.configuration.maxImageWidthForMemoryCache, this.configuration.maxImageHeightForMemoryCache);
        String memoryCacheKey = MemoryCacheUtil.generateKey(uri, targetSize);
        this.engine.prepareDisplayTaskFor(imageView, memoryCacheKey);
        listener.onLoadingStarted(uri, imageView);
        Bitmap bmp = (Bitmap) this.configuration.memoryCache.get(memoryCacheKey);
        if (bmp == null || bmp.isRecycled()) {
            if (options.shouldShowStubImage()) {
                imageView.setImageResource(options.getStubImage());
            } else if (options.isResetViewBeforeLoading()) {
                imageView.setImageBitmap(null);
            }
            this.engine.submit(new LoadAndDisplayImageTask(this.engine, new ImageLoadingInfo(uri, imageView, targetSize, memoryCacheKey, options, listener, this.engine.getLockForUri(uri)), options.getHandler()));
            return;
        }
        if (this.configuration.loggingEnabled) {
            C0112L.m4i(LOG_LOAD_IMAGE_FROM_MEMORY_CACHE, memoryCacheKey);
        }
        if (options.shouldPostProcess()) {
            this.engine.submit(new ProcessAndDisplayImageTask(this.engine, bmp, new ImageLoadingInfo(uri, imageView, targetSize, memoryCacheKey, options, listener, this.engine.getLockForUri(uri)), options.getHandler()));
            return;
        }
        options.getDisplayer().display(bmp, imageView);
        listener.onLoadingComplete(uri, imageView, bmp);
    }

    public void loadImage(String uri, ImageLoadingListener listener) {
        loadImage(uri, null, null, listener);
    }

    public void loadImage(String uri, ImageSize minImageSize, ImageLoadingListener listener) {
        loadImage(uri, minImageSize, null, listener);
    }

    public void loadImage(String uri, DisplayImageOptions options, ImageLoadingListener listener) {
        loadImage(uri, null, options, listener);
    }

    public void loadImage(String uri, ImageSize targetImageSize, DisplayImageOptions options, ImageLoadingListener listener) {
        DisplayImageOptions optionsWithFakeDisplayer;
        checkConfiguration();
        if (targetImageSize == null) {
            targetImageSize = new ImageSize(this.configuration.maxImageWidthForMemoryCache, this.configuration.maxImageHeightForMemoryCache);
        }
        if (options == null) {
            options = this.configuration.defaultDisplayImageOptions;
        }
        if (options.getDisplayer() instanceof FakeBitmapDisplayer) {
            optionsWithFakeDisplayer = options;
        } else {
            optionsWithFakeDisplayer = new Builder().cloneFrom(options).displayer(this.fakeBitmapDisplayer).build();
        }
        ImageView fakeImage = new ImageView(this.configuration.context);
        fakeImage.setLayoutParams(new LayoutParams(targetImageSize.getWidth(), targetImageSize.getHeight()));
        fakeImage.setScaleType(ScaleType.CENTER_CROP);
        displayImage(uri, fakeImage, optionsWithFakeDisplayer, listener);
    }

    private void checkConfiguration() {
        if (this.configuration == null) {
            throw new IllegalStateException(ERROR_NOT_INIT);
        }
    }

    public MemoryCacheAware<String, Bitmap> getMemoryCache() {
        checkConfiguration();
        return this.configuration.memoryCache;
    }

    public void clearMemoryCache() {
        checkConfiguration();
        this.configuration.memoryCache.clear();
    }

    public DiscCacheAware getDiscCache() {
        checkConfiguration();
        return this.configuration.discCache;
    }

    public void clearDiscCache() {
        checkConfiguration();
        this.configuration.discCache.clear();
    }

    public String getLoadingUriForView(ImageView imageView) {
        return this.engine.getLoadingUriForView(imageView);
    }

    public void cancelDisplayTask(ImageView imageView) {
        this.engine.cancelDisplayTaskFor(imageView);
    }

    public void denyNetworkDownloads(boolean denyNetworkDownloads) {
        this.engine.denyNetworkDownloads(denyNetworkDownloads);
    }

    public void handleSlowNetwork(boolean handleSlowNetwork) {
        this.engine.handleSlowNetwork(handleSlowNetwork);
    }

    public void pause() {
        this.engine.pause();
    }

    public void resume() {
        this.engine.resume();
    }

    public void stop() {
        this.engine.stop();
    }

    public void destroy() {
        if (this.configuration != null && this.configuration.loggingEnabled) {
            C0112L.m0d(LOG_DESTROY, new Object[0]);
        }
        stop();
        this.engine = null;
        this.configuration = null;
    }
}
