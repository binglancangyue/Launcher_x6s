package com.cywl.launcher.launcher_x6s;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.cywl.launcher.adapter.HomeRecycleViewOneTypeAdapter;
import com.cywl.launcher.listener.GaoDeCallBackListener;
import com.cywl.launcher.listener.OnRecyclerViewItemListener;
import com.cywl.launcher.model.CustomValue;
import com.cywl.launcher.model.MsgEvent;
import com.cywl.launcher.model.MusicData;
import com.cywl.launcher.receiver.GaoDeBroadCastReceiver;
import com.cywl.launcher.utils.CustomDecoration;
import com.cywl.launcher.utils.LeftSnapHelper;
import com.cywl.launcher.utils.RxBus;
import com.cywl.launcher.utils.ScreenTool;
import com.cywl.launcher.utils.SimpleItemTouchHelperCallback;
import com.cywl.launcher.utils.ToastUtils;
import com.txznet.sdk.TXZPowerManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.kuwo.autosdk.api.KWAPI;
import cn.kuwo.autosdk.api.OnEnterListener;
import cn.kuwo.autosdk.api.OnExitListener;
import cn.kuwo.autosdk.api.OnGetSongImgListener;
import cn.kuwo.autosdk.api.OnPlayerStatusListener;
import cn.kuwo.autosdk.api.PlayState;
import cn.kuwo.autosdk.api.PlayerStatus;
import cn.kuwo.base.bean.Music;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.txznet.sdk.TXZPowerManager.PowerAction.POWER_ACTION_BEFORE_SLEEP;


public class HomeActivity extends Activity implements GaoDeCallBackListener
        , OnRecyclerViewItemListener {
    private static final String TAG = "HomeActivity";
    private KWAPI mKwApi;
    private String mLocation = null;
    private Music mMusic;
    private Context mContext;
    private final InnerHandler mInnerHandler = new InnerHandler(this);
    private Music lastMusic;
    private BroadcastReceiver gaoDeBroadcastReceiver;
    private MyApplication myApplication;

    private List<MusicData> mDataList = new ArrayList<>();
    private static final String NOTIFICATION_CHANNEL_NAME = "BackgroundLocation";
    private NotificationManager notificationManager = null;
    boolean isCreateChannel = false;
    private final int mRequestCode = 100;
    private List<String> mPermissionList = new ArrayList<>();
    private PlayerStatus musicStatus = null;

    public static final int PERMISSION_REQUEST_CODE = 0;
    private static final int HANDLE_LOCATION = 2;
    private static final int HANDLE_MUSIC_INFO = 1;
    private static final int INIT_DATA = 3;

    private SharedPreferences sharedPreferences;
    private List<MusicData> changedData;
    /*    //???????????? ??????AMapLocationClient?????????
        public AMapLocationClient mLocationClient = null;
        //??????AMapLocationClientOption??????
        public AMapLocationClientOption mLocationOption = null;*/
    private int contentViewWidth = 902;//920
    private boolean isMove = false;
    private boolean isUp = false;
    private float mLastMotionX;
    private float mLastMotionY;
    //    private BounceRecyclerView recyclerView;
    private RecyclerView recyclerView;

    private ItemTouchHelper touchHelper;
    private CompositeDisposable compositeDisposable;
    //????????????????????????????????????????????????true???????????????????????????????????????????????????????????????
    private boolean needCheckBackLocation = false;
    //    private HomeRecycleViewAdapter homeRecycleViewAdapter;
    private HomeRecycleViewOneTypeAdapter homeRecycleViewAdapter;
    private Dialog permissionDialog;
    /**
     * ????????????????????????????????????????????????
     */
    private boolean isNeedCheck = true;
    private static String BACKGROUND_LOCATION_PERMISSION = "" +
            ".permission" +
            ".ACCESS_BACKGROUND_LOCATION";
    /**
     * ?????????????????????????????????
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getWindow().getDecorView();
        int visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        view.setSystemUiVisibility(visibility);
        setContentView(R.layout.activity_home_recycleview);
        mContext = this;
        myApplication = (MyApplication) getApplication();

        sharedPreferences = getSharedPreferences(CustomValue.SP_NAME,
                Context.MODE_PRIVATE);
        compositeDisposable = new CompositeDisposable();
//        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        intiView();
//        TXZVoiceControl control = new TXZVoiceControl();
    }

    private void intiView() {
        initRecycleView();
    }

    private void initRecycleView() {
        setRecyclerView();
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                mInnerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        getRecycleViewData();
                        setRecyclerViewAdapter();
                        asyncInitData();
                    }
                });
            }
        });
    }

    private void initOther() {
        initKwApi();
        rxBus();
        //        initGaoDePosition();
        //        CrowdSourceApi.getInstance(this).start();
    }


    private void setRecyclerViewAdapter() {
        int dividingLineWidth = ScreenTool.dipToPx(this, 20);//4????????????,??????5dp
//        homeRecycleViewAdapter = new HomeRecycleViewAdapter(this, mDataList,
//                contentViewWidth - dividingLineWidth);
        homeRecycleViewAdapter = new HomeRecycleViewOneTypeAdapter(this, mDataList, 20);
        // recycleView??????
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(homeRecycleViewAdapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        homeRecycleViewAdapter.setOnRecyclerViewItemListener(this);
        homeRecycleViewAdapter.setHandle(mInnerHandler);
        recyclerView.setAdapter(homeRecycleViewAdapter);
        //????????????????????????
//        recyclerView.addItemDecoration(new LinearLayoutItemDecoration(4));
/*        DividerItemDecoration divider = new DividerItemDecoration(this,
                DividerItemDecoration.HORIZONTAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable
                .transparent_dividing_line)));
        recyclerView.addItemDecoration(divider);*/
        recyclerView.addItemDecoration(new CustomDecoration(this, 0,
                R.drawable.transparent_dividing_line, 0));
        if (CustomValue.openDelayLongPress) {
            setRecycleViewTouchListen();
        }
    }

    private void setRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        recyclerView = findViewById(R.id.rcy_grid);
