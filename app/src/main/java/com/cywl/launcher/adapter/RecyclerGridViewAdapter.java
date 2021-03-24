package com.cywl.launcher.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cywl.launcher.launcher_x6s.R;
import com.cywl.launcher.model.AppInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class RecyclerGridViewAdapter extends RecyclerView.Adapter<RecyclerGridViewAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<AppInfo> mData;
    private LayoutInflater inflater;
    private int mIndex;
    private int mPagerSize;


    public interface OnRecyclerViewItemListener {
        void onItemClickListener(View view, int position);

        void onItemLongClickLister(View view, AppInfo appInfo, int position);
    }

    private OnRecyclerViewItemListener mOnRecyclerViewItemListener;

    public void setOnRecyclerViewItemListener(OnRecyclerViewItemListener listener) {
        mOnRecyclerViewItemListener = listener;
    }

    public RecyclerGridViewAdapter(Context context, ArrayList<AppInfo> data, int index,
                                   int pagerSize) {
        this.mContext = context;
        this.mData = data;
        this.mIndex = index;
        this.mPagerSize = pagerSize;
        inflater = LayoutInflater.from(mContext);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_recycle_gridview, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder == null) {
            return;
        }

        final int pos = position + mIndex * mPagerSize;//假设mPagerSize=12，假如点击的是第二页(即mIndex=1)
        // 上的第二个位置item(position=1),那么这个item的实际位置就是pos=13
        AppInfo info = mData.get(pos);
        holder.tvAppName.setText(info.getAppName());
        Glide.with(mContext).load(info.getAppIcon()).into(holder.ivAppIcon);
    }

    @Override
    public int getItemCount() {
        return mData.size() > (mIndex + 1) * mPagerSize ? mPagerSize :
                (mData.size() - mIndex * mPagerSize);
    }

    @Override
    public long getItemId(int position) {
        return position + mIndex * mPagerSize;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        private WeakReference<RecyclerGridViewAdapter> adapterWeakReference;
        TextView tvAppName;
        ImageView ivAppIcon;

        private ViewHolder(View itemView, RecyclerGridViewAdapter adapter) {
            super(itemView);
            adapterWeakReference = new WeakReference<>(adapter);
            tvAppName = itemView.findViewById(R.id.tv_app_name);
            ivAppIcon = itemView.findViewById(R.id.iv_app_icon);
            RecyclerGridViewAdapter gridViewAdapter = adapterWeakReference.get();
            if (gridViewAdapter.mOnRecyclerViewItemListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getLayoutPosition();
                        final int pos =
                                position + gridViewAdapter.mIndex * gridViewAdapter.mPagerSize;
                        gridViewAdapter.mOnRecyclerViewItemListener.onItemClickListener(itemView,
                                pos);
                    }
                });

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = getLayoutPosition();
                        final int pos =
                                position + gridViewAdapter.mIndex * gridViewAdapter.mPagerSize;
                        gridViewAdapter.mOnRecyclerViewItemListener.onItemLongClickLister(itemView,
                                gridViewAdapter.mData.get(pos)
                                , pos);
                        return true;
                    }
                });
            }
        }
    }

}
