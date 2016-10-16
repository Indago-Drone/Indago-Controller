package com.lewei.lib63;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import com.lewei.multiple.utils.Services;
import com.lewei.multiple.utils.Services.LocalBinder;

public class Device63 {
    private static final String TAG = "device63";
    private static int mSDCardState;
    private Thread connThread;
    private SDCardInfo info;
    private boolean isDevConnected;
    private boolean isNeedRemoteRecord;
    private boolean isNeedSDCardFormat;
    private boolean isRemoteRecording;
    private boolean isThreadStop;
    private ConnectState mConnectState;
    private final ServiceConnection mServiceConnection;
    private Services mServices;
    private int sleepInterval;

    /* renamed from: com.lewei.lib63.Device63.1 */
    class C00451 implements ServiceConnection {
        C00451() {
        }

        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.i(Device63.TAG, "sevice initialized connected");
            Device63.this.mServices = ((LocalBinder) service).getService();
            Device63.this.mServices.init();
        }

        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(Device63.TAG, "sevice initialized disconnected");
            Device63.this.mServices.exit();
            Device63.this.mServices = null;
        }
    }

    /* renamed from: com.lewei.lib63.Device63.2 */
    class C00462 implements Runnable {
        C00462() {
        }

        public void run() {
            while (!Device63.this.isThreadStop) {
                try {
                    if (LeweiLib63.LW63GetLogined()) {
                        if (!(Device63.this.isDevConnected || Device63.this.mConnectState == null)) {
                            Device63.this.mConnectState.onDeviceConnect();
                            Device63.this.isDevConnected = true;
                        }
                        if (Device63.this.isNeedSDCardFormat) {
                            Device63.this.formatRemoteSDCard(Device63.this.info);
                        } else {
                            Device63.this.checkSDCardInfo(Device63.this.info);
                        }
                        Device63.this.checkRemoteRecordState();
                        if (Device63.this.isNeedRemoteRecord) {
                            Device63.this.takeRemoteRecord();
                        }
                    } else if (!Device63.this.isDevConnected) {
                        LeweiLib63.LW63Login();
                    }
                    Device63.this.dealDevStatus(LeweiLib63.LW63GetDevStatus());
                    Thread.sleep((long) Device63.this.sleepInterval);
                } catch (InterruptedException e) {
                    Log.e(Device63.TAG, e.getMessage());
                    return;
                }
            }
            LeweiLib63.LW63Logout();
        }
    }

    static {
        mSDCardState = 0;
    }

    public Device63(Context context) {
        this.isThreadStop = false;
        this.isDevConnected = false;
        this.isRemoteRecording = false;
        this.isNeedRemoteRecord = false;
        this.sleepInterval = 500;
        this.isNeedSDCardFormat = false;
        this.info = new SDCardInfo();
        this.mServiceConnection = new C00451();
        setWifiEnable(context);
        context.bindService(new Intent(context, Services.class), this.mServiceConnection, 1);
    }

    private void setWifiEnable(Context context) {
        ((WifiManager) context.getSystemService("wifi")).setWifiEnabled(true);
    }

    public static int getSDCardState() {
        return mSDCardState;
    }

    public void startLoginThread() {
        if (this.connThread == null || !this.connThread.isAlive()) {
            this.connThread = new Thread(new C00462());
            this.connThread.start();
        }
    }

    public void setOnConnectState(ConnectState mConnectState) {
        this.isDevConnected = false;
        this.mConnectState = mConnectState;
    }

    public void stopLoginThread() {
        this.isThreadStop = true;
    }

    public void startFormatSDCard() {
        this.isNeedSDCardFormat = true;
    }

    private void dealDevStatus(int status) {
        switch (status) {
            case CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER /*2*/:
                Log.e(TAG, "device off line now");
                if (this.mConnectState != null) {
                    this.mConnectState.onDevOffLine();
                    this.isDevConnected = false;
                }
            default:
        }
    }

    private void formatRemoteSDCard(SDCardInfo info) {
        if (!LeweiLib63.LW63GetSDCardInfo(info)) {
            return;
        }
        if ((info.state & 4) > 0 && (info.state & 1) > 0 && (info.state & 2) > 0) {
            this.sleepInterval = 500;
            this.isNeedSDCardFormat = false;
        } else if ((info.state & 1) <= 0) {
            this.sleepInterval = 500;
            this.isNeedSDCardFormat = false;
            if (this.mConnectState != null) {
                this.mConnectState.onSDPushOut(true);
            }
        } else if ((info.state & 8) <= 0) {
            LeweiLib63.LW63StartSDCardFormat(0);
        } else {
            LeweiLib63.LW63GetSDCardFormatState(info);
            int progress = info.formatProgress;
            if (progress < 0) {
                progress = 100;
            }
            if (this.mConnectState != null) {
                this.mConnectState.onFormatState(progress);
            }
            if (progress >= 100) {
                this.isNeedSDCardFormat = false;
                this.sleepInterval = 500;
            }
        }
    }

    private void checkSDCardInfo(SDCardInfo sdInfo) {
        if (!LeweiLib63.LW63GetSDCardInfo(sdInfo)) {
            return;
        }
        if ((sdInfo.state & 4) > 0 && (sdInfo.state & 1) > 0 && (sdInfo.state & 2) > 0) {
            mSDCardState = 1;
            if (mSDCardState == 0 && this.mConnectState != null) {
                this.mConnectState.onSDPushOut(false);
            }
        } else if ((sdInfo.state & 1) <= 0) {
            mSDCardState = 0;
            if (mSDCardState == 1 && this.mConnectState != null) {
                this.mConnectState.onSDPushOut(true);
            }
        }
    }

    public void startRemoteRecord() {
        this.isNeedRemoteRecord = true;
    }

    private void takeRemoteRecord() {
        if (!this.isRemoteRecording) {
            formatRemoteSDCard(this.info);
            this.sleepInterval = 200;
            if (LeweiLib63.LW63TakeRemoteRecord()) {
                this.isRemoteRecording = true;
                if (this.mConnectState != null) {
                    this.mConnectState.onRecordStateChanged(true);
                }
            }
        } else if (LeweiLib63.LW63StopRemoteRecord()) {
            this.isRemoteRecording = false;
            if (this.mConnectState != null) {
                this.mConnectState.onRecordStateChanged(false);
            }
        }
        this.isNeedRemoteRecord = false;
    }

    private void checkRemoteRecordState() {
        if (LeweiLib63.LW63GetRemoteRecordState()) {
            if (!this.isRemoteRecording) {
                this.isRemoteRecording = true;
                if (this.mConnectState != null) {
                    this.mConnectState.onRecordStateChanged(true);
                }
            }
        } else if (this.isRemoteRecording) {
            this.isRemoteRecording = false;
            if (this.mConnectState != null) {
                this.mConnectState.onRecordStateChanged(false);
            }
        }
    }
}
