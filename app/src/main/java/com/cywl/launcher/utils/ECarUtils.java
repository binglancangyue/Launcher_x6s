package com.cywl.launcher.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import static com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest.TAG;

public class ECarUtils {
    private Context mContext;
    public static final String APPMANAGER_SERVICE_PACKGE = "com.ecar.AppManager";
    public static final String APPMANAGER_SERVICE_PACKGE_NAME =
            "com.ecar.AppManager.AppManagerService";
    public static final String BOOT_SERVICE_PACKGE = "com.ecar.assistantnew";
    public static final String BOOT_SERVICE_PACKGE_NAME =
            "com.ecar.assistantnew.service.BootService";
    public static final String ACTION_RECV_VOID = "com.android.ecar.recv";
    public static final String ACTION_ECAR_CAPTURE = "com.ecar.ACTION_CAPTURE_START";
    public static final String ACTION_ECAR_QUAKE = "com.service.intent.action.ACTION_DETECT_QUAKE";
    public static final String ACTION_ECAR_RECORD_FINISH = "com.service.ACTION_WX_RECORD_FINISH";
    public static final String ACTION_ECAR_WRITE_DATA = "com.ecar.video.broadcast";
    public static final String ACTION_SPEAK = "com.android.ecar.send";

    public String ECAR_KEY_CHAT = "Chat";//陪聊
    public String ECAR_KEY_ROADE = "Roade";//道路救援
    public String ECAR_KEY_RESCUE = "Rescue";//生命救援
    public String ECAR_KEY_MAKECALL = "MakeCall";//打电话
    public String ECAR_KEY_STARTECAR = "StartECar";//打开行车 SOS
    public String ECAR_KEY_ASSISTANT = "Assistant";//事故协助
    public String ECAR_KEY_LAW_SERVICE = "Law_service";//违章查询
    public String ECAR_KEY_CARTROUBLE = "CarTrouble";//故障咨询
    public String ECAR_KEY_GOTOPRIVILEGEPAGE = "GoToPrivilegePage";//我的特权
    public String ECAR_KEY_STARTECARRENEW = "StartECarRenew";//我要续费、续费升级、会员续费

    public static final String SD_CARD_PATH = "/storage/Tfcard/DCIM/Camera";


    public ECarUtils(Context context) {
        this.mContext = context;
    }

    public void startAppManagerService() {
        if (mContext != null) {
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName(APPMANAGER_SERVICE_PACKGE,
                    APPMANAGER_SERVICE_PACKGE_NAME);
            intent.setComponent(componentName);
            mContext.startService(intent);
            ToastUtils.showToast(mContext, "startAppManagerService");

        }
    }

    public void startBootService() {
        if (mContext != null) {
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName(BOOT_SERVICE_PACKGE,
                    BOOT_SERVICE_PACKGE_NAME);
            intent.setComponent(componentName);
            mContext.startService(intent);
            ToastUtils.showToast(mContext, "startBootService");
        }
    }

    public void stopAppManagerService() {
        if (mContext != null) {
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName(APPMANAGER_SERVICE_PACKGE,
                    APPMANAGER_SERVICE_PACKGE_NAME);
            intent.setComponent(componentName);
            mContext.stopService(intent);
        }
    }

    public void stopBootService() {
        if (mContext != null) {
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName(BOOT_SERVICE_PACKGE,
                    BOOT_SERVICE_PACKGE_NAME);
            intent.setComponent(componentName);
            mContext.stopService(intent);
        }
    }

    /**
     * @param ecarSendValue 执行的操作
     */
    public void sendEcarBroadcast(String ecarSendValue) {
        if (mContext != null) {
            Intent intent = new Intent(ACTION_RECV_VOID);
            intent.putExtra("ecarSendKey", ecarSendValue);
            mContext.sendBroadcast(intent);
        }
    }

    /**
     * @param cameraId 0 为前录，1 为后录,2 为双录
     * @param path     所拍摄视频的路径
     * @param sid      任务Id
     * @param errcode  抓拍失败错误码
     * @param size     抓拍的视频或者图片的分辨率
     */
    public void sendEcarRecordFinishBroadcast(int cameraId, String path, String sid, int errcode,
                                              String size) {
        Intent intent = new Intent();
        intent.setAction(ACTION_ECAR_RECORD_FINISH);
        intent.putExtra("from", "ecar");
        intent.putExtra("index", cameraId);
        intent.putExtra("path", path);
        intent.putExtra("from", "ecar");
        intent.putExtra("sid", sid);
        intent.putExtra("size", size);
        intent.putExtra("errcode", errcode);
    }

