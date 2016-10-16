package com.lewei.lib;

import android.support.v4.media.TransportMediator;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import com.lewei.lib63.LeweiLib63;
import com.lewei.multiple.app.Applications;

import java.util.Random;

public class FlyCtrl {
    private static final String TAG = "FlyCtrl";
    public static int[] rudderdata;
    public static byte[] serialdata;
    public static int trim_left_landscape;
    public static int trim_right_landscape;
    public static int trim_right_portrait;
    private boolean isNeedSendData;
    private Thread sendThread63;
    private Thread sendThread93;

    /* renamed from: com.lewei.lib.FlyCtrl.1 */
    class C00401 extends Thread {
        C00401() {
        }

        public void run() {
            FlyCtrl.this.isNeedSendData = true;
            LeweiLib.LW93InitUdpSocket();
            while (FlyCtrl.this.isNeedSendData) {
                if (!Applications.isAllCtrlHide) {
                    FlyCtrl.this.updateSendData();
                    LeweiLib.LW93SendUdpData(FlyCtrl.serialdata, FlyCtrl.serialdata.length);
                    //System.out.println("DATA SENT!!!!!!!!!");
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            LeweiLib.LW93CloseUdpSocket();
        }
    }

    /* renamed from: com.lewei.lib.FlyCtrl.2 */
    class C00412 implements Runnable {
        C00412() {
        }

        public void run() {
            try {
                Log.e(FlyCtrl.TAG, "start send serial data");
                FlyCtrl.this.isNeedSendData = true;
                Thread.sleep(100);
                while (!FlyCtrl.this.isNeedSendData && LeweiLib63.LW63GetClientSize() != 1) {
                    Thread.sleep(2000);
                }
                while (FlyCtrl.this.isNeedSendData) {
                    if (LeweiLib63.LW63GetLogined()) {
                        if (!LeweiLib63.LW63GetSerialState()) {
                            LeweiLib63.LW63StartSerial(19200);
                        }
                        if (!Applications.isAllCtrlHide) {
                            FlyCtrl.this.updateSendData();
                            LeweiLib63.LW63SendSerialData(FlyCtrl.serialdata, FlyCtrl.serialdata.length);
                            Log.e(FlyCtrl.TAG, "  Data:  " + FlyCtrl.byteToHex(FlyCtrl.serialdata) + "  state:" + LeweiLib63.LW63GetSerialState());
                        }
                        Thread.sleep(50);
                    } else if (LeweiLib63.LW63GetSerialState()) {
                        LeweiLib63.LW63StopSerial();
                    }
                }
                Thread.sleep(20);
                Log.e(FlyCtrl.TAG, "stop send serial data");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static {
        serialdata = new byte[8];
        rudderdata = new int[5];
        trim_left_landscape = 0;
        trim_right_landscape = 0;
        trim_right_portrait = 0;
    }

    public FlyCtrl() {
        this.isNeedSendData = true;
        serialdata[0] = (byte) -52;
        serialdata[1] = (byte) 0;
        serialdata[2] = (byte) 0;
        serialdata[3] = (byte) 0;
        serialdata[4] = (byte) 0;
        serialdata[5] = (byte) 0;
        serialdata[6] = checkSum(serialdata);
        serialdata[7] = (byte) 51;
        rudderdata[1] = TransportMediator.FLAG_KEY_MEDIA_NEXT;
        rudderdata[2] = TransportMediator.FLAG_KEY_MEDIA_NEXT;
        rudderdata[3] = 0;
        rudderdata[4] = TransportMediator.FLAG_KEY_MEDIA_NEXT;
        Log.i(TAG, "initialize the serial data.");
    }

    private byte checkSum(byte[] data) {
        return (byte) (((((data[1] ^ data[2]) ^ data[3]) ^ data[4]) ^ data[5]) & MotionEventCompat.ACTION_MASK);
    }

    public static String byteToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & MotionEventCompat.ACTION_MASK);
            if (hex.length() == 1) {
                hex = new StringBuilder(String.valueOf('0')).append(hex).toString();
            }
            sb.append(new StringBuilder(String.valueOf(hex)).append(" ").toString());
        }
        return sb.toString();
    }

    public void startSendDataThread93() {
        if (this.sendThread93 == null || !this.sendThread93.isAlive()) {
            this.sendThread93 = new C00401();
            this.sendThread93.start();
        }
    }

    public void startSendDataThread63() {
        if (this.sendThread63 == null || !this.sendThread63.isAlive()) {
            this.sendThread63 = new Thread(new C00412());
            this.sendThread63.start();
        }
    }

    private void updateSendData() {
        int rotate = rudderdata[4] + (trim_left_landscape * 2);
        if (rotate < 0) {
            rotate = 1;
        } else if (rotate >= MotionEventCompat.ACTION_MASK) {
            rotate = MotionEventCompat.ACTION_MASK;
        }
        int ail = rudderdata[1] + (trim_right_landscape * 1);
        int ele = rudderdata[2] + (trim_right_portrait * 1);
        if (ail < 0) {
            ail = 1;
        } else if (ail >= MotionEventCompat.ACTION_MASK) {
            ail = MotionEventCompat.ACTION_MASK;
        }
        if (ele < 0) {
            ele = 1;
        } else if (ele >= MotionEventCompat.ACTION_MASK) {
            ele = MotionEventCompat.ACTION_MASK;
        }
        serialdata[1] = (byte) ail;
        serialdata[2] = (byte) ele;
        serialdata[3] = (byte) rudderdata[3];
        serialdata[4] = (byte) rotate;
        serialdata[6] = checkSum(serialdata);
    }

    public void stopSendDataThread() {
        this.isNeedSendData = false;
    }
}
