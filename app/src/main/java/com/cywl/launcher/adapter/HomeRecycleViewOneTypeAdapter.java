package com.cywl.launcher.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.cywl.launcher.launcher_x6s.R;
import com.cywl.launcher.listener.ItemTouchHelperAdapter;
import com.cywl.launcher.listener.OnRecyclerViewItemListener;
import com.cywl.launcher.model.CustomValue;
import com.cywl.launcher.model.MusicData;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import cn.kuwo.base.bean.Music;

/**
 * 常见写法 加载一个itemview 布局
 */
public class HomeRecycleViewOneTypeAdapter extends RecyclerView.Adapter<HomeRecycleViewOneTypeAdapter.NorMalTypeViewHolder> implements ItemTouchHelperAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<MusicData> mDataList;
    private SharedPreferences sharedPreferences;
    private Music mMusic;
    private Bitmap musicIcon;
    private boolean mSelected = false;
    private int mWidth;
    private static final String TAG = "HomeRecycleViewOneTypeAdapter";
    private Handler mHandler;
    private View.OnLongClickListener onLongClickListener;
    private View mLongClickView;
    private HomeRecycleViewOneTypeAdapter.MyRunnable myRunnable;

    public void setMusic(Music music, Bitmap icon) {
        mMusic = music;
        musicIcon = icon;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    public void setHandle(Handler handle) {
        this.mHandler = handle;
    }


    private OnRecyclerViewItemListener mOnRecyclerViewItemListener;

    public void setOnRecyclerViewItemListener(OnRecyclerViewItemListener listener) {
        mOnRecyclerViewItemListener = listener;
    }

    public HomeRecycleViewOneTypeAdapter(Context context, List<MusicData> dataList, int width) {
        this.mContext = context;
        this.mDataList = dataList;
        this.sharedPreferences = mContext.getSharedPreferences(CustomValue.SP_NAME,
                Context.MODE_PRIVATE);
        this.mWidth = width;
        inflater = LayoutInflater.from(mContext);
        myRunnable =
                new HomeRecycleViewOneTypeAdapter.MyRunnable(HomeRecycleViewOneTypeAdapter.this);
//        this.musicState = 0;
    }

    static class NorMalTypeViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivIcon;
        private TextView tvTitle;
        private TextView tvString;
//        private ImageView ivButton;

        private NorMalTypeViewHolder(View itemView, HomeRecycleViewOneTypeAdapter adapter) {
            super(itemView);
            WeakReference<HomeRecycleViewOneTypeAdapter> adapterWeakReference =
                    new WeakReference<>(adapter);
            HomeRecycleViewOneTypeAdapter HomeRecycleViewOneTypeAdapter =
                    adapterWeakReference.get();
            ivIcon = itemView.findViewById(R.id.iv_normal_icon);
            tvTitle = itemView.findViewById(R.id.tv_normal_title);
            tvString = itemView.findViewById(R.id.tv_normal_string);
//            ivButton = itemView.findViewById(R.id.iv_normal_button);

            ivIcon.setTag(1);
            ivIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeRecycleViewOneTypeAdapter.mOnRecyclerViewItemListener.onItemClickListener(itemView,
                            ivIcon, getAdapterPosition());
                }
            });

            if (CustomValue.openDelayLongPress) {
                HomeRecycleViewOneTypeAdapter.setLongClick(itemView,
                        new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                HomeRecycleViewOneTypeAdapter.mOnRecyclerViewItemListener.onItemLongClickListener(itemView,
                                        itemView, null, getAdapterPosition());
                                return true;
                            }
                        });
            }

