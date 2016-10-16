package com.lewei.multiple.main;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.media.TransportMediator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import com.lewei.lib.LeweiLib;
import com.lewei.lib.RecList;
import com.lewei.multiple.adapter.SdcardVideosAdapter;
import com.lewei.multiple.fydrone.R;
import com.lewei.multiple.utils.PathConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CardVideoActivity extends Activity {
    public static RecList[] videoLists;
    private OnClickListener clickListener;
    private DownloadTask downloadTask;
    private GetSdcardVideoList getSdcardVideoList;
    private Handler handler;
    private boolean isStop;
    private ImageView iv_SdcardVideo_Actionbar_Back;
    private SdcardVideosAdapter mAdapter;
    private GridView mGridView;
    private ProgressDialog mProgressDialog;
    private boolean running;
    private List<String> videoList;

    /* renamed from: com.lewei.multiple.main.CardVideoActivity.1 */
    class C00561 implements OnClickListener {
        C00561() {
        }

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_sdcardvideo_actionbar_back:
                    CardVideoActivity.this.onBackPressed();
                default:
            }
        }
    }

    /* renamed from: com.lewei.multiple.main.CardVideoActivity.2 */
    class C00572 implements Callback {
        C00572() {
        }

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case DialogFragment.STYLE_NORMAL /*0*/:
                    if (CardVideoActivity.this.mProgressDialog != null && CardVideoActivity.this.mProgressDialog.isShowing()) {
                        CardVideoActivity.this.mProgressDialog.setProgress(msg.arg1);
                        if (msg.arg1 == 100) {
                            CardVideoActivity.this.mProgressDialog.dismiss();
                            break;
                        }
                    }
                    break;
            }
            return true;
        }
    }

    /* renamed from: com.lewei.multiple.main.CardVideoActivity.3 */
    class C00583 implements OnItemClickListener {
        C00583() {
        }

        public void onItemClick(AdapterView<?> adapterView, View v, int position, long arg3) {
            CardVideoActivity.this.showDialogVideoOperation(position);
        }
    }

    /* renamed from: com.lewei.multiple.main.CardVideoActivity.4 */
    class C00594 implements OnItemLongClickListener {
        C00594() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View v, int position, long arg3) {
            return true;
        }
    }

    /* renamed from: com.lewei.multiple.main.CardVideoActivity.5 */
    class C00605 implements Runnable {
        C00605() {
        }

        public void run() {
            CardVideoActivity.this.running = true;
            CardVideoActivity.this.isStop = false;
            while (!CardVideoActivity.this.isStop) {
                try {
                    int file_size = LeweiLib.getDownloadFileSize();
                    int recv_size = LeweiLib.getDownloadRecvSize();
                    if (recv_size > 0) {
                        Message msg = new Message();
                        msg.what = 0;
                        msg.arg1 = (int) ((((float) recv_size) / ((float) file_size)) * 100.0f);
                        CardVideoActivity.this.handler.sendMessage(msg);
                    }
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    /* renamed from: com.lewei.multiple.main.CardVideoActivity.6 */
    class C00616 implements DialogInterface.OnClickListener {
        private final /* synthetic */ int val$position;

        C00616(int i) {
            this.val$position = i;
        }

        public void onClick(DialogInterface dialog, int which) {
            String path = (String) CardVideoActivity.this.videoList.get(this.val$position);
            String absPath = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().toString())).append(PathConfig.VIDEOS_PATH).append(path.substring(path.lastIndexOf(47))).toString();
            File file = new File(absPath);
            Intent intent;
            if (file == null || !file.exists()) {
                intent = new Intent(CardVideoActivity.this, ReplayActivity.class);
                intent.putExtra("position", this.val$position);
                CardVideoActivity.this.startActivity(intent);
            } else if (absPath.endsWith(".mp4")) {
                Uri uri = Uri.fromFile(file);
                intent = new Intent("android.intent.action.VIEW");
                Log.v("URI:::::::::", uri.toString());
                intent.setDataAndType(uri, "video/*");
                CardVideoActivity.this.startActivity(intent);
            }
        }
    }

    /* renamed from: com.lewei.multiple.main.CardVideoActivity.7 */
    class C00637 implements DialogInterface.OnClickListener {
        private final /* synthetic */ int val$position;

        /* renamed from: com.lewei.multiple.main.CardVideoActivity.7.1 */
        class C00621 implements OnKeyListener {
            C00621() {
            }

            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == 4) {
                    CardVideoActivity.this.downloadTask.cancel(true);
                    LeweiLib.LW93StopDownloadFile();
                    CardVideoActivity.this.mProgressDialog.dismiss();
                    CardVideoActivity.this.isStop = true;
                    CardVideoActivity.this.running = false;
                    Toast.makeText(CardVideoActivity.this, CardVideoActivity.this.getString(R.string.str_sdcardvideo_cancel_download), 1).show();
                }
                return false;
            }
        }

        C00637(int i) {
            this.val$position = i;
        }

        public void onClick(DialogInterface dialog, int which) {
            CardVideoActivity.this.downloadTask = new DownloadTask();
            CardVideoActivity.this.downloadTask.execute(new String[]{(String) CardVideoActivity.this.videoList.get(this.val$position)});
            CardVideoActivity.this.mProgressDialog = new ProgressDialog(CardVideoActivity.this);
            CardVideoActivity.this.mProgressDialog.setProgressStyle(1);
            CardVideoActivity.this.mProgressDialog.setTitle("downloading now...");
            CardVideoActivity.this.mProgressDialog.setMax(100);
            CardVideoActivity.this.mProgressDialog.setProgress(0);
            CardVideoActivity.this.mProgressDialog.setIndeterminate(false);
            CardVideoActivity.this.mProgressDialog.show();
            CardVideoActivity.this.mProgressDialog.setCanceledOnTouchOutside(false);
            CardVideoActivity.this.mProgressDialog.setOnKeyListener(new C00621());
            CardVideoActivity.this.startUpdateProgess();
        }
    }

    /* renamed from: com.lewei.multiple.main.CardVideoActivity.8 */
    class C00648 implements DialogInterface.OnClickListener {
        private final /* synthetic */ int val$position;

        C00648(int i) {
            this.val$position = i;
        }

        public void onClick(DialogInterface dialog, int which) {
            if (LeweiLib.LW93SendDeleteFile((String) CardVideoActivity.this.videoList.get(this.val$position)) == 0) {
                CardVideoActivity.this.videoList.remove(this.val$position);
                CardVideoActivity.this.mAdapter.notifyDataSetChanged();
                return;
            }
            Toast.makeText(CardVideoActivity.this, CardVideoActivity.this.getString(R.string.str_sdcardvideo_file_notexist), 1).show();
        }
    }

    /* renamed from: com.lewei.multiple.main.CardVideoActivity.9 */
    class C00659 implements OnDismissListener {
        private final /* synthetic */ Builder val$mDialogBuilder;

        C00659(Builder builder) {
            this.val$mDialogBuilder = builder;
        }

        public void onDismiss(DialogInterface dialog) {
            this.val$mDialogBuilder.create().dismiss();
        }
    }

    public class DownloadTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... params) {
            String folder = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().toString())).append(PathConfig.VIDEOS_PATH).toString();
            File folderFile = new File(folder);
            if (!folderFile.exists()) {
                folderFile.mkdirs();
            }
            return LeweiLib.LW93StartDownloadFile(folder, params[0], 1);
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            CardVideoActivity.this.mAdapter.notifyDataSetChanged();
            CardVideoActivity.this.mProgressDialog.dismiss();
            if (result != null) {
                Toast.makeText(CardVideoActivity.this, CardVideoActivity.this.getString(R.string.str_sdcardvideo_download_ok), 1).show();
                CardVideoActivity.this.updatePhotoToAlbum(result);
                return;
            }
            Toast.makeText(CardVideoActivity.this, CardVideoActivity.this.getString(R.string.str_sdcardvideo_download_fail), 1).show();
            CardVideoActivity.this.isStop = true;
            LeweiLib.LW93StopDownloadFile();
            CardVideoActivity.this.running = false;
        }

        protected void onCancelled() {
            super.onCancelled();
            Log.e("", "cancel task");
        }

        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    public class GetSdcardVideoList extends AsyncTask<Integer, Integer, RecList[]> {
        protected RecList[] doInBackground(Integer... params) {
            CardVideoActivity.videoLists = LeweiLib.LW93SendGetRecList();
            return CardVideoActivity.videoLists;
        }

        protected void onPostExecute(RecList[] result) {
            super.onPostExecute(result);
            if (result != null) {
                for (int i = 0; i < result.length; i++) {
                    CardVideoActivity.this.videoList.add(result[i].file_name);
                    Log.i("get sdcard videos", new StringBuilder(String.valueOf(result[i].file_name)).append("  ").append(result[i].record_start_time).append("  ").append(result[i].record_time).toString());
                }
                CardVideoActivity.this.mAdapter = new SdcardVideosAdapter(CardVideoActivity.this, CardVideoActivity.this.videoList, CardVideoActivity.this.mGridView);
                CardVideoActivity.this.mGridView.setAdapter(CardVideoActivity.this.mAdapter);
            }
        }

        protected void onPreExecute() {
            super.onPreExecute();
            CardVideoActivity.this.videoList.clear();
        }
    }

    public CardVideoActivity() {
        this.videoList = new ArrayList();
        this.isStop = false;
        this.running = false;
        this.clickListener = new C00561();
        this.handler = new Handler(new C00572());
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(TransportMediator.FLAG_KEY_MEDIA_NEXT);
        setContentView(R.layout.activity_cardvideo);
        init();
        this.getSdcardVideoList = new GetSdcardVideoList();
        this.getSdcardVideoList.execute(new Integer[]{Integer.valueOf(1)});
    }

    private void init() {
        this.iv_SdcardVideo_Actionbar_Back = (ImageView) findViewById(R.id.iv_sdcardvideo_actionbar_back);
        this.iv_SdcardVideo_Actionbar_Back.setOnClickListener(this.clickListener);
        this.mGridView = (GridView) findViewById(R.id.gd_sdcardvideo_Videos);
        this.mGridView.setOnItemClickListener(new C00583());
        this.mGridView.setOnItemLongClickListener(new C00594());
    }

    private void startUpdateProgess() {
        if (!this.running) {
            new Thread(new C00605()).start();
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
        LeweiLib.LW93StopDownloadFile();
        this.isStop = true;
        this.running = false;
        startActivity(new Intent(this, SelectActivity.class));
    }

    private void showDialogVideoOperation(int position) {
        Builder mDialogBuilder = new Builder(this).setIcon(17301514).setTitle(getString(R.string.str_sdcardvideo_tips)).setPositiveButton(getString(R.string.str_sdcardvideo_play), new C00616(position)).setNeutralButton(getString(R.string.str_sdcardvideo_download), new C00637(position)).setNegativeButton(getString(R.string.str_sdcardvideo_delete), new C00648(position));
        mDialogBuilder.create().show();
        mDialogBuilder.create().setOnDismissListener(new C00659(mDialogBuilder));
    }

    private void updatePhotoToAlbum(String path) {
        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        Uri uri = Uri.fromFile(new File(path));
        Log.e("Display Activity", "uri  " + uri.toString());
        intent.setData(uri);
        getApplicationContext().sendBroadcast(intent);
    }
}
