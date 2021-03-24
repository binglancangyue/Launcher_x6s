package com.cywl.launcher.model;

import com.cywl.launcher.launcher_x6s.R;

public class CustomValue {
    public static final String PACKAGE_NAME_AUTONAVI = "com.autonavi.amapautolite";//车镜版
    // com.autonavi.amapautolite 车载版:com.autonavi.amapauto
    public final static String ACTION_STANDARD = "AUTONAVI_STANDARD_BROADCAST_RECV";
    public final static String PACKAGE_NAME_XIMALAYA = "com.ximalaya.ting.android.car";
    public final static String PACKAGE_NAME_KAOLA = "com.edog.car";//电子狗

    public final static int[] HOME_ICON = {
            R.drawable.icon_navigation, R.drawable.icon_home_music,
            R.drawable.icon_home_ximalaya, R.drawable.icon_radio,
            R.drawable.icon_bluetooth, R.drawable.icon_home_setting,
            R.drawable.icon_home_dog, R.drawable.icon_vip,
            R.drawable.icon_home_video, R.drawable.icon_file_management,
            R.drawable.icon_app_management
    };
    /* public final static int[] HOME_FIRST_STRING = {
             R.string.home_first_navigation, R.string.home_first_music,
             R.string.home_first_radio, R.string.home_first_fm,
             R.string.home_first_bluetooth, R.string.home_first_sos,
             R.string.home_first_setting, R.string.home_first_personal_center,
             R.string.home_first_electronic_dog, R.string.home_first_dvr,
             R.string.home_first_playback};*/
    public final static int[] HOME_FIRST_STRING = {
            R.string.home_first_navigation, R.string.home_first_music,
            R.string.home_first_radio, R.string.home_first_fm,
            R.string.home_first_bluetooth, R.string.home_first_setting,
            R.string.home_first_electronic_dog, R.string.home_first_dvr,
            R.string.home_first_playback, R.string.home_first_file_management,
            R.string.home_first_app_management};

    public final static int[] HOME_SECOND_STRING = {
            R.string.home_second_navigation, R.string.home_second_music,
            R.string.home_second_radio, R.string.home_second_fm,
            R.string.home_second_bluetooth, R.string.home_second_setting,
            R.string.home_second_electronic_dog, R.string.home_second_dvr,
            R.string.home_second_playback, R.string.home_second_file_management,
            R.string.home_second_app_management};

    public final static int[] HOME_BUTTON_ICON = {
            R.drawable.button_navigation, R.drawable.selector_music_status,
            R.drawable.selector_music_status, R.drawable.selector_music_status,
            R.drawable.button_navigation, R.drawable.button_navigation,
            R.drawable.button_navigation, R.drawable.button_vip,
            R.drawable.button_navigation, R.drawable.button_camera,
            R.drawable.button_navigation};


    /*sharePreference key*/
    public final static String SP_NAME = "DATE";
    public final static String SP_KEY_SAVE_DATA = "SAVE_DATA";

    /*item layout adapter index 区分item布局监听的index*/
    public final static int ITEM_NAVIGATION_TAG = 0;
    public final static int ITEM_MUSIC_TAG = 1;
    public final static int ITEM_XIMALAYA_TAG = 2;
    public final static int ITEM_FM_TAG = 3;
    public final static int ITEM_BLUETOOTH_TAG = 4;
    public final static int ITEM_SETTING_TAG = 5;
    public final static int ITEM_E_DOG_TAG = 6;
    public final static int ITEM_DVR_TAG = 7;
    public final static int ITEM_PLAYBACK_TAG = 8;
    public final static int ITEM_FILE_MGT_TAG = 9;
    public final static int ITEM_APP_MGT_TAG = 10;

    //布局类型 0:normal 1:app box 2:surfaceView
    public final static int[] ITEM_VIEW_TYPE = {0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0};
    //item默认设置的index,用于拖拽后的item的识别
    public final static int[] ITEM_VIEW_INDEX = {
            ITEM_NAVIGATION_TAG, ITEM_MUSIC_TAG,
            ITEM_XIMALAYA_TAG, ITEM_FM_TAG,
            ITEM_BLUETOOTH_TAG, ITEM_SETTING_TAG,
            ITEM_E_DOG_TAG, ITEM_DVR_TAG,
            ITEM_PLAYBACK_TAG, ITEM_FILE_MGT_TAG,
            ITEM_APP_MGT_TAG};
    public final static boolean openLongPressDelete = false;//应用列表是否长按删除应用
    public final static boolean openDelayLongPress = true;//是否延迟4秒
}
