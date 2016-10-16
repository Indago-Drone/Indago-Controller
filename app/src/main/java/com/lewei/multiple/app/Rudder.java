package com.lewei.multiple.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable.Callback;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.support.v4.app.DialogFragment;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.CursorAdapter;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import com.lewei.multiple.fydrone.C0052R;
import com.lewei.multiple.fydrone.R;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Rudder extends GLSurfaceView implements Callback {
    public static final boolean USE_SQUARE_RUDDER = true;
    public static Point p_left;
    public static Point p_left_default;
    public static int p_left_start_x;
    public static int p_left_start_y;
    public static Point p_right;
    public static int p_right_bottom;
    public static Point p_right_default;
    public static int p_right_left;
    public static int p_right_right;
    public static int p_right_start_x;
    public static int p_right_start_y;
    public static int p_right_up;
    private int FlyMaxPower;
    private int FlyMaxRotate;
    private int FlyMaxSpeed;
    private int MaxSpeed;
    private int MidSpeed;
    private int MinSpeed;
    private int RightMaxRight;
    private int _id_left;
    private int _id_right;
    private Bitmap bLeft;
    private Bitmap bRight;
    private int bWidth;
    private boolean isStop;
    private int l_left;
    private int l_right;
    private int mDistance;
    private SurfaceHolder mHolder;
    private Paint mPaint;
    private Thread mThread;
    private OnRudderListener onRudderListener;
    private int p_left_bottom;
    private int p_left_left;
    private int p_left_right;
    private int p_left_up;
    private Renderer renderer;
    private int screenHeight;
    private int screenWidth;

    /* renamed from: com.lewei.multiple.app.Rudder.1 */
    class C00501 extends Thread {
        C00501() {
        }

        public void run() {
            super.run();
            Rudder.this.isStop = false;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            while (!Rudder.this.isStop) {
                /*Canvas canvas = Rudder.this.mHolder.lockCanvas();
                if (canvas == null) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                canvas.drawColor(0, Mode.CLEAR);
                canvas.drawBitmap(Rudder.this.bLeft, (float) (Rudder.p_left.x - Rudder.this.bWidth), (float) (Rudder.p_left.y - Rudder.this.bWidth), Rudder.this.mPaint);
                canvas.drawBitmap(Rudder.this.bRight, (float) (Rudder.p_right.x - Rudder.this.bWidth), (float) (Rudder.p_right.y - Rudder.this.bWidth), Rudder.this.mPaint);
                if (Rudder.this.mHolder != null) {
                    Rudder.this.mHolder.unlockCanvasAndPost(canvas);
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }*/
            }
        }
    }

    public class MyRenderer implements Renderer {
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glViewport(0, 0, width, height);
        }

        public void onDrawFrame(GL10 gl) {
        }
    }

    public interface OnRudderListener {
        void OnLeftRudder(int i, int i2);

        void OnRightRudder(int i, int i2, boolean z);

        void OnRightRudderUp();
    }

    static {
        p_left = new Point();
        p_right = new Point();
        p_left_default = new Point();
        p_right_default = new Point();
        p_right_up = 0;
        p_right_bottom = 0;
        p_right_left = 0;
        p_right_right = 0;
        p_left_start_y = 0;
        p_left_start_x = 0;
        p_right_start_x = 0;
        p_right_start_y = 0;
    }

    public Rudder(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mHolder = null;
        this.mPaint = null;
        this.screenWidth = 0;
        this.screenHeight = 0;
        this.bLeft = null;
        this.bRight = null;
        this.isStop = false;
        this.mThread = null;
        this.bWidth = 0;
        this.l_left = 0;
        this.l_right = 0;
        this._id_left = -1;
        this._id_right = -1;
        this.p_left_up = 0;
        this.p_left_bottom = 0;
        this.p_left_left = 0;
        this.p_left_right = 0;
        this.FlyMaxPower = MotionEventCompat.ACTION_MASK;
        this.FlyMaxRotate = TransportMediator.KEYCODE_MEDIA_PAUSE;
        this.MinSpeed = 40;
        this.MidSpeed = 60;
        this.MaxSpeed = TransportMediator.KEYCODE_MEDIA_PAUSE;
        this.FlyMaxSpeed = this.MinSpeed;
        this.RightMaxRight = 42;
        this.onRudderListener = null;
        this.mDistance = 0;
        DisplayMetrics dmMetrics = getResources().getDisplayMetrics();
        this.screenWidth = dmMetrics.widthPixels;
        this.screenHeight = dmMetrics.heightPixels;
        this.mDistance = this.screenWidth / 30;
        this.mHolder = getHolder();
        this.mHolder.addCallback(this);
        this.mPaint = new Paint();
        this.mPaint.setColor(getResources().getColor(R.color.beige));//-16776961
        this.mPaint.setAntiAlias(USE_SQUARE_RUDDER);
        setFocusable(USE_SQUARE_RUDDER);
        setFocusableInTouchMode(USE_SQUARE_RUDDER);
        setZOrderOnTop(USE_SQUARE_RUDDER);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.btn_down);
        float f_round = ((float) this.screenWidth) / ((float) (bitmap.getWidth() * 16));
        Matrix matrix = new Matrix();
        matrix.setScale(f_round, f_round);
        Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, USE_SQUARE_RUDDER);
        this.bLeft = createBitmap;
        this.bRight = createBitmap;
        this.bWidth = this.bLeft.getWidth() / 2;
        this.mHolder.setFormat(-2);
        this.renderer = new MyRenderer();
        setRenderer(this.renderer);
        System.gc();
        initPosition();
    }

    public void setSpeedLevel(int isHigh) {
        switch (isHigh) {
            case CursorAdapter.FLAG_AUTO_REQUERY /*1*/:
                this.FlyMaxSpeed = this.MinSpeed;
            case CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER /*2*/:
                this.FlyMaxSpeed = this.MidSpeed;
            case 3 /*3 FragmentManagerImpl.ANIM_STYLE_CLOSE_ENTER*/:
                this.FlyMaxSpeed = this.MaxSpeed;
            default:
        }
    }

    public void setOnRudderListener(OnRudderListener onRudderListener) {
        this.onRudderListener = onRudderListener;
    }

    private void initPosition() {
        Point point;
        int i;
        if (Applications.isRightHandMode) {
            point = p_left;
            i = (this.screenWidth * 240) / 960;
            p_left_default.x = i;
            point.x = i;
            point = p_left;
            i = (((this.screenHeight + 50) - ((this.screenWidth * 360) / 960)) / 2) + ((this.screenWidth * 180) / 960);
            p_right_default.y = i;
            point.y = i;
            p_left_default.y = (this.screenHeight + 50) / 2;
            point = p_right;
            i = (this.screenWidth * 720) / 960;
            p_right_default.x = i;
            point.x = i;
            p_right.y = ((((this.screenHeight + 50) - ((this.screenWidth * 360) / 960)) / 2) + ((this.screenWidth * 360) / 960)) - (this.bWidth * 2);
        } else {
            point = p_left;
            i = (this.screenWidth * 240) / 960;
            p_left_default.x = i;
            point.x = i;
            p_left.y = ((((this.screenHeight + 50) - ((this.screenWidth * 360) / 960)) / 2) + ((this.screenWidth * 360) / 960)) - (this.bWidth * 2);
            p_left_default.y = (this.screenHeight + 50) / 2;
            point = p_right;
            i = (this.screenWidth * 720) / 960;
            p_right_default.x = i;
            point.x = i;
            point = p_right;
            i = (((this.screenHeight + 50) - ((this.screenWidth * 360) / 960)) / 2) + ((this.screenWidth * 180) / 960);
            p_right_default.y = i;
            point.y = i;
        }
        int i2 = (this.screenWidth * 115) / 960;
        this.l_left = i2;
        this.l_right = i2;
        p_left_start_x = p_left_default.x - this.l_left;
        p_left_start_y = p_left_default.y + this.l_left;
        p_right_start_x = p_right_default.x - this.l_right;
        p_right_start_y = p_right_default.y + this.l_right;
        this.p_left_up = (p_left_default.y - ((this.screenWidth * 125) / 960)) - this.bWidth;
        this.p_left_bottom = (p_left_default.y + ((this.bWidth * 2) / 3)) + ((this.screenWidth * 125) / 960);
        this.p_left_left = (p_left_default.x - ((this.screenWidth * 125) / 960)) - (this.bWidth * 2);
        this.p_left_right = (p_left_default.x + ((this.screenWidth * 125) / 960)) + (this.bWidth * 2);
        p_right_up = (p_right_default.y - ((this.screenWidth * 125) / 960)) - this.bWidth;
        p_right_bottom = (p_right_default.y + ((this.bWidth * 2) / 3)) + ((this.screenWidth * 125) / 960);
        p_right_left = (p_right_default.x - ((this.screenWidth * 125) / 960)) - (this.bWidth * 2);
        p_right_right = (p_right_default.x + ((this.screenWidth * 125) / 960)) + (this.bWidth * 2);
        RudderCoordinate.setInitLeft(p_left_default.x, p_left_default.y, this.l_left);
        RudderCoordinate.setInitRight(p_right_default.x, p_right_default.y, this.l_right);
    }

    public void startRudder() {
        if (this.mThread == null || !this.mThread.isAlive()) {
            this.mThread = new C00501();
            this.mThread.start();
        }
    }

    public void stopRudder() {
        this.isStop = USE_SQUARE_RUDDER;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        stopRudder();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        startRudder();
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent event) {
        int count = event.getPointerCount();
        int pointerId = (event.getAction() & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >>> 8;
        int x;
        int y;
        switch (event.getAction()) {
            case DialogFragment.STYLE_NORMAL /*0*/:
                x = (int) event.getX(pointerId);
                y = (int) event.getY(pointerId);
                if (x < this.p_left_left || x > this.p_left_right || y < this.p_left_up || y > this.p_left_bottom) {
                    if (x >= p_right_left && x <= p_right_right && y >= p_right_up && y <= p_right_bottom) {
                        this._id_right = pointerId;
                        dealRight(x, y);
                        break;
                    }
                }
                this._id_left = pointerId;
                dealLeft(x, y);
                break;
                //break;
            case CursorAdapter.FLAG_AUTO_REQUERY /*1*/:
                if (this._id_left == pointerId) {
                    if (Applications.isRightHandMode) {
                        dealLeft(p_left_default.x, p_left_default.y);
                    } else if (Applications.isLimitedHigh) {
                        dealLeft(p_left_default.x, p_left_default.y);
                    } else {
                        dealLeft(p_left_default.x, p_left.y);
                    }
                }
                if (this._id_right == pointerId) {
                    if (!Applications.isRightHandMode) {
                        this.onRudderListener.OnRightRudderUp();
                        dealRight(p_right_default.x, p_right_default.y);
                    } else if (Applications.isLimitedHigh) {
                        dealRight(p_right_default.x, p_right_default.y);
                    } else {
                        dealRight(p_right_default.x, p_right.y);
                    }
                }
                this._id_left = -1;
                this._id_right = -1;
                break;
            case CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER /*2*/:
                for (int i = 0; i < count; i++) {
                    x = (int) event.getX(i);
                    y = (int) event.getY(i);
                    if (x >= this.p_left_left && x <= this.p_left_right && y >= this.p_left_up && y <= this.p_left_bottom) {
                        this._id_left = i;
                    } else if (x >= p_right_left && x <= p_right_right && y >= p_right_up && y <= p_right_bottom) {
                        this._id_right = i;
                    }
                    if (this._id_left == i) {
                        dealLeft(x, y);
                    } else if (this._id_right == i) {
                        dealRight(x, y);
                    }
                }
                break;
            case 5/*5 FragmentManagerImpl.ANIM_STYLE_FADE_ENTER*/:
                x = (int) event.getX(pointerId);
                y = (int) event.getY(pointerId);
                if (x < this.p_left_left || x > this.p_left_right || y < this.p_left_up || y > this.p_left_bottom) {
                    if (x >= p_right_left && x <= p_right_right && y >= p_right_up && y <= p_right_bottom) {
                        this._id_right = pointerId;
                        dealRight(x, y);
                        break;
                    }
                }
                this._id_left = pointerId;
                dealLeft(x, y);
                break;
                //break;
            case 6 /*6 FragmentManagerImpl.ANIM_STYLE_FADE_EXIT*/:
                if (this._id_left != pointerId) {
                    if (this._id_right == pointerId) {
                        if (!Applications.isRightHandMode) {
                            this.onRudderListener.OnRightRudderUp();
                            dealRight(p_right_default.x, p_right_default.y);
                        } else if (Applications.isLimitedHigh) {
                            dealRight(p_right_default.x, p_right_default.y);
                        } else {
                            dealRight(p_right_default.x, p_right.y);
                        }
                        this._id_right = -1;
                        break;
                    }
                }
                if (Applications.isRightHandMode) {
                    dealLeft(p_left_default.x, p_left_default.y);
                } else if (Applications.isLimitedHigh) {
                    dealLeft(p_left_default.x, p_left_default.y);
                } else {
                    dealLeft(p_left_default.x, p_left.y);
                }
                this._id_left = -1;
                break;
                //break;
            case 261:
                x = (int) event.getX(pointerId);
                y = (int) event.getY(pointerId);
                if (x < this.p_left_left || x > this.p_left_right || y < this.p_left_up || y > this.p_left_bottom) {
                    if (x >= p_right_left && x <= p_right_right && y >= p_right_up && y <= p_right_bottom) {
                        this._id_right = pointerId;
                        dealRight(x, y);
                        break;
                    }
                }
                this._id_left = pointerId;
                dealLeft(x, y);
                break;
                //break;
            case 262:
                if (this._id_left != pointerId) {
                    if (this._id_right == pointerId) {
                        if (!Applications.isRightHandMode) {
                            this.onRudderListener.OnRightRudderUp();
                            dealRight(p_right_default.x, p_right_default.y);
                        } else if (Applications.isLimitedHigh) {
                            dealRight(p_right_default.x, p_right_default.y);
                        } else {
                            dealRight(p_right_default.x, p_right.y);
                        }
                        this._id_right = -1;
                        break;
                    }
                }
                if (Applications.isRightHandMode) {
                    dealLeft(p_left_default.x, p_left_default.y);
                } else if (Applications.isLimitedHigh) {
                    dealLeft(p_left_default.x, p_left_default.y);
                } else {
                    dealLeft(p_left_default.x, p_left.y);
                }
                this._id_left = -1;
                break;
                //break;
        }
        return USE_SQUARE_RUDDER;
    }

    public void setLeftPointPosition(int x, int y) {
        doLeftSquare(x, y);
    }

    private void doLeftSquare(int x, int y) {
        if (x >= RudderCoordinate.getLeftLeft() && x <= RudderCoordinate.getLeftRight() && y >= RudderCoordinate.getLeftTop() && y <= RudderCoordinate.getLeftBottom()) {
            p_left.x = x;
            p_left.y = y;
        } else if (x >= RudderCoordinate.getLeftLeft() && x <= RudderCoordinate.getLeftRight()) {
            p_left.x = x;
            if (y < RudderCoordinate.getLeftTop()) {
                p_left.y = RudderCoordinate.getLeftTop();
            }
            if (y > RudderCoordinate.getLeftBottom()) {
                p_left.y = RudderCoordinate.getLeftBottom();
            }
        } else if (y < RudderCoordinate.getLeftTop() || y > RudderCoordinate.getLeftBottom()) {
            if (x < RudderCoordinate.getLeftLeft()) {
                p_left.x = RudderCoordinate.getLeftLeft();
            }
            if (x > RudderCoordinate.getLeftRight()) {
                p_left.x = RudderCoordinate.getLeftRight();
            }
            if (y < RudderCoordinate.getLeftTop()) {
                p_left.y = RudderCoordinate.getLeftTop();
            }
            if (y > RudderCoordinate.getLeftBottom()) {
                p_left.y = RudderCoordinate.getLeftBottom();
            }
        } else {
            p_left.y = y;
            if (x < RudderCoordinate.getLeftLeft()) {
                p_left.x = RudderCoordinate.getLeftLeft();
            }
            if (x > RudderCoordinate.getLeftRight()) {
                p_left.x = RudderCoordinate.getLeftRight();
            }
        }
    }

    private void dealLeft(int x, int y) {
        if (!Applications.isSensorOn || !Applications.isRightHandMode) {
            setLeftPointPosition(x, y);
            if (Applications.isRightHandMode) {
                int leftAndRight = RudderUtils.GetLR(p_left.x, this.l_left, p_left_default.x, this.FlyMaxSpeed);
                int frontAndBack = RudderUtils.GetUpDown(p_left.y, this.l_left, p_left_default.y, this.FlyMaxSpeed);
                if (p_left.x < p_left_default.x) {
                    leftAndRight = 128 - leftAndRight;
                } else {
                    leftAndRight += TransportMediator.FLAG_KEY_MEDIA_NEXT;
                }
                if (p_left.y < p_left_default.y) {
                    frontAndBack += TransportMediator.FLAG_KEY_MEDIA_NEXT;
                } else {
                    frontAndBack = 128 - frontAndBack;
                }
                this.onRudderListener.OnLeftRudder(leftAndRight, frontAndBack);
                return;
            }
            int xLocation;
            int xDistance = p_left.x - p_left_default.x;
            if (Math.abs(xDistance) < this.mDistance) {
                xLocation = p_left_default.x;
            } else if (xDistance - this.mDistance < 0) {
                xLocation = p_left.x + this.mDistance;
            } else {
                xLocation = p_left.x - this.mDistance;
            }
            int rotate = RudderUtils.GetLR(xLocation, this.l_left - this.mDistance, p_left_default.x, this.FlyMaxRotate);
            int power = RudderUtils.GetUpDown(p_left.y, this.l_left * 2, p_left_start_y, this.FlyMaxPower);
            if (p_left.x < p_left_default.x) {
                rotate = 128 - rotate;
            } else {
                rotate += TransportMediator.FLAG_KEY_MEDIA_NEXT;
            }
            this.onRudderListener.OnLeftRudder(rotate, power);
        }
    }

    public void setRightPointPosition(int x, int y) {
        doRightSquare(x, y);
    }

    private void doRightSquare(int x, int y) {
        if (x >= RudderCoordinate.getRightLeft() && x <= RudderCoordinate.getRightRight() && y >= RudderCoordinate.getRightTop() && y <= RudderCoordinate.getRightBottom()) {
            p_right.x = x;
            p_right.y = y;
        } else if (x >= RudderCoordinate.getRightLeft() && x <= RudderCoordinate.getRightRight()) {
            p_right.x = x;
            if (y < RudderCoordinate.getRightTop()) {
                p_right.y = RudderCoordinate.getRightTop();
            }
            if (y > RudderCoordinate.getRightBottom()) {
                p_right.y = RudderCoordinate.getRightBottom();
            }
        } else if (y < RudderCoordinate.getRightTop() || y > RudderCoordinate.getRightBottom()) {
            if (x < RudderCoordinate.getRightLeft()) {
                p_right.x = RudderCoordinate.getRightLeft();
            }
            if (x > RudderCoordinate.getRightRight()) {
                p_right.x = RudderCoordinate.getRightRight();
            }
            if (y < RudderCoordinate.getRightTop()) {
                p_right.y = RudderCoordinate.getRightTop();
            }
            if (y > RudderCoordinate.getRightBottom()) {
                p_right.y = RudderCoordinate.getRightBottom();
            }
        } else {
            p_right.y = y;
            if (x < RudderCoordinate.getRightLeft()) {
                p_right.x = RudderCoordinate.getRightLeft();
            }
            if (x > RudderCoordinate.getRightRight()) {
                p_right.x = RudderCoordinate.getRightRight();
            }
        }
    }

    private void dealRight(int x, int y) {
        if (!Applications.isSensorOn || Applications.isRightHandMode) {
            setRightPointPosition(x, y);
            boolean isHighSpeed;
            if (Applications.isRightHandMode) {
                int xLocation;
                isHighSpeed = false;
                int xDistance = p_right.x - p_right_default.x;
                if (Math.abs(xDistance) < this.mDistance) {
                    xLocation = p_right_default.x;
                } else if (xDistance - this.mDistance < 0) {
                    xLocation = p_right.x + this.mDistance;
                } else {
                    xLocation = p_right.x - this.mDistance;
                }
                int rotate = RudderUtils.GetLR(xLocation, this.l_right - this.mDistance, p_right_default.x, this.FlyMaxRotate);
                int power = RudderUtils.GetUpDown(p_right.y, this.l_right * 2, p_right_start_y, this.FlyMaxPower);
                if (p_right.x < p_right_default.x) {
                    rotate = 128 - rotate;
                } else {
                    rotate += TransportMediator.FLAG_KEY_MEDIA_NEXT;
                }
                if (this.RightMaxRight == TransportMediator.KEYCODE_MEDIA_PAUSE && ((double) RudderUtils.getLineLength(p_right.x, p_right.y, p_right_default.x, p_right_default.y)) >= 0.7d * ((double) this.l_right)) {
                    isHighSpeed = USE_SQUARE_RUDDER;
                }
                this.onRudderListener.OnRightRudder(rotate, power, isHighSpeed);
                return;
            }
            isHighSpeed = false;
            int leftAndRight = RudderUtils.GetLR(p_right.x, this.l_right, p_right_default.x, this.FlyMaxSpeed);
            int frontAndBack = RudderUtils.GetUpDown(p_right.y, this.l_right, p_right_default.y, this.FlyMaxSpeed);
            if (p_right.x < p_right_default.x) {
                leftAndRight = 128 - leftAndRight;
            } else {
                leftAndRight += TransportMediator.FLAG_KEY_MEDIA_NEXT;
            }
            if (p_right.y < p_right_default.y) {
                frontAndBack += TransportMediator.FLAG_KEY_MEDIA_NEXT;
            } else {
                frontAndBack = 128 - frontAndBack;
            }
            if (this.RightMaxRight == TransportMediator.KEYCODE_MEDIA_PAUSE && ((double) RudderUtils.getLineLength(p_right.x, p_right.y, p_right_default.x, p_right_default.y)) >= 0.7d * ((double) this.l_right)) {
                isHighSpeed = USE_SQUARE_RUDDER;
            }
            this.onRudderListener.OnRightRudder(leftAndRight, frontAndBack, isHighSpeed);
        }
    }
}
