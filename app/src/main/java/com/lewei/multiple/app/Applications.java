package com.lewei.multiple.app;

import android.app.Application;
import android.os.Build.VERSION;
import com.lewei.lib.LeweiLib;
import com.lewei.lib63.Device63;
import com.lewei.multiple.utils.ParamsConfig;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration.Builder;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class Applications extends Application {
    public static final int LIB_TYPE_63 = 1;
    public static final int LIB_TYPE_83 = 2;
    public static final int LIB_TYPE_93 = 4;
    public static final int LIB_TYPE_NULL = 0;
    public static final int SPEED_LOW = 1;
    public static final int SPEED_MAX = 3;
    public static final int SPEED_MID = 2;
    public static boolean isAllCtrlHide;
    public static int isFlip;
    public static boolean isLimitedHigh;
    public static boolean isParamsAutoSave;
    public static boolean isRightHandMode;
    public static boolean isSensorOn;
    public static Device63 mDevice63;
    public static int mRunLeweiLibType;
    public static int speed_level;

    static {
        isFlip = LIB_TYPE_NULL;
        isAllCtrlHide = false;
        isLimitedHigh = false;
        isSensorOn = false;
        isRightHandMode = false;
        isParamsAutoSave = true;
        speed_level = SPEED_LOW;
        mRunLeweiLibType = LIB_TYPE_NULL;
    }

    public void onCreate() {
        super.onCreate();
        System.out.println("Create Applications");
        ImageLoader.getInstance().init(new Builder(getApplicationContext()).threadPriority(SPEED_MAX).denyCacheImageMultipleSizesInMemory().discCacheFileCount(20).discCacheFileNameGenerator(new Md5FileNameGenerator()).defaultDisplayImageOptions(DisplayImageOptions.createSimple()).tasksProcessingOrder(QueueProcessingType.LIFO).enableLogging().build());
        mDevice63 = new Device63(getApplicationContext());
        mDevice63.startLoginThread();
        isRightHandMode = ParamsConfig.readRightHandMode(getApplicationContext());
        isParamsAutoSave = ParamsConfig.readParamsAutoSave(getApplicationContext());
        LeweiLib.HD_flag = ParamsConfig.readHDflag(getApplicationContext());
        if (VERSION.SDK_INT >= 16) {
            LeweiLib.Hardware_flag = ParamsConfig.readHardwareDecode(getApplicationContext());
            return;
        }
        LeweiLib.Hardware_flag = LIB_TYPE_NULL;
        ParamsConfig.writeHardwareDecode(getApplicationContext(), LIB_TYPE_NULL);
    }
}
