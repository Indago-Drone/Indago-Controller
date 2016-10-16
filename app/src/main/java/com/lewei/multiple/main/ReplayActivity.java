package com.lewei.multiple.main;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.media.TransportMediator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.lewei.lib.LeweiLib;
import com.lewei.lib63.LeweiLib63;
import com.lewei.lib63.RecordInfo;
import com.lewei.multiple.app.Applications;
import com.lewei.multiple.fydrone.R;
import com.lewei.multiple.view.ReplaySurfaceView;

public class ReplayActivity extends Activity {
    private OnClickListener clickListener;
    private String currTimeString;
    public Handler handler;
    private boolean isShowMenuBar;
    private boolean isStop;
    private ImageView iv_Replay_Top_Back;
    private RelativeLayout layout_Replay_Bottom_Menubar;
    private RelativeLayout layout_Replay_Top_Menubar;
    private int position;
    private RecordInfo recordInfo;
    private ReplaySurfaceView replaySurfaceView;
    private SeekBar seekBar_Replay_Play;
    private boolean sendChangeAttr;
    private TextView txt_Replay_Bottom_Time;
    private TextView txt_Replay_Top_Title;
    private int videoAttrEnd;
    private String videoAttrName;
    private int videoAttrStart;
    private int videoTime;
    private String videoTimeString;

