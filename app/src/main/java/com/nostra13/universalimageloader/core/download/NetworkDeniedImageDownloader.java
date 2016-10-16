package com.nostra13.universalimageloader.core.download;

import android.support.v4.widget.CursorAdapter;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;
import java.io.IOException;
import java.io.InputStream;

public class NetworkDeniedImageDownloader implements ImageDownloader {
    private final ImageDownloader wrappedDownloader;

    /* renamed from: com.nostra13.universalimageloader.core.download.NetworkDeniedImageDownloader.1 */
    static /* synthetic */ class C01091 {
        static final /* synthetic */ int[] f4x4730d1a3;

        static {
            f4x4730d1a3 = new int[Scheme.values().length];
            try {
                f4x4730d1a3[Scheme.HTTP.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f4x4730d1a3[Scheme.HTTPS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public NetworkDeniedImageDownloader(ImageDownloader wrappedDownloader) {
        this.wrappedDownloader = wrappedDownloader;
    }

    public InputStream getStream(String imageUri, Object extra) throws IOException {
        switch (C01091.f4x4730d1a3[Scheme.ofUri(imageUri).ordinal()]) {
            case CursorAdapter.FLAG_AUTO_REQUERY /*1*/:
            case CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER /*2*/:
                throw new IllegalStateException();
            default:
                return this.wrappedDownloader.getStream(imageUri, extra);
        }
    }
}
