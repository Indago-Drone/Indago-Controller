package com.lewei.lib63;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.media.FaceDetector;
import android.os.Handler;
import android.util.Log;

import com.lewei.multiple.fydrone.R;
import com.lewei.multiple.utils.PathConfig;
import com.lewei.multiple.view.MySurfaceView;

import org.opencv.android.Utils;
import org.opencv.contrib.FaceRecognizer;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Stream63 {
    private Context context;
    private Handler handler;
    private boolean isStop63;
    private boolean is_recording_now;
    private MySurfaceView mSurfaceView;

    public static Bitmap img;

    FaceDetector fD;
    FaceDetector.Face[] faceArray;


    /* renamed from: com.lewei.lib63.Stream63.1 */
    class C00471 implements Runnable {
        MatOfRect faceDetections;
        FaceRecognizer faceRecognizer;
        private Mat mRgba;
        private Mat mGray;
        CascadeClassifier faceDetector;
        Mat m;
        InputStream is;
        File mCascadeFile;
        FaceDetector.Face[] faces;
        C00471() {
            //faceDetections = new MatOfRect();
            //faceRecognizer = createFisherFaceRecognizer();
            //faceDetector = new CascadeClassifier(Stream63.class.getResource("haarcascade_frontalface_alt.xml").getPath().substring(1));
            //faceDetector = new CascadeClassifier();
            //faceDetector = new CascadeClassifier(Stream63.this.context.getDir("lbpcascade_frontalface.xml", Context.MODE_PRIVATE).getAbsolutePath());
            //InputStream is = Stream63.this.context.getResources().openRawResource(R.raw.lbpcascade_frontalface);
            //File cascadeDir = Stream63.this.context.getDir("cascade", Context.MODE_PRIVATE);
            //mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            //System.out.println("cas empty: " + faceDetector.empty());
            /*FileOutputStream os;
            faces = new FaceDetector.Face[3];
            //System.out.println("nu,m "faces.length);

            try {
                //os = new FileOutputStream(mCascadeFile);
                byte[] buffer = new byte[4096];
                int bytesRead;
               // while ((bytesRead = is.read(buffer)) != -1) {
                    //os.write(buffer, 0, bytesRead);
                //}
                is.close();
                //os.close();
             }catch(Exception e){
                 e.printStackTrace();
            }*/
        }

        public void run() {
            Stream63.this.isStop63 = false;
            final Bitmap bmp = Bitmap.createBitmap(720, 576, Config.ARGB_8888);
            while (!Stream63.this.isStop63) {
                //System.out.println("cam man");
                if (LeweiLib63.LW63StartPreview(0, 0, 0) > 0) {
                    Stream63.this.handler.sendEmptyMessage(2);
                    while (!Stream63.this.isStop63) {
                        if (LeweiLib63.LW63DrawBitmapFrame(bmp) > 0) {
                            Stream63.this.mSurfaceView.setBitmap(bmp);
                            Stream63.this.img = bmp;
                            //System.out.println("bit the map");
                            //m = new Mat();
                            //Utils.bitmapToMat(bmp, m);
                            //faceDetector.detectMultiScale(m, faceDetections);
                            //faceDetector.detectMultiScale(mGray, faceDetections, 1.1, 2, 2,
                                    //new Size(0, 0), new Size());
                            //System.out.println("numFaces = " + faceDetections.toArray().length + "\n");
                            /*FaceDetector face_detector = new FaceDetector(
                                    bmp.getWidth(), bmp.getHeight(),
                                    3);
                            int face_count = face_detector.findFaces(bmp, faces);
                            System.out.println("numFaces "  + face_count);*/
                        } else {
                            Stream63.this.msleep(5);
                            //System.out.println("otherb");
                            System.gc();
                        }
                    }
                } else {
                    Stream63.this.msleep(1000);
                }
            }
            LeweiLib63.LW63StopPreview();
            Log.e("surfaceview 63", "stop preview 63");
        }
    }

    public Stream63(Context context, Handler handler, MySurfaceView mSurfaceView) {
        this.isStop63 = false;
        this.is_recording_now = false;
        this.context = context;
        this.handler = handler;
        this.mSurfaceView = mSurfaceView;
    }

    public void stopStream63() {
        this.isStop63 = true;
        LeweiLib63.LW63StopLocalRecord();
    }

    public void startStream63() {
        new Thread(new C00471()).start();
    }

    public void takeRecord() {
        if (this.is_recording_now) {
            LeweiLib63.LW63StopLocalRecord();
            this.handler.sendEmptyMessage(19);
            this.is_recording_now = false;
            return;
        }
        LeweiLib63.LW63TakeLocalRecord(PathConfig.getVideoPath());
        this.is_recording_now = true;
        this.handler.sendEmptyMessage(17);
    }

    private void msleep(int ms) {
        try {
            Thread.sleep((long) ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
