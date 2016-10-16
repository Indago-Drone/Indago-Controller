package com.lewei.multiple.photoview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.widget.OverScroller;
import android.widget.Scroller;

public abstract class ScrollerProxy {

    @TargetApi(9)
    private static class GingerScroller extends ScrollerProxy {
        private OverScroller mScroller;

        public GingerScroller(Context context) {
            this.mScroller = new OverScroller(context);
        }

        public boolean computeScrollOffset() {
            return this.mScroller.computeScrollOffset();
        }

        public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY, int overX, int overY) {
            this.mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, overX, overY);
        }

        public void forceFinished(boolean finished) {
            this.mScroller.forceFinished(finished);
        }

        public int getCurrX() {
            return this.mScroller.getCurrX();
        }

        public int getCurrY() {
            return this.mScroller.getCurrY();
        }
    }

    private static class PreGingerScroller extends ScrollerProxy {
        private Scroller mScroller;

        public PreGingerScroller(Context context) {
            this.mScroller = new Scroller(context);
        }

        public boolean computeScrollOffset() {
            return this.mScroller.computeScrollOffset();
        }

        public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY, int overX, int overY) {
            this.mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
        }

        public void forceFinished(boolean finished) {
            this.mScroller.forceFinished(finished);
        }

        public int getCurrX() {
            return this.mScroller.getCurrX();
        }

        public int getCurrY() {
            return this.mScroller.getCurrY();
        }
    }

    public abstract boolean computeScrollOffset();

    public abstract void fling(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10);

    public abstract void forceFinished(boolean z);

    public abstract int getCurrX();

    public abstract int getCurrY();

    public static ScrollerProxy getScroller(Context context) {
        if (VERSION.SDK_INT < 9) {
            return new PreGingerScroller(context);
        }
        return new GingerScroller(context);
    }
}
