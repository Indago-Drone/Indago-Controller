package com.lewei.lib;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.lewei.multiple.utils.PathConfig;
import java.io.File;
import java.util.TimeZone;

public class LeweiLib {
    public static int HD_flag;
    public static int Hardware_flag;
    public static boolean isNeedTakePhoto;
    public static boolean isNeedTakeRecord;
    private Handler handler;
    private boolean isFisrtSendCMD;
    private boolean isStop;
    private OnTcpListener tcpListener;

    /* renamed from: com.lewei.lib.LeweiLib.1 */
    class C00421 implements Runnable {
        C00421() {
        }

        public void run() {
            while (!LeweiLib.this.isStop) {
                if (LeweiLib.this.isFisrtSendCMD) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    LeweiLib.this.isFisrtSendCMD = false;
                    int mTimeZoneOffset = TimeZone.getDefault().getOffset(System.currentTimeMillis());
                    Log.i("LeweiLib", "Now get the timezone is " + mTimeZoneOffset + "  " + TimeZone.getDefault().getRawOffset());
                    if (LeweiLib.LW93SendSetRemoteTime2(mTimeZoneOffset / 1000) <= 0) {
                        LeweiLib.this.handler.sendEmptyMessage(49);
                        System.out.println("1: " + LeweiLib.this);
                        System.out.println("2: " + LeweiLib.this.handler);
                        //System.out.println("3: " + LeweiLib.this.handler.sendEmptyMessage(12));
                    }
                    if (LeweiLib.LW93SendGetRecPlan() == 1) {
                        LeweiLib.this.handler.sendEmptyMessage(54);
                        Log.d("", "remote sdcard not recording.");
                    } else {
                        Log.d("", "remote sdcard not recording.");
                    }
                }
                if (LeweiLib.isNeedTakePhoto) {
                    LeweiLib.this.takeSDcardCapture();
                    LeweiLib.isNeedTakePhoto = false;
                }
                if (LeweiLib.isNeedTakeRecord) {
                    LeweiLib.this.takeSDcardRecord();
                    LeweiLib.isNeedTakeRecord = false;
                }
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    public static native void LW93ChangeRecordReplayAttr(String str, int i, int i2);

    public static native void LW93CloseUdpSocket();

    public static native int LW93DrawBitmapFrame(Bitmap bitmap);

    public static native int LW93GetCurrTimestamp();

    public static native int LW93InitUdpSocket();

    public static native byte[] LW93RecvUdpData();

    public static native String LW93SendCapturePhoto(String str);

    public static native int LW93SendChangeChannel(int i);

    public static native int LW93SendChangeRecPlan(int i);

    public static native int LW93SendChangeWifiName(String str);

    public static native int LW93SendChangeWifiPassword(String str);

    public static native int LW93SendDeleteFile(String str);

    public static native int LW93SendGetBaudrate();

    public static native int LW93SendGetCameraFlip();

    public static native RecList[] LW93SendGetRecList();

    public static native int LW93SendGetRecPlan();

    public static native int LW93SendGetRemoteTime();

    public static native int LW93SendRebootWifi();

    public static native int LW93SendResetWifi();

    public static native int LW93SendSdcardFormat();

    public static native int LW93SendSetBaudrate(int i);

    public static native int LW93SendSetCameraFlip(int i);

    @Deprecated
    public static native int LW93SendSetRemoteTime();

    public static native int LW93SendSetRemoteTime2(int i);

    public static native int LW93SendUdpData(byte[] bArr, int i);

    public static native String LW93StartDownloadFile(String str, String str2, int i);

    public static native int LW93StartLiveStream(int i, int i2);

    public static native int LW93StartLocalRecord(String str, int i);

    public static native int LW93StartRecordReplay(String str, int i, int i2, int i3);

    public static native void LW93StopDownloadFile();

    public static native void LW93StopLiveStream();

    public static native int LW93StopLocalRecord();

    public static native void LW93StopRecordReplay();

    public static native int getDownloadFileSize();

    public static native int getDownloadRecvSize();

    public static native int getFrameHeight();

    public static native int getFrameWidth();

    public static native H264Frame getH264Frame();

    public static native int getSdcardStatus();

    public native int LW93SendTcpData(byte[] bArr, int i);

    public native int LW93StartTcpThread();

    public native void LW93StopTcpThread();

    static {
        isNeedTakePhoto = false;
        isNeedTakeRecord = false;
        HD_flag = 0;
        Hardware_flag = 0;
        System.loadLibrary("lewei");
    }

    public LeweiLib(Handler handler) {
        this.isStop = false;
        this.isFisrtSendCMD = true;
        this.handler = handler;
    }

    public void setOnTcpListener(OnTcpListener tcpListener) {
        this.tcpListener = tcpListener;
    }

    public void startCMDThread() {
        new Thread(new C00421()).start();
    }

    public void stopThread() {
        this.isStop = true;
    }

    public void takeSDcardCapture() {
        String folder = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().toString())).append(PathConfig.PHOTOS_PATH).toString();
        File folderFile = new File(folder);
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }
        String file_name = LW93SendCapturePhoto(folder);
        Message msg = Message.obtain();
        msg.what = 56;
        msg.obj = file_name;
        this.handler.sendMessage(msg);
    }

    private void takeSDcardRecord() {
        int ret = LW93SendGetRecPlan();
        if (ret < 0) {
            this.handler.sendEmptyMessage(50);
        } else if (ret != 0) {
            ret = LW93SendChangeRecPlan(0);
            this.handler.sendEmptyMessage(55);
        } else if (LW93SendChangeRecPlan(1) <= 0) {
            this.handler.sendEmptyMessage(53);
        } else {
            this.handler.sendEmptyMessage(54);
        }
    }

    public void LW93TcpConnected() {
        this.tcpListener.TcpConnected();
    }

    public void LW93TcpDisconnected() {
        this.tcpListener.TcpDisconnected();
    }

    public void LW93TcpReceived(byte[] data, int size) {
        this.tcpListener.TcpReceive(data, size);
    }
}
