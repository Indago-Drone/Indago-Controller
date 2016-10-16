package com.nostra13.universalimageloader.utils;

import android.content.Context;
import android.os.Environment;
import java.io.File;
import java.io.IOException;

public final class StorageUtils {
    private static final String INDIVIDUAL_DIR_NAME = "uil-images";

    private StorageUtils() {
    }

    public static File getCacheDirectory(Context context) {
        File appCacheDir = null;
        if (Environment.getExternalStorageState().equals("mounted")) {
            appCacheDir = getExternalCacheDir(context);
        }
        if (appCacheDir == null) {
            return context.getCacheDir();
        }
        return appCacheDir;
    }

    public static File getIndividualCacheDirectory(Context context) {
        File cacheDir = getCacheDirectory(context);
        File individualCacheDir = new File(cacheDir, INDIVIDUAL_DIR_NAME);
        if (individualCacheDir.exists() || individualCacheDir.mkdir()) {
            return individualCacheDir;
        }
        return cacheDir;
    }

    public static File getOwnCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = null;
        if (Environment.getExternalStorageState().equals("mounted")) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
        }
        if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
            return context.getCacheDir();
        }
        return appCacheDir;
    }

    private static File getExternalCacheDir(Context context) {
        File appCacheDir = new File(new File(new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data"), context.getPackageName()), "cache");
        if (appCacheDir.exists()) {
            return appCacheDir;
        }
        if (appCacheDir.mkdirs()) {
            try {
                new File(appCacheDir, ".nomedia").createNewFile();
                return appCacheDir;
            } catch (IOException e) {
                C0112L.m4i("Can't create \".nomedia\" file in application external cache directory", new Object[0]);
                return appCacheDir;
            }
        }
        C0112L.m5w("Unable to create external cache directory", new Object[0]);
        return null;
    }
}