    /* renamed from: com.lewei.multiple.main.ReplayActivity.1 */
    class C00851 implements OnClickListener {
        C00851() {
        }

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.replaySurfaceView:
                    if (ReplayActivity.this.isShowMenuBar) {
                        ReplayActivity.this.layout_Replay_Bottom_Menubar.setVisibility(4);
                        ReplayActivity.this.layout_Replay_Top_Menubar.setVisibility(4);
                        ReplayActivity.this.isShowMenuBar = false;
                        return;
                    }
                    ReplayActivity.this.layout_Replay_Bottom_Menubar.setVisibility(0);
                    ReplayActivity.this.layout_Replay_Top_Menubar.setVisibility(0);
                    ReplayActivity.this.isShowMenuBar = true;
                case R.id.iv_replay_top_back:
                    ReplayActivity.this.onBackPressed();
                default:
            }
        }
    }

    /* renamed from: com.lewei.multiple.main.ReplayActivity.2 */
    class C00862 implements Callback {
        C00862() {
        }

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 3 /*FragmentManagerImpl.ANIM_STYLE_CLOSE_ENTER 3*/:
                    int timestamp = ((Integer) msg.obj).intValue();
                    ReplayActivity.this.seekBar_Replay_Play.setProgress(timestamp);
                    int time_m = (timestamp % 3600) / 60;
                    int time_s = timestamp % 60;
                    int time_h = (timestamp % 216000) / 60;
                    if (timestamp / 3600 > 0) {
                        ReplayActivity.this.currTimeString = new StringBuilder(String.valueOf(String.format("%02d", new Object[]{Integer.valueOf(time_h)}))).append(":").append(String.format("%02d", new Object[]{Integer.valueOf(time_m)})).append(":").append(String.format("%02d", new Object[]{Integer.valueOf(time_s)})).toString();
                    } else {
                        ReplayActivity.this.currTimeString = new StringBuilder(String.valueOf(String.format("%02d", new Object[]{Integer.valueOf(time_m)}))).append(":").append(String.format("%02d", new Object[]{Integer.valueOf(time_s)})).toString();
                    }
                    ReplayActivity.this.txt_Replay_Bottom_Time.setText(new StringBuilder(String.valueOf(ReplayActivity.this.currTimeString)).append("/").append(ReplayActivity.this.videoTimeString).toString());
                    break;
            }
            return false;
        }
    }

    /* renamed from: com.lewei.multiple.main.ReplayActivity.3 */
    class C00873 implements OnSeekBarChangeListener {
        C00873() {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }
    }

    /* renamed from: com.lewei.multiple.main.ReplayActivity.4 */
    class C00884 implements Runnable {
        C00884() {
        }

        public void run() {
            while (!ReplayActivity.this.isStop) {
                try {
                    int timestamp;
                    if ((Applications.mRunLeweiLibType & 1) > 0) {
                        timestamp = (int) ((LeweiLib63.LW63GetNowPts() - ReplayActivity.this.recordInfo.uStartTime) / 1000);
                    } else {
                        timestamp = LeweiLib.LW93GetCurrTimestamp();
                    }
                    Message msg = new Message();
                    msg.what = 3;
                    msg.obj = Integer.valueOf(timestamp / 1000);
                    ReplayActivity.this.handler.sendMessage(msg);
                    Thread.sleep(200);
                    if (ReplayActivity.this.sendChangeAttr) {
                        LeweiLib.LW93ChangeRecordReplayAttr(ReplayActivity.this.videoAttrName, ReplayActivity.this.videoAttrStart, ReplayActivity.this.videoAttrEnd);
                        ReplayActivity.this.sendChangeAttr = false;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ReplayActivity() {
        this.isStop = false;
        this.sendChangeAttr = false;
        this.isShowMenuBar = true;
        this.clickListener = new C00851();
        this.handler = new Handler(new C00862());
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(TransportMediator.FLAG_KEY_MEDIA_NEXT);
        setContentView(R.layout.activity_replay);
        this.position = getIntent().getIntExtra("position", 0);
        widgetInit();
        startGetCurrTimestamp();
    }

    private void widgetInit() {
        this.layout_Replay_Top_Menubar = (RelativeLayout) findViewById(R.id.layout_replay_top_menubar);
        this.layout_Replay_Bottom_Menubar = (RelativeLayout) findViewById(R.id.layout_replay_bottom_menubar);
        this.iv_Replay_Top_Back = (ImageView) findViewById(R.id.iv_replay_top_back);
        this.txt_Replay_Top_Title = (TextView) findViewById(R.id.txt_replay_top_title);
        this.txt_Replay_Bottom_Time = (TextView) findViewById(R.id.txt_replay_bottom_time);
        this.seekBar_Replay_Play = (SeekBar) findViewById(R.id.seekbar_replay_play);
        this.iv_Replay_Top_Back.setOnClickListener(this.clickListener);
        if ((Applications.mRunLeweiLibType & 1) > 0) {
            doWidget63();
        } else {
            doWidget93();
        }
        this.seekBar_Replay_Play.setOnSeekBarChangeListener(new C00873());
    }

    private void doWidget93() {
        this.txt_Replay_Top_Title.setText(CardVideoActivity.videoLists[this.position].file_name);
        this.seekBar_Replay_Play.setMax(CardVideoActivity.videoLists[this.position].record_time - 1);
        this.videoTime = CardVideoActivity.videoLists[this.position].record_time - 1;
        int time_m = (this.videoTime % 3600) / 60;
        int time_s = this.videoTime % 60;
        if (this.videoTime / 3600 > 0) {
            this.videoTimeString = new StringBuilder(String.valueOf(String.format("%02d", new Object[]{Integer.valueOf(this.videoTime / 3600)}))).append(":").append(String.format("%02d", new Object[]{Integer.valueOf(time_m)})).append(":").append(String.format("%02d", new Object[]{Integer.valueOf(time_s)})).toString();
        } else {
            this.videoTimeString = new StringBuilder(String.valueOf(String.format("%02d", new Object[]{Integer.valueOf(time_m)}))).append(":").append(String.format("%02d", new Object[]{Integer.valueOf(time_s)})).toString();
        }
        this.replaySurfaceView = (ReplaySurfaceView) findViewById(R.id.replaySurfaceView);
        this.replaySurfaceView.startMySurface(CardVideoActivity.videoLists[this.position].file_name, CardVideoActivity.videoLists[this.position].record_start_time, CardVideoActivity.videoLists[this.position].record_start_time + CardVideoActivity.videoLists[this.position].record_time);
        this.replaySurfaceView.setOnClickListener(this.clickListener);
    }

    private void doWidget63() {
        this.txt_Replay_Top_Title.setText("");
        this.recordInfo = (RecordInfo) CardVideo63Activity.mVideoList.get(this.position);
        this.seekBar_Replay_Play.setMax(this.recordInfo.recordTime);
        this.videoTime = this.recordInfo.recordTime;
        int time_m = (this.videoTime % 3600) / 60;
        int time_s = this.videoTime % 60;
        if (this.videoTime / 3600 > 0) {
            this.videoTimeString = new StringBuilder(String.valueOf(String.format("%02d", new Object[]{Integer.valueOf(this.videoTime / 3600)}))).append(":").append(String.format("%02d", new Object[]{Integer.valueOf(time_m)})).append(":").append(String.format("%02d", new Object[]{Integer.valueOf(time_s)})).toString();
        } else {
            this.videoTimeString = new StringBuilder(String.valueOf(String.format("%02d", new Object[]{Integer.valueOf(time_m)}))).append(":").append(String.format("%02d", new Object[]{Integer.valueOf(time_s)})).toString();
        }
        this.replaySurfaceView = (ReplaySurfaceView) findViewById(R.id.replaySurfaceView);
        this.replaySurfaceView.startLW63Surface(this.recordInfo.uStartTime, this.recordInfo.uStopTime);
        this.replaySurfaceView.setOnClickListener(this.clickListener);
    }

    private void startGetCurrTimestamp() {
        new Thread(new C00884()).start();
    }

    public void onBackPressed() {
        super.onBackPressed();
        this.isStop = true;
        this.replaySurfaceView.stop();
        finish();
    }
}
