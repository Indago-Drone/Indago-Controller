package com.lewei.multiple.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.StatFs;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class PathConfig {
    private static final String PARENTFOLDER = "DRONE";
    private static final String PHOTOS = "Photos";
    public static final String PHOTOS_PATH = "/DRONE/Photos";
    private static final String VIDEOS = "Videos";
    public static final String VIDEOS_PATH = "/DRONE/Videos";
    public static SdcardSelector sdcardItem;
    private static List<String> videoList;

    /* renamed from: com.lewei.multiple.utils.PathConfig.1 */
    static class C00911 implements FileFilter { //findme wasn't static
        public C00911() {
        }

        public boolean accept(File file) {
            if (file.isFile() && (file.getAbsolutePath().toLowerCase().endsWith(".bmp") || file.getAbsolutePath().toLowerCase().endsWith(".jpg") || file.getAbsolutePath().toLowerCase().endsWith(".png"))) {
                return true;
            }
            return false;
        }
    }

    /* renamed from: com.lewei.multiple.utils.PathConfig.2 */
    class C00922 implements Comparator<File> {
        C00922() {
        }

        public int compare(File curFile, File nextFile) {
            return curFile.lastModified() > nextFile.lastModified() ? 1 : -1;
        }
    }

    public enum SdcardSelector {
        BUILT_IN,
        EXTERNAL
    }

    static {
        sdcardItem = SdcardSelector.BUILT_IN;
        videoList = new ArrayList();
    }

    public void setSdcardItem(SdcardSelector item) {
        sdcardItem = item;
    }

    public static String getPhotoPath() {
        try {
            String sdCardDir;
            if (sdcardItem == SdcardSelector.BUILT_IN) {
                sdCardDir = SdCardUtils.getFirstExternPath();
            } else {
                sdCardDir = SdCardUtils.getSecondExternPath();
                if (sdCardDir == null) {
                    return null;
                }
            }
            String photoPath = new StringBuilder(String.valueOf(sdCardDir)).append("/").append(PARENTFOLDER).append("/").append(PHOTOS).append("/").toString();
            File folder = new File(photoPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            return new File(new StringBuilder(String.valueOf(photoPath)).append(new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date(System.currentTimeMillis()))).append(".jpg").toString()).getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getVideoPath(String parentFolder, String videoName) {
        String absolutePath = null;
        try {
            String sdCardDir;
            if (sdcardItem == SdcardSelector.BUILT_IN) {
                sdCardDir = SdCardUtils.getFirstExternPath();
            } else {
                sdCardDir = SdCardUtils.getSecondExternPath();
                if (sdCardDir == null) {
                    return null;
                }
            }
            String videoPath = new StringBuilder(String.valueOf(sdCardDir)).append("/").append(parentFolder).append("/").toString();
            File folder = new File(videoPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File saveVideo = new File(new StringBuilder(String.valueOf(videoPath)).append(videoName).toString());
            if (!saveVideo.exists()) {
                saveVideo.createNewFile();
            }
            absolutePath = saveVideo.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return absolutePath;
    }

    public static String getVideoPath() {
        try {
            String sdCardDir;
            if (sdcardItem == SdcardSelector.BUILT_IN) {
                sdCardDir = SdCardUtils.getFirstExternPath();
            } else {
                sdCardDir = SdCardUtils.getSecondExternPath();
                if (sdCardDir == null) {
                    return null;
                }
            }
            String videoPath = new StringBuilder(String.valueOf(sdCardDir)).append("/").append(PARENTFOLDER).append("/").append(VIDEOS).append("/").toString();
            File folder = new File(videoPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            return new File(new StringBuilder(String.valueOf(videoPath)).append(new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date(System.currentTimeMillis()))).append(".mp4").toString()).getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getRootPath() {
        if (sdcardItem == SdcardSelector.BUILT_IN) {
            return SdCardUtils.getFirstExternPath();
        }
        String sdCardDir = SdCardUtils.getSecondExternPath();
        if (sdCardDir == null) {
            return null;
        }
        return sdCardDir;
    }

    public void savePhoto(Context context, String parentFolder, String photoName, byte[] imagedata) {
        String sdCardDir = getRootPath();
        if (sdCardDir != null) {
            try {
                String photoPath = new StringBuilder(String.valueOf(sdCardDir)).append("/").append(parentFolder).append("/").toString();
                File folder = new File(photoPath);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                File savePhoto = new File(photoPath, photoName);
                if (!savePhoto.exists()) {
                    savePhoto.createNewFile();
                }
                String absolutePath = savePhoto.getAbsolutePath();
                Log.e("path", absolutePath);
                FileOutputStream fout = new FileOutputStream(absolutePath);
                fout.write(imagedata, 0, imagedata.length);
                fout.close();
                Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                Uri uri = path2uri(context, Uri.fromFile(new File(new StringBuilder(String.valueOf(photoPath)).append(photoName).toString())));
                Log.e("Display Activity", "uri  " + uri.toString());
                intent.setData(uri);
                context.sendBroadcast(intent);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    public void savePhoto(Context context, Bitmap bmp) {
        String sdCardDir = getRootPath();
        if (sdCardDir != null) {
            try {
                String timeString = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.getDefault()).format(new Date(System.currentTimeMillis()));
                String photoPath = new StringBuilder(String.valueOf(sdCardDir)).append("/").append(PARENTFOLDER).append("/").append(PHOTOS).toString();
                File folder = new File(photoPath);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                String photoName = new StringBuilder(String.valueOf(timeString)).append(".jpg").toString();
                File savePhoto = new File(photoPath, photoName);
                if (!savePhoto.exists()) {
                    savePhoto.createNewFile();
                }
                String absolutePath = savePhoto.getAbsolutePath();
                Log.e("path", absolutePath);
                FileOutputStream fout = new FileOutputStream(absolutePath);
                bmp.compress(CompressFormat.JPEG, 80, fout);
                fout.close();
                Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                Uri uri = path2uri(context, Uri.fromFile(new File(new StringBuilder(String.valueOf(photoPath)).append(photoName).toString())));
                Log.e("Display Activity", "uri  " + uri.toString());
                intent.setData(uri);
                context.sendBroadcast(intent);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    @SuppressLint({"DefaultLocale"})
    public static List<String> getImagesList(File photoPath) {
        List<String> photoList = new ArrayList();
        PathConfig p = new PathConfig();
        File[] filterFiles = photoPath.listFiles(new C00911());
        if (filterFiles != null && filterFiles.length > 0) {
            for (File file : filterFiles) {
                if (photoList.indexOf(file.getAbsolutePath()) == -1) {
                    photoList.add(file.getAbsolutePath());
                }
            }
        }
        return photoList;
    }

    public static List<String> getVideosList(File videoPath) {
        videoList.clear();
        getVideoListNew(videoPath);
        return videoList;
    }

    @SuppressLint({"DefaultLocale"})
    private static void getVideoListNew(File videoPath) {
        List<String> temp = new ArrayList();
        File[] files = videoPath.listFiles();
        if (files != null && files.length > 0) {
            int i = 0;
            while (i < files.length) {
                if (files[i].isFile()) {
                    if (files[i].getAbsolutePath().toLowerCase().endsWith(".avi") || files[i].getAbsolutePath().toLowerCase().endsWith(".3gp") || files[i].getAbsolutePath().toLowerCase().endsWith(".mp4")) {
                        File videoFile = new File(files[i].getAbsolutePath());
                        if (videoFile.exists() && temp.indexOf(videoFile.getAbsolutePath()) == -1) {
                            temp.add(videoFile.getAbsolutePath());
                            videoList.add(videoFile.toString());
                        }
                    }
                } else if (files[i].isDirectory() && files[i].getPath().indexOf("/.") == -1) {
                    getVideoListNew(files[i]);
                }
                i++;
            }
        }
    }

    @SuppressLint({"DefaultLocale"})
    private static void getVideoList(File videoPath) {
        List<String> temp = new ArrayList();
        File[] files = videoPath.listFiles();
        if (files != null && files.length > 0) {
            int i = 0;
            while (i < files.length) {
                if (files[i].isFile()) {
                    if (files[i].getAbsolutePath().toLowerCase().endsWith(".avi") || files[i].getAbsolutePath().toLowerCase().endsWith(".3gp") || files[i].getAbsolutePath().toLowerCase().endsWith(".mp4")) {
                        String lcPath = files[i].getAbsolutePath().toLowerCase();
                        String absPath = files[i].getAbsolutePath();
                        String photopath = null;
                        if (lcPath.contains(".avi")) {
                            photopath = absPath.replace(".avi", ".jpg");
                        } else if (lcPath.contains(".mp4")) {
                            photopath = absPath.replace(".mp4", ".jpg");
                        } else if (lcPath.contains(".3gp")) {
                            photopath = absPath.replace(".3gp", ".jpg");
                        }
                        File photofile = new File(photopath);
                        if (photofile.exists() && temp.indexOf(photofile.getAbsolutePath()) == -1) {
                            temp.add(photofile.getAbsolutePath());
                            videoList.add(photofile.toString());
                        }
                    }
                } else if (files[i].isDirectory() && files[i].getPath().indexOf("/.") == -1) {
                    getVideoList(files[i]);
                }
                i++;
            }
        }
    }

    private Uri path2uri(Context context, Uri uri) {
        if (!uri.getScheme().equals("file")) {
            return uri;
        }
        String path = uri.getEncodedPath();
        if (path == null) {
            return uri;
        }
        path = Uri.decode(path);
        ContentResolver cr = context.getContentResolver();
        StringBuffer buff = new StringBuffer();
        buff.append("(").append("_data").append("=").append("'" + path + "'").append(")");
        Cursor cur = cr.query(Media.EXTERNAL_CONTENT_URI, new String[]{"_id"}, buff.toString(), null, null);
        int index = 0;
        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            index = cur.getInt(cur.getColumnIndex("_id"));
            cur.moveToNext();
        }
        if (index == 0) {
            return uri;
        }
        Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);
        Log.d("", "uri_temp is " + uri_temp);
        if (uri_temp != null) {
            return uri_temp;
        }
        return uri;
    }

    public int getSdcardAvilibleSize() {
        StatFs stat = new StatFs(new File(getRootPath()).getPath());
        return (int) (((((long) stat.getAvailableBlocks()) * ((long) stat.getBlockSize())) / 1024) / 1024);
    }

    public int getSdcardTotalSize() {
        StatFs stat = new StatFs(new File(getRootPath()).getPath());
        return (int) (((((long) stat.getBlockCount()) * ((long) stat.getBlockSize())) / 1024) / 1024);
    }

    public List<File> sortVideoList(List<File> photoList) {
        Collections.sort(photoList, new C00922());
        return photoList;
    }

    public static void deleteFiles(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File deleteFiles : files) {
                    deleteFiles(deleteFiles);
                }
            }
            file.delete();
        }
    }
}
