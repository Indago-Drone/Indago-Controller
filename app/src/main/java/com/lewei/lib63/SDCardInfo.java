package com.lewei.lib63;

public class SDCardInfo {
    public static final byte FHNPEN_SDCardState_FORMATING = (byte) 8;
    public static final byte FHNPEN_SDCardState_FOUND = (byte) 1;
    public static final byte FHNPEN_SDCardState_LOADED = (byte) 2;
    public static final byte FHNPEN_SDCardState_NORMAL = (byte) 4;
    public int formatProgress;
    public int formatState;
    public byte state;
    public long totalSize;
    public long usedSize;
}
