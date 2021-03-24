package com.cywl.launcher.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.cywl.launcher.launcher_x6s.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CheckPermissionsUtils {
    private Context mContext;
    /**
     * 判断是否需要检测，防止不停的弹框
     */
    public boolean isNeedCheck = true;
    private static String BACKGROUND_LOCATION_PERMISSION = "android.permission" +
            ".ACCESS_BACKGROUND_LOCATION";
    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    public static final int PERMISSON_REQUESTCODE = 0;
    //是否需要检测后台定位权限，设置为true时，如果用户没有给予后台定位权限会弹窗提示
    private boolean needCheckBackLocation = false;

    /*
        @Override
        protected void onResume() {
            super.onResume();
            if (Build.VERSION.SDK_INT >= 23
                    && getApplicationInfo().targetSdkVersion >= 23) {
                if (isNeedCheck) {
                    checkPermissions(needPermissions);
                }
            }
        }*/
    public CheckPermissionsUtils(Context context) {
        this.mContext = context;
    }

    /**
     * @param
     * @since 2.5.0
     */
    public void checkPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= 23
                    && mContext.getApplicationInfo().targetSdkVersion >= 23) {
                List<String> needRequestPermissonList = findDeniedPermissions(needPermissions);
                Log.d("ReboundHScrollView", "checkPermissions: a" + needRequestPermissonList.size());
                if (null != needRequestPermissonList
                        && needRequestPermissonList.size() > 0) {
                    String[] array =
                            needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
                    Method method = getClass().getMethod("requestPermissions",
                            new Class[]{String[].class, int.class});
                    Log.d("ACTION_aa", "checkPermissions: ");
                    method.invoke(this, array, PERMISSON_REQUESTCODE);
                }
            }
        } catch (Throwable e) {
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        Log.d("a", "findDeniedPermissions: " + permissions.length);
        List<String> needRequestPermissonList = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= 23
                && mContext.getApplicationInfo().targetSdkVersion >= 23) {
            try {
                for (String perm : permissions) {
//                    Method checkSelfMethod = getClass().getMethod("checkSelfPermission",
//                            String.class);
//                    Method shouldShowRequestPermissionRationaleMethod = getClass().getMethod(
//                            "shouldShowRequestPermissionRationale",
//                            String.class);
                    if (ContextCompat.checkSelfPermission(mContext, perm) !=
                            PackageManager.PERMISSION_GRANTED) {
                        Log.d("ReboundHScrollView", "findDeniedPermissions:ReboundHScrollView ");
                        if (!needCheckBackLocation
                                && BACKGROUND_LOCATION_PERMISSION.equals(perm)) {
                            continue;
                        }
                        needRequestPermissonList.add(perm);
                    }
                }
            } catch (Throwable e) {
                Log.d("ACTION_aa", "findDeniedPermissions: " + e.getMessage());
            }
        }
        return needRequestPermissonList;
    }

    /**
     * 检测是否所有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    public boolean verifyPermissions(int[] grantResults) {
        Log.d("ACTION_aa", "verifyPermissions: ");
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

/*    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                showMissingPermissionDialog();
                isNeedCheck = false;
            }
        }
    }*/

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    public void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.notifyTitle);
        builder.setMessage(R.string.notifyMsg);

        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
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

        builder.show();
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    public void startAppSettings() {
        Log.d("ACTION_aa", "startAppSettings: ");
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + mContext.getPackageName()));
        mContext.startActivity(intent);
    }
}
