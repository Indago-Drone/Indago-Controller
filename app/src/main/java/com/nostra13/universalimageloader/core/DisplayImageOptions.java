package com.nostra13.universalimageloader.core;

import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

public final class DisplayImageOptions {
    private final boolean cacheInMemory;
    private final boolean cacheOnDisc;
    private final Options decodingOptions;
    private final int delayBeforeLoading;
    private final BitmapDisplayer displayer;
    private final Object extraForDownloader;
    private final Handler handler;
    private final int imageForEmptyUri;
    private final int imageOnFail;
    private final ImageScaleType imageScaleType;
    private final BitmapProcessor postProcessor;
    private final BitmapProcessor preProcessor;
    private final boolean resetViewBeforeLoading;
    private final int stubImage;

    public static class Builder {
        private boolean cacheInMemory;
        private boolean cacheOnDisc;
        private Options decodingOptions;
        private int delayBeforeLoading;
        private BitmapDisplayer displayer;
        private Object extraForDownloader;
        private Handler handler;
        private int imageForEmptyUri;
        private int imageOnFail;
        private ImageScaleType imageScaleType;
        private BitmapProcessor postProcessor;
        private BitmapProcessor preProcessor;
        private boolean resetViewBeforeLoading;
        private int stubImage;

        public Builder() {
            this.stubImage = 0;
            this.imageForEmptyUri = 0;
            this.imageOnFail = 0;
            this.resetViewBeforeLoading = false;
            this.cacheInMemory = false;
            this.cacheOnDisc = false;
            this.imageScaleType = ImageScaleType.IN_SAMPLE_POWER_OF_2;
            this.decodingOptions = new Options();
            this.delayBeforeLoading = 0;
            this.extraForDownloader = null;
            this.preProcessor = null;
            this.postProcessor = null;
            this.displayer = DefaultConfigurationFactory.createBitmapDisplayer();
            this.handler = null;
            this.decodingOptions.inPurgeable = true;
            this.decodingOptions.inInputShareable = true;
        }

        public Builder showStubImage(int stubImageRes) {
            this.stubImage = stubImageRes;
            return this;
        }

        public Builder showImageForEmptyUri(int imageRes) {
            this.imageForEmptyUri = imageRes;
            return this;
        }

        public Builder showImageOnFail(int imageRes) {
            this.imageOnFail = imageRes;
            return this;
        }

        public Builder resetViewBeforeLoading() {
            this.resetViewBeforeLoading = true;
            return this;
        }

        public Builder cacheInMemory() {
            this.cacheInMemory = true;
            return this;
        }

        public Builder cacheOnDisc() {
            this.cacheOnDisc = true;
            return this;
        }

        public Builder imageScaleType(ImageScaleType imageScaleType) {
            this.imageScaleType = imageScaleType;
            return this;
        }

        public Builder bitmapConfig(Config bitmapConfig) {
            this.decodingOptions.inPreferredConfig = bitmapConfig;
            return this;
        }

        public Builder decodingOptions(Options decodingOptions) {
            this.decodingOptions = decodingOptions;
            return this;
        }

        public Builder delayBeforeLoading(int delayInMillis) {
            this.delayBeforeLoading = delayInMillis;
            return this;
        }

        public Builder extraForDownloader(Object extra) {
            this.extraForDownloader = extra;
            return this;
        }

        public Builder preProcessor(BitmapProcessor preProcessor) {
            this.preProcessor = preProcessor;
            return this;
        }

        public Builder postProcessor(BitmapProcessor postProcessor) {
            this.postProcessor = postProcessor;
            return this;
        }

        public Builder displayer(BitmapDisplayer displayer) {
            this.displayer = displayer;
            return this;
        }

        public Builder handler(Handler handler) {
            this.handler = handler;
            return this;
        }

        public Builder cloneFrom(DisplayImageOptions options) {
            this.stubImage = options.stubImage;
            this.imageForEmptyUri = options.imageForEmptyUri;
            this.imageOnFail = options.imageOnFail;
            this.resetViewBeforeLoading = options.resetViewBeforeLoading;
            this.cacheInMemory = options.cacheInMemory;
            this.cacheOnDisc = options.cacheOnDisc;
            this.imageScaleType = options.imageScaleType;
            this.decodingOptions = options.decodingOptions;
            this.delayBeforeLoading = options.delayBeforeLoading;
            this.extraForDownloader = options.extraForDownloader;
            this.preProcessor = options.preProcessor;
            this.postProcessor = options.postProcessor;
            this.displayer = options.displayer;
            this.handler = options.handler;
            return this;
        }

        public DisplayImageOptions build() {
            return new DisplayImageOptions(this);
        }
    }

    private DisplayImageOptions(Builder builder) {
        this.stubImage = builder.stubImage;
        this.imageForEmptyUri = builder.imageForEmptyUri;
        this.imageOnFail = builder.imageOnFail;
        this.resetViewBeforeLoading = builder.resetViewBeforeLoading;
        this.cacheInMemory = builder.cacheInMemory;
        this.cacheOnDisc = builder.cacheOnDisc;
        this.imageScaleType = builder.imageScaleType;
        this.decodingOptions = builder.decodingOptions;
        this.delayBeforeLoading = builder.delayBeforeLoading;
        this.extraForDownloader = builder.extraForDownloader;
        this.preProcessor = builder.preProcessor;
        this.postProcessor = builder.postProcessor;
        this.displayer = builder.displayer;
        this.handler = builder.handler;
    }

    public boolean shouldShowStubImage() {
        return this.stubImage != 0;
    }

    public boolean shouldShowImageForEmptyUri() {
        return this.imageForEmptyUri != 0;
    }

    public boolean shouldShowImageOnFail() {
        return this.imageOnFail != 0;
    }

    public boolean shouldPreProcess() {
        return this.preProcessor != null;
    }

    public boolean shouldPostProcess() {
        return this.postProcessor != null;
    }

    public boolean shouldDelayBeforeLoading() {
        return this.delayBeforeLoading > 0;
    }

    public int getStubImage() {
        return this.stubImage;
    }

    public int getImageForEmptyUri() {
        return this.imageForEmptyUri;
    }

    public int getImageOnFail() {
        return this.imageOnFail;
    }

    public boolean isResetViewBeforeLoading() {
        return this.resetViewBeforeLoading;
    }

    public boolean isCacheInMemory() {
        return this.cacheInMemory;
    }

    public boolean isCacheOnDisc() {
        return this.cacheOnDisc;
    }

    public ImageScaleType getImageScaleType() {
        return this.imageScaleType;
    }

    public Options getDecodingOptions() {
        return this.decodingOptions;
    }

    public int getDelayBeforeLoading() {
        return this.delayBeforeLoading;
    }

    public Object getExtraForDownloader() {
        return this.extraForDownloader;
    }

    public BitmapProcessor getPreProcessor() {
        return this.preProcessor;
    }

    public BitmapProcessor getPostProcessor() {
        return this.postProcessor;
    }

    public BitmapDisplayer getDisplayer() {
        return this.displayer;
    }

    public Handler getHandler() {
        return this.handler == null ? new Handler() : this.handler;
    }

    public static DisplayImageOptions createSimple() {
        return new Builder().build();
    }
}
