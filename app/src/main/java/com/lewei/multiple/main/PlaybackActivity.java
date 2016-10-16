package com.lewei.multiple.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.lewei.multiple.adapter.PhotosAdapter;
import com.lewei.multiple.adapter.VideosAdapter;
import com.lewei.multiple.fydrone.R;
import com.lewei.multiple.utils.PathConfig;
import com.lewei.multiple.utils.PathConfig.SdcardSelector;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaybackActivity extends Activity {
    public static final String ACTION_DELETE_PHOTO = "com.action.send.ACTION_DELETE_PHOTO";
    private static final int SCAN_OK = 0;
    private static final String TAG = "PlaybackActivity";
    public static final int TYPE_NULL = 0;
    public static final int TYPE_PHOTO = 1;
    public static final int TYPE_VIDEO = 2;
    public static List<String> photoList;
    private BroadcastReceiver broadcastReceivers;
    private OnClickListener clickListener;
    private int currSdcardItem;
    private GridView gd_Photos;
    private Handler handler;
    private ImageView iv_Actionbar_Back;
    private ImageView iv_Actionbar_More;
    private RelativeLayout layout_Actionbar;
    private int layout_Actionbar_Height;
    private SimpleAdapter mAdapter;
    private int mMediaType;
    private PhotosAdapter mPhotosAdapter;
    private ProgressDialog mProgressDialog;
    private VideosAdapter mVideosAdapter;
    private PopupWindow pWindowMenu;
    private int screenWidth;
    private TextView txt_Playback_Actionbar;
    private List<String> videoList;

    /* renamed from: com.lewei.multiple.main.PlaybackActivity.10 */
    class AnonymousClass10 implements DialogInterface.OnClickListener {
        private final /* synthetic */ int val$mType;
        private final /* synthetic */ int val$position;

        AnonymousClass10(int i, int i2) {
            this.val$mType = i;
            this.val$position = i2;
        }

        public void onClick(DialogInterface dialog, int which) {
            if (this.val$mType == PlaybackActivity.TYPE_PHOTO) {
                new File((String) PlaybackActivity.photoList.get(this.val$position)).delete();
                PlaybackActivity.photoList.remove(this.val$position);
                PlaybackActivity.this.mPhotosAdapter.notifyDataSetChanged();
            } else if (this.val$mType == PlaybackActivity.TYPE_VIDEO) {
                new File((String) PlaybackActivity.this.videoList.get(this.val$position)).delete();
                PlaybackActivity.this.videoList.remove(this.val$position);
                PlaybackActivity.this.mVideosAdapter.notifyDataSetChanged();
            }
        }
    }

    /* renamed from: com.lewei.multiple.main.PlaybackActivity.1 */
    class C00761 implements OnClickListener {
        C00761() {
        }

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_playback_actionbar_back:
                    PlaybackActivity.this.onBackPressed();
                default:
            }
        }
    }

    /* renamed from: com.lewei.multiple.main.PlaybackActivity.2 */
    class C00772 implements Callback {
        C00772() {
        }

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case PlaybackActivity.TYPE_NULL /*0*/:
                    if (PlaybackActivity.this.mProgressDialog != null && PlaybackActivity.this.mProgressDialog.isShowing()) {
                        PlaybackActivity.this.mProgressDialog.dismiss();
                    }
                    if (PlaybackActivity.this.mMediaType != PlaybackActivity.TYPE_PHOTO) {
                        PlaybackActivity.this.mVideosAdapter = new VideosAdapter(PlaybackActivity.this, PlaybackActivity.this.videoList, PlaybackActivity.this.gd_Photos);
                        PlaybackActivity.this.gd_Photos.setAdapter(PlaybackActivity.this.mVideosAdapter);
                        break;
                    }
                    PlaybackActivity.this.mPhotosAdapter = new PhotosAdapter(PlaybackActivity.this, PlaybackActivity.photoList, PlaybackActivity.this.gd_Photos);
                    PlaybackActivity.this.gd_Photos.setAdapter(PlaybackActivity.this.mPhotosAdapter);
                    break;
            }
            return true;
        }
    }

    /* renamed from: com.lewei.multiple.main.PlaybackActivity.3 */
    class C00783 extends BroadcastReceiver {
        C00783() {
        }

        public void onReceive(Context arg0, Intent arg1) {
            if (arg1.getAction().equals(PlaybackActivity.ACTION_DELETE_PHOTO)) {
                Log.e(PlaybackActivity.TAG, "get receiver");
                PlaybackActivity.this.mPhotosAdapter.notifyDataSetChanged();
            }
        }
    }

    /* renamed from: com.lewei.multiple.main.PlaybackActivity.4 */
    class C00794 implements OnItemClickListener {
        C00794() {
        }

        public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
            Intent intent;
            if (PlaybackActivity.this.mMediaType == PlaybackActivity.TYPE_PHOTO) {
                intent = new Intent(PlaybackActivity.this, ImagePagerActivity.class);
                intent.putExtra("position", position);
                PlaybackActivity.this.startActivity(intent);
                return;
            }
            Uri uri = Uri.fromFile(new File((String) PlaybackActivity.this.videoList.get(position)));
            intent = new Intent("android.intent.action.VIEW");
            Log.v("URI:::::::::", uri.toString());
            intent.setDataAndType(uri, "video/*");
            PlaybackActivity.this.startActivity(intent);
        }
    }

    /* renamed from: com.lewei.multiple.main.PlaybackActivity.5 */
    class C00805 implements OnItemLongClickListener {
        C00805() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
            PlaybackActivity.this.dialogDelete(PlaybackActivity.this.mMediaType, position);
            return true;
        }
    }

    /* renamed from: com.lewei.multiple.main.PlaybackActivity.6 */
    class C00816 implements Runnable {
        C00816() {
        }

        public void run() {
            PlaybackActivity.this.loadImage();
        }
    }

    /* renamed from: com.lewei.multiple.main.PlaybackActivity.7 */
    class C00827 implements Runnable {
        C00827() {
        }

        public void run() {
            PlaybackActivity.this.layout_Actionbar_Height = PlaybackActivity.this.layout_Actionbar.getHeight();
        }
    }

    /* renamed from: com.lewei.multiple.main.PlaybackActivity.8 */
    class C00838 implements OnItemClickListener {
        C00838() {
        }

        public void onItemClick(AdapterView<?> adapterView, View arg1, int item, long arg3) {
            switch (item) {
                case PlaybackActivity.TYPE_NULL /*0*/:
                    if (PlaybackActivity.this.currSdcardItem != 0) {
                        PathConfig.sdcardItem = SdcardSelector.BUILT_IN;
                        PlaybackActivity.this.loadImage();
                    }
                    PlaybackActivity.this.pWindowMenu.dismiss();
                case PlaybackActivity.TYPE_PHOTO /*1*/:
                    if (PlaybackActivity.this.currSdcardItem != PlaybackActivity.TYPE_PHOTO) {
                        PathConfig.sdcardItem = SdcardSelector.EXTERNAL;
                        PlaybackActivity.this.loadImage();
                    }
                    PlaybackActivity.this.pWindowMenu.dismiss();
                default:
            }
        }
    }

    /* renamed from: com.lewei.multiple.main.PlaybackActivity.9 */
    class C00849 implements Runnable {
        private final /* synthetic */ File val$photoPath;
        private final /* synthetic */ File val$videoPath;

        C00849(File file, File file2) {
            this.val$photoPath = file;
            this.val$videoPath = file2;
        }

        public void run() {
            PlaybackActivity.photoList.clear();
            PlaybackActivity.photoList = PathConfig.getImagesList(this.val$photoPath);
            PlaybackActivity.this.videoList.clear();
            PlaybackActivity.this.videoList = PathConfig.getVideosList(this.val$videoPath);
            Log.e(PlaybackActivity.TAG, "images size:" + PlaybackActivity.photoList.size() + " videos size:" + PlaybackActivity.this.videoList.size());
            PlaybackActivity.this.handler.sendEmptyMessage(PlaybackActivity.TYPE_NULL);
        }
    }

    public class MyPagerAdapter extends PagerAdapter {
        public List<View> mListViews;

        public MyPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) this.mListViews.get(arg1));
        }

        public void finishUpdate(View arg0) {
        }

        public int getCount() {
            return this.mListViews.size();
        }

        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView((View) this.mListViews.get(arg1), PlaybackActivity.TYPE_NULL);
            return this.mListViews.get(arg1);
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        public Parcelable saveState() {
            return null;
        }

        public void startUpdate(View arg0) {
        }
    }

    public PlaybackActivity() {
        this.currSdcardItem = TYPE_NULL;
        this.videoList = new ArrayList();
        this.mMediaType = TYPE_PHOTO;
        this.clickListener = new C00761();
        this.handler = new Handler(new C00772());
        this.broadcastReceivers = new C00783();
    }

    static {
        photoList = new ArrayList();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        this.screenWidth = getResources().getDisplayMetrics().widthPixels;
        this.mMediaType = getIntent().getIntExtra("MediaType", TYPE_PHOTO);
        initImageView();
        InitViewPager();
    }

    protected void onResume() {
        super.onResume();
        if (this.mPhotosAdapter != null) {
            this.mPhotosAdapter.notifyDataSetChanged();
        }
        register();
    }

    private void InitViewPager() {
        this.gd_Photos = (GridView) findViewById(R.id.gd_photos);
        this.gd_Photos.setOnItemClickListener(new C00794());
        this.gd_Photos.setOnItemLongClickListener(new C00805());
    }

    private void initImageView() {
        this.txt_Playback_Actionbar = (TextView) findViewById(R.id.txt_Playback_Actionbar);
        if (this.mMediaType == TYPE_PHOTO) {
            this.txt_Playback_Actionbar.setText(getString(R.string.tv_Playback_Photos));
        } else {
            this.txt_Playback_Actionbar.setText(getString(R.string.tv_Playback_Videos));
        }
        this.txt_Playback_Actionbar.post(new C00816());
        this.layout_Actionbar = (RelativeLayout) findViewById(R.id.layout_actionbar);
        this.iv_Actionbar_Back = (ImageView) findViewById(R.id.iv_playback_actionbar_back);
        this.iv_Actionbar_Back.setOnClickListener(this.clickListener);
        this.iv_Actionbar_More = (ImageView) findViewById(R.id.iv_playback_actionbar_more);
        this.iv_Actionbar_More.setOnClickListener(this.clickListener);
        this.iv_Actionbar_More.setVisibility(4);
        this.layout_Actionbar.post(new C00827());
    }

    @SuppressLint({"InflateParams"})
    private void initPopupWndMenu() {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popup_playback_actionbar_more, null);
        this.pWindowMenu = new PopupWindow(view, this.screenWidth / 3, -2);
        ListView mListView = (ListView) view.findViewById(R.id.listview_Popup_actionbar_more);
        List data = getData();
        String[] strArr = new String[TYPE_VIDEO];
        strArr[TYPE_NULL] = "icon";
        strArr[TYPE_PHOTO] = "info";
        this.mAdapter = new SimpleAdapter(this, data, R.layout.list_item_playback_menu, strArr, new int[]{R.id.icon, R.id.info});
        mListView.setAdapter(this.mAdapter);
        mListView.setOnItemClickListener(new C00838());
    }

    @SuppressLint({"RtlHardcoded"})
    private void showPopupWndMenu() {
        this.pWindowMenu.setFocusable(true);
        this.pWindowMenu.setBackgroundDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.color.transparent)));
        this.pWindowMenu.setOutsideTouchable(true);
        this.pWindowMenu.showAtLocation(this.layout_Actionbar, 53, TYPE_NULL, this.layout_Actionbar_Height);
    }

    private void loadImage() {
        this.mProgressDialog = ProgressDialog.show(this, null, "Loading...");
        if (PathConfig.sdcardItem != SdcardSelector.EXTERNAL) {
            this.currSdcardItem = TYPE_NULL;
        } else if (PathConfig.getRootPath() == null) {
            Toast.makeText(this, R.string.str_playback_notfindexcard, TYPE_PHOTO).show();
            PathConfig.sdcardItem = SdcardSelector.BUILT_IN;
            this.currSdcardItem = TYPE_NULL;
        } else {
            PathConfig.sdcardItem = SdcardSelector.EXTERNAL;
            this.currSdcardItem = TYPE_PHOTO;
        }
        getPhotoVideoList(new File(PathConfig.getRootPath() + PathConfig.PHOTOS_PATH), new File(PathConfig.getRootPath() + PathConfig.VIDEOS_PATH));
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList();
        Map<String, Object> photo = new HashMap();
        photo.put("icon", Integer.valueOf(R.drawable.playback_menu_phone));
        photo.put("info", getString(R.string.tv_Playback_BuiltinSdcard));
        list.add(photo);
        photo = new HashMap();
        photo.put("icon", Integer.valueOf(R.drawable.playback_menu_sdcard));
        photo.put("info", getString(R.string.tv_Playback_ExternalSdcard));
        list.add(photo);
        return list;
    }

    private void getPhotoVideoList(File photoPath, File videoPath) {
        new Thread(new C00849(photoPath, videoPath)).start();
    }

    private void register() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DELETE_PHOTO);
        registerReceiver(this.broadcastReceivers, intentFilter);
    }

    private void unregister() {
        unregisterReceiver(this.broadcastReceivers);
    }

    private void dialogDelete(int mType, int position) {
        String msg = "";
        String title = getString(R.string.str_playback_warning);
        if (mType == TYPE_PHOTO) {
            msg = getString(R.string.str_playback_deletephoto);
        } else if (mType == TYPE_VIDEO) {
            msg = getString(R.string.str_playback_deletevideo);
        }
        Builder builder = new Builder(this);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton(R.string.str_playback_confirm, new AnonymousClass10(mType, position));
        builder.setNegativeButton(R.string.str_playback_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onStop() {
        super.onStop();
        unregister();
        Log.d(TAG, "on stop.. unregister");
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(this, SelectActivity.class));
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
