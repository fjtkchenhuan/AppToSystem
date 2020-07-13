package com.ys.bodyinduction;

import android.app.Instrumentation;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;

import java.io.IOException;


public class GuardService extends Service {
    private final static int CHECK_APP = 0x3211;
    String curValue = "";
    String lastValue = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == CHECK_APP) {
                curValue = GpioUtils.getGpioValue(225);
//                Log.d("sky","curValue =" + curValue);
                if ("0".equals(lastValue) && "1".equals(curValue)) {
                    Log.d("sky","插入耳机，弹出音量调节窗");
                    sendKeyCode2(KeyEvent.KEYCODE_VOLUME_UP);
//                    sendKeyCode2(KeyEvent.KEYCODE_MEDIA_STOP);
//                    GpioUtils.writeNode("/sys/class/backlight/backlight/bl_power","1");
//                    GpioUtils.writeNode("/sys/bus/i2c/devices/2-0010/spkmode","1");
                } else if ("1".equals(lastValue) && "0".equals(curValue)){
                    SystemClock.sleep(50);
//                    Log.d("sky","人走了，开背光，开声音，播放视频");
//                    sendKeyCode2(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
//                    GpioUtils.writeNode("/sys/class/backlight/backlight/bl_power","0");
//                    GpioUtils.writeNode("/sys/bus/i2c/devices/2-0010/spkmode","1");
                }
                lastValue = curValue;
                mHandler.sendEmptyMessageDelayed(CHECK_APP, 1000);
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        mHandler.sendEmptyMessageDelayed(CHECK_APP, 1000);
    }

    private void sendKeyCode2(final int keyCode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 创建一个Instrumentation对象
                    Instrumentation inst = new Instrumentation();
                    // 调用inst对象的按键模拟方法
                    inst.sendKeyDownUpSync(keyCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
