package com.lewei.multiple.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.CursorAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.lewei.multiple.fydrone.R;
import com.lewei.multiple.photoview.PhotoView;
import com.lewei.multiple.photoview.PhotoViewAttacher.OnPhotoTapListener;
import com.lewei.multiple.utils.DateUtils;
import com.lewei.multiple.view.ViewPagerFixed;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.FailReason.FailType;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ImagePagerActivity extends Activity {
    private static String TAG;
    private OnClickListener clickListener;
    private int currItem;
    Bitmap defaultbmp;
    private Handler handler;
    protected ImageLoader imageLoader;
    private ImageView iv_Actionbar_Bottom_Delete;
    private ImageView iv_Actionbar_Bottom_Info;
    private ImageView iv_ImagePager_Actionbar_Back;
    private ImageView iv_ImagePager_Actionbar_More;
    private ImageView iv_ImagePager_Actionbar_Photo;
    private LinearLayout layout_Actionbar_Bottom;
    private SimpleAdapter mAdapter;
    private Timer mTimer;
    private ActionbarTimerTask mTimerTask;
    private ViewPagerFixed mViewPager;
    SamplePagerAdapter mViewPagerAdapter;
    DisplayImageOptions options;
    private PopupWindow pWindow;
    private int screenHeight;
    private int screenWidth;
    private TextView txtcountTextView;

    /* renamed from: com.lewei.multiple.main.ImagePagerActivity.1 */
    class C00661 implements OnClickListener {
        C00661() {
        }

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_ImagePager_Actionbar_Back:
                case R.id.iv_ImagePager_Actionbar_Photo:
                    ImagePagerActivity.this.onBackPressed();
                case R.id.iv_ImagePager_Actionbar_More:
                    ImagePagerActivity.this.layout_Actionbar_Bottom.setVisibility(View.VISIBLE);
                    ImagePagerActivity.this.startActionbarTimer();
                case R.id.iv_Actionbar_Bottom_Info:
                    if (PlaybackActivity.photoList.size() > 0) {
                        ImagePagerActivity.this.initPopupPhotoinfo();
                        ImagePagerActivity.this.popupWindowPhotoinfo();
                        return;
                    }
                    ImagePagerActivity.this.onBackPressed();
                case R.id.iv_Actionbar_Bottom_Delete:
                    if (PlaybackActivity.photoList.size() > 0) {
                        ImagePagerActivity.this.deleteFileOper(ImagePagerActivity.this.currItem);
                    } else {
                        ImagePagerActivity.this.onBackPressed();
                    }
                default:
            }
        }
    }

    /* renamed from: com.lewei.multiple.main.ImagePagerActivity.2 */
    class C00672 implements Callback {
        C00672() {
        }

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case DialogFragment.STYLE_NORMAL /*0*/:
                    ImagePagerActivity.this.layout_Actionbar_Bottom.setVisibility(View.INVISIBLE);
                    break;
            }
            return true;
        }
    }

    /* renamed from: com.lewei.multiple.main.ImagePagerActivity.4 */
    class C00684 implements OnDismissListener {
        C00684() {
        }

        public void onDismiss() {
        }
    }

    private class ActionbarTimerTask extends TimerTask {
        private ActionbarTimerTask() {
        }

        public void run() {
            ImagePagerActivity.this.handler.sendEmptyMessage(0);
        }
    }

    /* renamed from: com.lewei.multiple.main.ImagePagerActivity.3 */
    class C01323 implements OnPageChangeListener {
        C01323() {
        }

        public void onPageSelected(int position) {
            ImagePagerActivity.this.txtcountTextView.setText(new StringBuilder(String.valueOf(String.valueOf(position + 1))).append("/").append(String.valueOf(PlaybackActivity.photoList.size())).toString());
            ImagePagerActivity.this.currItem = position;
        }

        public void onPageScrollStateChanged(int state) {
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
    }
    public  static /* synthetic */ int[] f7x7cadcdf4; //findme further down in c01351
     class SamplePagerAdapter extends PagerAdapter {
        private LayoutInflater inflater;

        /* renamed from: com.lewei.multiple.main.ImagePagerActivity.SamplePagerAdapter.2 */
        class C01332 implements OnPhotoTapListener {
            C01332() {
            }

            public void onPhotoTap(View view, float x, float y) {
                ImagePagerActivity.this.layout_Actionbar_Bottom.setVisibility(View.VISIBLE);
                ImagePagerActivity.this.startActionbarTimer();
            }
        }

        /* renamed from: com.lewei.multiple.main.ImagePagerActivity.SamplePagerAdapter.1 */
         class C01351 extends SimpleImageLoadingListener {
            //public  static /* synthetic */ int[] f7x7cadcdf4; //findme was static
            private final /* synthetic */ ProgressBar val$spinner;

            C01351(ProgressBar progressBar) {
                this.val$spinner = progressBar;
            }

              /* synthetic */ int[] m6x7cadcdf4() { //findme was static
                int[] iArr = f7x7cadcdf4;
                if (iArr == null) {
                    iArr = new int[FailType.values().length];
                    try {
                        iArr[FailType.DECODING_ERROR.ordinal()] = 2;
                    } catch (NoSuchFieldError e) {
                    }
                    try {
                        iArr[FailType.IO_ERROR.ordinal()] = 1;
                    } catch (NoSuchFieldError e2) {
                    }
                    try {
                        iArr[FailType.NETWORK_DENIED.ordinal()] = 3;
                    } catch (NoSuchFieldError e3) {
                    }
                    try {
                        iArr[FailType.OUT_OF_MEMORY.ordinal()] = 4;
                    } catch (NoSuchFieldError e4) {
                    }
                    try {
                        iArr[FailType.UNKNOWN.ordinal()] = 5;
                    } catch (NoSuchFieldError e5) {
                    }
                    f7x7cadcdf4 = iArr;
                }
                return iArr;
            }

            public void onLoadingStarted(String imageUri, View view) {
                this.val$spinner.setVisibility(View.VISIBLE);
            }

            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                String message = null;
                switch (m6x7cadcdf4()[failReason.getType().ordinal()]) {
                    case CursorAdapter.FLAG_AUTO_REQUERY /*1*/:
                        message = "Input/Output error";
                        break;
                    case CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER /*2*/:
                        message = "Image can't be decoded";
                        break;
                    case 3 /* FragmentManagerImpl.ANIM_STYLE_CLOSE_ENTER 3*/:
                        message = "Downloads are denied";
                        break;
                    case TransportMediator.FLAG_KEY_MEDIA_PLAY /*4*/:
                        message = "Out Of Memory error";
                        break;
                    case 5 /*FragmentManagerImpl.ANIM_STYLE_FADE_ENTER 5*/:
                        message = "Unknown error";
                        break;
                }
                Toast.makeText(ImagePagerActivity.this, message, Toast.LENGTH_SHORT).show();
                this.val$spinner.setVisibility(View.GONE);
            }

            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                this.val$spinner.setVisibility(View.GONE);
            }
        }

        SamplePagerAdapter() {
            this.inflater = ImagePagerActivity.this.getLayoutInflater();
        }

        public int getCount() {
            return PlaybackActivity.photoList.size();
        }

        public int getItemPosition(Object object) {
            return -2;
        }

        public View instantiateItem(ViewGroup container, int position) {
            View imageLayout = this.inflater.inflate(R.layout.item_imagepager, container, false);
            PhotoView photoView = (PhotoView) imageLayout.findViewById(R.id.image);
            ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
            Uri uri = Uri.fromFile(new File((String) PlaybackActivity.photoList.get(position)));
            ImagePagerActivity.this.imageLoader.displayImage(uri.getScheme() + "://" + uri.getAuthority() + uri.getPath(), photoView, ImagePagerActivity.this.options, new C01351(spinner));
            photoView.setOnPhotoTapListener(new C01332());
            ((ViewPager) container).addView(imageLayout, 0);
            return imageLayout;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    public ImagePagerActivity() {
        this.imageLoader = ImageLoader.getInstance();
        this.currItem = 0;
        this.clickListener = new C00661();
        this.handler = new Handler(new C00672());
    }

    static {
        TAG = "ImagePagerActivity";
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagepager);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        this.screenWidth = dm.widthPixels;
        this.screenHeight = dm.heightPixels;
        this.defaultbmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        this.imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        this.options = new Builder().showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).resetViewBeforeLoading().cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Config.RGB_565).displayer(new FadeInBitmapDisplayer(300)).build();
        this.mTimer = new Timer(true);
        widgetInit();
    }

    private void widgetInit() {
        Log.e(TAG, "images size:" + PlaybackActivity.photoList.size());
        this.mViewPager = (ViewPagerFixed) findViewById(R.id.viewpager);
        this.mViewPagerAdapter = new SamplePagerAdapter();
        this.mViewPager.setAdapter(this.mViewPagerAdapter);
        int item = getIntent().getIntExtra("position", 0);
        this.mViewPager.setCurrentItem(item);
        this.currItem = item;
        this.layout_Actionbar_Bottom = (LinearLayout) findViewById(R.id.layout_Actionbar_Bottom);
        this.iv_ImagePager_Actionbar_Back = (ImageView) findViewById(R.id.iv_ImagePager_Actionbar_Back);
        this.iv_ImagePager_Actionbar_Back.setOnClickListener(this.clickListener);
        this.iv_ImagePager_Actionbar_Photo = (ImageView) findViewById(R.id.iv_ImagePager_Actionbar_Photo);
        this.iv_ImagePager_Actionbar_Photo.setOnClickListener(this.clickListener);
        this.iv_ImagePager_Actionbar_More = (ImageView) findViewById(R.id.iv_ImagePager_Actionbar_More);
        this.iv_ImagePager_Actionbar_More.setOnClickListener(this.clickListener);
        this.iv_Actionbar_Bottom_Info = (ImageView) findViewById(R.id.iv_Actionbar_Bottom_Info);
        this.iv_Actionbar_Bottom_Info.setOnClickListener(this.clickListener);
        this.iv_Actionbar_Bottom_Delete = (ImageView) findViewById(R.id.iv_Actionbar_Bottom_Delete);
        this.iv_Actionbar_Bottom_Delete.setOnClickListener(this.clickListener);
        this.txtcountTextView = (TextView) findViewById(R.id.txt_imagecount);
        this.txtcountTextView.setText((item + 1) + "/" + String.valueOf(PlaybackActivity.photoList.size()));
        this.mViewPager.setOnPageChangeListener(new C01323());
    }

    @SuppressLint({"InflateParams"})
    private void initPopupPhotoinfo() {
        View mView = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_photo_info, null);
        this.pWindow = new PopupWindow(mView, (this.screenWidth * 4) / 5, (this.screenHeight * 3) / 5);
        ListView mListView = (ListView) mView.findViewById(R.id.listview_Popup_Photoinfo);
        this.mAdapter = new SimpleAdapter(this, getData(), R.layout.list_item_photoinfo, new String[]{"title", "info"}, new int[]{R.id.title, R.id.info});
        mListView.setAdapter(this.mAdapter);
    }

    private void popupWindowPhotoinfo() {
        this.pWindow.setFocusable(true);
        this.pWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_background_landscape));
        this.pWindow.setOutsideTouchable(true);
        this.pWindow.showAtLocation(this.mViewPager, 17, 0, 0);
        this.pWindow.setOnDismissListener(new C00684());
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList();
        Map<String, Object> photo = new HashMap();
        int index = ((String) PlaybackActivity.photoList.get(this.currItem)).lastIndexOf("/");
        String photoName = ((String) PlaybackActivity.photoList.get(this.currItem)).substring(index + 1);
        String str = "title";
        Random r = new Random();
        photo.put(new Integer(r.nextInt(100000)).toString(), getString(R.string.str_imagepager_filename)); //findme !!!!!! randome hashing was r26
        photo.put("info", photoName);
        list.add(photo);
        photo = new HashMap();
        String photoRoute = ((String) PlaybackActivity.photoList.get(this.currItem)).substring(0, index);
        str = "title";
        photo.put(new Integer(r.nextInt(100000)).toString(), getString(R.string.str_imagepager_filepath));
        photo.put("info", photoRoute);
        list.add(photo);
        photo = new HashMap();
        long timeLastChange = new File((String) PlaybackActivity.photoList.get(this.currItem)).lastModified();
        Log.e(TAG, "time:" + timeLastChange);
        String timeDate = DateUtils.dateToStrLong(new Date(timeLastChange));
        str = "title";
        photo.put(new Integer(r.nextInt(100000)).toString(), getString(R.string.str_imagepager_time));
        photo.put("info", timeDate);
        list.add(photo);
        photo = new HashMap();
        float kbSize = ((float) new File((String) PlaybackActivity.photoList.get(this.currItem)).length()) / 1024.0f;
        double d;
        if (kbSize < 800.0f) {
            d = (double) kbSize;
            String kbSizeStr = new DecimalFormat(".00").format(d);
            str = "title";
            photo.put(new Integer(r.nextInt(100000)).toString(), getString(R.string.str_imagepager_filesize));
            photo.put("info", new StringBuilder(String.valueOf(kbSizeStr)).append(" KB").toString());
        } else {
            d = (double) (kbSize / 1024.0f);
            String mbSizeStr = new DecimalFormat(".00").format(d);
            str = "title";
            photo.put(new Integer(r.nextInt(100000)).toString(), getString(R.string.str_imagepager_filesize));
            photo.put("info", new StringBuilder(String.valueOf(mbSizeStr)).append(" MB").toString());
        }
        list.add(photo);
        photo = new HashMap();
        new Options().inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile((String) PlaybackActivity.photoList.get(this.currItem));
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        str = "title";
        photo.put(new Integer(r.nextInt(100000)).toString(), getString(R.string.str_imagepager_resolution));
        photo.put("info", new StringBuilder(String.valueOf(bmpWidth)).append(" \u00d7 ").append(bmpHeight).toString());
        list.add(photo);
        photo = new HashMap();
        int typeIndex = ((String) PlaybackActivity.photoList.get(this.currItem)).lastIndexOf(".");
        String typeString = ((String) PlaybackActivity.photoList.get(this.currItem)).substring(typeIndex);
        str = "title";
        photo.put(new Integer(r.nextInt(100000)).toString(), getString(R.string.str_imagepager_filetype));
        photo.put("info", "MJEPG/" + typeString);
        list.add(photo);
        return list;
    }

    private void deleteFileOper(int position) {
        File file = new File((String) PlaybackActivity.photoList.get(position));
        if (file.exists()) {
            file.delete();
            PlaybackActivity.photoList.remove(position);
            this.mViewPagerAdapter.notifyDataSetChanged();
            Intent intent = new Intent(PlaybackActivity.ACTION_DELETE_PHOTO);
            intent.putExtra("deletePosition", 0);
            sendBroadcast(intent);
            this.txtcountTextView.setText(new StringBuilder(String.valueOf(this.currItem + 1)).append("/").append(String.valueOf(PlaybackActivity.photoList.size())).toString());
        }
    }

    private void startActionbarTimer() {
        if (!(this.mTimer == null || this.mTimerTask == null)) {
            this.mTimerTask.cancel();
        }
        this.mTimerTask = new ActionbarTimerTask();
        this.mTimer.schedule(this.mTimerTask, 2500);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 82) {
            this.layout_Actionbar_Bottom.setVisibility(View.VISIBLE);
            startActionbarTimer();
        }
        return super.onKeyDown(keyCode, event);
    }
}
