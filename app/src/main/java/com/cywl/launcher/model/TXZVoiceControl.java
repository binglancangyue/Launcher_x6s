package com.cywl.launcher.model;

import com.cywl.launcher.utils.RxBus;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZTtsManager;

public class TXZVoiceControl {
    public TXZVoiceControl() {
        customRegistration();
        customCommandListener();
    }

    private void customRegistration() {
        TXZAsrManager.getInstance().regCommand(new String[]{"打开应用管理", "打开应用管理器"},
                "CMD_OPEN_APP_MGT");
        TXZAsrManager.getInstance().regCommand(new String[]{"打开文件管理", "打开文件管理器"},
                "CMD_OPEN_FILE_MGT");
        TXZAsrManager.getInstance().regCommand(new String[]{"打开电子狗"},
                "CMD_OPEN_E_DOG");
        TXZAsrManager.getInstance().regCommand(new String[]{"打开视频回放"},
                "CMD_OPEN_V_PLAYBACK");
        TXZAsrManager.getInstance().regCommand(new String[]{"打开西瓜"}, "CMD_XIGUA_OPEN");
        TXZAsrManager.getInstance().regCommand(new String[]{"吃饭了"}, "CMD_HUIJIA_OPEN");
        TXZAsrManager.getInstance().regCommand(new String[]{"去洗澡"}, "CMD_SHUI_OPEN");
        customCommandListener();
    }

    private void customCommandListener() {
        TXZAsrManager.getInstance().addCommandListener(new TXZAsrManager.CommandListener() {
            @Override
            public void onCommand(String cmd, String data) {
                if (data.equals("CMD_OPEN_APP_MGT")) {
                    toSpeakText("APP管理已打开");
                    RxBus.getInstance().post(new MsgEvent(1, "1"));
                    return;
                }
                if (data.equals("CMD_OPEN_FILE_MGT")) {
                    RxBus.getInstance().post(new MsgEvent(2, "2"));
                    toSpeakText("文件管理已打开");
                    return;
                }
                if (data.equals("CMD_OPEN_E_DOG")) {
                    toSpeakText("电子狗已打开");
                    return;
                }
                if (data.equals("CMD_OPEN_V_PLAYBACK")) {
                    toSpeakText("视频回放已打开");
                    return;
                }
                if (data.equals("CMD_XIGUA_OPEN")) {
                    toSpeakText("西瓜已切好");
                    return;
                }
                if (data.equals("CMD_HUIJIA_OPEN")) {
                    toSpeakText("马上回家");
                    return;
                }
                if (data.equals("CMD_SHUI_OPEN")) {
                    toSpeakText("浴室已准备好");
                }
            }
        });
    }

    private void toSpeakText(String s) {
        TXZTtsManager.getInstance().speakText(s);
    }
}
