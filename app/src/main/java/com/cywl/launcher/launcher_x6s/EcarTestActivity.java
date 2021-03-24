package com.cywl.launcher.launcher_x6s;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.cywl.launcher.receiver.EcarCaptureReceiver;
import com.cywl.launcher.utils.ECarUtils;

public class EcarTestActivity extends Activity implements View.OnClickListener {
    private ECarUtils carUtils;
    private EcarCaptureReceiver ecarRecordFinishReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ecar_test);
        carUtils = new ECarUtils(this);
        carUtils.startAppManagerService();
        carUtils.startBootService();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadCastReceiver();
    }

    private void initView() {
        Button buttonChat = findViewById(R.id.btn_peiliao);
        Button btnRoade = findViewById(R.id.btn_Roade);
        Button btnRescue = findViewById(R.id.btn_Rescue);
        Button btnCall = findViewById(R.id.btn_call);
        Button btnStartECar = findViewById(R.id.btn_StartECar);
        Button btnAssistant = findViewById(R.id.btn_Assistant);
        Button btnLawService = findViewById(R.id.btn_Law_service);
        Button btnCarTrouble = findViewById(R.id.btn_CarTrouble);
        Button btnGoToPrivilegePage = findViewById(R.id.btn_GoToPrivilegePage);
        Button btnStartECarRenew = findViewById(R.id.btn_StartECarRenew);
        Button btnACC_ON = findViewById(R.id.btn_ACC_ON);
        Button btnACC_OFF = findViewById(R.id.btn_ACC_OFF);
        Button btnQuake = findViewById(R.id.btn_quake);
        Button btnRecordFinish = findViewById(R.id.btn_record_finish);
        Button btnWriteData = findViewById(R.id.btn_write_data);
        btnRescue.setOnClickListener(this);
        btnCall.setOnClickListener(this);
        btnAssistant.setOnClickListener(this);
        btnCarTrouble.setOnClickListener(this);
        btnGoToPrivilegePage.setOnClickListener(this);
        btnACC_ON.setOnClickListener(this);
        btnACC_OFF.setOnClickListener(this);
        btnQuake.setOnClickListener(this);
        buttonChat.setOnClickListener(this);
        btnRoade.setOnClickListener(this);
        btnStartECarRenew.setOnClickListener(this);
        btnStartECar.setOnClickListener(this);
        btnLawService.setOnClickListener(this);
        btnRecordFinish.setOnClickListener(this);
        btnWriteData.setOnClickListener(this);
    }

    /**
     * 注册高德广播
     */
    private void registerBroadCastReceiver() {
        ecarRecordFinishReceiver = new EcarCaptureReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ECarUtils.ACTION_ECAR_CAPTURE);
        registerReceiver(ecarRecordFinishReceiver, filter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_peiliao:
                carUtils.sendEcarBroadcast(carUtils.ECAR_KEY_CHAT);
                break;
            case R.id.btn_Roade:
                carUtils.sendEcarBroadcast(carUtils.ECAR_KEY_ROADE);
                break;
            case R.id.btn_Rescue:
                carUtils.sendEcarBroadcast(carUtils.ECAR_KEY_RESCUE);
                break;
            case R.id.btn_call:
                carUtils.sendEcarBroadcast(carUtils.ECAR_KEY_MAKECALL);
                break;
            case R.id.btn_StartECar:
                carUtils.sendEcarBroadcast(carUtils.ECAR_KEY_STARTECAR);
                break;
            case R.id.btn_Assistant:
                carUtils.sendEcarBroadcast(carUtils.ECAR_KEY_ASSISTANT);
                break;
            case R.id.btn_Law_service:
                carUtils.sendEcarBroadcast(carUtils.ECAR_KEY_LAW_SERVICE);
                break;
            case R.id.btn_CarTrouble:
                carUtils.sendEcarBroadcast(carUtils.ECAR_KEY_CARTROUBLE);
                break;
            case R.id.btn_GoToPrivilegePage:
                carUtils.sendEcarBroadcast(carUtils.ECAR_KEY_GOTOPRIVILEGEPAGE);
                break;
            case R.id.btn_StartECarRenew:
                carUtils.sendEcarBroadcast(carUtils.ECAR_KEY_STARTECARRENEW);
                break;
            case R.id.btn_ACC_ON:
                carUtils.sendEcarAccOnOrOffBroadcast("ACC_ON", null);
                break;
            case R.id.btn_ACC_OFF:
                carUtils.sendEcarAccOnOrOffBroadcast("ACC_OFF", null);
                break;
            case R.id.btn_quake:
                carUtils.sendEcarQuakeBroadcast(3, 5, 1);
                break;
            case R.id.btn_record_finish:
//                carUtils.sendEcarRecordFinishBroadcast();
                break;
            case R.id.btn_write_data:
                carUtils.sendVedioDataBroadcast();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ecarRecordFinishReceiver != null) {
            unregisterReceiver(ecarRecordFinishReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (carUtils != null) {
            carUtils.stopAppManagerService();
            carUtils.stopBootService();
        }
    }
}
