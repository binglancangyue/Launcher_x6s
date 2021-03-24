package com.cywl.launcher.listener;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cywl.launcher.model.MusicData;

import java.util.List;

public interface OnRecyclerViewItemListener {
    void onItemClickListener(View parent, View view, int position);

    void onItemLongClickListener(View parent, View view, RecyclerView.ViewHolder viewHolder,
                                 int position);

    void upDataItemPosition(List<MusicData> mDataList);
}
