package com.lewei.multiple.main;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.media.TransportMediator;
import android.support.v4.widget.CursorAdapter;
import android.text.Layout;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.lewei.lib.FlyCtrl;
import com.lewei.lib.LeweiLib;
import com.lewei.lib.Stream93;
import com.lewei.lib63.ConnectState;
import com.lewei.lib63.Device63;
import com.lewei.lib63.LeweiLib63;
import com.lewei.lib63.Stream63;
import com.lewei.multiple.app.Applications;
import com.lewei.multiple.app.HandlerParams;
import com.lewei.multiple.app.Media;
import com.lewei.multiple.app.Rudder;
import com.lewei.multiple.app.Rudder.OnRudderListener;
import com.lewei.multiple.app.Sensors;
import com.lewei.multiple.app.Sensors.OnSensorValue;
import com.lewei.multiple.fydrone.R;
import com.lewei.multiple.fydrone.C0052R;
import com.lewei.multiple.fydrone.R;
import com.lewei.multiple.utils.ParamsConfig;
import com.lewei.multiple.utils.PathConfig;
import com.lewei.multiple.view.MySurfaceView;

//import org.bytedeco.javacpp.Loader;
//import org.bytedeco.javacpp.opencv_objdetect;
//import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

//import controller.indago.com.indago.R;
import org.opencv.objdetect.CascadeClassifier;