    /**
     * 震动提醒广播接口(睡眠）
     *
     * @param level 震动等级
     * @param max   振动器的最大值
     * @param min   振动器的最小值
     */
    public void sendEcarQuakeBroadcast(int level, int max, int min) {
        if (mContext != null) {
            Intent intent = new Intent(ACTION_ECAR_QUAKE);
            intent.putExtra("level", level);
            intent.putExtra("max", max);
            intent.putExtra("min", min);
            mContext.sendBroadcast(intent);
        }
    }

    /**
     * ACC OFF(熄火)广播 or ACC ON(点火)广播
     * <p>
     * 车机设备都有 ACC ON(点火)和 ACC OFF(熄火)消息,方案商需要在检测到点火和熄火消息时,发送一次广播通知翼卡应用;
     *
     * @param ecarSendValue ACC_ON or ACC_OFF
     * @param cmdTypeValue  standCMD
     */
    public void sendEcarAccOnOrOffBroadcast(String ecarSendValue, String cmdTypeValue) {
        if (mContext != null) {
            sendTTSSpeakBroadcast("点火");

            Intent intent = new Intent(ACTION_RECV_VOID);
            intent.putExtra("ecarSendKey", ecarSendValue);
            intent.putExtra("cmdType", "standCMD");
            intent.putExtra("keySet", "");
            mContext.sendBroadcast(intent);
        }
    }

    /**
     * 申请 MIC 接口
     * <p>
     * 当车机为单麦并被语音（或蓝牙）占用情况下,建立行车 SOS 的相关服务时，
     * 后台客服将无法听到用户的声音,导致服务失败，需要系统或语音释放 MIC;
     * a.翼卡应用开始拨号时,发送(“EcarCallStart”)广播消息;
     * b.翼卡应用结束通话时,发送(“EcarCallEnd”)广播消息;
     *
     * @param cmdTypeValue EcarCallStart or EcarCallEnd
     */
    public void sendEcarCallStartOrEndBroadcast(String cmdTypeValue) {
        if (mContext != null) {
            Intent tmpIntent = new Intent();
            tmpIntent.putExtra("cmdType", cmdTypeValue);
            tmpIntent.putExtra("cmdType", "standCMD");
            tmpIntent.putExtra("keySet", "");
            mContext.sendBroadcast(tmpIntent);
        }
    }

    /**
     * 写入共享内存的视频数据格式
     * 方案商从摄像头拿到视频数据，将其写入共享内存，翼卡通过 binder 通信读取视频数据。
     * 方案商需定义写入数据 Service 的名称由翼卡来绑定，并持续循环写入。翼卡绑定Service 的
     * package 是 “com.zqc.camera ” ,class 名称是“com.zqc.camera.other.ShareService”
     * 广播的方式
     */
    public void sendVedioDataBroadcast() {
        if (mContext != null) {
            Intent broadCastIntent = new Intent();
            broadCastIntent.setAction(ACTION_ECAR_WRITE_DATA);
//            broadCastIntent.putExtra("uacceler", uacceler++);
            Bundle broadCastBundle = new Bundle();
            broadCastBundle.putInt("HEIGHT", 1280);
            broadCastBundle.putInt("WIDTH", 720);
            broadCastBundle.putInt("FRAME", 24);
            broadCastBundle.putString("FROMAT", "YUV21");
            broadCastIntent.putExtras(broadCastBundle);
            mContext.sendBroadcast(broadCastIntent);
        }
    }

    public void sendTTSSpeakBroadcast(String text) {
        if (mContext != null) {
            Intent tmpIntent = new Intent(ACTION_SPEAK);
            tmpIntent.putExtra("ecarSendKey", "TTSSpeak");
            tmpIntent.putExtra("cmdType", "standCMD");
            tmpIntent.putExtra("keySet", "text");
            tmpIntent.putExtra("text", text);
            mContext.sendBroadcast(tmpIntent);
        }
    }

}
