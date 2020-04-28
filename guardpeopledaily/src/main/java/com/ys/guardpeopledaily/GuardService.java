package com.ys.guardpeopledaily;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.util.List;

import androidx.annotation.Nullable;

public class GuardService extends Service {

    private final static int CHECK_APP = 0x01;
    private final static String PEOPLE_APP_PACKAGE_NAME = "com.simpleprezi.smartplayer";
    private final static String PEOPLE_APP_CLASS_NAME = "com.simpleprezi.smartplayer.MainActivity";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == CHECK_APP) {
//                Log.d("sky","GuardService = " + Utils.getValueFromProp("persist.sys.guardApp"));
                if (PEOPLE_APP_PACKAGE_NAME.equals(Utils.getValueFromProp("persist.sys.guardApp"))) {
                    boolean isRunning = isMyAppRunning(GuardService.this,PEOPLE_APP_PACKAGE_NAME);
                    String topActivity = getTopActivity();
                    if (!isRunning || !PEOPLE_APP_PACKAGE_NAME.equals(topActivity)) {
                        Intent intent = new Intent();
                        intent.setClassName(PEOPLE_APP_PACKAGE_NAME,PEOPLE_APP_CLASS_NAME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
                mHandler.sendEmptyMessageDelayed(CHECK_APP, 1000);
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        mHandler.sendEmptyMessageDelayed(CHECK_APP, 1000);
    }

    private boolean isMyAppRunning(Context context, String packageName) {
        boolean result = false;
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
        if (appProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : appProcesses) {
                if (runningAppProcessInfo.processName.equals(packageName)) {
                    int status = runningAppProcessInfo.importance;
                    if (status == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
                            || status == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }


    private String getTopActivity() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> forGroundActivity = activityManager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo currentActivity;
        currentActivity = forGroundActivity.get(0);
        String topActivity = currentActivity.topActivity.getPackageName();
        return topActivity;
    }

    @Override
    public void onDestroy() {
        Intent service = new Intent(this,GuardService.class);
        startService(service);
        super.onDestroy();
    }
}
