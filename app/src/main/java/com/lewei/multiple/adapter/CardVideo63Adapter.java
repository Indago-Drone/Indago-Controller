package com.lewei.multiple.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.lewei.lib63.RecordInfo;
import com.lewei.multiple.fydrone.R;
import java.util.ArrayList;
import java.util.List;

public class CardVideo63Adapter extends BaseAdapter {
    private ViewHolder mHolder;
    private LayoutInflater mInflater;
    private List<RecordInfo> mListVideo;
    private int screen_height;

    private class ViewHolder {
        public TextView tv_end;
        public TextView tv_size;
        public TextView tv_start;

        private ViewHolder() {
            this.tv_start = null;
            this.tv_end = null;
            this.tv_size = null;
        }
    }

    public CardVideo63Adapter(Context context, List<RecordInfo> mListRecs) {
        this.mListVideo = new ArrayList();
        this.mInflater = null;
        this.mHolder = null;
        this.screen_height = 0;
        this.mInflater = LayoutInflater.from(context);
        this.mListVideo = mListRecs;
        this.screen_height = context.getResources().getDisplayMetrics().heightPixels;
    }

    public int getCount() {
        return this.mListVideo.size();
    }

    public Object getItem(int arg0) {
        return this.mListVideo.get(arg0);
    }

    public long getItemId(int arg0) {
        return (long) arg0;
    }

    @SuppressLint({"InflateParams"})
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            this.mHolder = new ViewHolder();
            convertView = this.mInflater.inflate(R.layout.item_listview_cardview63, null);
            this.mHolder.tv_start = (TextView) convertView.findViewById(R.id.tv_start);
            this.mHolder.tv_end = (TextView) convertView.findViewById(R.id.tv_stop);
            this.mHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            convertView.setTag(this.mHolder);
            convertView.setLayoutParams(new LayoutParams(-1, (this.screen_height * 100) / 640));
        } else {
            this.mHolder = (ViewHolder) convertView.getTag();
        }
        RecordInfo recordInfo = (RecordInfo) this.mListVideo.get(position);
        this.mHolder.tv_start.setText(recordInfo.str_start_time);
        this.mHolder.tv_end.setText(recordInfo.str_stop_time);
        this.mHolder.tv_size.setText(recordInfo.str_video_size);
        return convertView;
    }
}
