package com.lewei.multiple.utils;

import android.os.Environment;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SdCardUtils {
    public static String getSecondExternPath() {
        List<String> paths = getAllExterSdcardPath();
        if (paths.size() != 2) {
            return null;
        }
        for (String path : paths) {
            if (path != null && !path.equals(getFirstExternPath())) {
                return path;
            }
        }
        return null;
    }

    public static boolean isFirstSdcardMounted() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            return true;
        }
        return false;
    }

    public static boolean isSecondSDcardMounted() {
        String sd2 = getSecondExternPath();
        if (sd2 == null) {
            return false;
        }
        return checkFsWritable(new StringBuilder(String.valueOf(sd2)).append(File.separator).toString());
    }

    private static boolean checkFsWritable(String dir) {
        if (dir == null) {
            return false;
        }
        File directory = new File(dir);
        if (!directory.isDirectory() && !directory.mkdirs()) {
            return false;
        }
        File f = new File(directory, ".keysharetestgzc");
        try {
            if (f.exists()) {
                f.delete();
            }
            if (!f.createNewFile()) {
                return false;
            }
            f.delete();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getFirstExternPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    public static List<String> getAllExterSdcardPath() {
        List<String> SdList = new ArrayList();
        String firstPath = getFirstExternPath();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("mount").getInputStream()));
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                } else if (!(line.contains("secure") || line.contains("asec") || line.contains("media") || line.contains("system") || line.contains("cache") || line.contains("sys") || line.contains("data") || line.contains("tmpfs") || line.contains("shell") || line.contains("root") || line.contains("acct") || line.contains("proc") || line.contains("misc") || line.contains("obb"))) {
                    if (line.contains("fat") || line.contains("fuse") || line.contains("ntfs")) {
                        String[] columns = line.split(" ");
                        if (columns != null && columns.length > 1) {
                            String path = columns[1].toLowerCase(Locale.getDefault());
                            if (!(path == null || SdList.contains(path) || !path.contains("sd"))) {
                                SdList.add(columns[1]);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!SdList.contains(firstPath)) {
            SdList.add(firstPath);
        }
        return SdList;
    }
}
