package com.cywl.launcher.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
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
 * itemview 加载多个布局
 */
public class HomeRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemTouchHelperAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private static final int TYPE_NORMAL = 0;
    //    private static final int TYPE_BOX = 1;
//    private static final int TYPE_CAMERA = 2;
    private List<MusicData> mDataList;
    private SharedPreferences sharedPreferences;
    private Music mMusic;
    private Bitmap musicIcon;
    private boolean mSelected = false;
    private int mWidth;
    private static final String TAG = "HomeRecycleViewAdapter";
    private Handler mHandler;
    private View.OnLongClickListener onLongClickListener;
    private View mLongClickView;
    private MyRunnable myRunnable;

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

    public HomeRecycleViewAdapter(Context context, List<MusicData> dataList, int width) {
        this.mContext = context;
        this.mDataList = dataList;
        this.sharedPreferences = mContext.getSharedPreferences(CustomValue.SP_NAME,
                Context.MODE_PRIVATE);
        this.mWidth = width;
        inflater = LayoutInflater.from(mContext);
        myRunnable = new MyRunnable(HomeRecycleViewAdapter.this);
    }

    private static class NorMalTypeViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivIcon;
        private TextView tvTitle;
        private TextView tvString;
//        private ImageView ivButton;

        private NorMalTypeViewHolder(View itemView, HomeRecycleViewAdapter adapter) {
            super(itemView);
            WeakReference<HomeRecycleViewAdapter> adapterWeakReference =
                    new WeakReference<>(adapter);
            HomeRecycleViewAdapter homeRecycleViewAdapter = adapterWeakReference.get();
            ivIcon = itemView.findViewById(R.id.iv_normal_icon);
            tvTitle = itemView.findViewById(R.id.tv_normal_title);
            tvString = itemView.findViewById(R.id.tv_normal_string);
//            ivButton = itemView.findViewById(R.id.iv_normal_button);

            ivIcon.setTag(1);
            ivIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    homeRecycleViewAdapter.mOnRecyclerViewItemListener.onItemClickListener(itemView,
                            ivIcon, getAdapterPosition());
                }
            });

            if (CustomValue.openDelayLongPress) {
                homeRecycleViewAdapter.setLongClick(itemView, new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        homeRecycleViewAdapter.mOnRecyclerViewItemListener.onItemLongClickListener(itemView,
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

    private static class CameraViewHolder extends RecyclerView.ViewHolder {
        private SurfaceView surfaceView;
        private TextView tvTitle;
        private TextView tvString;
//        private ImageView ivButton;

        private CameraViewHolder(View itemView, HomeRecycleViewAdapter adapter) {
            super(itemView);
            WeakReference<HomeRecycleViewAdapter> adapterWeakReference =
                    new WeakReference<>(adapter);
            HomeRecycleViewAdapter homeRecycleViewAdapter = adapterWeakReference.get();
            surfaceView = itemView.findViewById(R.id.iv_camera);
//            ivButton = itemView.findViewById(R.id.iv_camera_button);
            tvTitle = itemView.findViewById(R.id.tv_camera_title);
            tvString = itemView.findViewById(R.id.tv_camera_string);
            surfaceView.setTag(1);
            surfaceView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    homeRecycleViewAdapter.mOnRecyclerViewItemListener.onItemClickListener(itemView,
                            surfaceView, getAdapterPosition());
                }
            });
            if (CustomValue.openDelayLongPress) {
                homeRecycleViewAdapter.setLongClick(itemView, new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        homeRecycleViewAdapter.mOnRecyclerViewItemListener.onItemLongClickListener(itemView,
                                itemView, null, getAdapterPosition());
                        return true;
                    }
                });
            }
        }
    }

  /*  private class BoxTypeViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivApp1;
        private TextView tvName1;
        private ImageView ivApp2;
        private TextView tvName2;
        private ImageView ivApp3;
        private TextView tvName3;
        private ImageView ivApp4;
        private TextView tvName4;
        private ImageView ivButton;

        private BoxTypeViewHolder(View itemView) {
            super(itemView);
        }
    }*/

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        ViewGroup.LayoutParams layoutParams;
        RecyclerView.ViewHolder viewHolder;
        if (viewType == TYPE_NORMAL) {
            view = inflater.inflate(R.layout.item_recycleview_type_normal_new, parent, false);
            viewHolder = new NorMalTypeViewHolder(view, this);
        } else {
            view = inflater.inflate(R.layout.item_recycleview_type_camare_new, parent, false);
            viewHolder = new CameraViewHolder(view, this);
        }

