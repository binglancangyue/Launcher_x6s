package com.cywl.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cywl.launcher.listener.GaoDeCallBackListener;
import com.cywl.launcher.model.CustomValue;

public class GaoDeBroadCastReceiver extends BroadcastReceiver {
    private final static String TAG = "GaoDeBroadCastReceiver";
    private GaoDeCallBackListener listener;

    public GaoDeBroadCastReceiver(GaoDeCallBackListener callBackListener) {
        this.listener = callBackListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            return;
        }
        if (intent.getAction().equals(CustomValue.ACTION_STANDARD)) {
            int keyType = intent.getIntExtra(("KEY_TYPE"), -1);
            if (keyType == -1) {
                return;
            }

            Log.d(TAG, "onReceive: "+keyType+" "+intent.getStringExtra(keyType+""));
            if (listener != null) {
                listener.upDataInfo();
            }
        }
    }
}