//        recyclerView.setOritation(1);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(15);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //????????????,????????????????????????
        ((DefaultItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        //??????????????????
//        new LinearSnapHelper().attachToRecyclerView(recyclerView);
        //??????????????????
        new LeftSnapHelper().attachToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (HomeActivity.this.isFinishing()) {
                    return;
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Glide.with(mContext).resumeRequests();//??????Glide????????????
                } else {
                    Glide.with(mContext).pauseRequests();//??????Glide????????????
                }
            }
        });
    }

    @SuppressLint("CheckResult")
    private void asyncInitData() {
        compositeDisposable.add(
                Observable.create((ObservableOnSubscribe<String>) emitter -> {
                    initOther();
                    myApplication.initAppList();
                    emitter.onComplete();
                }).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(s -> Log.d(TAG, "accept: init over")));
    }


    /**
     * ????????????adapter?????????
     */
    private void getRecycleViewData() {
        String json = sharedPreferences.getString(CustomValue.SP_KEY_SAVE_DATA, null);
        if (json == null) {
            int length = CustomValue.HOME_ICON.length;
            for (int i = 0; i < length; i++) {
                mDataList.add(new MusicData(
                        CustomValue.HOME_ICON[i],
                        CustomValue.HOME_FIRST_STRING[i],
                        CustomValue.HOME_SECOND_STRING[i],
                        CustomValue.ITEM_VIEW_TYPE[i],
                        CustomValue.ITEM_VIEW_INDEX[i]));
            }
        } else {
            //Gson??????
     /*       Type type = new TypeToken<List<MusicData>>() {}.getType();
            mDataList = new Gson().fromJson(json, type);*/

            //fastJson??????
            mDataList = JSON.parseArray(json, MusicData.class);
        }
    }

    /**
     * ??????????????????
     */
    private void registerBroadCastReceiver() {
        gaoDeBroadcastReceiver = new GaoDeBroadCastReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(CustomValue.ACTION_STANDARD);
        registerReceiver(gaoDeBroadcastReceiver, filter);
    }

    @Override
    public void upDataInfo() {

    }

    /**
     * ??????????????????
     */
    private void jumpToFileManagement() {
        Intent file = new Intent(Intent.ACTION_GET_CONTENT);
        file.setType("*/*");
        file.addCategory(Intent.CATEGORY_OPENABLE);
        startActivity(file);
    }

    private void jumpByAction(String action) {
        Intent intent = new Intent(action);
        startActivity(intent);
    }

    private void jumpToActivity(Class aClass) {
        Intent intent = new Intent(this, aClass);
        startActivity(intent);
    }

    /**
     * recyclerView adapter??????????????????ItemLongClick??????
     *
     * @param parent     ??????????????????item
     * @param view       item?????????????????????
     * @param viewHolder viewHolder
     * @param position   item??????
     */
    @Override
    public void onItemLongClickListener(View parent, View view,
                                        RecyclerView.ViewHolder viewHolder, int position) {
//        Log.d(TAG, "onItemLongClickListener: " + parent.isPressed() + " " + parent.isSelected()
//                + " " + parent.isFocused() + " " + parent.isInLayout() + " " + isMove);
        if (!isMove && !isUp) {
            touchHelper.startDrag(recyclerView.getChildViewHolder(view));
            isMove = false;
        }
    }

    /**
     * recyclerView adapter??????????????????ItemClick??????
     *
     * @param parent   ??????????????????item
     * @param view     ??????????????????item
     * @param position item??????
     */
    @Override
    public void onItemClickListener(View parent, View view, int position) {
        int parentTag = (int) parent.getTag();
        int viewTag = (int) view.getTag();
        switch (parentTag) {
            case CustomValue.ITEM_NAVIGATION_TAG:
                if (viewTag == 1) {
                    launchByPackageName(CustomValue.PACKAGE_NAME_AUTONAVI);
                } else {
//                    checkPermissions();
                }
                break;
            case CustomValue.ITEM_MUSIC_TAG:
                if (viewTag == 1) {
                    openKuWO();
                } else {
                    upDataMusicStatus();
                }
                break;
            case CustomValue.ITEM_XIMALAYA_TAG:
                launchByPackageName(CustomValue.PACKAGE_NAME_XIMALAYA);
                break;
            case CustomValue.ITEM_FM_TAG:
                launchByPackageName(CustomValue.PACKAGE_NAME_KAOLA);
                break;
            case CustomValue.ITEM_SETTING_TAG:
                if (viewTag == 1) {
                    jumpByAction(Settings.ACTION_SETTINGS);
                }
                break;
            case CustomValue.ITEM_BLUETOOTH_TAG:
                if (viewTag == 1) {
                    jumpByAction(Settings.ACTION_BLUETOOTH_SETTINGS);
                }
                break;
            case CustomValue.ITEM_E_DOG_TAG:
                jumpToActivity(EcarTestActivity.class);
                break;
            case CustomValue.ITEM_DVR_TAG:
            case CustomValue.ITEM_PLAYBACK_TAG:
                jumpToActivity(TestActivity.class);
                break;
            case CustomValue.ITEM_FILE_MGT_TAG:
                jumpToFileManagement();
                break;
            case CustomValue.ITEM_APP_MGT_TAG:
                jumpToActivity(AppListActivity.class);
                break;
            default:
                break;
        }
    }

    /**
     * recyclerView adapter???????????????????????????????????????????????????
     *
     * @param mDataList
     */
    @Override
    public void upDataItemPosition(List<MusicData> mDataList) {
        changedData = mDataList;
    }

    private static class InnerHandler extends Handler {
        private final WeakReference<HomeActivity> activityWeakReference;
        private HomeActivity mActivity;

        InnerHandler(HomeActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
            this.mActivity = activityWeakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mActivity != null) {
                if (msg.what == HANDLE_MUSIC_INFO) {
//                    Music music = activity.getMusic();
//                        activity.upDataHomeMusicInfo(music, activity);
                }
            }
        }
    }


    /**
     * ????????????????????????
     *
     * @param packageName clicked app
     */
    private void launchByPackageName(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            Log.i(TAG, "package name is null!");
            return;
        }

        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent == null) {
            ToastUtils.showToast(mContext, R.string.app_not_install);
        } else {
            startActivity(launchIntent);
        }
    }

    /**
     * ??????????????????
     */
    private void openKuWO() {
        if (mKwApi != null) {
            mKwApi.startAPP(false);
        }
    }

    /**
     * ?????????????????????
     */
    public void initKwApi() {
        if (mKwApi == null) {
            mKwApi = KWAPI.createKWAPI(mContext, "auto");
        }

        mKwApi.registerEnterListener(new OnEnterListener() {
            @Override
            public void onEnter() {
                mKwApi.bindAutoSdkService();
                Log.d(TAG, "?????????????????????");
            }
        });
        mKwApi.registerExitListener(new OnExitListener() {
            @Override
            public void onExit() {
                mKwApi.unbindAutoSdkService();
            }
        });
        //?????????????????????????????????
        mKwApi.registerPlayerStatusListener(new OnPlayerStatusListener() {
            @Override
            public void onPlayerStatus(PlayerStatus arg0, Music music) {
                Log.d(TAG, "onPlayerStatus:status " + arg0.name());
                if (arg0 == PlayerStatus.PAUSE || arg0 == PlayerStatus.INIT) {
                    homeRecycleViewAdapter.setSelected(false);
                    homeRecycleViewAdapter.notifyItemChanged(getItemIndex(CustomValue.ITEM_MUSIC_TAG));
                } else if (arg0 == PlayerStatus.PLAYING) {
                    homeRecycleViewAdapter.setSelected(true);
                    homeRecycleViewAdapter.notifyItemChanged(getItemIndex(CustomValue.ITEM_MUSIC_TAG));
                }
                if (music != null) {
                    if (lastMusic == null || !music.name.equals(lastMusic.name)) {
                        lastMusic = music;
                        setMusic(music);
//                        mInnerHandler.sendEmptyMessage(HANDLE_MUSIC_INFO);
                        upDataHomeMusicInfo(mMusic);
                    }
                }
            }
        });
    }

    private void setMusic(Music music) {
        mMusic = music;
    }

    private Music getMusic() {
        return mMusic;
    }

    /**
     * ????????????????????????home??????item??????
     *
     * @param mMusic ???????????????
     */
    private void upDataHomeMusicInfo(Music mMusic) {
//        mKwApi.displayImage(mMusic,ivMusicIcon,R.id.iv_normal_icon);
        mKwApi.setMusicImg(mMusic, new OnGetSongImgListener() {
            @Override
            public void sendSyncNotice_HeadPicStart(Music music) {

            }

            @Override
            public void sendSyncNotice_HeadPicFinished(Music music, Bitmap bitmap) {
                homeRecycleViewAdapter.setMusic(music, bitmap);
                homeRecycleViewAdapter.notifyItemChanged(getItemIndex(CustomValue.ITEM_MUSIC_TAG));
            }

            @Override
            public void sendSyncNotice_HeadPicFailed(Music music) {

            }

            @Override
            public void sendSyncNotice_HeadPicNone(Music music) {

            }
        });
    }

    private void sendBroadCast() {
        Intent intent = new Intent();
        intent.setAction(CustomValue.ACTION_STANDARD);
        intent.putExtra("KEY_TYPE", 10008);
        sendBroadcast(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (!verifyPermissions(grantResults)) {
                showMissingPermissionDialog();
                isNeedCheck = false;
            } else {
                //??????????????????
//                mLocationClient.enableBackgroundLocation(2001, buildNotification());
            }
        }
    }

    /*    *//**
     * ??????8.0??????,????????????????????????????????????Notification
     *
     * @return
     *//*
    @SuppressLint({"NewApi", "ObsoleteSdkInt"})
    private Notification buildNotification() {
        Notification.Builder builder;
        Notification notification;
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            //Android O??????Notification?????????????????????????????????targetSDKVersion>=26???????????????????????????????????????
            if (null == notificationManager) {
                notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            String channelId = getPackageName();
            if (!isCreateChannel) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId,
                        NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.enableLights(true);//???????????????icon????????????????????????
                notificationChannel.setLightColor(Color.BLUE); //???????????????
                notificationChannel.setShowBadge(true); //??????????????????????????????????????????????????????
                notificationManager.createNotificationChannel(notificationChannel);
                isCreateChannel = true;
            }
            builder = new Notification.Builder(getApplicationContext(), channelId);
        } else {
            builder = new Notification.Builder(getApplicationContext());
        }
        builder.setSmallIcon(R.drawable.icon_navigation)
                .setContentTitle("????????????")
                .setContentText("??????????????????")
                .setWhen(System.currentTimeMillis());

        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification = builder.build();
        } else {
            return builder.getNotification();
        }
        return notification;
    }*/

    /**
     * ????????????
     */
    public void checkPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= 23
                    && getApplicationInfo().targetSdkVersion >= 23) {
                List<String> needRequestPermissionList = findDeniedPermissions(needPermissions);
                if (null != needRequestPermissionList
                        && needRequestPermissionList.size() > 0) {
                    String[] array =
                            needRequestPermissionList.toArray(new String[needRequestPermissionList.size()]);
                   /* Method method = getClass().getMethod("requestPermissions",
                            new Class[]{String[].class, int.class});
                    method.invoke(this, array, PERMISSION_REQUEST_CODE);*/
                    ActivityCompat.requestPermissions(this, array, PERMISSION_REQUEST_CODE);
                } else {
                    Log.d(TAG, "checkPermissions: OK");
                    //????????????????????????????????????????????????ID???????????????APP????????????
//                    mLocationClient.enableBackgroundLocation(2001, buildNotification());
                }
            }
        } catch (Throwable e) {
            Log.d(TAG, "checkPermissions: " + e.getMessage());
        }
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param permissions ????????????
     * @return
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissionList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 23
                && getApplicationInfo().targetSdkVersion >= 23) {
            try {
                for (String perm : permissions) {
                    if (ContextCompat.checkSelfPermission(mContext, perm) !=
                            PackageManager.PERMISSION_GRANTED) {
                        if (!needCheckBackLocation
                                && BACKGROUND_LOCATION_PERMISSION.equals(perm)) {
                            continue;
                        }
                        needRequestPermissionList.add(perm);
                    }
                }
            } catch (Throwable e) {
                Log.d(TAG, "findDeniedPermissions: " + e.getMessage());
            }


        }
        return needRequestPermissionList;
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param grantResults
     * @return
     */
    public boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    /**
     * ??????????????????
     */
    public void showMissingPermissionDialog() {
        if (permissionDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(R.string.notifyTitle);
            builder.setMessage(R.string.notifyMsg);
            builder.setNegativeButton(R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            builder.setPositiveButton(R.string.setting,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startAppSettings();
                        }
                    });
            builder.setCancelable(false);
            permissionDialog = builder.create();
        }
        permissionDialog.show();
    }

    /**
     * ?????????????????????
     *
     * @since 2.5.0
     */
    public void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setRecycleViewTouchListen() {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        isUp = true;
                        homeRecycleViewAdapter.removeCallback();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (Math.abs(mLastMotionX - x) > 50
                                || Math.abs(mLastMotionY - y) > 50) {
                            isMove = true;
                            homeRecycleViewAdapter.removeCallback();
                        }
                        break;
                    case MotionEvent.ACTION_DOWN:
                        isMove = false;
                        isUp = false;
                        mLastMotionX = x;
                        mLastMotionY = y;
                        break;
                }
                return false;
            }
        });
    }


    /**
     * ??????????????????????????????item?????????????????????
     */
    private void upDataMusicStatus() {
        if (mKwApi == null) {
            return;
        }
        musicStatus = mKwApi.getPlayerStatus();
        if (musicStatus == null) {
            return;
        }
        if (musicStatus == PlayerStatus.PAUSE) {
            mKwApi.setPlayState(PlayState.STATE_PLAY);
        } else if (musicStatus == PlayerStatus.PLAYING) {
            mKwApi.setPlayState(PlayState.STATE_PAUSE);
        } else if (musicStatus == PlayerStatus.INIT) {
            mKwApi.startAPP(true);
        }
    }

    /**
     * ???????????????,??????itemViewIndex????????????????????????item???position
     *
     * @param itemViewIndex ???????????????index
     * @return ??????
     */
    private int getItemIndex(int itemViewIndex) {
        if (changedData == null) {
            String json = sharedPreferences.getString(CustomValue.SP_KEY_SAVE_DATA, null);
            if (json == null) {
                return 0;
            }
            changedData = JSON.parseArray(json, MusicData.class);
        }
        int size = changedData.size();
        int tag;
        for (int i = 0; i < size; i++) {
            tag = changedData.get(i).getItemViewTag();
            if (tag == itemViewIndex) {
                return i;
            }
        }
        return 0;
    }

    /**
     * ???evenBus,??????????????? ?????????????????????
     */
    private void rxBus() {
        RxBus.getInstance().toObservable(MsgEvent.class).subscribe(new Observer<MsgEvent>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(MsgEvent msgEvent) {
                switch (msgEvent.getType()) {
                    case 1:
                        jumpToActivity(AppListActivity.class);
                        break;
                    case 2:
                        jumpToFileManagement();
                        break;
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

   /* //?????????????????????????????????
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    mLocation = aMapLocation.getAddress();
                    StringBuilder builder = new StringBuilder();
                    builder.append(aMapLocation.getProvider()).append("\n")//?????????
                            .append(aMapLocation.getDistrict()).append("\n")//????????????
                            .append(aMapLocation.getCity()).append("\n")
                            .append(aMapLocation.getDistrict()).append("\n")//????????????
                            .append(aMapLocation.getStreet()).append("\n")//????????????
                            .append(aMapLocation.getStreetNum()).append("\n")//?????????????????????
                            .append(aMapLocation.getAdCode()).append("\n")//????????????
                            .append(aMapLocation.getAoiName());//????????????????????????AOI??????
                    Log.d(TAG, "onLocationChanged: " + builder.toString());
                    builder = null;
                    mLocationClient.stopLocation();
                    mInnerHandler.sendEmptyMessage(HANDLE_LOCATION);
                } else {
                    //???????????????????????????ErrCode????????????????????????????????????????????????errInfo???????????????????????????????????????
                    Log.e("onLocationChanged Error", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };*/

    /**
     * ?????????????????????
     */
    private void initGaoDePosition() {
       /* mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationOption = new AMapLocationClientOption();
        mLocationClient.setLocationListener(mLocationListener);
        //?????????AMapLocationClientOption??????
*//*        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(mLocationOption);
            //???????????????????????????????????????stop????????????start???????????????????????????
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }*//*

        //????????????3s???????????????????????????????????????
        // ?????????true??????????????????SDK???????????????3s?????????????????????????????????????????????????????????true???
        // setOnceLocation(boolean b)????????????????????????true???????????????????????????false???
        mLocationOption.setOnceLocationLatest(true);
        //??????????????????,????????????,?????????2000ms?????????1000ms???
        mLocationOption.setInterval(2000);
        //????????????????????????????????????????????????????????????
        mLocationOption.setNeedAddress(true);
    *//*    //????????????????????????30000???????????????????????????????????????8000?????????
        mLocationOption.setHttpTimeOut(20000);*//*
//        //???????????????:Hight_Accuracy ???????????????:Device_Sensors ???????????????:Battery_Saving
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //?????????????????????????????????????????????false???
        mLocationOption.setOnceLocation(false);
        //??????????????????????????????,?????????false????????????????????????
        mLocationOption.setMockEnable(false);
        //????????????????????????WIFI????????????????????????
        mLocationOption.setWifiActiveScan(true);
        //??????????????????????????????????????????
        mLocationClient.setLocationOption(mLocationOption);*/
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadCastReceiver();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        getContentViewWidth(HomeActivity.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(gaoDeBroadcastReceiver);
        if (permissionDialog != null) {
            permissionDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        mKwApi.unRegisterEnterListener();
        mKwApi.unRegisterExitListener();
        mKwApi.unRegisterPlayerStatusListener();
        mKwApi.unbindAutoSdkService();
        mKwApi.unBindKuWoApp();
        /*if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
            mLocationClient = null;
        }*/
        super.onDestroy();
        mDataList.clear();
        mDataList = null;
        if (homeRecycleViewAdapter != null) {
            homeRecycleViewAdapter.setHandle(null);
        }
        mInnerHandler.removeCallbacksAndMessages(null);
        TXZPowerManager.getInstance().notifyPowerAction(POWER_ACTION_BEFORE_SLEEP);
        TXZPowerManager.getInstance().releaseTXZ();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
    }

}
