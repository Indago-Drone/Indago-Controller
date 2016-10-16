package com.lewei.multiple.photoview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.media.TransportMediator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.lewei.lib63.LeweiLib63;
import com.lewei.multiple.photoview.VersionedGestureDetector.OnGestureListener;
import java.lang.ref.WeakReference;

public class PhotoViewAttacher implements IPhotoView, OnTouchListener, OnGestureListener, OnDoubleTapListener, OnGlobalLayoutListener {
    private static /* synthetic */ int[] $SWITCH_TABLE$android$widget$ImageView$ScaleType = null;
    static final boolean DEBUG;
    public static final float DEFAULT_MAX_SCALE = 3.0f;
    public static final float DEFAULT_MID_SCALE = 1.75f;
    public static final float DEFAULT_MIN_SCALE = 1.0f;
    static final int EDGE_BOTH = 2;
    static final int EDGE_LEFT = 0;
    static final int EDGE_NONE = -1;
    static final int EDGE_RIGHT = 1;
    static final String LOG_TAG = "PhotoViewAttacher";
    private boolean mAllowParentInterceptOnEdge;
    private final Matrix mBaseMatrix;
    private FlingRunnable mCurrentFlingRunnable;
    private final RectF mDisplayRect;
    private final Matrix mDrawMatrix;
    private GestureDetector mGestureDetector;
    private WeakReference<ImageView> mImageView;
    private int mIvBottom;
    private int mIvLeft;
    private int mIvRight;
    private int mIvTop;
    private OnLongClickListener mLongClickListener;
    private OnMatrixChangedListener mMatrixChangeListener;
    private final float[] mMatrixValues;
    private float mMaxScale;
    private float mMidScale;
    private float mMinScale;
    private OnPhotoTapListener mPhotoTapListener;
    private VersionedGestureDetector mScaleDragDetector;
    private ScaleType mScaleType;
    private int mScrollEdge;
    private final Matrix mSuppMatrix;
    private OnViewTapListener mViewTapListener;
    private ViewTreeObserver mViewTreeObserver;
    private boolean mZoomEnabled;

    /* renamed from: com.lewei.multiple.photoview.PhotoViewAttacher.1 */
    class C00891 extends SimpleOnGestureListener {
        C00891() {
        }

        public void onLongPress(MotionEvent e) {
            if (PhotoViewAttacher.this.mLongClickListener != null) {
                PhotoViewAttacher.this.mLongClickListener.onLongClick((View) PhotoViewAttacher.this.mImageView.get());
            }
        }
    }

    private class AnimatedZoomRunnable implements Runnable {
        static final float ANIMATION_SCALE_PER_ITERATION_IN = 1.07f;
        static final float ANIMATION_SCALE_PER_ITERATION_OUT = 0.93f;
        private final float mDeltaScale;
        private final float mFocalX;
        private final float mFocalY;
        private final float mTargetZoom;

        public AnimatedZoomRunnable(float currentZoom, float targetZoom, float focalX, float focalY) {
            this.mTargetZoom = targetZoom;
            this.mFocalX = focalX;
            this.mFocalY = focalY;
            if (currentZoom < targetZoom) {
                this.mDeltaScale = ANIMATION_SCALE_PER_ITERATION_IN;
            } else {
                this.mDeltaScale = ANIMATION_SCALE_PER_ITERATION_OUT;
            }
        }

        public void run() {
            ImageView imageView = PhotoViewAttacher.this.getImageView();
            if (imageView != null) {
                PhotoViewAttacher.this.mSuppMatrix.postScale(this.mDeltaScale, this.mDeltaScale, this.mFocalX, this.mFocalY);
                PhotoViewAttacher.this.checkAndDisplayMatrix();
                float currentScale = PhotoViewAttacher.this.getScale();
                if ((this.mDeltaScale <= PhotoViewAttacher.DEFAULT_MIN_SCALE || currentScale >= this.mTargetZoom) && (this.mDeltaScale >= PhotoViewAttacher.DEFAULT_MIN_SCALE || this.mTargetZoom >= currentScale)) {
                    float delta = this.mTargetZoom / currentScale;
                    PhotoViewAttacher.this.mSuppMatrix.postScale(delta, delta, this.mFocalX, this.mFocalY);
                    PhotoViewAttacher.this.checkAndDisplayMatrix();
                    return;
                }
                Compat.postOnAnimation(imageView, this);
            }
        }
    }

