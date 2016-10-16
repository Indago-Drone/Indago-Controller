package com.lewei.multiple.app;

import android.content.Context;
import android.media.SoundPool;
import com.lewei.multiple.fydrone.C0052R;
import com.lewei.multiple.fydrone.R;
import com.lewei.multiple.photoview.PhotoViewAttacher;

public class Media {
    private SoundPool mSoundPool;

    public Media(Context context) {
        this.mSoundPool = new SoundPool(5, 1, 5);
        this.mSoundPool.load(context, R.raw.btn_turn, 1);
        this.mSoundPool.load(context, R.raw.btn_middle, 2);
        this.mSoundPool.load(context, R.raw.shutter, 3);
    }

    public void playShutter() {
        this.mSoundPool.play(3, PhotoViewAttacher.DEFAULT_MIN_SCALE, PhotoViewAttacher.DEFAULT_MIN_SCALE, 3, 0, PhotoViewAttacher.DEFAULT_MIN_SCALE);
    }

    public void playBtnTurn() {
        this.mSoundPool.play(1, PhotoViewAttacher.DEFAULT_MIN_SCALE, PhotoViewAttacher.DEFAULT_MIN_SCALE, 1, 0, PhotoViewAttacher.DEFAULT_MIN_SCALE);
    }

    public void playBtnMiddle() {
        this.mSoundPool.play(2, PhotoViewAttacher.DEFAULT_MIN_SCALE, PhotoViewAttacher.DEFAULT_MIN_SCALE, 2, 0, PhotoViewAttacher.DEFAULT_MIN_SCALE);
    }
}
