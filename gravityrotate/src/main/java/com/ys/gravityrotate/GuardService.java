package com.ys.gravityrotate;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;


public class GuardService extends Service {
    private final static int CHECK_APP = 0x3211;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == CHECK_APP) {
                String io1 = GpioUtils.getGpioValue(68);
                String io2 = GpioUtils.getGpioValue(69);
                Log.d("sky","io1 = " + io1 + ",io2 = " + io2);
                Intent intent = new Intent("com.ys.set_display_rotate");

                if ("0".equals(io1) && "0".equals(io2))
                    intent.putExtra("displayRotate","180");
                else if ("0".equals(io1) && "1".equals(io2))
                    intent.putExtra("displayRotate","270");
                else if ("1".equals(io1) && "0".equals(io2))
                    intent.putExtra("displayRotate","90");
                else if ("1".equals(io1) && "1".equals(io2))
                    intent.putExtra("displayRotate","0");

                sendBroadcast(intent);
                mHandler.sendEmptyMessageDelayed(CHECK_APP, 1000);
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        mHandler.sendEmptyMessageDelayed(CHECK_APP, 3000);
    }
}