    private class FlingRunnable implements Runnable {
        private int mCurrentX;
        private int mCurrentY;
        private final ScrollerProxy mScroller;

        public FlingRunnable(Context context) {
            this.mScroller = ScrollerProxy.getScroller(context);
        }

        public void cancelFling() {
            if (PhotoViewAttacher.DEBUG) {
                Log.d(PhotoViewAttacher.LOG_TAG, "Cancel Fling");
            }
            this.mScroller.forceFinished(true);
        }

        public void fling(int viewWidth, int viewHeight, int velocityX, int velocityY) {
            RectF rect = PhotoViewAttacher.this.getDisplayRect();
            if (rect != null) {
                int minX;
                int maxX;
                int minY;
                int maxY;
                int startX = Math.round(-rect.left);
                if (((float) viewWidth) < rect.width()) {
                    minX = PhotoViewAttacher.EDGE_LEFT;
                    maxX = Math.round(rect.width() - ((float) viewWidth));
                } else {
                    maxX = startX;
                    minX = startX;
                }
                int startY = Math.round(-rect.top);
                if (((float) viewHeight) < rect.height()) {
                    minY = PhotoViewAttacher.EDGE_LEFT;
                    maxY = Math.round(rect.height() - ((float) viewHeight));
                } else {
                    maxY = startY;
                    minY = startY;
                }
                this.mCurrentX = startX;
                this.mCurrentY = startY;
                if (PhotoViewAttacher.DEBUG) {
                    Log.d(PhotoViewAttacher.LOG_TAG, "fling. StartX:" + startX + " StartY:" + startY + " MaxX:" + maxX + " MaxY:" + maxY);
                }
                if (startX != maxX || startY != maxY) {
                    this.mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, PhotoViewAttacher.EDGE_LEFT, PhotoViewAttacher.EDGE_LEFT);
                }
            }
        }

