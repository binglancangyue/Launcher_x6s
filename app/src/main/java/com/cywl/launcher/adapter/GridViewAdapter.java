package com.cywl.launcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cywl.launcher.launcher_x6s.R;
import com.cywl.launcher.model.AppInfo;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {
    private ArrayList<AppInfo> data;
    private LayoutInflater inflater;
    private Context mContext;
    private int mIndex;
    private int mPagerSize;

    public GridViewAdapter(Context context, ArrayList<AppInfo> data, int mIndex, int mPagerSize) {
        this.mContext = context;
        this.data = data;
        this.mIndex = mIndex;
        this.mPagerSize = mPagerSize;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size() > (mIndex + 1) * mPagerSize ? mPagerSize :
                (data.size() - mIndex * mPagerSize);
    }

    @Override
    public Object getItem(int position) {
        return data.get(position + mIndex * mPagerSize);
    }

    @Override
    public long getItemId(int position) {
        return position + mIndex * mPagerSize;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_gridview_applist, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.iv_app_icon);
            holder.name = convertView.findViewById(R.id.tv_app_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //重新确定position（因为拿到的是总的数据源，数据源是分页加载到每页的GridView上的，为了确保能正确的点对不同页上的item）
        final int pos = position + mIndex * mPagerSize;//假设mPagerSize=8，假如点击的是第二页（即mIndex=1
        // ）上的第二个位置item(position=1),那么这个item的实际位置就是pos=9
        AppInfo info = data.get(pos);
        holder.name.setText(info.getAppName());
        Glide.with(mContext).load(info.getAppIcon()).into(holder.imageView);
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView name;
    }

}
