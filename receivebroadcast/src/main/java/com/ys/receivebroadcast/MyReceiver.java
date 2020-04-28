package com.ys.receivebroadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.io.File;

public class MyReceiver extends BroadcastReceiver {
    //继电器控制
    private static final String RELAY_3288 = "/sys/class/custom_class/custom_dev/relay";
    //绿色补光灯
    private static final String GREEN_LIGHT_3288 = "/sys/class/custom_class/custom_dev/green_led";
    //红色补光灯
    private static final String RED_LIGHT_3288 = "/sys/class/custom_class/custom_dev/red_led";
    //白色补光灯
    private static final String WHITE_LIGHT_3288 = "/sys/class/custom_class/custom_dev/white_led";
    private Handler handler;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        handler = new Handler();
        Log.d("hongruan","action = " + action);
        if ("com.arcsoft.arcfacesingle.ACTION_OPEN_DOOR".equals(action)) {
            Utils.writeStringFileFor7(new File(RELAY_3288),"1");
        } else if ("com.arcsoft.arcfacesingle.ACTION_CLOSE_DOOR".equals(action)) {
            Utils.writeStringFileFor7(new File(RELAY_3288),"0");
        } else if ("com.arcsoft.arcfacesingle.ACTION_TURN_ON_RED_LIGHT".equals(action)) {
            if ("1".equals(Utils.readGpioPG(GREEN_LIGHT_3288)))
                Utils.writeStringFileFor7(new File(GREEN_LIGHT_3288),"0");

            Utils.writeStringFileFor7(new File(RED_LIGHT_3288),"1");
            handler.postDelayed(closeRed,1500);
        } else if ("com.arcsoft.arcfacesingle.ACTION_TURN_ON_GREEN_LIGHT".equals(action)) {
            if ("1".equals(Utils.readGpioPG(RED_LIGHT_3288)))
                Utils.writeStringFileFor7(new File(RED_LIGHT_3288),"0");

            Utils.writeStringFileFor7(new File(GREEN_LIGHT_3288),"1");
            handler.postDelayed(closeGreen,1500);
        } else if ("com.arcsoft.arcfacesingle.ACTION_START_IDENTIFY".equals(action)) {
            Utils.writeStringFileFor7(new File(WHITE_LIGHT_3288),"1");
        } else if ("com.arcsoft.arcfacesingle.ACTION_STOP_IDENTIFY".equals(action)) {
            Utils.writeStringFileFor7(new File(WHITE_LIGHT_3288),"0");
        } else if ("com.arcsoft.arcfacesingle.ACTION_FACE_DETECT_HAS_FACE".equals(action)) {
            Utils.writeStringFileFor7(new File(WHITE_LIGHT_3288),"1");
        } else if ("com.arcsoft.arcfacesingle.ACTION_FACE_DETECT_NO_FACE".equals(action)) {
            Utils.writeStringFileFor7(new File(WHITE_LIGHT_3288),"0");
        }
    }

    private Runnable closeRed = new Runnable() {
        @Override
        public void run() {
            Utils.writeStringFileFor7(new File(RED_LIGHT_3288),"0");
        }
    };

    private Runnable closeGreen = new Runnable() {
        @Override
        public void run() {
            Utils.writeStringFileFor7(new File(GREEN_LIGHT_3288),"0");
        }
    };
}
