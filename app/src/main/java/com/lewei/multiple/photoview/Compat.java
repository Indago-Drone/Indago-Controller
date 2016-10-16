package com.lewei.multiple.photoview;

import android.os.Build.VERSION;
import android.view.View;

public class Compat {
    private static final int SIXTY_FPS_INTERVAL = 16;

    public static void postOnAnimation(View view, Runnable runnable) {
        if (VERSION.SDK_INT >= SIXTY_FPS_INTERVAL) {
            SDK16.postOnAnimation(view, runnable);
        } else {
            view.postDelayed(runnable, 16);
        }
    }
}
