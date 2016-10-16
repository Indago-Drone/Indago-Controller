package com.lewei.multiple.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.lewei.multiple.adapter.MyImageView.OnMeasureListener;
import com.lewei.multiple.adapter.NativeImageLoader.LOAD_TYPE;
import com.lewei.multiple.adapter.NativeImageLoader.NativeImageCallBack;
import com.lewei.multiple.fydrone.C0052R;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class VideosAdapter extends BaseAdapter {
    private List<String> list;
    private GridView mGridView;
    protected LayoutInflater mInflater;
    private Point mPoint;
    @SuppressLint({"UseSparseArrays"})
    private HashMap<Integer, Boolean> mSelectMap;

    public static class ViewHolder {
        public MyImageView mImageView;
        public TextView mTextView;
    }

    /* renamed from: com.lewei.multiple.adapter.VideosAdapter.1 */
    class C01301 implements OnMeasureListener {
        C01301() {
        }

        public void onMeasureSize(int width, int height) {
            VideosAdapter.this.mPoint.set(width, height);
        }
    }

    /* renamed from: com.lewei.multiple.adapter.VideosAdapter.2 */
    class C01312 implements NativeImageCallBack {
        private final /* synthetic */ ViewHolder val$viewHolder;

        C01312(ViewHolder viewHolder) {
            this.val$viewHolder = viewHolder;
        }

        public void onImageLoader(Bitmap bitmap, String path) {
            ImageView mImageView = (ImageView) VideosAdapter.this.mGridView.findViewWithTag(path);
            if (bitmap != null && mImageView != null) {
                mImageView.setImageBitmap(bitmap);
                this.val$viewHolder.mTextView.setText(new File(path).getName());
            }
        }
    }

    public VideosAdapter(Context context, List<String> list, GridView mGridView) {
        this.mPoint = new Point(0, 0);
        this.mSelectMap = new HashMap();
        this.list = list;
        this.mGridView = mGridView;
        this.mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return this.list.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    @SuppressLint({"InflateParams"})
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        String path = (String) this.list.get(position);
        String videoName = new File(path).getName();
        if (convertView == null) {
            convertView = this.mInflater.inflate(C0052R.layout.item_gridview_videos, null);
            viewHolder = new ViewHolder();
            viewHolder.mImageView = (MyImageView) convertView.findViewById(C0052R.id.iv_Videos_Item);
            viewHolder.mTextView = (TextView) convertView.findViewById(C0052R.id.tv_Videos_Name_Item);
            viewHolder.mImageView.setOnMeasureListener(new C01301());
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.mImageView.setImageResource(C0052R.drawable.friends_sends_pictures_no);
            viewHolder.mTextView.setText("");
        }
        viewHolder.mImageView.setTag(path);
        Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(LOAD_TYPE.VIDEO, path, this.mPoint, new C01312(viewHolder));
        if (bitmap != null) {
            viewHolder.mImageView.setImageBitmap(bitmap);
            viewHolder.mTextView.setText(videoName);
        } else {
            viewHolder.mImageView.setImageResource(C0052R.drawable.friends_sends_pictures_no);
            viewHolder.mTextView.setText("");
        }
        return convertView;
    }

    public List<Integer> getSelectItems() {
        List<Integer> list = new ArrayList();
        for (Entry<Integer, Boolean> entry : this.mSelectMap.entrySet()) {
            if (((Boolean) entry.getValue()).booleanValue()) {
                list.add((Integer) entry.getKey());
            }
        }
        return list;
    }
}
