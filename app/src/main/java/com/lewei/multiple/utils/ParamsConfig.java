package com.lewei.multiple.utils;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import com.lewei.multiple.app.Applications;

public class ParamsConfig {
    public static void writeTrimOffset(Context context, int flag, int trim) {
        if (Applications.isParamsAutoSave) {
            String name = "trim_offset" + Integer.toString(flag);
            Editor edit = context.getSharedPreferences(name, 0).edit();
            edit.putInt(name, trim);
            edit.commit();
        }
    }

    public static int readTrimOffset(Context context, int flag) {
        String name = "trim_offset" + Integer.toString(flag);
        return context.getSharedPreferences(name, 0).getInt(name, 0);
    }

    public static boolean readCtrlHide(Context context) {
        return context.getSharedPreferences("isCtrlShow", 0).getBoolean("isCtrlShow", false);
    }

    public static void writeCtrlHide(Context context, Boolean mode) {
        Editor edit = context.getSharedPreferences("isCtrlShow", 0).edit();
        edit.putBoolean("isCtrlShow", mode.booleanValue());
        edit.commit();
    }

    public static boolean readHighLimit(Context context) {
        return context.getSharedPreferences("isHighLimit", 0).getBoolean("isHighLimit", false);
    }

    public static void writeHighLimit(Context context, Boolean mode) {
        Editor edit = context.getSharedPreferences("isHighLimit", 0).edit();
        edit.putBoolean("isHighLimit", mode.booleanValue());
        edit.commit();
    }

    public static boolean readRightHandMode(Context context) {
        return context.getSharedPreferences("isRightHandMode", 0).getBoolean("isRightHandMode", false);
    }

    public static void writeRightHandMode(Context context, Boolean mode) {
        Editor edit = context.getSharedPreferences("isRightHandMode", 0).edit();
        edit.putBoolean("isRightHandMode", mode.booleanValue());
        edit.commit();
    }

    public static void writeParamsAutoSave(Context context, Boolean mode) {
        Editor edit = context.getSharedPreferences("isParamsAutoSave", 0).edit();
        edit.putBoolean("isParamsAutoSave", mode.booleanValue());
        edit.commit();
    }

    public static boolean readParamsAutoSave(Context context) {
        return context.getSharedPreferences("isParamsAutoSave", 0).getBoolean("isParamsAutoSave", true);
    }

    public static int readHDflag(Context context) {
        return context.getSharedPreferences("HD_flag", 0).getInt("HD_flag", 0);
    }

    public static void writeHDflag(Context context, int flag) {
        Editor edit = context.getSharedPreferences("HD_flag", 0).edit();
        edit.putInt("HD_flag", flag);
        edit.commit();
    }

    public static int readHardwareDecode(Context context) {
        return context.getSharedPreferences("HardwareDecode", 0).getInt("HardwareDecode", 0);
    }

    public static void writeHardwareDecode(Context context, int flag) {
        Editor edit = context.getSharedPreferences("HardwareDecode", 0).edit();
        edit.putInt("HardwareDecode", flag);
        edit.commit();
    }

    public static boolean readHDPLAY(Context context) {
        return context.getSharedPreferences("HDPLAY", 0).getBoolean("HDPLAY", true);
    }

    public static void writeHDPLAY(Context context, Boolean hdplay) {
        Editor edit = context.getSharedPreferences("HDPLAY", 0).edit();
        edit.putBoolean("HDPLAY", hdplay.booleanValue());
        edit.commit();
    }

    public static boolean readAutoFlip(Context context) {
        return context.getSharedPreferences("AutoFlip", 0).getBoolean("AutoFlip", true);
    }

    public static void writeAutoFlip(Context context, Boolean flip) {
        Editor edit = context.getSharedPreferences("AutoFlip", 0).edit();
        edit.putBoolean("AutoFlip", flip.booleanValue());
        edit.commit();
    }
}
