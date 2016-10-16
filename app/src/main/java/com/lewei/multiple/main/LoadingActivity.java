package com.lewei.multiple.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.lewei.multiple.fydrone.R;

public class LoadingActivity extends Activity {
    public static final String TAG = "LoadingActivity";
    private Handler mHandler;
    Runnable mJumeRunnable;

    /* renamed from: com.lewei.multiple.main.LoadingActivity.1 */
    class C00751 implements Runnable {
        C00751() {
        }

        public void run() {
            LoadingActivity.this.startIntent(HomeActivity.class);
        }
    }

    public LoadingActivity() {
        this.mHandler = new Handler();
        this.mJumeRunnable = new C00751();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
    }

    protected void onResume() {
        super.onResume();
        this.mHandler.postDelayed(this.mJumeRunnable, 1500);
    }

    protected void onStop() {
        super.onStop();
        this.mHandler.removeCallbacks(this.mJumeRunnable);
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    private void startIntent(Class<?> cls) {
        finish();
        Intent intent = new Intent();
        intent.setClass(this, cls);
        startActivity(intent);
        overridePendingTransition(17432578, 17432579);
    }
}
