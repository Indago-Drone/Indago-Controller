package com.lewei.lib63;

public interface ConnectState {
    void onDevOffLine();

    void onDeviceConnect();

    void onFormatState(int i);

    void onRecordStateChanged(boolean z);

    void onSDPushOut(boolean z);
}