/*            ivButton.setTag(2);
            ivButton.setOnClickListener(new View
                    .OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnRecyclerViewItemListener.onItemClickListener(itemView, ivButton,
                            getAdapterPosition());
                }
            });*/

        }
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public NorMalTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_recycleview_type_normal_new, parent, false);
        return new NorMalTypeViewHolder(view, this);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(@NonNull NorMalTypeViewHolder holder, int position) {
        MusicData itemData = mDataList.get(position);
        int tag = itemData.getItemViewTag();
        int title = itemData.getTitle();
        int message = itemData.getText();

        Log.d(TAG, "onBindViewHolder: "+mContext.getResources().getString(title));
        holder.tvTitle.setText(title);
        holder.tvString.setText(message);
/*            Glide.with(mContext).load(CustomValue.HOME_BUTTON_ICON[tag]).into((
            (NorMalTypeViewHolder) holder).ivButton);*/
        holder.itemView.setTag(itemData.getItemViewTag());
        switch (tag) {
            case CustomValue.ITEM_MUSIC_TAG:
                if (mMusic != null) {
                    holder.tvTitle.setText(mMusic.name);
                    holder.tvString.setText(mMusic.artist);
                    Glide.with(mContext).load(musicIcon)
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(holder.ivIcon);
                } else {
                    Glide.with(mContext).load(itemData.getIcon())
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(holder.ivIcon);
                }
                break;
            case CustomValue.ITEM_SETTING_TAG:
            case CustomValue.ITEM_E_DOG_TAG:
            case CustomValue.ITEM_FILE_MGT_TAG:
            case CustomValue.ITEM_APP_MGT_TAG:
            case CustomValue.ITEM_PLAYBACK_TAG:
                Glide.with(mContext).load(itemData.getIcon())
                        .into(holder.ivIcon);
                holder.tvString.setText("");
                break;
            default:
                //other tag
                Glide.with(mContext).load(itemData.getIcon()).into(holder.ivIcon);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    /**
     * 当Item被回收的时候调用
     *
     * @param holder
     */
    @Override
    public void onViewRecycled(@NonNull NorMalTypeViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onItemMove(RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        int fromPosition = source.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        if (fromPosition < mDataList.size() && toPosition < mDataList.size()) {
            //交换数据位置
            Collections.swap(mDataList, fromPosition, toPosition);
            //刷新位置交换
            notifyItemMoved(fromPosition, toPosition);
        }
//        mOnRecyclerViewItemListener.upDataItemPosition(mDataList);
        //移动过程中移除view的放大效果
//        onItemClear(source);

    }

    private void saveData(List<MusicData> mDataList) {
        String data = JSON.toJSONString(mDataList);
        Log.d("mDataList", "saveData: " + data);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CustomValue.SP_KEY_SAVE_DATA, data);
        editor.apply();
    }

    @Override
    public void onItemDismiss(RecyclerView.ViewHolder source) {
        int position = source.getAdapterPosition();
        mDataList.remove(position); //移除数据
        notifyItemRemoved(position);//刷新数据移除
    }

    @Override
    public void onItemSelect(RecyclerView.ViewHolder viewHolder) {
        //当拖拽选中时缩小选中的view
        viewHolder.itemView.setScaleX(0.8f);
        viewHolder.itemView.setScaleY(0.8f);
    }

    @Override
    public void onItemClear(RecyclerView.ViewHolder viewHolder) {
        //拖拽结束后恢复view的状态
        mOnRecyclerViewItemListener.upDataItemPosition(mDataList);
        saveData(mDataList);
        viewHolder.itemView.setScaleX(1.0f);
        viewHolder.itemView.setScaleY(1.0f);
    }

    private void setLongClick(final View longClickView,
                              final View.OnLongClickListener longClickListener) {
        longClickView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onLongClickListener = longClickListener;
                mLongClickView = longClickView;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mHandler != null) {
                        mHandler.postDelayed(myRunnable, 3000);
                    }
                }
                return false;
            }
        });
    }

    public void removeCallback() {
        if (mHandler != null) {
            mHandler.removeCallbacks(myRunnable);
        }
    }

    private static class MyRunnable implements Runnable {
        private WeakReference<HomeRecycleViewOneTypeAdapter> weakReference;

        MyRunnable(HomeRecycleViewOneTypeAdapter adapter) {
            this.weakReference = new WeakReference<>(adapter);
        }

        @Override
        public void run() {
            HomeRecycleViewOneTypeAdapter HomeRecycleViewOneTypeAdapter = weakReference.get();
            if (HomeRecycleViewOneTypeAdapter.onLongClickListener != null) {
                HomeRecycleViewOneTypeAdapter.onLongClickListener.onLongClick(HomeRecycleViewOneTypeAdapter.mLongClickView);
            }
        }
    }
}
