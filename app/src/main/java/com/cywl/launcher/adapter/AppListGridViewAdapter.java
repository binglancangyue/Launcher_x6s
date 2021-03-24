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

/**
 * Created by Administrator on 2019/7/8.
 */

public class AppListGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<AppInfo> datas;
    private LayoutInflater inflater;
    private int mWindowsStatus;

    public AppListGridViewAdapter(Context context, ArrayList<AppInfo> data) {
        this.mContext = context;
        this.datas = data;
        this.inflater = LayoutInflater.from(this.mContext);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_gridview_applist, null);
            viewHolder.imageView = convertView.findViewById(R.id.iv_app_icon);
            viewHolder.name = convertView.findViewById(R.id.tv_app_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        AppInfo data = datas.get(position);
        Glide.with(mContext).load(data.getAppIcon()).into(viewHolder.imageView);
        viewHolder.name.setText(data.getAppName());
        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView name;
    }

}
