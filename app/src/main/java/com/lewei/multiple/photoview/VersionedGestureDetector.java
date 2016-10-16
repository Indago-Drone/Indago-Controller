package com.lewei.multiple.photoview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.CursorAdapter;
import android.util.FloatMath;
//import android.support.v4.app
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

public abstract class VersionedGestureDetector {
    static final String LOG_TAG = "VersionedGestureDetector";
    OnGestureListener mListener;

    public interface OnGestureListener {
        void onDrag(float f, float f2);

        void onFling(float f, float f2, float f3, float f4);

        void onScale(float f, float f2, float f3);
    }

    private static class CupcakeDetector extends VersionedGestureDetector {
        private boolean mIsDragging;
        float mLastTouchX;
        float mLastTouchY;
        final float mMinimumVelocity;
        final float mTouchSlop;
        private VelocityTracker mVelocityTracker;

        public CupcakeDetector(Context context) {
            ViewConfiguration configuration = ViewConfiguration.get(context);
            this.mMinimumVelocity = (float) configuration.getScaledMinimumFlingVelocity();
            this.mTouchSlop = (float) configuration.getScaledTouchSlop();
        }

        float getActiveX(MotionEvent ev) {
            return ev.getX();
        }

        float getActiveY(MotionEvent ev) {
            return ev.getY();
        }

        public boolean isScaling() {
            return false;
        }

        @SuppressLint({"FloatMath"})
        public boolean onTouchEvent(MotionEvent ev) {
            boolean z = false;
            switch (ev.getAction()) {
                case DialogFragment.STYLE_NORMAL /*0*/:
                    this.mVelocityTracker = VelocityTracker.obtain();
                    this.mVelocityTracker.addMovement(ev);
                    this.mLastTouchX = getActiveX(ev);
                    this.mLastTouchY = getActiveY(ev);
                    this.mIsDragging = false;
                    break;
                case CursorAdapter.FLAG_AUTO_REQUERY /*1*/:
                    if (this.mIsDragging && this.mVelocityTracker != null) {
                        this.mLastTouchX = getActiveX(ev);
                        this.mLastTouchY = getActiveY(ev);
                        this.mVelocityTracker.addMovement(ev);
                        this.mVelocityTracker.computeCurrentVelocity(1000);
                        float vX = this.mVelocityTracker.getXVelocity();
                        float vY = this.mVelocityTracker.getYVelocity();
                        if (Math.max(Math.abs(vX), Math.abs(vY)) >= this.mMinimumVelocity) {
                            this.mListener.onFling(this.mLastTouchX, this.mLastTouchY, -vX, -vY);
                        }
                    }
                    if (this.mVelocityTracker != null) {
                        this.mVelocityTracker.recycle();
                        this.mVelocityTracker = null;
                        break;
                    }
                    break;
                case CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER /*2*/:
                    float x = getActiveX(ev);
                    float y = getActiveY(ev);
                    float dx = x - this.mLastTouchX;
                    float dy = y - this.mLastTouchY;
                    if (!this.mIsDragging) {
                        if (Math.sqrt((dx * dx) + (dy * dy)) >= this.mTouchSlop) {
                            z = true;
                        }
                        this.mIsDragging = z;
                    }
                    if (this.mIsDragging) {
                        this.mListener.onDrag(dx, dy);
                        this.mLastTouchX = x;
                        this.mLastTouchY = y;
                        if (this.mVelocityTracker != null) {
                            this.mVelocityTracker.addMovement(ev);
                            break;
                        }
                    }
                    break;
                  //case FragmentManagerImpl.ANIM_STYLE_CLOSE_ENTER /*3*/:
                    /*if (this.mVelocityTracker != null) {
                        this.mVelocityTracker.recycle();
                        this.mVelocityTracker = null;
                        break;
                    }
                    break;*/
            }
            return true;
        }
    }

    @TargetApi(5)
    private static class EclairDetector extends CupcakeDetector {
        private static final int INVALID_POINTER_ID = -1;
        private int mActivePointerId;
        private int mActivePointerIndex;

        public EclairDetector(Context context) {
            super(context);
            this.mActivePointerId = INVALID_POINTER_ID;
            this.mActivePointerIndex = 0;
        }

        float getActiveX(MotionEvent ev) {
            try {
                return ev.getX(this.mActivePointerIndex);
            } catch (Exception e) {
                return ev.getX();
            }
        }

        float getActiveY(MotionEvent ev) {
            try {
                return ev.getY(this.mActivePointerIndex);
            } catch (Exception e) {
                return ev.getY();
            }
        }

        public boolean onTouchEvent(MotionEvent ev) {
            int i = 0;
            switch (ev.getAction() & MotionEventCompat.ACTION_MASK) {
                case DialogFragment.STYLE_NORMAL /*0*/:
                    this.mActivePointerId = ev.getPointerId(0);
                    break;
                case CursorAdapter.FLAG_AUTO_REQUERY /*1*/:
                case 3 /*3 FragmentManagerImpl.ANIM_STYLE_CLOSE_ENTER*/:
                   this.mActivePointerId = INVALID_POINTER_ID;
                    break;
                case 6 /*6 FragmentManagerImpl.ANIM_STYLE_FADE_EXIT*/:
                    int pointerIndex = (ev.getAction() & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
                    if (ev.getPointerId(pointerIndex) == this.mActivePointerId) {
                        int newPointerIndex;
                        if (pointerIndex == 0) {
                            newPointerIndex = 1;
                        } else {
                            newPointerIndex = 0;
                        }
                        this.mActivePointerId = ev.getPointerId(newPointerIndex);
                        this.mLastTouchX = ev.getX(newPointerIndex);
                        this.mLastTouchY = ev.getY(newPointerIndex);
                        break;
                    }
                    break;
            }
            if (this.mActivePointerId != INVALID_POINTER_ID) {
                i = this.mActivePointerId;
            }
            this.mActivePointerIndex = ev.findPointerIndex(i);
            return super.onTouchEvent(ev);
        }
    }

    @TargetApi(8)
    private static class FroyoDetector extends EclairDetector {
        private final ScaleGestureDetector mDetector;
        private final OnScaleGestureListener mScaleListener;

        /* renamed from: com.lewei.multiple.photoview.VersionedGestureDetector.FroyoDetector.1 */
        class C00901 implements OnScaleGestureListener {
            C00901() {
            }

            public boolean onScale(ScaleGestureDetector detector) {
                FroyoDetector.this.mListener.onScale(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
                return true;
            }

            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            public void onScaleEnd(ScaleGestureDetector detector) {
            }
        }

        public FroyoDetector(Context context) {
            super(context);
            this.mScaleListener = new C00901();
            this.mDetector = new ScaleGestureDetector(context, this.mScaleListener);
        }

        public boolean isScaling() {
            return this.mDetector.isInProgress();
        }

        public boolean onTouchEvent(MotionEvent ev) {
            this.mDetector.onTouchEvent(ev);
            return super.onTouchEvent(ev);
        }
    }

    public abstract boolean isScaling();

    public abstract boolean onTouchEvent(MotionEvent motionEvent);

    public static VersionedGestureDetector newInstance(Context context, OnGestureListener listener) {
        VersionedGestureDetector detector;
        int sdkVersion = VERSION.SDK_INT;
        if (sdkVersion < 5) {
            detector = new CupcakeDetector(context);
        } else if (sdkVersion < 8) {
            detector = new EclairDetector(context);
        } else {
            detector = new FroyoDetector(context);
        }
        detector.mListener = listener;
        return detector;
    }
}