import org.opencv.core.*;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class LW93MainActivity extends Activity implements OnRudderListener, OnSensorValue {
    static {
        if (!OpenCVLoader.initDebug()) {
            System.out.println("OPENCV ERROR!!!!@@@");
        }
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(TransportMediator.FLAG_KEY_MEDIA_NEXT);
        //System.out.println("!!!!!!!! " + R.layout.activity_main);
        setContentView(R.layout.activity_main);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        this.screenWidth = dm.widthPixels;
        this.screenHeight = dm.heightPixels;
        initWidget();
        initTrimBar();
        initParamsSetting();
        initLib93();
        initLib63();
        this.media = new Media(this);
        this.showTimer = new Timer();

        new Thread() {
            public void run() {
                CascadeClassifier cascadeClassifier = null;
                try {
                    // Copy the resource into a temp file so OpenCV can load it
                    InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                    File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                    File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                    FileOutputStream os = new FileOutputStream(mCascadeFile);


                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    is.close();
                    os.close();


                    // Load the cascade classifier
                    cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                } catch (Exception e) {
                    Log.e("OpenCVActivity", "Error loading cascade", e);
                }
                Mat m = new Mat();
                Mat grayscaleImage;
                MatOfRect faces = new MatOfRect();
                while(true) {
                    if(Stream63.img != null) {
                        grayscaleImage = new Mat(Stream63.img.getHeight(), Stream63.img.getWidth(), CvType.CV_8UC4);
                        Utils.bitmapToMat(Stream63.img, m);
                        Imgproc.cvtColor(m, grayscaleImage, Imgproc.COLOR_RGBA2RGB);
                        if(cascadeClassifier != null)
                        cascadeClassifier.detectMultiScale(grayscaleImage, faces, 1.1, 2, 2,
                                new Size(0, 0), new Size());
                        System.out.println("numFaces " + faces.toArray().length);

                    }
                    try {
                        Thread.sleep(20);
                    } catch(InterruptedException e) {

                    }
                    //break;
                }
            }
        }.start();
        //System.out.println("InitLW");

    }
    public void OnLeftRudder(int x, int y) {
        if (Applications.isRightHandMode) {
            System.out.println("efjksdxvnk.sdjfbvk.sdjbfkjsdnfkvj");
            FlyCtrl.rudderdata[1] = x;
            FlyCtrl.rudderdata[2] = y;
            if (this.isFlipCtrl && (Math.abs(x - 128) > 64 || Math.abs(y - 128) > 64)) {
                this.isFlipOver = true;
                this.isFlipCtrl = false;
            }
            if (this.isFlipOver && Math.abs(x - 128) <= 3 && Math.abs(y - 128) <= 3) {
                this.iv_Main_Flip.setImageResource(R.drawable.main_top_flip);
                byte[] bArr = FlyCtrl.serialdata;
                bArr[5] = (byte) (bArr[5] & -9);
                this.rudder.setSpeedLevel(Applications.speed_level);
                return;
            }
            return;
        }
        FlyCtrl.rudderdata[3] = y;
        FlyCtrl.rudderdata[4] = x;
        //System.out.println("Throttle X: " + x);
        System.out.println("Throttle Y: " + y);
    }
    public void OnRightRudder(int x, int y, boolean isHighSpeed) {
        if (Applications.isRightHandMode) {
            FlyCtrl.rudderdata[4] = x;
            FlyCtrl.rudderdata[3] = y;
            return;
        }
        FlyCtrl.rudderdata[1] = x;
        FlyCtrl.rudderdata[2] = y;
        //System.out.println("Right X: " + x);
        //System.out.println("Right Y: " + y);
        if (this.isFlipCtrl && (Math.abs(x - 128) > 64 || Math.abs(y - 128) > 64)) {
            this.isFlipOver = true;
            this.isFlipCtrl = false;
        }
        if (this.isFlipOver && Math.abs(x - 128) <= 3 && Math.abs(y - 128) <= 3) {
            this.iv_Main_Flip.setImageResource(R.drawable.main_top_flip);
            byte[] bArr = FlyCtrl.serialdata;
            bArr[5] = (byte) (bArr[5] & -9);
            this.rudder.setSpeedLevel(Applications.speed_level);
        }
    }
    public void OnRightRudderUp() {
    }
    private void initLib93() {
        this.mLeweiLib = new LeweiLib(this.handler);
        this.mLeweiLib.startCMDThread();
        this.mFlyCtrl = new FlyCtrl();
        this.mFlyCtrl.startSendDataThread93();
        this.mSurfaceView = (MySurfaceView) findViewById(R.id.mSurfaceview);
        this.mStream93 = new Stream93(getApplicationContext(), this.handler, this.mSurfaceView);
        this.mStream93.startStream93();
    }
    public void setValue(int x, int y) {
        //System.out.println("12#$");
        int ail = 0;
        int ele = 0;
        switch (Applications.speed_level) {
            case CursorAdapter.FLAG_AUTO_REQUERY://*1:
                ail = (x * 40) / 60;
                ele = (y * 40) / 60;
                break;
            case CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER://*2:
                ail = (x * 60) / 60;
                ele = (y * 60) / 60;
                break;
            case 3://* FragmentManagerImpl.ANIM_STYLE_CLOSE_ENTER 3:
                ail = (x * TransportMediator.KEYCODE_MEDIA_PAUSE) / 60;
                ele = (y * TransportMediator.KEYCODE_MEDIA_PAUSE) / 60;
                break;
        }
        ail += TransportMediator.FLAG_KEY_MEDIA_NEXT;
        ele = 128 - ele;
        if (this.isFlipCtrl && (Math.abs(ail - 128) >= 64 || Math.abs(ele - 128) >= 64)) {
            this.isFlipOver = true;
            this.isFlipCtrl = false;
        }
        if (this.isFlipOver && Math.abs(ail - 128) <= 0 && Math.abs(ele - 128) <= 0) {
            this.iv_Main_Flip.setImageResource(R.drawable.main_top_flip);
            byte[] bArr = FlyCtrl.serialdata;
            bArr[5] = (byte) (bArr[5] & -9);
            this.rudder.setSpeedLevel(Applications.speed_level);
        }
        FlyCtrl.rudderdata[1] = ail;
        FlyCtrl.rudderdata[2] = ele;
        if (Applications.isRightHandMode) {
            this.rudder.setLeftPointPosition(Rudder.p_left_default.x + ((((x * 125) * this.screenWidth) / 960) / 60), Rudder.p_left_default.y + ((((y * 125) * this.screenWidth) / 960) / 60));
            return;
        }
    }
    public LW93MainActivity() {
        this.screenOrientation = 0;
        this.isHandRight = false;
        this.isTrimHide = true;
        this.isFlipCtrl = false;
        this.isFlipOver = false;
        this.rudder = null;
        //this.mRunnableHideStatus = new C00691();
        //this.listener = new C00702();
        this.handler = new Handler(new C00713());
    }

    private static final String TAG = "LW93Main";
    public Handler handler;
    private boolean isFlipCtrl;
    private boolean isFlipOver;
    private boolean isHandRight;
    private boolean isTrimHide;
    private ImageView iv_Left_Rudder;
    private ImageView iv_Main_Back;
    private ImageView iv_Main_Background;
    private ImageView iv_Main_ChangeHand;
    private ImageView iv_Main_Flip;
    private ImageView iv_Main_Gravity;
    private ImageView iv_Main_Left_Landscape_Add;
    private ImageView iv_Main_Left_Landscape_Sub;
    private ImageView iv_Main_LimitedHigh;
    private ImageView iv_Main_Local_Record;
    private ImageView iv_Main_PlayBack;
    private ImageView iv_Main_Reverse;
    private ImageView iv_Main_Right_Landscape_Add;
    private ImageView iv_Main_Right_Landscape_Sub;
    private ImageView iv_Main_Right_Portrait_Add;
    private ImageView iv_Main_Right_Portrait_Sub;
    private ImageView iv_Main_Sdcard_Capture;
    private ImageView iv_Main_Sdcard_Record;
    private ImageView iv_Main_Setup;
    private ImageView iv_Main_Speed_Level;
    private ImageView iv_Main_Visibility;
    private ImageView iv_Right_Rudder;
    private LinearLayout layout_Left_Landscape_Trim;
    private RelativeLayout layout_Main_All_Ctrl;
    private FrameLayout layout_Progressbar;
    private LinearLayout layout_Right_Landscape_Trim;
    private LinearLayout layout_Right_Portrait_Trim;
    private OnClickListener listener;
    private FlyCtrl mFlyCtrl;
    private LeweiLib mLeweiLib;
    private Runnable mRunnableHideStatus;
    private Stream63 mStream63;
    private Stream93 mStream93;
    private MySurfaceView mSurfaceView;
    private Media media;
    private int recTime;
    private Rudder rudder;
    private int screenHeight;
    private int screenOrientation;
    private int screenWidth;
    private int second;
    private Sensors sensors;
    private Timer showTimer;
    private ShowTimerTask showTimerTask; //findme cant find declartion!!!!!!
    private TextView tv_Main_Device_Status;
    private TextView tv_Main_Left_Landscape;
    private TextView tv_Main_Record_Time;
    private TextView tv_Main_Right_Landscape;
    private TextView tv_Main_Right_Portrait;


    //enamed from: com.lewei.multiple.main.LW93MainActivity.1
    class C00691 implements Runnable {
        C00691() {
        }

        public void run() {
            LW93MainActivity.this.tv_Main_Device_Status.setVisibility(View.GONE);
        }
    }

    //renamed from: com.lewei.multiple.main.LW93MainActivity.2
    class C00702 implements OnClickListener {
        C00702() {
        }

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_lw93main_center_flip:
                    byte[] bArr = FlyCtrl.serialdata;
                    bArr[5] = (byte) (bArr[5] | 8);
                    LW93MainActivity.this.iv_Main_Flip.setImageResource(R.drawable.main_top_flip_h);
                    LW93MainActivity.this.isFlipCtrl = true;
                    LW93MainActivity.this.isFlipOver = false;
                    LW93MainActivity.this.rudder.setSpeedLevel(3);
                case R.id.iv_lw93main_left_landscape_sub:
                    if (FlyCtrl.trim_left_landscape > -31) {
                        FlyCtrl.trim_left_landscape--;
                        LW93MainActivity.this.tv_Main_Left_Landscape.setText(Integer.toString(FlyCtrl.trim_left_landscape));
                        ParamsConfig.writeTrimOffset(LW93MainActivity.this.getApplicationContext(), 1, FlyCtrl.trim_left_landscape);
                        if (FlyCtrl.trim_left_landscape == 0) {
                            LW93MainActivity.this.media.playBtnMiddle();
                        } else {
                            LW93MainActivity.this.media.playBtnTurn();
                        }
                    }
                case R.id.iv_lw93main_left_landscape_add:
                    if (FlyCtrl.trim_left_landscape < 31) {
                        FlyCtrl.trim_left_landscape++;
                        LW93MainActivity.this.tv_Main_Left_Landscape.setText(Integer.toString(FlyCtrl.trim_left_landscape));
                        ParamsConfig.writeTrimOffset(LW93MainActivity.this.getApplicationContext(), 1, FlyCtrl.trim_left_landscape);
                        if (FlyCtrl.trim_left_landscape == 0) {
                            LW93MainActivity.this.media.playBtnMiddle();
                        } else {
                            LW93MainActivity.this.media.playBtnTurn();
                        }
                    }
                case R.id.iv_lw93main_right_landscape_sub:
                    if (FlyCtrl.trim_right_landscape > -31) {
                        FlyCtrl.trim_right_landscape--;
                        LW93MainActivity.this.tv_Main_Right_Landscape.setText(Integer.toString(FlyCtrl.trim_right_landscape));
                        ParamsConfig.writeTrimOffset(LW93MainActivity.this.getApplicationContext(), 2, FlyCtrl.trim_right_landscape);
                        if (FlyCtrl.trim_right_landscape == 0) {
                            LW93MainActivity.this.media.playBtnMiddle();
                        } else {
                            LW93MainActivity.this.media.playBtnTurn();
                        }
                    }
                case R.id.iv_lw93main_right_landscape_add:
                    if (FlyCtrl.trim_right_landscape < 31) {
                        FlyCtrl.trim_right_landscape++;
                        LW93MainActivity.this.tv_Main_Right_Landscape.setText(Integer.toString(FlyCtrl.trim_right_landscape));
                        ParamsConfig.writeTrimOffset(LW93MainActivity.this.getApplicationContext(), 2, FlyCtrl.trim_right_landscape);
                        if (FlyCtrl.trim_right_landscape == 0) {
                            LW93MainActivity.this.media.playBtnMiddle();
                        } else {
                            LW93MainActivity.this.media.playBtnTurn();
                        }
                    }
                case R.id.iv_lw93main_right_portrait_add:
                    if (FlyCtrl.trim_right_portrait < 24) {
                        FlyCtrl.trim_right_portrait++;
                        LW93MainActivity.this.tv_Main_Right_Portrait.setText(Integer.toString(FlyCtrl.trim_right_portrait));
                        ParamsConfig.writeTrimOffset(LW93MainActivity.this.getApplicationContext(), 3, FlyCtrl.trim_right_portrait);
                        if (FlyCtrl.trim_right_portrait == 0) {
                            LW93MainActivity.this.media.playBtnMiddle();
                        } else {
                            LW93MainActivity.this.media.playBtnTurn();
                        }
                    }
                case R.id.iv_lw93main_right_portrait_sub:
                    if (FlyCtrl.trim_right_portrait > -31) {
                        FlyCtrl.trim_right_portrait--;
                        LW93MainActivity.this.tv_Main_Right_Portrait.setText(Integer.toString(FlyCtrl.trim_right_portrait));
                        ParamsConfig.writeTrimOffset(LW93MainActivity.this.getApplicationContext(), 3, FlyCtrl.trim_right_portrait);
                        if (FlyCtrl.trim_right_portrait == 0) {
                            LW93MainActivity.this.media.playBtnMiddle();
                        } else {
                            LW93MainActivity.this.media.playBtnTurn();
                        }
                    }
                case R.id.iv_lw93main_top_back:
                    LW93MainActivity.this.startIntent(LW93MainActivity.this, HomeActivity.class);
                case R.id.iv_lw93main_top_changehand:
                    LW93MainActivity.this.onRightHandMode();
                case R.id.iv_lw93main_top_setup:
                    if (!Applications.isAllCtrlHide) {
                        if (LW93MainActivity.this.isTrimHide) {
                            LW93MainActivity.this.isTrimHide = false;
                            LW93MainActivity.this.layout_Right_Portrait_Trim.setVisibility(View.VISIBLE);
                            LW93MainActivity.this.layout_Left_Landscape_Trim.setVisibility(View.VISIBLE);
                            LW93MainActivity.this.layout_Right_Landscape_Trim.setVisibility(View.VISIBLE);
                            return;
                        }
                        LW93MainActivity.this.isTrimHide = true;
                        LW93MainActivity.this.layout_Right_Portrait_Trim.setVisibility(View.GONE);
                        LW93MainActivity.this.layout_Left_Landscape_Trim.setVisibility(View.GONE);
                        LW93MainActivity.this.layout_Right_Landscape_Trim.setVisibility(View.GONE);
                    }
                case R.id.iv_lw93main_top_speed_level:
                    LW93MainActivity.this.onSpeedButtonClick();
                case R.id.iv_lw93main_top_visibility:
                    LW93MainActivity.this.onVisibilityButtonClick();
                case R.id.iv_lw93main_top_gravity:
                    LW93MainActivity.this.onGravityButtonClick();
                case R.id.iv_lw93main_top_sdcard_capture:
                    LW93MainActivity.this.media.playShutter();
                    if ((Applications.mRunLeweiLibType & 4) > 0) {
                        LeweiLib.isNeedTakePhoto = true;
                    } else if ((Applications.mRunLeweiLibType & 1) > 0) {
                        String path = PathConfig.getPhotoPath();
                        if (LeweiLib63.LW63TakePhoto(path, true)) {
                            LW93MainActivity.this.updatePhotoToAlbum(path);
                        }
                    }
                case R.id.iv_lw93main_top_local_record:
                    if ((Applications.mRunLeweiLibType & 4) > 0) {
                        if (LeweiLib.getSdcardStatus() > 0) {
                            LeweiLib.isNeedTakeRecord = true;
                        } else {
                            LW93MainActivity.this.mStream93.takeRecord();
                        }
                    } else if ((Applications.mRunLeweiLibType & 1) > 0 && LeweiLib63.LW63GetLogined()) {
                        if (Device63.getSDCardState() > 0) {
                            Applications.mDevice63.startRemoteRecord();
                        } else {
                            LW93MainActivity.this.mStream63.takeRecord();
                        }
                    }
                case R.id.iv_lw93main_top_sdcard_record:
                    if ((Applications.mRunLeweiLibType & 4) > 0) {
                        LeweiLib.isNeedTakeRecord = true;
                    } else if ((Applications.mRunLeweiLibType & 1) > 0) {
                        Applications.mDevice63.startRemoteRecord();
                    }
                case R.id.iv_lw93main_top_playback:
                    LW93MainActivity.this.startIntent(LW93MainActivity.this, SelectActivity.class);
                case R.id.iv_lw93main_top_limitedhigh:
                    LW93MainActivity.this.onHighLimitButtonClick();
                case R.id.iv_lw93main_top_reverse:
                    if ((Applications.mRunLeweiLibType & 4) > 0) {
                        int flip = LeweiLib.LW93SendGetCameraFlip();
                        if (flip == 0) {
                            LeweiLib.LW93SendSetCameraFlip(3);
                        } else if (flip == 3) {
                            LeweiLib.LW93SendSetCameraFlip(0);
                        }
                    } else if ((Applications.mRunLeweiLibType & 1) > 0) {
                        LeweiLib63.LW63SetMirrorCamera();
                    }
                default:
            }
        }
    }

    // renamed from: com.lewei.multiple.main.LW93MainActivity.3
    class C00713 implements Callback {
        C00713() {
        }

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case DialogFragment.STYLE_NORMAL: //0:
                    LW93MainActivity.this.showProgressbar(true);
                    break;
                case CursorAdapter.FLAG_AUTO_REQUERY ://1:
                    LW93MainActivity.this.showProgressbar(false);
                    LW93MainActivity.this.iv_Main_Background.setVisibility(View.GONE);
                    LW93MainActivity.this.mStream63.stopStream63();
                    Applications.mDevice63.stopLoginThread();
                    Applications.mRunLeweiLibType = 0;
                    Applications.mRunLeweiLibType |= 4;
                    break;
                case CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER ://*2:
                    LW93MainActivity.this.showProgressbar(false);
                    LW93MainActivity.this.iv_Main_Background.setVisibility(View.GONE);
                    LW93MainActivity.this.mStream93.stopStream93();
                    LW93MainActivity.this.mLeweiLib.stopThread();
                    Applications.mRunLeweiLibType = 0;
                    Applications.mRunLeweiLibType |= 1;
                    break;
                case 3 ://*3 FragmentManagerImpl.ANIM_STYLE_CLOSE_ENTER:
                    LW93MainActivity.this.setTime();
                    break;
                case HandlerParams.RECORD_START_OK ://*17:
                    LW93MainActivity.this.initTime();
                    LW93MainActivity.this.iv_Main_Local_Record.setImageResource(R.drawable.main_top_video_h);
                    LW93MainActivity.this.tv_Main_Record_Time.setVisibility(View.VISIBLE);
                    LW93MainActivity.this.startShowTimer();
                    break;
                case HandlerParams.RECORD_STOP ://*19:
                    LW93MainActivity.this.iv_Main_Local_Record.setImageResource(R.drawable.main_top_video);
                    LW93MainActivity.this.tv_Main_Record_Time.setVisibility(View.INVISIBLE);
                    LW93MainActivity.this.clearTime();
                    break;
                case HandlerParams.SET_RECPLAN_FAIL ://*53:
                    Toast.makeText(LW93MainActivity.this, LW93MainActivity.this.getString(R.string.str_lw93main_start_record_fail), Toast.LENGTH_SHORT).show();
                    LW93MainActivity.this.iv_Main_Local_Record.setImageResource(R.drawable.main_top_video);
                    break;
                case HandlerParams.SET_RECPLAN_START ://*54:
                    if (LeweiLib.getSdcardStatus() <= 0) {
                        LW93MainActivity.this.iv_Main_Local_Record.setImageResource(R.drawable.main_top_video);
                        Toast.makeText(LW93MainActivity.this, LW93MainActivity.this.getString(R.string.str_lw93main_record_not_findcard), Toast.LENGTH_SHORT).show();
                        break;
                    }
                    break;
                case HandlerParams.SET_RECPLAN_STOP ://*55:
                    Toast.makeText(LW93MainActivity.this, LW93MainActivity.this.getString(R.string.str_lw93main_cancel_recordplan), Toast.LENGTH_SHORT).show();
                    LW93MainActivity.this.iv_Main_Local_Record.setImageResource(R.drawable.main_top_video);
                    break;
                case HandlerParams.SEND_CAPTURE_PHOTO ://*56:
                    if (msg.obj != null) {
                        String file_name = msg.obj.toString();
                        Toast.makeText(LW93MainActivity.this, new StringBuilder(String.valueOf(LW93MainActivity.this.getString(R.string.str_lw93main_capture_success))).append(file_name).toString(), Toast.LENGTH_SHORT).show();
                        LW93MainActivity.this.updatePhotoToAlbum(file_name);
                        break;
                    }
                    Toast.makeText(LW93MainActivity.this, LW93MainActivity.this.getString(R.string.str_lw93main_capture_fail), Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    }

    private class ShowTimerTask extends TimerTask {
        private ShowTimerTask() {
        }

        public void run() {
            LW93MainActivity.this.handler.sendEmptyMessage(3);
        }
    }

    //renamed from: com.lewei.multiple.main.LW93MainActivity.4
    class C01344 implements ConnectState {

        //renamed from: com.lewei.multiple.main.LW93MainActivity.4.1
        class C00721 implements Runnable {
            private final boolean val$push; // synthetic

            C00721(boolean z) {
                this.val$push = z;
            }

            public void run() {
                if (this.val$push) {
                    LW93MainActivity.this.showDeviceStatus(LW93MainActivity.this.getString(R.string.str_sdcard_push_out));
                } else {
                    LW93MainActivity.this.showDeviceStatus(LW93MainActivity.this.getString(R.string.str_sdcard_push_in));
                }
            }
        }

        // renamed from: com.lewei.multiple.main.LW93MainActivity.4.2
        class C00732 implements Runnable {
            private final boolean val$recording; // synthetic

            C00732(boolean z) {
                this.val$recording = z;
            }

            public void run() {
                if (this.val$recording) {
                    LW93MainActivity.this.iv_Main_Local_Record.setImageResource(R.drawable.main_top_video_h);
                } else {
                    LW93MainActivity.this.iv_Main_Local_Record.setImageResource(R.drawable.main_top_video);
                }
            }
        }

        //renamed from: com.lewei.multiple.main.LW93MainActivity.4.3
        class C00743 implements Runnable {
            private final  int val$progress; // synthetic

            C00743(int i) {
                this.val$progress = i;
            }

            public void run() {
                LW93MainActivity.this.showDeviceStatus(new StringBuilder(String.valueOf(LW93MainActivity.this.getString(R.string.str_sdcard_formating_progress))).append(this.val$progress).append("%").toString());
            }
        }

        C01344() {
        }

        public void onSDPushOut(boolean push) {
            LW93MainActivity.this.handler.post(new C00721(push));
        }

        public void onRecordStateChanged(boolean recording) {
            LW93MainActivity.this.handler.post(new C00732(recording));
        }

        public void onFormatState(int progress) {
            LW93MainActivity.this.handler.post(new C00743(progress));
        }

        public void onDeviceConnect() {
            LW93MainActivity.this.mFlyCtrl.startSendDataThread63();
            LW93MainActivity.this.mStream63.startStream63();
            //Object a = null;
            //System.out.println(a.toString());
            System.out.println("connection");
        }

        public void onDevOffLine() {
            LW93MainActivity.this.mFlyCtrl.stopSendDataThread();
            LW93MainActivity.this.mStream63.stopStream63();
            LeweiLib63.LW63Logout();
        }
    }


    private void initWidget() {
        this.layout_Progressbar = (FrameLayout) findViewById(R.id.layout_progressbar);
        this.layout_Main_All_Ctrl = (RelativeLayout) findViewById(R.id.layout_lw93main_all_ctrl);
        this.layout_Right_Portrait_Trim = (LinearLayout) findViewById(R.id.layout_lw93main_right_portrait_trim);
        this.layout_Left_Landscape_Trim = (LinearLayout) findViewById(R.id.layout_lw93main_left_landscape_trim);
        this.layout_Right_Landscape_Trim = (LinearLayout) findViewById(R.id.layout_lw93main_right_landscape_trim);
        this.rudder = (Rudder) findViewById(R.id.rudder_lw93main);
        this.rudder.setOnRudderListener(this);
        this.sensors = new Sensors(this);
        this.sensors.setOnSensorValue(this);
        this.iv_Main_Background = (ImageView) findViewById(R.id.iv_lw93main_background);
        this.iv_Main_Back = (ImageView) findViewById(R.id.iv_lw93main_top_back);
        this.iv_Main_ChangeHand = (ImageView) findViewById(R.id.iv_lw93main_top_changehand);
        this.iv_Main_Setup = (ImageView) findViewById(R.id.iv_lw93main_top_setup);
        this.iv_Main_Sdcard_Capture = (ImageView) findViewById(R.id.iv_lw93main_top_sdcard_capture);
        this.iv_Main_Local_Record = (ImageView) findViewById(R.id.iv_lw93main_top_local_record);
        this.iv_Main_Sdcard_Record = (ImageView) findViewById(R.id.iv_lw93main_top_sdcard_record);
        this.iv_Main_PlayBack = (ImageView) findViewById(R.id.iv_lw93main_top_playback);
        this.iv_Main_Gravity = (ImageView) findViewById(R.id.iv_lw93main_top_gravity);
        this.iv_Main_LimitedHigh = (ImageView) findViewById(R.id.iv_lw93main_top_limitedhigh);
        this.iv_Main_Visibility = (ImageView) findViewById(R.id.iv_lw93main_top_visibility);
        this.iv_Main_Speed_Level = (ImageView) findViewById(R.id.iv_lw93main_top_speed_level);
        this.iv_Main_Reverse = (ImageView) findViewById(R.id.iv_lw93main_top_reverse);
        this.iv_Main_Flip = (ImageView) findViewById(R.id.iv_lw93main_center_flip);
        this.iv_Main_Back.setOnClickListener(this.listener);
        this.iv_Main_ChangeHand.setOnClickListener(this.listener);
        this.iv_Main_Setup.setOnClickListener(this.listener);
        this.iv_Main_Sdcard_Capture.setOnClickListener(this.listener);
        this.iv_Main_Local_Record.setOnClickListener(this.listener);
        this.iv_Main_Sdcard_Record.setOnClickListener(this.listener);
        this.iv_Main_PlayBack.setOnClickListener(this.listener);
        this.iv_Main_PlayBack.setOnClickListener(this.listener);
        this.iv_Main_Gravity.setOnClickListener(this.listener);
        this.iv_Main_LimitedHigh.setOnClickListener(this.listener);
        this.iv_Main_Visibility.setOnClickListener(this.listener);
        this.iv_Main_Speed_Level.setOnClickListener(this.listener);
        this.iv_Main_Reverse.setOnClickListener(this.listener);
        this.iv_Main_Flip.setOnClickListener(this.listener);
        this.tv_Main_Device_Status = (TextView) findViewById(R.id.tv_main_device_status);
        this.tv_Main_Record_Time = (TextView) findViewById(R.id.tv_lw93main_record_time);
        this.iv_Left_Rudder = (ImageView) findViewById(R.id.iv_lw93main_left_rudder);
        this.iv_Right_Rudder = (ImageView) findViewById(R.id.iv_lw93main_right_rudder);
        LayoutParams lp = new LayoutParams(-2, -2);
        lp.width = (this.screenWidth * 360) / 960;
        lp.height = (this.screenWidth * 360) / 960;
        lp.leftMargin = (this.screenWidth * 60) / 960;
        lp.topMargin = ((this.screenHeight - lp.height) + 50) / 2;
        this.iv_Left_Rudder.setLayoutParams(lp);
        lp = new LayoutParams(-2, -2);
        lp.width = (this.screenWidth * 360) / 960;
        lp.height = (this.screenWidth * 360) / 960;
        lp.leftMargin = (this.screenWidth * 540) / 960;
        lp.topMargin = ((this.screenHeight - lp.height) + 50) / 2;
        this.iv_Right_Rudder.setLayoutParams(lp);
        this.iv_Main_Left_Landscape_Sub = (ImageView) findViewById(R.id.iv_lw93main_left_landscape_sub);
        this.iv_Main_Left_Landscape_Add = (ImageView) findViewById(R.id.iv_lw93main_left_landscape_add);
        this.iv_Main_Right_Landscape_Sub = (ImageView) findViewById(R.id.iv_lw93main_right_landscape_sub);
        this.iv_Main_Right_Landscape_Add = (ImageView) findViewById(R.id.iv_lw93main_right_landscape_add);
        this.iv_Main_Right_Portrait_Sub = (ImageView) findViewById(R.id.iv_lw93main_right_portrait_sub);
        this.iv_Main_Right_Portrait_Add = (ImageView) findViewById(R.id.iv_lw93main_right_portrait_add);
        this.iv_Main_Left_Landscape_Sub.setOnClickListener(this.listener);
        this.iv_Main_Left_Landscape_Add.setOnClickListener(this.listener);
        this.iv_Main_Right_Landscape_Sub.setOnClickListener(this.listener);
        this.iv_Main_Right_Landscape_Add.setOnClickListener(this.listener);
        this.iv_Main_Right_Portrait_Sub.setOnClickListener(this.listener);
        this.iv_Main_Right_Portrait_Add.setOnClickListener(this.listener);
    }

    private void initTrimBar() {
        this.tv_Main_Left_Landscape = (TextView) findViewById(R.id.tv_lw93main_left_landscape);
        this.tv_Main_Right_Landscape = (TextView) findViewById(R.id.tv_lw93main_right_landscape);
        this.tv_Main_Right_Portrait = (TextView) findViewById(R.id.tv_lw93main_right_portrait);
        FlyCtrl.trim_left_landscape = ParamsConfig.readTrimOffset(getApplicationContext(), 1);
        FlyCtrl.trim_right_landscape = ParamsConfig.readTrimOffset(getApplicationContext(), 2);
        FlyCtrl.trim_right_portrait = ParamsConfig.readTrimOffset(getApplicationContext(), 3);
        this.tv_Main_Left_Landscape.setText(Integer.toString(FlyCtrl.trim_left_landscape));
        this.tv_Main_Right_Landscape.setText(Integer.toString(FlyCtrl.trim_right_landscape));
        this.tv_Main_Right_Portrait.setText(Integer.toString(FlyCtrl.trim_right_portrait));
    }

    private void initParamsSetting() {
        //this.iv_Left_Rudder.setImageResource(R.drawable.main_fly_rudder_power);
        //this.iv_Right_Rudder.setImageResource(R.drawable.main_fly_rudder_ranger);
        Applications.isRightHandMode = false;
        Applications.isAllCtrlHide = false;
        Applications.isLimitedHigh = false;
        Applications.isSensorOn = false;
        Applications.speed_level = 2;
        //this.rudder.setSpeedLevel(Applications.speed_level);
        if (Applications.isAllCtrlHide) {
            //this.layout_Main_All_Ctrl.setVisibility(View.INVISIBLE);
            //this.rudder.setVisibility(View.INVISIBLE);
            //this.iv_Main_Visibility.setImageResource(R.drawable.main_top_off);
            return;
        }
        //this.layout_Main_All_Ctrl.setVisibility(View.VISIBLE);
        //this.rudder.setVisibility(View.VISIBLE);
        //this.iv_Main_Visibility.setImageResource(R.drawable.main_top_on);
    }

    private void onRightHandMode() {
        LayoutParams layoutParams;
        if (this.isHandRight) {
            this.isHandRight = false;
            Applications.isRightHandMode = false;
            layoutParams = new LayoutParams(-2, -2);
            layoutParams = new LayoutParams(-2, -2);
            layoutParams.addRule(8, this.iv_Left_Rudder.getId());
            layoutParams.addRule(6, this.iv_Left_Rudder.getId());
            layoutParams.addRule(11, this.iv_Left_Rudder.getId());
            this.iv_Left_Rudder.setImageResource(R.drawable.main_fly_rudder_power);
            this.iv_Right_Rudder.setImageResource(R.drawable.main_fly_rudder_ranger);
            this.iv_Main_ChangeHand.setImageResource(R.drawable.main_top_changehand);
            Rudder.p_left.x = Rudder.p_left_default.x;
            Rudder.p_left.y = Rudder.p_right.y;
            Rudder.p_right.x = Rudder.p_right_default.x;
            Rudder.p_right.y = Rudder.p_right_default.y;
            this.layout_Right_Portrait_Trim.setLayoutParams(layoutParams);
            return;
        }
        this.isHandRight = true;
        Applications.isRightHandMode = true;
        layoutParams = new LayoutParams(-2, -2);
        layoutParams = new LayoutParams(-2, -2);
        layoutParams.addRule(8, this.iv_Left_Rudder.getId());
        layoutParams.addRule(6, this.iv_Left_Rudder.getId());
        layoutParams.addRule(9, this.iv_Left_Rudder.getId());
        this.iv_Left_Rudder.setImageResource(R.drawable.main_fly_rudder_ranger);
        this.iv_Right_Rudder.setImageResource(R.drawable.main_fly_rudder_power);
        this.iv_Main_ChangeHand.setImageResource(R.drawable.main_top_changehand_h);
        Rudder.p_right.x = Rudder.p_right_default.x;
        Rudder.p_right.y = Rudder.p_left.y;
        Rudder.p_left.x = Rudder.p_left_default.x;
        Rudder.p_left.y = Rudder.p_left_default.y;
        this.layout_Right_Portrait_Trim.setLayoutParams(layoutParams);
    }


    private void initLib63() {
        this.mStream63 = new Stream63(getApplicationContext(), this.handler, this.mSurfaceView);
        Applications.mDevice63.setOnConnectState(new C01344());
    }

    private void showDeviceStatus(String content) {
        if (this.handler != null) {
            this.handler.removeCallbacks(this.mRunnableHideStatus);
            this.tv_Main_Device_Status.setText(content);
            this.tv_Main_Device_Status.setVisibility(View.VISIBLE);
            this.handler.postDelayed(this.mRunnableHideStatus, 2000);
        }
    }

    private void showProgressbar(boolean state) {
        this.layout_Progressbar.setVisibility(state ? View.VISIBLE : View.GONE); //findme
    }

    private void onGravityButtonClick() {
        int i;
        if (Applications.isSensorOn) {
            Applications.isSensorOn = false;
            this.sensors.unregister();
            if (!Applications.isRightHandMode) {
                Rudder.p_right.x = Rudder.p_right_default.x;
                Rudder.p_right.y = Rudder.p_right_default.y;
            }
        } else {
            Applications.isSensorOn = true;
            this.sensors.register();
        }
        ImageView imageView = this.iv_Main_Gravity;
        if (Applications.isSensorOn) {
            i = R.drawable.main_top_gyroscope_h;
        } else {
            i = R.drawable.main_top_gyroscope;
        }
        imageView.setImageResource(i);
    }

    private void onHighLimitButtonClick() {
        int i;
        if (Applications.isLimitedHigh) {
            Applications.isLimitedHigh = false;
            if (Applications.isRightHandMode) {
                Rudder.p_right.x = Rudder.p_right_default.x;
                Rudder.p_right.y = Rudder.p_right_start_y;
                FlyCtrl.rudderdata[3] = 1;
            } else {
                Rudder.p_left.x = Rudder.p_left_default.x;
                Rudder.p_left.y = Rudder.p_left_start_y;
                FlyCtrl.rudderdata[3] = 1;
            }
        } else {
            Applications.isLimitedHigh = true;
            if (Applications.isRightHandMode) {
                Rudder.p_right.x = Rudder.p_right_default.x;
                Rudder.p_right.y = Rudder.p_right_default.y;
                FlyCtrl.rudderdata[3] = TransportMediator.FLAG_KEY_MEDIA_NEXT;
            } else {
                Rudder.p_left.x = Rudder.p_left_default.x;
                Rudder.p_left.y = Rudder.p_left_default.y;
                FlyCtrl.rudderdata[3] = TransportMediator.FLAG_KEY_MEDIA_NEXT;
            }
        }
        ImageView imageView = this.iv_Main_LimitedHigh;
        if (Applications.isLimitedHigh) {
            i = R.drawable.main_limited_high_pressed;
        } else {
            i = R.drawable.main_limited_high_normal;
        }
        imageView.setImageResource(i);
    }

    private void onVisibilityButtonClick() {
        if (Applications.isAllCtrlHide) {
            this.layout_Main_All_Ctrl.setVisibility(View.VISIBLE);
            this.iv_Main_Visibility.setImageResource(R.drawable.main_top_on);
            this.rudder.setVisibility(View.VISIBLE);
            this.iv_Main_Flip.setVisibility(View.VISIBLE);
            Applications.isAllCtrlHide = false;
            if (Applications.isRightHandMode) {
                Rudder.p_right.x = Rudder.p_right_default.x;
                if (Applications.isLimitedHigh) {
                    Rudder.p_right.y = Rudder.p_right_default.y;
                    FlyCtrl.rudderdata[3] = TransportMediator.FLAG_KEY_MEDIA_NEXT;
                    return;
                }
                Rudder.p_right.y = Rudder.p_right_start_y;
                FlyCtrl.rudderdata[3] = 1;
                return;
            }
            Rudder.p_left.x = Rudder.p_left_default.x;
            if (Applications.isLimitedHigh) {
                Rudder.p_left.y = Rudder.p_left_default.y;
                FlyCtrl.rudderdata[3] = TransportMediator.FLAG_KEY_MEDIA_NEXT;
                return;
            }
            Rudder.p_left.y = Rudder.p_left_start_y;
            FlyCtrl.rudderdata[3] = 1;
            return;
        }
        this.layout_Main_All_Ctrl.setVisibility(View.GONE);
        this.iv_Main_Visibility.setImageResource(R.drawable.main_top_off);
        this.rudder.setVisibility(View.GONE);
        this.iv_Main_Flip.setVisibility(View.GONE);
        Applications.isAllCtrlHide = true;
    }

    private void onSpeedButtonClick() {
        if (Applications.speed_level == 2) {
            Applications.speed_level = 3;
            this.iv_Main_Speed_Level.setImageResource(R.drawable.main_top_speed_high);
        } else if (Applications.speed_level == 3) {
            Applications.speed_level = 2;
            this.iv_Main_Speed_Level.setImageResource(R.drawable.main_top_speed_low);
        }
        this.rudder.setSpeedLevel(Applications.speed_level);
    }

    /*private void doReverseScreen() {
        if (VERSION.SDK_INT < 9) {
            Builder builder = new Builder(this);
            builder.setTitle("").setMessage("\u8be5\u7248\u672c\u4e0d\u652f\u6301\u6b64\u529f\u80fd!").setPositiveButton(17039370, null);
            builder.show();
            return;
        }
        if (this.screenOrientation == 0) {
            this.screenOrientation = 8;
        } else {
            this.screenOrientation = 0;
        }
        setRequestedOrientation(this.screenOrientation); prem remove
    }*/

    private void updatePhotoToAlbum(String path) {
        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        Uri uri = Uri.fromFile(new File(path));
        Log.e("Display Activity", "uri  " + uri.toString());
        intent.setData(uri);
        getApplicationContext().sendBroadcast(intent);
    }

    protected void onStop() {
        super.onStop();
        if (Applications.isSensorOn) {
            Applications.isSensorOn = false;
            this.sensors.unregister();
            this.iv_Main_Gravity.setImageResource(R.drawable.main_top_gyroscope);
        }
        finish();
        this.mStream63.stopStream63();
        this.mStream93.stopStream93();
        this.mLeweiLib.stopThread();
        this.mFlyCtrl.stopSendDataThread();
    }

    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode != 4 || keyEvent.getRepeatCount() != 0) {
            return false;
        }
        startIntent(this, HomeActivity.class);
        return true;
    }

    private void startIntent(Context context, Class<?> cls) {
        finish();
        startActivity(new Intent(this, cls));
    }

    private void setTime() {
        Time t = new Time();
        t.setToNow();
        int s = t.second;
        if (this.second != s) {
            this.second = s;
            this.recTime++;
            int time_h = this.recTime / 3600;
            int time_m = (this.recTime % 3600) / 60;
            int time_s = this.recTime % 60;
            this.tv_Main_Record_Time.setText("REC:" + String.format("%02d", new Object[]{Integer.valueOf(time_h)}) + ":" + String.format("%02d", new Object[]{Integer.valueOf(time_m)}) + ":" + String.format("%02d", new Object[]{Integer.valueOf(time_s)}));
        }
    }

    private void initTime() {
        this.recTime = 0;
        Time time = new Time();
        time.setToNow();
        this.second = time.second;
    }

    private void clearTime() {
        this.second = 0;
    }

    private void startShowTimer() {
        if (!(this.showTimer == null || this.showTimerTask == null)) {
            this.showTimerTask.cancel();
        }
        this.showTimerTask = new ShowTimerTask();
        this.showTimer.schedule(this.showTimerTask, 500, 500);
    }

}
