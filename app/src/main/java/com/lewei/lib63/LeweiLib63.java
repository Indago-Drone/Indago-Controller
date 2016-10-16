package com.lewei.lib63;

import android.graphics.Bitmap;

public class LeweiLib63 {
    public static final int FHNPEN_ALM_IPConflict = 88;
    public static final int FHNPEN_ALM_MDAlarm = 84;
    public static final int FHNPEN_ALM_SDError = 80;
    public static final int FHNPEN_ALM_SDFull = 81;
    public static final int FHNPEN_ALM_VideoLost = 86;
    public static final int FHNPEN_ALM_WritePicErr = 92;
    public static final int FHNPEN_ALM_WriteRecErr = 91;
    public static final int FHNPEN_OPT_PicFinish = 132;
    public static final int FHNPEN_OPT_RecFinish = 131;
    public static final int FHNPEN_OPT_RecStart = 130;
    public static final int FHNPEN_SYS_BatteryV = 15;
    public static final int FHNPEN_SYS_FlipMirror = 17;
    public static final int FHNPEN_SYS_IRLight = 16;
    public static final int FHNPEN_SYS_Kick = 1;
    public static final int FHNPEN_SYS_OffLine = 2;
    public static final int FHNPEN_SYS_ReConnect = 13;
    public static final int FHNPEN_SYS_Reboot = 5;
    public static final int FHNPEN_SYS_Reset = 6;
    public static final int FHNPEN_SYS_ShotBtnPush = 18;
    public static final int FHNPEN_SYS_ShutDown = 4;
    public static final int FHNPEN_SYS_Upgrade = 7;

    public static native int LW63DrawBitmapFrame(Bitmap bitmap);

    public static native int LW63GetClientSize();

    public static native int LW63GetDevStatus();

    public static native boolean LW63GetLogined();

    public static native long LW63GetNowPts();

    public static native boolean LW63GetRemoteRecordState();

    public static native boolean LW63GetReplayState();

    public static native boolean LW63GetSDCardFormatState(SDCardInfo sDCardInfo);

    public static native boolean LW63GetSDCardInfo(SDCardInfo sDCardInfo);

    public static native boolean LW63GetSerialState();

    public static native boolean LW63GetWiFiConfig(WiFiConfig wiFiConfig);

    public static native boolean LW63Login();

    public static native void LW63Logout();

    public static native void LW63SearchRecClean();

    public static native boolean LW63SearchRecInit();

    public static native boolean LW63SearchRecords(RecordInfo recordInfo);

    public static native int LW63SendSerialData(byte[] bArr, int i);

    public static native void LW63SetMirrorCamera();

    public static native boolean LW63SetWiFiConfig(WiFiConfig wiFiConfig);

    public static native int LW63StartPreview(int i, long j, long j2);

    public static native boolean LW63StartSDCardFormat(int i);

    public static native boolean LW63StartSerial(long j);

    public static native void LW63StopLocalRecord();

    public static native void LW63StopPreview();

    public static native boolean LW63StopRemoteRecord();

    public static native boolean LW63StopSDCardFormat();

    public static native void LW63StopSerial();

    public static native void LW63TakeLocalRecord(String str);

    public static native boolean LW63TakePhoto(String str, boolean z);

    public static native boolean LW63TakeRemoteRecord();

    static {
        System.loadLibrary("FHDEV_Discover");
        System.loadLibrary("FHDEV_Net");
        System.loadLibrary("main");
    }
}
