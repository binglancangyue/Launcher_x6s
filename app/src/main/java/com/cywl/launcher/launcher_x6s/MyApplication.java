package com.cywl.launcher.launcher_x6s;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.bumptech.glide.request.target.ViewTarget;
import com.cywl.launcher.model.AppInfo;
import com.cywl.launcher.model.CustomValue;
import com.txznet.sdk.TXZConfigManager;
import com.txznet.sdk.TXZTtsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyApplication extends MultiDexApplication implements TXZConfigManager.InitListener,
        TXZConfigManager.ActiveListener {
    public ArrayList<AppInfo> mAppList = new ArrayList<>();
    private static MyApplication instance;
    TXZConfigManager.InitParam mInitParam;

    public static MyApplication getApp() {
        return instance;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        instance = this;
        ViewTarget.setTagId(R.id.glideIndexTag);
        init();
    }

    public void init() {
        new Thread(() -> {
            initTXZ();
//            initAppList();
        }).start();
    }

    public void initAppList() {
        if (mAppList.size() > 0) {
            mAppList.clear();
        }
        PackageManager pm = getPackageManager();
        Intent main = new Intent(Intent.ACTION_MAIN, null);
        main.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> apps = pm.queryIntentActivities(main, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(pm));
        if (apps != null) {
            for (ResolveInfo resolveInfo : apps) {
                AppInfo info = new AppInfo();
                ActivityInfo activityInfo = resolveInfo.activityInfo;
                if (ignoreApp(activityInfo.packageName)) {
                    info.setAppName(resolveInfo.loadLabel(pm).toString());
                    info.setPkgName(activityInfo.packageName);
                    info.setFlags(activityInfo.flags);
                    info.setAppIcon(activityInfo.loadIcon(pm));
                    info.setAppintent(pm.getLaunchIntentForPackage(activityInfo.packageName));
                    mAppList.add(info);
                }
//                Log.d("MyApplication",
//                        "AppList apps info: " + activityInfo.packageName);
            }
        }
    }

    public ArrayList<AppInfo> getShowAppList() {
        if (mAppList.size() <= 0) {
            initAppList();
        }
        return mAppList;
    }

    private void initTXZ() {
        //  获取接入分配的appId和appToken
        String appId = this.getResources().getString(
                R.string.txz_sdk_init_app_id);
        String appToken = this.getResources().getString(
                R.string.txz_sdk_init_app_token);
        //  设置初始化参数
        mInitParam = new TXZConfigManager.InitParam(appId, appToken);
        //  可以设置自己的客户ID，同行者后台协助计数/鉴权
        // mInitParam.setAppCustomId("ABCDEFG");
        //  可以设置自己的硬件唯一标识码
        // mInitParam.setUUID("0123456789");

        //  设置识别和tts引擎类型
        mInitParam.setAsrType(TXZConfigManager.AsrEngineType.ASR_YUNZHISHENG).setTtsType(
                TXZConfigManager.TtsEngineType.TTS_YUNZHISHENG);
        //  设置唤醒词
        String[] wakeupKeywords = this.getResources().getStringArray(
                R.array.txz_sdk_init_wakeup_keywords);
        mInitParam.setWakeupKeywordsNew(wakeupKeywords);
        // 19.7.24  跑模拟器时显示语音按钮
        // 掩藏语音按钮
//        mInitParam.setFloatToolType(FLOAT_NONE);

        //  可以按需要设置自己的对话模式
        // mInitParam.setAsrMode(AsrMode.ASR_MODE_CHAT);
        //  设置识别模式，默认自动模式即可
        // mInitParam.setAsrServiceMode(AsrServiceMode.ASR_SVR_MODE_AUTO);
        //  设置是否允许启用服务号
        // mInitParam.setEnableServiceContact(true);
        //  设置开启回音消除模式
        mInitParam.setFilterNoiseType(1);
        //  其他设置

        //  初始化在这里
        TXZConfigManager.getInstance().initialize(this, mInitParam, this, this);
//        TXZConfigManager.getInstance().initialize(this, this);
    }

    private boolean ignoreApp(String pkgName) {
        if (pkgName.equals(CustomValue.PACKAGE_NAME_AUTONAVI)
//                pkgName.equals("com.cywl.launcher.launcher_x6s")
                || pkgName.equals("com.android.settings")
                || pkgName.equals("com.google.android.deskclock")
                || pkgName.equals("com.android.documentsui")
                || pkgName.equals("com.ximalaya.ting.android.car")
                || pkgName.equals("com.edog.car")
                || pkgName.equals("cn.kuwo.kwmusiccar")) {
            return true;
        }
        return false;
    }

    @Override
    public void onFirstActived() {
        //  首次联网激活，如果需要出厂激活提示，可以在这里完成
    }

    @Override
    public void onSuccess() {
        // 掩藏语音按钮
//        TXZConfigManager.getInstance().showFloatTool(FLOAT_NONE);
        TXZTtsManager.getInstance().speakText("引擎初始化成功 阿依西的路");
    }

    @Override
    public void onError(int i, String s) {
        //  初始化出错
        Log.d("a", "txz onError: " + s);
        TXZTtsManager.getInstance().speakText("初始化出错");
    }
}
