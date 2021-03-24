package com.cywl.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cywl.launcher.listener.OnAppRemoveListener;

public class AppRemoveAddedReceiver extends BroadcastReceiver {
    private final static String TAG = "AppRemoveAddedReceiver";
    private OnAppRemoveListener onAppRemoveListener;

    public void setOnAppRemoveListener(OnAppRemoveListener listener) {
        onAppRemoveListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            return;
        }
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
            Log.d(TAG, "onReceive: ACTION_PACKAGE_REMOVED");
            return;
        }
        if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
            Log.d(TAG, "onReceive: ACTION_PACKAGE_ADDED");
            return;
        }
        if (action.equals(Intent.ACTION_INSTALL_PACKAGE)) {
            Log.d(TAG, "onReceive: ACTION_INSTALL_PACKAGE");
            return;
        }
        if (action.equals(Intent.ACTION_UID_REMOVED)) {
            Log.d(TAG, "onReceive: ACTION_UID_REMOVED");
            if (onAppRemoveListener == null) {
                return;
            }
            onAppRemoveListener.onAppRemove();
            return;
        }
    }

}
