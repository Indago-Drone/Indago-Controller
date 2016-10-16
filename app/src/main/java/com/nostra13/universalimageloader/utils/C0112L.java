package com.nostra13.universalimageloader.utils;

import android.util.Log;
import com.nostra13.universalimageloader.core.ImageLoader;

/* renamed from: com.nostra13.universalimageloader.utils.L */
public final class C0112L {
    private static final String LOG_FORMAT = "%1$s\n%2$s";

    private C0112L() {
    }

    public static void m0d(String message, Object... args) {
        C0112L.log(3, null, message, args);
    }

    public static void m4i(String message, Object... args) {
        C0112L.log(4, null, message, args);
    }

    public static void m5w(String message, Object... args) {
        C0112L.log(5, null, message, args);
    }

    public static void m2e(Throwable ex) {
        C0112L.log(6, ex, null, new Object[0]);
    }

    public static void m1e(String message, Object... args) {
        C0112L.log(6, null, message, args);
    }

    public static void m3e(Throwable ex, String message, Object... args) {
        C0112L.log(6, ex, message, args);
    }

    private static void log(int priority, Throwable ex, String message, Object... args) {
        String log;
        if (args.length > 0) {
            message = String.format(message, args);
        }
        if (ex == null) {
            log = message;
        } else {
            String logMessage;
            if (message == null) {
                logMessage = ex.getMessage();
            } else {
                logMessage = message;
            }
            String logBody = Log.getStackTraceString(ex);
            log = String.format(LOG_FORMAT, new Object[]{logMessage, logBody});
        }
        Log.println(priority, ImageLoader.TAG, log);
    }
}
