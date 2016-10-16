package com.lewei.multiple.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.lewei.multiple.app.Applications;
import com.lewei.multiple.fydrone.R;

public class HomeActivity extends Activity {
    public static final String TAG = "HomeActivity";
    private ImageView iv_Home_Help;
    private ImageView iv_Home_Play;
    private ImageView iv_Home_Setting;

    private class ClickListener implements OnClickListener {
        private ClickListener() {
        }

        public void onClick(View arg0) {
            Intent intent;
            switch (arg0.getId()) {
                case R.id.iv_home_help:
                    intent = new Intent();
                    intent.setClass(HomeActivity.this, HelpActivity.class);
                    HomeActivity.this.startActivity(intent);
                case R.id.iv_home_setting:
                    intent = new Intent();
                    intent.setClass(HomeActivity.this, SetActivity.class);
                    HomeActivity.this.startActivity(intent);
                case R.id.iv_home_play_lw63:
                    HomeActivity.this.startIntent(LW93MainActivity.class);
                default:
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        widget_init();
    }

    private void widget_init() {
        this.iv_Home_Play = (ImageView) findViewById(R.id.iv_home_play_lw63);
        this.iv_Home_Help = (ImageView) findViewById(R.id.iv_home_help);
        this.iv_Home_Setting = (ImageView) findViewById(R.id.iv_home_setting);
        this.iv_Home_Play.setOnClickListener(new ClickListener());
        this.iv_Home_Help.setOnClickListener(new ClickListener());
        this.iv_Home_Setting.setOnClickListener(new ClickListener());
    }

    protected void onStop() {
        super.onStop();
    }

    public void onBackPressed() {
        super.onBackPressed();
        Applications.mDevice63.stopLoginThread();
        finish();
        Process.killProcess(Process.myPid());
    }

    private void startIntent(Class<?> cls) {
        finish();
        Intent intent = new Intent();
        intent.setClass(this, cls);
        startActivity(intent);
        overridePendingTransition(17432578, 17432579);
    }
}
