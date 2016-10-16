package com.lewei.multiple.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import com.lewei.lib63.LeweiLib63;
import com.lewei.lib63.RecordInfo;
import com.lewei.lib63.SDCardInfo;
import com.lewei.multiple.adapter.CardVideo63Adapter;
import com.lewei.multiple.fydrone.R;
import com.lewei.multiple.utils.DateUtils;
import java.util.ArrayList;
import java.util.List;

public class CardVideo63Activity extends Activity {
    public static List<RecordInfo> mVideoList;
    protected final String TAG;
    private OnItemClickListener itemClickListener;
    private CardVideo63Adapter mAdapter;
    private Handler mHandler;
    private ListView mListView;
    private Runnable mSearchRecs;

    /* renamed from: com.lewei.multiple.main.CardVideo63Activity.1 */
    class C00531 implements Runnable {
        C00531() {
        }

        public void run() {
            if (LeweiLib63.LW63SearchRecInit()) {
                CardVideo63Activity.mVideoList.clear();
                while (true) {
                    RecordInfo recordInfo = new RecordInfo();
                    if (!LeweiLib63.LW63SearchRecords(recordInfo)) {
                        break;
                    }
                    if (recordInfo.dataSize < 1048576) {
                        recordInfo.str_video_size = String.format("%.1fKB", new Object[]{Float.valueOf(((float) recordInfo.dataSize) / 1024.0f)});
                    } else {
                        recordInfo.str_video_size = String.format("%.1fMB", new Object[]{Float.valueOf(((float) recordInfo.dataSize) / 1048576.0f)});
                    }
                    recordInfo.str_start_time = DateUtils.timeToStr(recordInfo.uStartTime / 1000);
                    recordInfo.str_stop_time = DateUtils.timeToStr(recordInfo.uStopTime / 1000);
                    recordInfo.str_channel = String.valueOf(recordInfo.channel);
                    recordInfo.recordTime = ((int) ((recordInfo.uStopTime - recordInfo.uStartTime) / 1000000)) - 1;
                    CardVideo63Activity.mVideoList.add(recordInfo);
                }
            } else {
                Toast.makeText(CardVideo63Activity.this, CardVideo63Activity.this.getApplication().getString(R.string.str_cardvideo63_search_error), 0).show();
            }
            CardVideo63Activity.this.mAdapter.notifyDataSetChanged();
            CardVideo63Activity.this.searchClean();
        }
    }

    /* renamed from: com.lewei.multiple.main.CardVideo63Activity.2 */
    class C00542 implements OnItemClickListener {
        C00542() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if (!LeweiLib63.LW63GetLogined()) {
                Toast.makeText(CardVideo63Activity.this, CardVideo63Activity.this.getString(R.string.str_device_offline), 1).show();
            } else if (CardVideo63Activity.this.isSDCardNormal()) {
                Intent intent = new Intent(CardVideo63Activity.this, ReplayActivity.class);
                intent.putExtra("position", position);
                CardVideo63Activity.this.startActivity(intent);
            } else {
                Toast.makeText(CardVideo63Activity.this, CardVideo63Activity.this.getString(R.string.str_sdcard_not_find), 1).show();
            }
        }
    }

    /* renamed from: com.lewei.multiple.main.CardVideo63Activity.3 */
    class C00553 implements Runnable {
        C00553() {
        }

        public void run() {
            LeweiLib63.LW63SearchRecClean();
        }
    }

    public CardVideo63Activity() {
        this.TAG = "CardVideo63";
        this.mListView = null;
        this.mAdapter = null;
        this.mHandler = new Handler();
        this.mSearchRecs = new C00531();
        this.itemClickListener = new C00542();
    }

    static {
        mVideoList = new ArrayList();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardvideo63);
        widget_init();
        this.mHandler.postDelayed(this.mSearchRecs, 500);
    }

    private void widget_init() {
        this.mListView = (ListView) findViewById(R.id.lv_sdcardvideo63);
        this.mAdapter = new CardVideo63Adapter(this, mVideoList);
        this.mListView.setAdapter(this.mAdapter);
        this.mListView.setOnItemClickListener(this.itemClickListener);
    }

    private boolean isSDCardNormal() {
        SDCardInfo info = new SDCardInfo();
        if (!LeweiLib63.LW63GetSDCardInfo(info) || ((info.state & 1) > 0 && (info.state & 2) > 0 && (info.state & 4) > 0)) {
            return true;
        }
        return false;
    }

    private void searchClean() {
        new Thread(new C00553()).start();
    }

    public void onBackPressed(View pressed) {
        onBackPressed();
    }

    public void onBackPressed() {
        super.onBackPressed();
        startIntent(SelectActivity.class);
    }

    private void startIntent(Class<?> cls) {
        finish();
        Intent intent = new Intent();
        intent.setClass(this, cls);
        startActivity(intent);
        overridePendingTransition(17432578, 17432579);
    }
}
