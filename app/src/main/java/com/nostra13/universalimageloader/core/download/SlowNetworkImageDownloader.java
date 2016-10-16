package com.nostra13.universalimageloader.core.download;

import android.support.v4.widget.CursorAdapter;
import com.nostra13.universalimageloader.core.assist.FlushedInputStream;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;
import java.io.IOException;
import java.io.InputStream;

public class SlowNetworkImageDownloader implements ImageDownloader {
    private final ImageDownloader wrappedDownloader;

    /* renamed from: com.nostra13.universalimageloader.core.download.SlowNetworkImageDownloader.1 */
    static /* synthetic */ class C01101 {
        static final /* synthetic */ int[] f5x4730d1a3;

        static {
            f5x4730d1a3 = new int[Scheme.values().length];
            try {
                f5x4730d1a3[Scheme.HTTP.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f5x4730d1a3[Scheme.HTTPS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public SlowNetworkImageDownloader(ImageDownloader wrappedDownloader) {
        this.wrappedDownloader = wrappedDownloader;
    }

    public InputStream getStream(String imageUri, Object extra) throws IOException {
        InputStream imageStream = this.wrappedDownloader.getStream(imageUri, extra);
        switch (C01101.f5x4730d1a3[Scheme.ofUri(imageUri).ordinal()]) {
            case CursorAdapter.FLAG_AUTO_REQUERY /*1*/:
            case CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER /*2*/:
                return new FlushedInputStream(imageStream);
            default:
                return imageStream;
        }
    }
}
