package com.lewei.multiple.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MyImageView extends ImageView {
    private OnMeasureListener onMeasureListener;

    public interface OnMeasureListener {
        void onMeasureSize(int i, int i2);
    }

    public void setOnMeasureListener(OnMeasureListener onMeasureListener) {
        this.onMeasureListener = onMeasureListener;
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.onMeasureListener != null) {
            this.onMeasureListener.onMeasureSize(getMeasuredWidth(), getMeasuredHeight());
        }
    }
}