        public void run() {
            ImageView imageView = PhotoViewAttacher.this.getImageView();
            if (imageView != null && this.mScroller.computeScrollOffset()) {
                int newX = this.mScroller.getCurrX();
                int newY = this.mScroller.getCurrY();
                if (PhotoViewAttacher.DEBUG) {
                    Log.d(PhotoViewAttacher.LOG_TAG, "fling run(). CurrentX:" + this.mCurrentX + " CurrentY:" + this.mCurrentY + " NewX:" + newX + " NewY:" + newY);
                }
                PhotoViewAttacher.this.mSuppMatrix.postTranslate((float) (this.mCurrentX - newX), (float) (this.mCurrentY - newY));
                PhotoViewAttacher.this.setImageViewMatrix(PhotoViewAttacher.this.getDisplayMatrix());
                this.mCurrentX = newX;
                this.mCurrentY = newY;
                Compat.postOnAnimation(imageView, this);
            }
        }
    }

    public interface OnMatrixChangedListener {
        void onMatrixChanged(RectF rectF);
    }

    public interface OnPhotoTapListener {
        void onPhotoTap(View view, float f, float f2);
    }

    public interface OnViewTapListener {
        void onViewTap(View view, float f, float f2);
    }

    static /* synthetic */ int[] $SWITCH_TABLE$android$widget$ImageView$ScaleType() {
        int[] iArr = $SWITCH_TABLE$android$widget$ImageView$ScaleType;
        if (iArr == null) {
            iArr = new int[ScaleType.values().length];
            try {
                iArr[ScaleType.CENTER.ordinal()] = EDGE_RIGHT;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[ScaleType.CENTER_CROP.ordinal()] = EDGE_BOTH;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[ScaleType.CENTER_INSIDE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[ScaleType.FIT_CENTER.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[ScaleType.FIT_END.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[ScaleType.FIT_START.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[ScaleType.FIT_XY.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                iArr[ScaleType.MATRIX.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            $SWITCH_TABLE$android$widget$ImageView$ScaleType = iArr;
        }
        return iArr;
    }

    static {
        DEBUG = Log.isLoggable(LOG_TAG, 3);
    }

    private static void checkZoomLevels(float minZoom, float midZoom, float maxZoom) {
        if (minZoom >= midZoom) {
            throw new IllegalArgumentException("MinZoom should be less than MidZoom");
        } else if (midZoom >= maxZoom) {
            throw new IllegalArgumentException("MidZoom should be less than MaxZoom");
        }
    }

    private static boolean hasDrawable(ImageView imageView) {
        return (imageView == null || imageView.getDrawable() == null) ? DEBUG : true;
    }

    private static boolean isSupportedScaleType(ScaleType scaleType) {
        if (scaleType == null) {
            return DEBUG;
        }
        switch ($SWITCH_TABLE$android$widget$ImageView$ScaleType()[scaleType.ordinal()]) {
            case TransportMediator.FLAG_KEY_MEDIA_PLAY_PAUSE /*8*/:
                throw new IllegalArgumentException(scaleType.name() + " is not supported in PhotoView");
            default:
                return true;
        }
    }

    private static void setImageViewScaleTypeMatrix(ImageView imageView) {
        if (imageView != null && !(imageView instanceof PhotoView)) {
            imageView.setScaleType(ScaleType.MATRIX);
        }
    }

    public PhotoViewAttacher(ImageView imageView) {
        this.mMinScale = DEFAULT_MIN_SCALE;
        this.mMidScale = DEFAULT_MID_SCALE;
        this.mMaxScale = DEFAULT_MAX_SCALE;
        this.mAllowParentInterceptOnEdge = true;
        this.mBaseMatrix = new Matrix();
        this.mDrawMatrix = new Matrix();
        this.mSuppMatrix = new Matrix();
        this.mDisplayRect = new RectF();
        this.mMatrixValues = new float[9];
        this.mScrollEdge = EDGE_BOTH;
        this.mScaleType = ScaleType.FIT_CENTER;
        this.mImageView = new WeakReference(imageView);
        imageView.setOnTouchListener(this);
        this.mViewTreeObserver = imageView.getViewTreeObserver();
        this.mViewTreeObserver.addOnGlobalLayoutListener(this);
        setImageViewScaleTypeMatrix(imageView);
        if (!imageView.isInEditMode()) {
            this.mScaleDragDetector = VersionedGestureDetector.newInstance(imageView.getContext(), this);
            this.mGestureDetector = new GestureDetector(imageView.getContext(), new C00891());
            this.mGestureDetector.setOnDoubleTapListener(this);
            setZoomable(true);
        }
    }

    public final boolean canZoom() {
        return this.mZoomEnabled;
    }

    @SuppressLint({"NewApi"})
    public final void cleanup() {
        if (VERSION.SDK_INT >= 16) {
            if (this.mImageView != null) {
                ((ImageView) this.mImageView.get()).getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
            if (this.mViewTreeObserver != null && this.mViewTreeObserver.isAlive()) {
                this.mViewTreeObserver.removeOnGlobalLayoutListener(this);
                this.mViewTreeObserver = null;
                this.mMatrixChangeListener = null;
                this.mPhotoTapListener = null;
                this.mViewTapListener = null;
                this.mImageView = null;
                return;
            }
            return;
        }
        if (this.mImageView != null) {
            ((ImageView) this.mImageView.get()).getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
        if (this.mViewTreeObserver != null && this.mViewTreeObserver.isAlive()) {
            this.mViewTreeObserver.removeGlobalOnLayoutListener(this);
            this.mViewTreeObserver = null;
            this.mMatrixChangeListener = null;
            this.mPhotoTapListener = null;
            this.mViewTapListener = null;
            this.mImageView = null;
        }
    }

    public final RectF getDisplayRect() {
        checkMatrixBounds();
        return getDisplayRect(getDisplayMatrix());
    }

    public final ImageView getImageView() {
        ImageView imageView = null;
        if (this.mImageView != null) {
            imageView = (ImageView) this.mImageView.get();
        }
        if (imageView != null) {
            return imageView;
        }
        cleanup();
        throw new IllegalStateException("ImageView no longer exists. You should not use this PhotoViewAttacher any more.");
    }

    public float getMinScale() {
        return this.mMinScale;
    }

    public float getMidScale() {
        return this.mMidScale;
    }

    public float getMaxScale() {
        return this.mMaxScale;
    }

    public final float getScale() {
        return getValue(this.mSuppMatrix, EDGE_LEFT);
    }

    public final ScaleType getScaleType() {
        return this.mScaleType;
    }

    public final boolean onDoubleTap(MotionEvent ev) {
        try {
            float scale = getScale();
            float x = ev.getX();
            float y = ev.getY();
            if (scale < this.mMidScale) {
                zoomTo(this.mMidScale, x, y);
            } else if (scale < this.mMidScale || scale >= this.mMaxScale) {
                zoomTo(this.mMinScale, x, y);
            } else {
                zoomTo(this.mMaxScale, x, y);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        return true;
    }

    public final boolean onDoubleTapEvent(MotionEvent e) {
        return DEBUG;
    }

    public final void onDrag(float dx, float dy) {
        if (DEBUG) {
            String str = LOG_TAG;
            Object[] objArr = new Object[EDGE_BOTH];
            objArr[EDGE_LEFT] = Float.valueOf(dx);
            objArr[EDGE_RIGHT] = Float.valueOf(dy);
            Log.d(str, String.format("onDrag: dx: %.2f. dy: %.2f", objArr));
        }
        ImageView imageView = getImageView();
        if (imageView != null && hasDrawable(imageView)) {
            this.mSuppMatrix.postTranslate(dx, dy);
            checkAndDisplayMatrix();
            if (this.mAllowParentInterceptOnEdge && !this.mScaleDragDetector.isScaling()) {
                if (this.mScrollEdge == EDGE_BOTH || ((this.mScrollEdge == 0 && dx >= DEFAULT_MIN_SCALE) || (this.mScrollEdge == EDGE_RIGHT && dx <= -1.0f))) {
                    imageView.getParent().requestDisallowInterceptTouchEvent(DEBUG);
                }
            }
        }
    }

    public final void onFling(float startX, float startY, float velocityX, float velocityY) {
        if (DEBUG) {
            Log.d(LOG_TAG, "onFling. sX: " + startX + " sY: " + startY + " Vx: " + velocityX + " Vy: " + velocityY);
        }
        ImageView imageView = getImageView();
        if (hasDrawable(imageView)) {
            this.mCurrentFlingRunnable = new FlingRunnable(imageView.getContext());
            this.mCurrentFlingRunnable.fling(imageView.getWidth(), imageView.getHeight(), (int) velocityX, (int) velocityY);
            imageView.post(this.mCurrentFlingRunnable);
        }
    }

    public final void onGlobalLayout() {
        ImageView imageView = getImageView();
        if (imageView != null && this.mZoomEnabled) {
            int top = imageView.getTop();
            int right = imageView.getRight();
            int bottom = imageView.getBottom();
            int left = imageView.getLeft();
            if (top != this.mIvTop || bottom != this.mIvBottom || left != this.mIvLeft || right != this.mIvRight) {
                updateBaseMatrix(imageView.getDrawable());
                this.mIvTop = top;
                this.mIvRight = right;
                this.mIvBottom = bottom;
                this.mIvLeft = left;
            }
        }
    }

    public final void onScale(float scaleFactor, float focusX, float focusY) {
        if (DEBUG) {
            Log.d(LOG_TAG, String.format("onScale: scale: %.2f. fX: %.2f. fY: %.2f", new Object[]{Float.valueOf(scaleFactor), Float.valueOf(focusX), Float.valueOf(focusY)}));
        }
        if (!hasDrawable(getImageView())) {
            return;
        }
        if (getScale() < this.mMaxScale || scaleFactor < DEFAULT_MIN_SCALE) {
            this.mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
            checkAndDisplayMatrix();
        }
    }

    public final boolean onSingleTapConfirmed(MotionEvent e) {
        ImageView imageView = getImageView();
        if (imageView != null) {
            if (this.mPhotoTapListener != null) {
                RectF displayRect = getDisplayRect();
                if (displayRect != null) {
                    float x = e.getX();
                    float y = e.getY();
                    if (displayRect.contains(x, y)) {
                        this.mPhotoTapListener.onPhotoTap(imageView, (x - displayRect.left) / displayRect.width(), (y - displayRect.top) / displayRect.height());
                        return true;
                    }
                }
            }
            if (this.mViewTapListener != null) {
                this.mViewTapListener.onViewTap(imageView, e.getX(), e.getY());
            }
        }
        return DEBUG;
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public final boolean onTouch(View v, MotionEvent ev) {
        boolean handled = DEBUG;
        if (!this.mZoomEnabled) {
            return DEBUG;
        }
        switch (ev.getAction()) {
            case EDGE_LEFT /*0*/:
                v.getParent().requestDisallowInterceptTouchEvent(true);
                cancelFling();
                break;
            case EDGE_RIGHT /*1*/:
            //case FragmentManagerImpl.ANIM_STYLE_CLOSE_ENTER /*3*/:
               /* if (getScale() < this.mMinScale) {
                    RectF rect = getDisplayRect();
                    if (rect != null) {
                        v.post(new AnimatedZoomRunnable(getScale(), this.mMinScale, rect.centerX(), rect.centerY()));
                        handled = true;
                        break;
                    }
                }
                break;*/
        }
        if (this.mGestureDetector != null && this.mGestureDetector.onTouchEvent(ev)) {
            handled = true;
        }
        if (this.mScaleDragDetector == null || !this.mScaleDragDetector.onTouchEvent(ev)) {
            return handled;
        }
        return true;
    }

    public void setAllowParentInterceptOnEdge(boolean allow) {
        this.mAllowParentInterceptOnEdge = allow;
    }

    public void setMinScale(float minScale) {
        checkZoomLevels(minScale, this.mMidScale, this.mMaxScale);
        this.mMinScale = minScale;
    }

    public void setMidScale(float midScale) {
        checkZoomLevels(this.mMinScale, midScale, this.mMaxScale);
        this.mMidScale = midScale;
    }

    public void setMaxScale(float maxScale) {
        checkZoomLevels(this.mMinScale, this.mMidScale, maxScale);
        this.mMaxScale = maxScale;
    }

    public final void setOnLongClickListener(OnLongClickListener listener) {
        this.mLongClickListener = listener;
    }

    public final void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
        this.mMatrixChangeListener = listener;
    }

    public final void setOnPhotoTapListener(OnPhotoTapListener listener) {
        this.mPhotoTapListener = listener;
    }

    public final void setOnViewTapListener(OnViewTapListener listener) {
        this.mViewTapListener = listener;
    }

    public final void setScaleType(ScaleType scaleType) {
        if (isSupportedScaleType(scaleType) && scaleType != this.mScaleType) {
            this.mScaleType = scaleType;
            update();
        }
    }

    public final void setZoomable(boolean zoomable) {
        this.mZoomEnabled = zoomable;
        update();
    }

    public final void update() {
        ImageView imageView = getImageView();
        if (imageView == null) {
            return;
        }
        if (this.mZoomEnabled) {
            setImageViewScaleTypeMatrix(imageView);
            updateBaseMatrix(imageView.getDrawable());
            return;
        }
        resetMatrix();
    }

    public final void zoomTo(float scale, float focalX, float focalY) {
        ImageView imageView = getImageView();
        if (imageView != null) {
            imageView.post(new AnimatedZoomRunnable(getScale(), scale, focalX, focalY));
        }
    }

    protected Matrix getDisplayMatrix() {
        this.mDrawMatrix.set(this.mBaseMatrix);
        this.mDrawMatrix.postConcat(this.mSuppMatrix);
        return this.mDrawMatrix;
    }

    private void cancelFling() {
        if (this.mCurrentFlingRunnable != null) {
            this.mCurrentFlingRunnable.cancelFling();
            this.mCurrentFlingRunnable = null;
        }
    }

    private void checkAndDisplayMatrix() {
        checkMatrixBounds();
        setImageViewMatrix(getDisplayMatrix());
    }

    private void checkImageViewScaleType() {
        ImageView imageView = getImageView();
        if (imageView != null && !(imageView instanceof PhotoView) && imageView.getScaleType() != ScaleType.MATRIX) {
            throw new IllegalStateException("The ImageView's ScaleType has been changed since attaching a PhotoViewAttacher");
        }
    }

    private void checkMatrixBounds() {
        ImageView imageView = getImageView();
        if (imageView != null) {
            RectF rect = getDisplayRect(getDisplayMatrix());
            if (rect != null) {
                float height = rect.height();
                float width = rect.width();
                float deltaX = 0.0f;
                float deltaY = 0.0f;
                int viewHeight = imageView.getHeight();
                if (height <= ((float) viewHeight)) {
                    switch ($SWITCH_TABLE$android$widget$ImageView$ScaleType()[this.mScaleType.ordinal()]) {
                        case 5 /*5 FragmentManagerImpl.ANIM_STYLE_FADE_ENTER*/:
                            deltaY = (((float) viewHeight) - height) - rect.top;
                            break;
                        case 6 /*6 FragmentManagerImpl.ANIM_STYLE_FADE_EXIT*/:
                            deltaY = -rect.top;
                            break;
                        default:
                            deltaY = ((((float) viewHeight) - height) / 2.0f) - rect.top;
                            break;
                    }
                } else if (rect.top > 0.0f) {
                    deltaY = -rect.top;
                } else if (rect.bottom < ((float) viewHeight)) {
                    deltaY = ((float) viewHeight) - rect.bottom;
                }
                int viewWidth = imageView.getWidth();
                if (width <= ((float) viewWidth)) {
                    switch ($SWITCH_TABLE$android$widget$ImageView$ScaleType()[this.mScaleType.ordinal()]) {
                        case 5 /*5 FragmentManagerImpl.ANIM_STYLE_FADE_ENTER*/:
                            deltaX = (((float) viewWidth) - width) - rect.left;
                            break;
                        case 6 /*6 FragmentManagerImpl.ANIM_STYLE_FADE_EXIT*/:
                            deltaX = -rect.left;
                            break;
                        default:
                            deltaX = ((((float) viewWidth) - width) / 2.0f) - rect.left;
                            break;
                    }
                    this.mScrollEdge = EDGE_BOTH;
                } else if (rect.left > 0.0f) {
                    this.mScrollEdge = EDGE_LEFT;
                    deltaX = -rect.left;
                } else if (rect.right < ((float) viewWidth)) {
                    deltaX = ((float) viewWidth) - rect.right;
                    this.mScrollEdge = EDGE_RIGHT;
                } else {
                    this.mScrollEdge = EDGE_NONE;
                }
                this.mSuppMatrix.postTranslate(deltaX, deltaY);
            }
        }
    }

    private RectF getDisplayRect(Matrix matrix) {
        ImageView imageView = getImageView();
        if (imageView != null) {
            Drawable d = imageView.getDrawable();
            if (d != null) {
                this.mDisplayRect.set(0.0f, 0.0f, (float) d.getIntrinsicWidth(), (float) d.getIntrinsicHeight());
                matrix.mapRect(this.mDisplayRect);
                return this.mDisplayRect;
            }
        }
        return null;
    }

    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(this.mMatrixValues);
        return this.mMatrixValues[whichValue];
    }

    private void resetMatrix() {
        this.mSuppMatrix.reset();
        setImageViewMatrix(getDisplayMatrix());
        checkMatrixBounds();
    }

    private void setImageViewMatrix(Matrix matrix) {
        ImageView imageView = getImageView();
        if (imageView != null) {
            checkImageViewScaleType();
            imageView.setImageMatrix(matrix);
            if (this.mMatrixChangeListener != null) {
                RectF displayRect = getDisplayRect(matrix);
                if (displayRect != null) {
                    this.mMatrixChangeListener.onMatrixChanged(displayRect);
                }
            }
        }
    }

    private void updateBaseMatrix(Drawable d) {
        ImageView imageView = getImageView();
        if (imageView != null && d != null) {
            float viewWidth = (float) imageView.getWidth();
            float viewHeight = (float) imageView.getHeight();
            int drawableWidth = d.getIntrinsicWidth();
            int drawableHeight = d.getIntrinsicHeight();
            this.mBaseMatrix.reset();
            float widthScale = viewWidth / ((float) drawableWidth);
            float heightScale = viewHeight / ((float) drawableHeight);
            if (this.mScaleType != ScaleType.CENTER) {
                float scale;
                if (this.mScaleType != ScaleType.CENTER_CROP) {
                    if (this.mScaleType != ScaleType.CENTER_INSIDE) {
                        RectF mTempSrc = new RectF(0.0f, 0.0f, (float) drawableWidth, (float) drawableHeight);
                        RectF mTempDst = new RectF(0.0f, 0.0f, viewWidth, viewHeight);
                        switch ($SWITCH_TABLE$android$widget$ImageView$ScaleType()[this.mScaleType.ordinal()]) {
                            case TransportMediator.FLAG_KEY_MEDIA_PLAY /*4*/:
                                this.mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.CENTER);
                                break;
                            case 5 /*5 FragmentManagerImpl.ANIM_STYLE_FADE_ENTER*/:
                                this.mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.END);
                                break;
                            case 6 /*6 FragmentManagerImpl.ANIM_STYLE_FADE_EXIT*/:
                                this.mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.START);
                                break;
                            case LeweiLib63.FHNPEN_SYS_Upgrade /*7*/:
                                this.mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.FILL);
                                break;
                            default:
                                break;
                        }
                    }
                    scale = Math.min(DEFAULT_MIN_SCALE, Math.min(widthScale, heightScale));
                    this.mBaseMatrix.postScale(scale, scale);
                    this.mBaseMatrix.postTranslate((viewWidth - (((float) drawableWidth) * scale)) / 2.0f, (viewHeight - (((float) drawableHeight) * scale)) / 2.0f);
                } else {
                    scale = Math.max(widthScale, heightScale);
                    this.mBaseMatrix.postScale(scale, scale);
                    this.mBaseMatrix.postTranslate((viewWidth - (((float) drawableWidth) * scale)) / 2.0f, (viewHeight - (((float) drawableHeight) * scale)) / 2.0f);
                }
            } else {
                this.mBaseMatrix.postTranslate((viewWidth - ((float) drawableWidth)) / 2.0f, (viewHeight - ((float) drawableHeight)) / 2.0f);
            }
            resetMatrix();
        }
    }
}
