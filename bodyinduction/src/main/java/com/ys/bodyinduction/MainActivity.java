package com.ys.bodyinduction;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        GpioUtils.upgradeRootPermissionForExport();
//        GpioUtils.exportGpio(225);
//        GpioUtils.upgradeRootPermissionForGpio(225);
//
//        Intent service = new Intent(this,GuardService.class);
//        startService(service);

        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendKeyCode2(KeyEvent.KEYCODE_VOLUME_UP);
                SystemClock.sleep(5);
                sendKeyCode2(KeyEvent.KEYCODE_DEL);
            }
        });
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
