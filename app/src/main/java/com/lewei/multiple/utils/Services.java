package com.lewei.multiple.utils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;

public class Services extends Service {
    private static final String TAG = "Services";
    private BroadcastReceiver brReceiver;
    private boolean isWifiConnected;
    private LocalBinder mBinder;
    private WifiManager manager;

    /* renamed from: com.lewei.multiple.utils.Services.1 */
    class C00931 extends BroadcastReceiver {
        C00931() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.net.wifi.WIFI_STATE_CHANGED")) {
                switch (intent.getIntExtra("wifi_state", 0)) {
                    case CursorAdapter.FLAG_AUTO_REQUERY /*1*/:
                        Services.this.isWifiConnected = false;
                    default:
                }
            } else if (action.equals("android.net.wifi.STATE_CHANGE")) {
                Parcelable parcelableExtra = intent.getParcelableExtra("networkInfo");
                if (parcelableExtra != null) {
                    State state = ((NetworkInfo) parcelableExtra).getState();
                    String ssid = Services.this.manager.getConnectionInfo().getSSID();
                    if (state == State.CONNECTED) {
                        if (ssid.startsWith("\"")) {
                            ssid = ssid.substring(1, ssid.length() - 1);
                        }
                        Services.this.isWifiConnected = true;
                    } else if (state == State.DISCONNECTED) {
                        Log.d(Services.TAG, "Wifi State Change!");
                        Services.this.isWifiConnected = false;
                    }
                }
            }
        }
    }

    public class LocalBinder extends Binder {
        public Services getService() {
            return Services.this;
        }
    }

    public Services() {
        this.mBinder = new LocalBinder();
        this.manager = null;
        this.isWifiConnected = false;
        this.brReceiver = new C00931();
    }

    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    public void init() {
        Log.d(TAG, "register wifi network receiver.");
        this.manager = (WifiManager) getSystemService("wifi");
        register();
    }

    public void register() {
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.net.wifi.STATE_CHANGE");
        mIntentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        registerReceiver(this.brReceiver, mIntentFilter);
    }

    public boolean getWifiConnected() {
        return this.isWifiConnected;
    }

    public void exit() {
        Log.d(TAG, "unregister wifi network receiver.");
        unregisterReceiver(this.brReceiver);
    }
}
