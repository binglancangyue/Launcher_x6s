package com.cywl.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest.TAG;

/**
 * action：抓拍图片:takepicture 抓拍视频:takevideo
 * path：完成抓拍的文件路径
 * from：来源，为"ecar"
 * sid：抓拍的任务id
 * time：视频的时间长度
 */
public class EcarCaptureReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra("action");
        String from = intent.getStringExtra("from");
        String path = intent.getStringExtra("path");
        String sid = intent.getStringExtra("sid");
        int time = intent.getIntExtra("time", -1);
        Log.d(TAG, "onReceive:任务源: " + from + "\n 路径: " + path + "\n " +
                "任务ID: " + sid + "\n 错误码: " + time + "\n action: " + action);
    }
}