//        layoutParams = view.getLayoutParams();
//        layoutParams.width = (mWidth / 5);
//        view.setLayoutParams(layoutParams);
//        Log.d(TAG, "onCreateViewHolder: "+ layoutParams.width);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MusicData itemData = mDataList.get(position);
        int tag = itemData.getItemViewTag();
        if (holder instanceof NorMalTypeViewHolder) {
            ((NorMalTypeViewHolder) holder).tvTitle.setText(itemData.getTitle());
            ((NorMalTypeViewHolder) holder).tvString.setText(itemData.getText());
/*            Glide.with(mContext).load(CustomValue.HOME_BUTTON_ICON[tag]).into((
            (NorMalTypeViewHolder) holder).ivButton);*/
            holder.itemView.setTag(itemData.getItemViewTag());
            switch (tag) {
                case CustomValue.ITEM_MUSIC_TAG:
                    if (mMusic != null) {
                        ((NorMalTypeViewHolder) holder).tvTitle.setText(mMusic.name);
                        ((NorMalTypeViewHolder) holder).tvString.setText(mMusic.artist);
                        Glide.with(mContext).load(musicIcon)
                                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                .into(((NorMalTypeViewHolder) holder).ivIcon);
                    } else {
                        Glide.with(mContext).load(itemData.getIcon())
                                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                .into(((NorMalTypeViewHolder) holder).ivIcon);
                    }
/*                if (mSelected) {
                    Glide.with(mContext).load(R.drawable.button_play).into(((NorMalTypeViewHolder)
                            holder).ivButton);
                    ((NorMalTypeViewHolder) holder).ivButton.setSelected(true);
                } else {
                    Glide.with(mContext).load(R.drawable.button_pause).into((
                    (NorMalTypeViewHolder)
                            holder).ivButton);
                    ((NorMalTypeViewHolder) holder).ivButton.setSelected(false);
                }*/
                    break;
                case CustomValue.ITEM_SETTING_TAG:
                case CustomValue.ITEM_E_DOG_TAG:
                case CustomValue.ITEM_FILE_MGT_TAG:
                case CustomValue.ITEM_APP_MGT_TAG:
                case CustomValue.ITEM_PLAYBACK_TAG:
                    Glide.with(mContext).load(itemData.getIcon())
                            .into(((NorMalTypeViewHolder) holder).ivIcon);
                    ((NorMalTypeViewHolder) holder).tvString.setText("");
                    break;
                default:
                    //other tag
                    Glide.with(mContext).load(itemData.getIcon())
                            .into(((NorMalTypeViewHolder) holder).ivIcon);
                    break;
            }
        } else if (holder instanceof CameraViewHolder) {
            ((CameraViewHolder) holder).tvTitle.setText(itemData.getText());
            ((CameraViewHolder) holder).tvString.setText(itemData.getText());
//            Glide.with(mContext).load(itemData.getBtnIcon())
//                    .into(((CameraViewHolder) holder).ivButton);
            ((CameraViewHolder) holder).surfaceView.setTag(1);
//            ((CameraViewHolder) holder).ivButton.setTag(2);
            holder.itemView.setTag(itemData.getItemViewTag());
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

    /*    private void setLongClick(final Handler handler, final View longClickView,
                                  final long delayMillis,
                                  final View.OnLongClickListener longClickListener) {
            longClickView.setOnTouchListener(new View.OnTouchListener() {
    //            private float TOUCH_MAX = 0.1f;
    //            private float mLastMotionX;
    //            private float mLastMotionY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    onLongClickListener = longClickListener;
                    mLongClickView = longClickView;
    //                float x = event.getRawX();
    //                float y = event.getRawY();
                    mHandler = handler;
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            Log.d(TAG, "onTouch:ACTION_UP ACTION_UP");

                           *//* isUP = true;
                        handler.removeCallbacks(r);*//*
                        break;
                    case MotionEvent.ACTION_MOVE:
                     *//*   if (Math.abs(mLastMotionX - x) > TOUCH_MAX
                                || Math.abs(mLastMotionY - y) > TOUCH_MAX) {
                            isMove = true;
                            Log.d(TAG, "onTouch:ACTION_MOVE TOUCH_MAX");
                            handler.removeCallbacks(r);
                        }*//*
                        break;
                    case MotionEvent.ACTION_DOWN:
                     *//*   mCounter++;
                        downTime = System.currentTimeMillis();
                        motionEvent = event;
                        isUP = false;
                        isMove = false;
                        handler.removeCallbacks(r);
                        mLastMotionX = event.getRawX();
                        mLastMotionY = y;*//*
                        handler.postDelayed(r, delayMillis);
                        break;
                }
                return false;
//                return detector.onTouchEvent(event);
            }

        });
    }*/

    public void removeCallback() {
        if (mHandler != null) {
            mHandler.removeCallbacks(myRunnable);
        }
    }

    private static class MyRunnable implements Runnable {
        private WeakReference<HomeRecycleViewAdapter> weakReference;

        MyRunnable(HomeRecycleViewAdapter adapter) {
            this.weakReference = new WeakReference<>(adapter);
        }

        @Override
        public void run() {
            HomeRecycleViewAdapter homeRecycleViewAdapter = weakReference.get();
            if (homeRecycleViewAdapter.onLongClickListener != null) {
                homeRecycleViewAdapter.onLongClickListener.onLongClick(homeRecycleViewAdapter.mLongClickView);
            }
        }
    }


}
