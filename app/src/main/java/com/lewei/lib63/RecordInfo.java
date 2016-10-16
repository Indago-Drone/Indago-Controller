package com.lewei.lib63;

public class RecordInfo {
    public int channel;
    public long dataSize;
    public int lockFlag;
    public int recType;
    public int recordTime;
    public String str_channel;
    public String str_lock_flag;
    public String str_rec_type;
    public String str_start_ms;
    public String str_start_time;
    public String str_stop_ms;
    public String str_stop_time;
    public String str_video_size;
    public long uStartTime;
    public long uStopTime;

    public RecordInfo() {
        this.channel = 0;
        this.recType = 0;
        this.lockFlag = 0;
        this.uStartTime = 0;
        this.uStopTime = 0;
        this.dataSize = 0;
        this.recordTime = 0;
        this.str_start_time = "";
        this.str_stop_time = "";
        this.str_channel = "";
        this.str_rec_type = "";
        this.str_lock_flag = "";
        this.str_video_size = "";
        this.str_start_ms = "";
        this.str_stop_ms = "";
    }
}
